package com.gustavaopere.enshrouded.exposure.madness;

import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

/** Authoritative runtime seam. Gameplay behavior is introduced only after GameTest RED. */
public final class MadnessRuntime {
    private MadnessRuntime() {
    }

    public static MadnessStage apply(ServerPlayer player, ExposureSnapshot snapshot) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(snapshot, "snapshot");
        return snapshot.madnessStage();
    }
}
