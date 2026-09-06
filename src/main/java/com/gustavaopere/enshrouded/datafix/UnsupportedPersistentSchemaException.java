package com.gustavaopere.enshrouded.datafix;

import java.util.Objects;

/** Fail-closed signal for persistence outside the explicitly migratable version range. */
public final class UnsupportedPersistentSchemaException extends IllegalArgumentException {
    private final PersistentSubsystem subsystem;
    private final int schemaVersion;

    public UnsupportedPersistentSchemaException(PersistentSubsystem subsystem, int schemaVersion) {
        super("unsupported " + Objects.requireNonNull(subsystem, "subsystem").id()
                + " schema version: " + schemaVersion
                + " (migratable=" + subsystem.oldestMigratableVersion()
                + ".." + subsystem.currentVersion() + ")");
        this.subsystem = subsystem;
        this.schemaVersion = schemaVersion;
    }

    public PersistentSubsystem subsystem() {
        return subsystem;
    }

    public int schemaVersion() {
        return schemaVersion;
    }
}
