package com.gustavaopere.enshrouded.flame.state;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Server-global owner-scoped Flame progression.
 *
 * <p>The canonical storage lives in the Overworld data storage so player/team progression is not
 * accidentally split when the same owner crosses dimensions. This differs intentionally from the
 * dimension-local Shroud field.</p>
 */
public final class FlameProgressionSavedData extends SavedData {
    public static final String DATA_NAME = "enshrouded_flame_progression";

    private static final SavedData.Factory<FlameProgressionSavedData> FACTORY =
            new SavedData.Factory<>(FlameProgressionSavedData::create, FlameProgressionSavedData::load);

    private FlameProgressionState state;

    private FlameProgressionSavedData(FlameProgressionState state) {
        this.state = Objects.requireNonNull(state, "state");
    }

    public static FlameProgressionSavedData create() {
        return new FlameProgressionSavedData(FlameProgressionState.empty());
    }

    public static FlameProgressionSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        Objects.requireNonNull(tag, "tag");
        Objects.requireNonNull(registries, "registries");
        return new FlameProgressionSavedData(FlameProgressionCodec.decode(tag));
    }

    public static FlameProgressionSavedData get(MinecraftServer server) {
        Objects.requireNonNull(server, "server");
        var storage = server.overworld().getDataStorage();
        FlameProgressionSavedData loaded = storage.get(FACTORY, DATA_NAME);
        Path dataFile = server.getWorldPath(LevelResource.ROOT)
                .resolve("data")
                .resolve(DATA_NAME + ".dat");
        FlameProgressionSavedData resolved = resolveLoadedOrAbsent(
                loaded,
                dataFile,
                FlameProgressionSavedData::create
        );
        if (loaded == null) {
            storage.set(DATA_NAME, resolved);
        }
        return resolved;
    }

    static FlameProgressionSavedData resolveLoadedOrAbsent(
            FlameProgressionSavedData loaded,
            Path dataFile,
            Supplier<FlameProgressionSavedData> factory) {
        Objects.requireNonNull(dataFile, "dataFile");
        Objects.requireNonNull(factory, "factory");
        if (loaded != null) {
            return loaded;
        }
        if (!Files.notExists(dataFile)) {
            throw new IllegalStateException(
                    "Existing Flame progression data could not be loaded safely: " + dataFile.toAbsolutePath()
            );
        }
        return Objects.requireNonNull(factory.get(), "factory result");
    }

    public static FlameProgressionSavedData get(ServerLevel level) {
        Objects.requireNonNull(level, "level");
        return get(level.getServer());
    }

    public FlameProgressionState state() {
        return state;
    }

    public FlameProgressionState.OwnerProgression progression(ProgressionOwner owner) {
        return state.progression(owner);
    }

    /** Applies one ritual checkpoint atomically; duplicate IDs leave state and dirty flag unchanged. */
    public boolean applyRitualCheckpoint(
            ProgressionOwner owner,
            ResourceLocation ritualId,
            int flameLevel,
            int passageLevel) {
        var next = state.applyRitualCheckpoint(owner, ritualId, flameLevel, passageLevel);
        if (next.isEmpty()) {
            return false;
        }
        state = next.orElseThrow();
        setDirty();
        return true;
    }

    public void replace(FlameProgressionState newState) {
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
        tag.merge(FlameProgressionCodec.encode(state));
        return tag;
    }
}
