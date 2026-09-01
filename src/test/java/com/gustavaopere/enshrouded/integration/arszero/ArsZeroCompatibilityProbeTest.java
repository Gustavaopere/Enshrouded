package com.gustavaopere.enshrouded.integration.arszero;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ArsZeroCompatibilityProbeTest {
    @Test
    void absentOptionalModIsUnavailableWithoutBeingAnIncompatibility() {
        ArsZeroCompatibilityProbe probe = ArsZeroCompatibilityProbe.inspect(false, ignored -> Optional.empty());

        assertFalse(probe.available());
        assertEquals(ArsZeroCompatibilityProbe.Status.MOD_ABSENT, probe.status());
        assertTrue(probe.lichType().isEmpty());
    }

    @Test
    void loadedModRequiresTheExactLichRegistryEntry() {
        ArsZeroCompatibilityProbe missing = ArsZeroCompatibilityProbe.inspect(true, ignored -> Optional.empty());

        assertFalse(missing.available());
        assertEquals(ArsZeroCompatibilityProbe.Status.INCOMPATIBLE, missing.status());
        assertTrue(missing.lichType().isEmpty());
    }
}
