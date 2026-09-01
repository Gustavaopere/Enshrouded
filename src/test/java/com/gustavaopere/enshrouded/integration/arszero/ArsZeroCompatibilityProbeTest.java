package com.gustavaopere.enshrouded.integration.arszero;

import net.minecraft.world.entity.EntityType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
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

    @Test
    void nonMonsterRegistryEntryFailsClosedInsteadOfBecomingTheBossProvider() {
        ArsZeroCompatibilityProbe incompatible = ArsZeroCompatibilityProbe.inspect(
                true,
                id -> id.equals(ArsZeroCompatibilityProbe.LICH_ID)
                        ? Optional.of(EntityType.ARROW)
                        : Optional.empty()
        );

        assertFalse(incompatible.available());
        assertEquals(ArsZeroCompatibilityProbe.Status.INCOMPATIBLE, incompatible.status());
        assertTrue(incompatible.lichType().isEmpty());
    }

    @Test
    void exactRegistryEntryProducesOneStableReadySnapshot() {
        ArsZeroCompatibilityProbe ready = ArsZeroCompatibilityProbe.inspect(
                true,
                id -> id.equals(ArsZeroCompatibilityProbe.LICH_ID)
                        ? Optional.of(EntityType.ZOMBIE)
                        : Optional.empty()
        );

        assertTrue(ready.available());
        assertEquals(ArsZeroCompatibilityProbe.Status.READY, ready.status());
        assertSame(EntityType.ZOMBIE, ready.lichType().orElseThrow());
    }
}
