package com.gustavaopere.enshrouded.api.combat;

public enum MagicDamageKind {
    NON_MAGIC(false),
    GENERIC_MAGIC(true),
    ARCANE(true),
    NECROTIC(true),
    ELEMENTAL(true),
    UNKNOWN(false);

    private final boolean magical;

    MagicDamageKind(boolean magical) {
        this.magical = magical;
    }

    public boolean magical() {
        return magical;
    }
}
