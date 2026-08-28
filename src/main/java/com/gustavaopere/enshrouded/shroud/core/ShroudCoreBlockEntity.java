package com.gustavaopere.enshrouded.shroud.core;

import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.registry.ModBlockEntities;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;

import java.util.UUID;

public final class ShroudCoreBlockEntity extends BlockEntity {
    private static final String CORE_ID_TAG = "CoreId";
    private static final String REGION_ID_TAG = "RegionId";
    private static final String AUTO_ACTIVATE_TAG = "AutoActivate";
    private static final int LEVEL_ONE_TIER = 1;

    private UUID coreId;
    private UUID regionId;
    private boolean autoActivate;

    public ShroudCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHROUD_CORE.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ensureLocalIdentity();
        ShroudCoreRegistrationQueue.enqueue(
                serverLevel,
                worldPosition,
                coreId,
                regionId,
                LEVEL_ONE_TIER,
                EnshroudedConfig.coreMaxInfluenceRadius(),
                expansionSeed(coreId),
                autoActivate
        );
    }

    /**
     * Marks this physical core as an automatic seed rather than a manually placed dormant core.
     * The flag is persisted locally; canonical activation still happens only when the registration
     * queue drains on the server tick.
     */
    public void requestAutomaticActivation() {
        autoActivate = true;
        setChanged();
        if (level instanceof ServerLevel serverLevel) {
            ensureLocalIdentity();
            ShroudCoreRegistrationQueue.enqueue(
                    serverLevel,
                    worldPosition,
                    coreId,
                    regionId,
                    LEVEL_ONE_TIER,
                    EnshroudedConfig.coreMaxInfluenceRadius(),
                    expansionSeed(coreId),
                    true
            );
        }
    }

    boolean matchesIdentity(UUID expectedCoreId, UUID expectedRegionId) {
        return expectedCoreId.equals(coreId) && expectedRegionId.equals(regionId);
    }

    CoreMutationResult retirePhysicalCore(ServerLevel serverLevel) {
        ShroudSavedData savedData = ShroudSavedData.get(serverLevel);
        if (coreId == null) {
            return CoreMutationResult.unchanged(savedData.state());
        }

        ShroudCoreState core = savedData.state().cores().get(coreId);
        if (core == null) {
            return CoreMutationResult.unchanged(savedData.state());
        }

        CoreMutationResult retirement = switch (core.lifecycleState()) {
            case DORMANT -> ShroudCoreService.discardDormant(savedData.state(), coreId);
            case ACTIVE -> ShroudCoreService.destroy(savedData.state(), coreId);
            case DESTROYED, PURIFIED -> CoreMutationResult.unchanged(savedData.state());
        };

        if (retirement.changed()) {
            savedData.replace(retirement.state());
            if (core.lifecycleState() == CoreLifecycleState.ACTIVE) {
                NeoForge.EVENT_BUS.post(new ShroudCoreDestroyedEvent(serverLevel, coreId));
            }
        }
        return retirement;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        boolean hasCoreId = tag.hasUUID(CORE_ID_TAG);
        boolean hasRegionId = tag.hasUUID(REGION_ID_TAG);
        if (hasCoreId != hasRegionId) {
            throw new IllegalStateException("Shroud core block entity has incomplete persistent identity");
        }
        coreId = hasCoreId ? tag.getUUID(CORE_ID_TAG) : null;
        regionId = hasRegionId ? tag.getUUID(REGION_ID_TAG) : null;
        autoActivate = tag.getBoolean(AUTO_ACTIVATE_TAG);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (coreId != null && regionId != null) {
            tag.putUUID(CORE_ID_TAG, coreId);
            tag.putUUID(REGION_ID_TAG, regionId);
        }
        if (autoActivate) {
            tag.putBoolean(AUTO_ACTIVATE_TAG, true);
        }
    }

    private void ensureLocalIdentity() {
        if (coreId == null && regionId == null) {
            coreId = UUID.randomUUID();
            regionId = UUID.randomUUID();
            setChanged();
            return;
        }
        if (coreId == null || regionId == null) {
            throw new IllegalStateException("Shroud core block entity has incomplete local identity");
        }
    }

    private static long expansionSeed(UUID coreId) {
        return coreId.getMostSignificantBits() ^ coreId.getLeastSignificantBits();
    }
}
