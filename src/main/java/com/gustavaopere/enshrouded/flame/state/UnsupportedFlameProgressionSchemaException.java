package com.gustavaopere.enshrouded.flame.state;

/** Raised when persisted Flame progression was written by an unsupported future schema. */
public final class UnsupportedFlameProgressionSchemaException extends IllegalStateException {
    private final int schemaVersion;

    public UnsupportedFlameProgressionSchemaException(int schemaVersion) {
        super("unsupported Flame progression schema version: " + schemaVersion
                + " (current=" + FlameProgressionSchema.CURRENT_VERSION + ")");
        this.schemaVersion = schemaVersion;
    }

    public int schemaVersion() {
        return schemaVersion;
    }
}
