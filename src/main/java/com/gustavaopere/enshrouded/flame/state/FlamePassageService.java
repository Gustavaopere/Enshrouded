package com.gustavaopere.enshrouded.flame.state;

import com.gustavaopere.enshrouded.api.progression.FlamePassageQuery;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;
import java.util.function.Function;

/** Persistence-backed implementation of the Foundation Flame passage read boundary. */
public final class FlamePassageService implements FlamePassageQuery {
    private final Function<ProgressionOwner, FlameProgressionState.OwnerProgression> progressionReader;

    public FlamePassageService(Function<ProgressionOwner, FlameProgressionState.OwnerProgression> progressionReader) {
        this.progressionReader = Objects.requireNonNull(progressionReader, "progressionReader");
    }

    public static FlamePassageService forServer(MinecraftServer server) {
        Objects.requireNonNull(server, "server");
        return new FlamePassageService(owner -> FlameProgressionSavedData.get(server).progression(owner));
    }

    @Override
    public int passageLevel(ProgressionOwner owner) {
        Objects.requireNonNull(owner, "owner");
        FlameProgressionState.OwnerProgression progression = Objects.requireNonNull(
                progressionReader.apply(owner),
                "progressionReader returned null"
        );
        return progression.passageLevel();
    }
}
