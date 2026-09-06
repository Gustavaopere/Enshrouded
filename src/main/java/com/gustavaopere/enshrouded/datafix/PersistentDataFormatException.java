package com.gustavaopere.enshrouded.datafix;

import java.util.Objects;

/** Controlled failure for malformed versioned persistence that must never be replaced with defaults. */
public final class PersistentDataFormatException extends IllegalArgumentException {
    private final PersistentSubsystem subsystem;

    public PersistentDataFormatException(PersistentSubsystem subsystem, String detail) {
        super("invalid " + Objects.requireNonNull(subsystem, "subsystem").id() + " persistent data: " + detail);
        this.subsystem = subsystem;
    }

    public PersistentSubsystem subsystem() {
        return subsystem;
    }
}
