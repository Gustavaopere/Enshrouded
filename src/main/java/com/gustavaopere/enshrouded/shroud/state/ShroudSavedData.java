package com.gustavaopere.enshrouded.shroud.state;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Objects;

/**
 * Dimension-local persistence adapter for the canonical logical Shroud field.
 *
 * <p>Instances are attached to a single {@link ServerLevel}'s data storage. Cross-dimension
 * aggregation belongs to explicit higher-level services and must never be hidden here.</p>
 */
public final class ShroudSavedData extends SavedData {
    public static final String DATA_NAME = "enshrouded_shroud";

    private static final SavedData.Factory<ShroudSavedData> FACTORY =
            new SavedData.Factory<>(ShroudSavedData::create, ShroudSavedData::load);

    private ShroudWorldState state;

    private ShroudSavedData(ShroudWorldState state) {
        this.state = Objects.requireNonNull(state, "state");
    }

    public static ShroudSavedData create() {
        return new ShroudSavedData(ShroudWorldState.empty());
    }

    public static ShroudSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        Objects.requireNonNull(tag, "tag");
        Objects.requireNonNull(registries, "registries");
        return new ShroudSavedData(ShroudStateCodec.decode(tag));
    }

    public static ShroudSavedData get(ServerLevel level) {
        Objects.requireNonNull(level, "level");
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public ShroudWorldState state() {
        return state;
    }

    public void replace(ShroudWorldState newState) {
        Objects.requireNonNull(newState, "newState");
        if (!state.equals(newState)) {
            state = newState;
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        Objects.requireNonNull(tag, "tag");
        Objects.requireNonNull(registries, "registries");
        tag.merge(ShroudStateCodec.encode(state));
        return tag;
    }
}
