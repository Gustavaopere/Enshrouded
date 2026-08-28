package com.gustavaopere.enshrouded.shroud.state;

public final class UnsupportedShroudSchemaException extends IllegalArgumentException {
    public UnsupportedShroudSchemaException(int version) {
        super("Unsupported Shroud schema version: " + version);
    }
}
