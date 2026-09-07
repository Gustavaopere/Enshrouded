package com.gustavaopere.enshrouded.flame.altar;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FlameAltarFormationStateTest {
    @Test
    void currentSchemaMayRestoreFormedState() {
        FlameAltarFormationState restored = FlameAltarFormationState.fromPersisted(
                FlameAltarFormationState.CURRENT_SCHEMA_VERSION,
                true
        );

        assertTrue(restored.formed());
    }

    @Test
    void missingLegacyOrFutureSchemaFailsClosed() {
        assertFalse(FlameAltarFormationState.unformed().formed());
        assertFalse(FlameAltarFormationState.fromPersisted(0, true).formed());
        assertFalse(FlameAltarFormationState.fromPersisted(
                FlameAltarFormationState.CURRENT_SCHEMA_VERSION + 1,
                true
        ).formed());
    }

    @Test
    void explicitUnformedBitRemainsUnformedOnCurrentSchema() {
        assertFalse(FlameAltarFormationState.fromPersisted(
                FlameAltarFormationState.CURRENT_SCHEMA_VERSION,
                false
        ).formed());
    }
}
