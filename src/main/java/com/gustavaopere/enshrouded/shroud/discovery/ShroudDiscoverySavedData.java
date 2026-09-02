package com.gustavaopere.enshrouded.shroud.discovery;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Objects;

/** Server-global discovery persistence stored in the overworld data storage. */
public final class ShroudDiscoverySavedData extends SavedData {
    public static final String DATA_NAME = "enshrouded_discovery";

    private static final SavedData.Factory<ShroudDiscoverySavedData> FACTORY =
            new SavedData.Factory<>(ShroudDiscoverySavedData::create, ShroudDiscoverySavedData::load);

    private ShroudDiscoveryState state;

    private ShroudDiscoverySavedData(ShroudDiscoveryState state) {
        this.state = Objects.requireNonNull(state, "state");
    }

    public static ShroudDiscoverySavedData create() {
        return new ShroudDiscoverySavedData(ShroudDiscoveryState.empty());
    }

    public static ShroudDiscoverySavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        Objects.requireNonNull(tag, "tag");
        Objects.requireNonNull(registries, "registries");
        return new ShroudDiscoverySavedData(ShroudDiscoveryCodec.decode(tag));
    }

    public static ShroudDiscoverySavedData get(ServerLevel level) {
        Objects.requireNonNull(level, "level");
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public ShroudDiscoveryState state() {
        return state;
    }

    public void replace(ShroudDiscoveryState newState) {
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
        tag.merge(ShroudDiscoveryCodec.encode(state));
        return tag;
    }
}
