package com.gustavaopere.enshrouded.exposure.madness;

import java.util.Arrays;
import java.util.Optional;

/** Presentation/gameplay severity derived only from the existing exposure reserve. */
public enum MadnessStage {
    STABLE("stable", false, false, false, false),
    UNEASY("uneasy", false, false, false, false),
    DISTORTED("distorted", true, false, false, false),
    CRITICAL("critical", true, true, true, false),
    FATAL("fatal", true, true, true, true);

    private final String id;
    private final boolean visualHallucinations;
    private final boolean audioHallucinations;
    private final boolean sprintLocked;
    private final boolean fatal;

    MadnessStage(
            String id,
            boolean visualHallucinations,
            boolean audioHallucinations,
            boolean sprintLocked,
            boolean fatal) {
        this.id = id;
        this.visualHallucinations = visualHallucinations;
        this.audioHallucinations = audioHallucinations;
        this.sprintLocked = sprintLocked;
        this.fatal = fatal;
    }

    public String id() {
        return id;
    }

    public boolean visualHallucinations() {
        return visualHallucinations;
    }

    public boolean audioHallucinations() {
        return audioHallucinations;
    }

    public boolean sprintLocked() {
        return sprintLocked;
    }

    public boolean fatal() {
        return fatal;
    }

    public static Optional<MadnessStage> fromId(String id) {
        return Arrays.stream(values()).filter(stage -> stage.id.equals(id)).findFirst();
    }
}
