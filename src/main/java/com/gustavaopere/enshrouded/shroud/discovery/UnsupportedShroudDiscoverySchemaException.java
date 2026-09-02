package com.gustavaopere.enshrouded.shroud.discovery;

public final class UnsupportedShroudDiscoverySchemaException extends IllegalArgumentException {
    public UnsupportedShroudDiscoverySchemaException(int version) {
        super("Unsupported Shroud discovery schema version: " + version);
    }
}
