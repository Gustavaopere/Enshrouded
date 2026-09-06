package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.flame.ward.FlameWardRuntime;
import com.gustavaopere.enshrouded.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
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
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("animation.flame_altar.idle");
    private static final RawAnimation RITUAL_AVAILABLE = RawAnimation.begin().thenLoop("animation.flame_altar.ritual_available");
    private static final RawAnimation RITUAL_CHARGE = RawAnimation.begin().thenPlay("animation.flame_altar.ritual_charge");
    private static final RawAnimation RITUAL_SUCCESS = RawAnimation.begin().thenPlay("animation.flame_altar.ritual_success");
    private static final RawAnimation LEVEL_TRANSITION = RawAnimation.begin().thenPlay("animation.flame_altar.level_transition");
    private static final RawAnimation INACTIVE = RawAnimation.begin().thenLoop("animation.flame_altar.inactive");

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public FlameAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLAME_ALTAR.get(), pos, state);
    }

    public ItemStackHandler inventory() {
        return inventory;
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
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            FlameWardRuntime.onAltarLoaded(serverLevel, worldPosition);
        }
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
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(INVENTORY_TAG, inventory.serializeNBT(registries));
    }
}
