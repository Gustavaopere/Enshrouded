package com.gustavaopere.enshrouded.api.shroud;

import java.util.Arrays;
import java.util.Optional;

public enum ShroudSeverity {
    CLEAR("clear"),
    SHROUD("shroud"),
    DEADLY("deadly");

    private final String id;

    ShroudSeverity(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static Optional<ShroudSeverity> fromId(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(value -> value.id.equals(id)).findFirst();
    }
}
