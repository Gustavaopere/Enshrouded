package com.gustavaopere.enshrouded.shroud.core;

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
    private static final int LEVEL_ONE_TIER = 1;
    private static final int LEVEL_ONE_DEFAULT_MAX_INFLUENCE_RADIUS = 128;

    private UUID coreId;
    private UUID regionId;

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
        ShroudSavedData savedData = ShroudSavedData.get(serverLevel);
        CoreMutationResult registration = ShroudCoreService.registerDormant(
                savedData.state(),
                coreId,
                regionId,
                worldPosition,
                LEVEL_ONE_TIER,
                LEVEL_ONE_DEFAULT_MAX_INFLUENCE_RADIUS,
                expansionSeed(coreId)
        );
        if (registration.changed()) {
            savedData.replace(registration.state());
        }
    }

    CoreMutationResult retireActivePersistentCore(ServerLevel serverLevel) {
        ShroudSavedData savedData = ShroudSavedData.get(serverLevel);
        if (coreId == null) {
            return CoreMutationResult.unchanged(savedData.state());
        }

        ShroudCoreState core = savedData.state().cores().get(coreId);
        if (core == null || core.lifecycleState() != CoreLifecycleState.ACTIVE) {
            return CoreMutationResult.unchanged(savedData.state());
        }

        CoreMutationResult destruction = ShroudCoreService.destroy(savedData.state(), coreId);
        if (destruction.changed()) {
            savedData.replace(destruction.state());
            NeoForge.EVENT_BUS.post(new ShroudCoreDestroyedEvent(serverLevel, coreId));
        }
        return destruction;
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
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (coreId != null && regionId != null) {
            tag.putUUID(CORE_ID_TAG, coreId);
            tag.putUUID(REGION_ID_TAG, regionId);
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
