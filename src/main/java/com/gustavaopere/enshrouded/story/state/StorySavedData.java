package com.gustavaopere.enshrouded.story.state;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

/** Server-global persistent Lich story state stored in the Overworld data storage. */
public final class StorySavedData extends SavedData {
    public static final String DATA_NAME = "enshrouded_story";

    private static final SavedData.Factory<StorySavedData> FACTORY =
            new SavedData.Factory<>(StorySavedData::create, StorySavedData::load);

    private LichStoryState state;

    private StorySavedData(LichStoryState state) {
        this.state = Objects.requireNonNull(state, "state");
    }

    public static StorySavedData create() {
        return new StorySavedData(LichStoryState.empty());
    }

    public static StorySavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        Objects.requireNonNull(tag, "tag");
        Objects.requireNonNull(registries, "registries");
        return new StorySavedData(StoryCodec.decode(tag));
    }

    public static StorySavedData get(MinecraftServer server) {
        Objects.requireNonNull(server, "server");
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static StorySavedData get(ServerLevel level) {
        Objects.requireNonNull(level, "level");
        return get(level.getServer());
    }

    public synchronized LichStoryState state() {
        return state;
    }

    public synchronized boolean createEncounter(
            ProgressionOwner owner,
            UUID encounterId,
            int manifestationIndex) {
        return apply(state.createEncounter(owner, encounterId, manifestationIndex));
    }

    public synchronized boolean activateEncounter(UUID encounterId, UUID physicalEntityId) {
        return apply(state.activateEncounter(encounterId, physicalEntityId));
    }

    public synchronized boolean defeatEncounter(UUID encounterId) {
        return apply(state.defeatEncounter(encounterId));
    }

    public synchronized boolean abortEncounter(UUID encounterId) {
        return apply(state.abortEncounter(encounterId));
    }

    public synchronized boolean issueReward(UUID encounterId) {
        return apply(state.issueReward(encounterId));
    }

    public synchronized boolean reconcileActiveEncounters(Predicate<UUID> activeEntityAlive) {
        return apply(state.reconcileActiveEncounters(activeEntityAlive));
    }

    public synchronized void replace(LichStoryState newState) {
        Objects.requireNonNull(newState, "newState");
        if (!state.equals(newState)) {
            state = newState;
            setDirty();
        }
    }

    @Override
    public synchronized CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        Objects.requireNonNull(tag, "tag");
        Objects.requireNonNull(registries, "registries");
        tag.merge(StoryCodec.encode(state));
        return tag;
    }

    private boolean apply(java.util.Optional<LichStoryState> next) {
        if (next.isEmpty()) {
            return false;
        }
        state = next.orElseThrow();
        setDirty();
        return true;
    }
}
