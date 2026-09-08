package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.flame.ward.FlameWardRuntime;
import com.gustavaopere.enshrouded.protection.ProtectionRuntimeBindings;
import com.gustavaopere.enshrouded.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/** One-slot persistent inventory backing the physical, animated Flame Altar. */
public final class FlameAltarBlockEntity extends BlockEntity implements MenuProvider, GeoBlockEntity {
    private static final String INVENTORY_TAG = "Inventory";
    private static final String FORMATION_TAG = "Formation";
    private static final String FORMATION_SCHEMA_TAG = "SchemaVersion";
    private static final String FORMATION_FORMED_TAG = "Formed";
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("animation.flame_altar.idle");
    private static final RawAnimation RITUAL_AVAILABLE = RawAnimation.begin().thenLoop("animation.flame_altar.ritual_available");
    private static final RawAnimation RITUAL_CHARGE = RawAnimation.begin().thenPlay("animation.flame_altar.ritual_charge");
    private static final RawAnimation RITUAL_SUCCESS = RawAnimation.begin().thenPlay("animation.flame_altar.ritual_success");
    private static final RawAnimation LEVEL_TRANSITION = RawAnimation.begin().thenPlay("animation.flame_altar.level_transition");
    private static final RawAnimation INACTIVE = RawAnimation.begin().thenLoop("animation.flame_altar.inactive");
    private static final RawAnimation SANCTUARY_ACTIVE = RawAnimation.begin().thenLoop("animation.flame_altar.sanctuary_active");
    private static final RawAnimation PURIFICATION_RELEASE = RawAnimation.begin().thenPlay("animation.flame_altar.purification_release");

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private FlameAltarFormationState formationState = FlameAltarFormationState.unformed();
    private FlameAltarFormationState pendingFormationRecovery = FlameAltarFormationState.unformed();
    private FlameAltarFormationPhase formationPhase = FlameAltarFormationPhase.UNFORMED;

    public FlameAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLAME_ALTAR.get(), pos, state);
    }

    public ItemStackHandler inventory() {
        return inventory;
    }

    public boolean isFormed() {
        return formationState.formed() && formationPhase == FlameAltarFormationPhase.FORMED;
    }

    public FlameAltarFormationPhase formationPhase() {
        return formationPhase;
    }

    /**
     * Explicit server-side formation request from the authoritative controller interaction.
     * Validation is synchronous, bounded to the canonical 3x3 footprint and fail-closed.
     */
    FlameAltarStructureValidator.Result requestFormation(ServerLevel level) {
        if (isFormed()) {
            return new FlameAltarStructureValidator.Result(
                    FlameAltarStructureValidator.Status.VALID,
                    worldPosition
            );
        }

        formationPhase = FlameAltarFormationPhase.VALIDATING;
        FlameAltarStructureValidator.Result result = validateFormation(level);

        if (result.status() != FlameAltarStructureValidator.Status.VALID) {
            formationPhase = FlameAltarFormationPhase.UNFORMED;
            return result;
        }

        commitFormation(level);
        return result;
    }

    private FlameAltarStructureValidator.Result validateFormation(ServerLevel level) {
        return new FlameAltarStructureValidator(
                ProtectionRuntimeBindings.protectedAreas()
        ).validate(level, worldPosition);
    }

    private void commitFormation(ServerLevel level) {
        formationState = new FlameAltarFormationState(FlameAltarFormationState.CURRENT_SCHEMA_VERSION, true);
        pendingFormationRecovery = FlameAltarFormationState.unformed();
        formationPhase = FlameAltarFormationPhase.FORMED;
        setShellFormedPresentation(level, true);
        setChanged();
        FlameWardRuntime.onAltarLoaded(level, worldPosition);
    }

    /** Revokes only multiblock formation state; ritual inventory/progression remain untouched. */
    void unform(ServerLevel level) {
        boolean hadFormation = formationState.formed()
                || pendingFormationRecovery.formed()
                || formationPhase != FlameAltarFormationPhase.UNFORMED;
        formationState = FlameAltarFormationState.unformed();
        pendingFormationRecovery = FlameAltarFormationState.unformed();
        formationPhase = FlameAltarFormationPhase.UNFORMED;
        setShellFormedPresentation(level, false);
        FlameWardRuntime.onAltarRemoved(level, worldPosition);
        if (hadFormation) {
            setChanged();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!(level instanceof ServerLevel serverLevel) || !hasPendingFormationRecovery()) {
            return;
        }

        // NeoForge may invoke BlockEntity#onLoad before every block entity in the 3x3 footprint is
        // visible to level queries. Schedule exactly one bounded retry for the next server tick.
        // MinecraftServer#schedule honors TickTask timing; pushing a timed task through WORKQUEUE
        // only enqueues work and does not provide this delayed execution contract.
        var server = serverLevel.getServer();
        server.schedule(new TickTask(
                server.getTickCount() + 1,
                () -> {
                    if (!isRemoved() && level == serverLevel) {
                        retryPendingFormationRecovery(serverLevel);
                    }
                }
        ));
    }

    boolean hasPendingFormationRecovery() {
        return pendingFormationRecovery.formed() && !isFormed();
    }

    /**
     * Revalidates a persisted formation intent after the relevant chunk load has reached a safe,
     * deferred server tick. This never acquires chunks; the validator remains fail-closed.
     */
    void retryPendingFormationRecovery(ServerLevel serverLevel) {
        if (!hasPendingFormationRecovery()) {
            return;
        }

        formationPhase = FlameAltarFormationPhase.VALIDATING;
        FlameAltarStructureValidator.Result result = validateFormation(serverLevel);
        switch (result.status()) {
            case VALID -> commitFormation(serverLevel);
            case REQUIRED_CHUNK_UNLOADED, PROTECTION_INDETERMINATE -> {
                // Keep only the persisted recovery intent. A later adjacent ChunkEvent.Load may
                // supply the missing evidence; until then gameplay stays explicitly UNFORMED.
                formationState = FlameAltarFormationState.unformed();
                formationPhase = FlameAltarFormationPhase.UNFORMED;
                setShellFormedPresentation(serverLevel, false);
                FlameWardRuntime.onAltarRemoved(serverLevel, worldPosition);
            }
            default -> unform(serverLevel);
        }
    }

    private void setShellFormedPresentation(ServerLevel level, boolean formed) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                BlockPos pos = worldPosition.offset(dx, 0, dz);
                if (!level.getChunkSource().hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
                    continue;
                }
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof FlameAltarBraceBlock) {
                    if (state.getValue(FlameAltarBraceBlock.FORMED) != formed) {
                        level.setBlock(pos, state.setValue(FlameAltarBraceBlock.FORMED, formed), Block.UPDATE_CLIENTS);
                    }
                } else if (state.getBlock() instanceof FlameAltarRuneBlock
                        && state.getValue(FlameAltarRuneBlock.FORMED) != formed) {
                    level.setBlock(pos, state.setValue(FlameAltarRuneBlock.FORMED, formed), Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "flame_altar", 8,
                state -> state.setAndContinue(IDLE_ANIMATION))
                .triggerableAnim("ritual_available", RITUAL_AVAILABLE)
                .triggerableAnim("ritual_charge", RITUAL_CHARGE)
                .triggerableAnim("ritual_success", RITUAL_SUCCESS)
                .triggerableAnim("level_transition", LEVEL_TRANSITION)
                .triggerableAnim("inactive", INACTIVE));
        controllers.add(new AnimationController<>(this, "sanctuary_ward", 10,
                state -> state.setAndContinue(SANCTUARY_ACTIVE))
                .triggerableAnim("purification_release", PURIFICATION_RELEASE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    /**
     * Emits presentation only after the canonical server-side ritual result has been accepted.
     * GeckoLib distributes the trigger to tracking clients; it never decides or mutates progression.
     */
    void triggerAuthoritativePresentation(boolean levelTransition) {
        if (!(level instanceof ServerLevel)) {
            return;
        }
        triggerAnim("flame_altar", levelTransition ? "level_transition" : "ritual_success");
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel serverLevel) {
            FlameWardRuntime.onAltarRemoved(serverLevel, worldPosition);
        }
        super.setRemoved();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.enshrouded.flame_altar");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FlameAltarMenu(containerId, playerInventory, this);
    }

    void dropContents(ServerLevel level) {
        ItemStack stack = inventory.getStackInSlot(FlameAltarService.OFFERING_SLOT);
        if (stack.isEmpty()) {
            return;
        }
        Containers.dropItemStack(
                level,
                worldPosition.getX() + 0.5D,
                worldPosition.getY() + 0.5D,
                worldPosition.getZ() + 0.5D,
                stack.copy()
        );
        inventory.setStackInSlot(FlameAltarService.OFFERING_SLOT, ItemStack.EMPTY);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(INVENTORY_TAG)) {
            inventory.deserializeNBT(registries, tag.getCompound(INVENTORY_TAG));
        }

        // A persisted FORMED bit is only recovery intent. Gameplay remains UNFORMED until the
        // bounded world validator explicitly re-confirms the physical structure.
        formationState = FlameAltarFormationState.unformed();
        pendingFormationRecovery = FlameAltarFormationState.unformed();
        formationPhase = FlameAltarFormationPhase.UNFORMED;
        if (tag.contains(FORMATION_TAG)) {
            CompoundTag formation = tag.getCompound(FORMATION_TAG);
            pendingFormationRecovery = FlameAltarFormationState.fromPersisted(
                    formation.getInt(FORMATION_SCHEMA_TAG),
                    formation.getBoolean(FORMATION_FORMED_TAG)
            );
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(INVENTORY_TAG, inventory.serializeNBT(registries));

        FlameAltarFormationState persistedFormation = formationState.formed()
                ? formationState
                : pendingFormationRecovery;
        CompoundTag formation = new CompoundTag();
        formation.putInt(FORMATION_SCHEMA_TAG, persistedFormation.schemaVersion());
        formation.putBoolean(FORMATION_FORMED_TAG, persistedFormation.formed());
        tag.put(FORMATION_TAG, formation);
    }
}
