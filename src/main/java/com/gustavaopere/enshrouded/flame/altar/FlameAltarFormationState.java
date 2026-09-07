package com.gustavaopere.enshrouded.flame.altar;

/**
 * Persistable formation fact for the physical Flame Altar controller.
 *
 * <p>The state is deliberately small and fail-closed. Only the exact schema currently understood
 * by this build may restore a FORMED bit; missing, legacy or future schemas resolve to UNFORMED
 * until the bounded world structure is validated again.</p>
 */
public record FlameAltarFormationState(int schemaVersion, boolean formed) {
    public static final int CURRENT_SCHEMA_VERSION = 1;

    public static FlameAltarFormationState unformed() {
        return new FlameAltarFormationState(CURRENT_SCHEMA_VERSION, false);
    }

    public static FlameAltarFormationState fromPersisted(int schemaVersion, boolean formed) {
        if (schemaVersion != CURRENT_SCHEMA_VERSION) {
            return unformed();
        }
        return new FlameAltarFormationState(CURRENT_SCHEMA_VERSION, formed);
    }
}
