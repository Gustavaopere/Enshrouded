package com.gustavaopere.enshrouded.combat.magic;

import com.gustavaopere.enshrouded.api.combat.MagicDamageClassification;
import com.gustavaopere.enshrouded.api.combat.MagicDamageConfidence;
import com.gustavaopere.enshrouded.api.combat.MagicDamageKind;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class MagicResistanceServiceTest {
    private static final MagicDamageClassification MAGIC = new MagicDamageClassification(
            MagicDamageKind.GENERIC_MAGIC,
            MagicDamageConfidence.CERTAIN
    );
    private static final MagicDamageClassification PHYSICAL = new MagicDamageClassification(
            MagicDamageKind.NON_MAGIC,
            MagicDamageConfidence.CERTAIN
    );
    private static final MagicDamageClassification UNKNOWN = new MagicDamageClassification(
            MagicDamageKind.UNKNOWN,
            MagicDamageConfidence.UNKNOWN
    );

    @Test
    void positivelyClassifiedMagicScalesOnceWithCorruptionIntensity() {
        MagicResistanceService service = new MagicResistanceService(0.35D);

        assertEquals(10.0F, service.reduceDamage(10.0F, 0.0F, MAGIC), 0.0001F);
        assertEquals(8.25F, service.reduceDamage(10.0F, 0.50F, MAGIC), 0.0001F);
        assertEquals(6.50F, service.reduceDamage(10.0F, 1.0F, MAGIC), 0.0001F);
    }

    @Test
    void physicalAndUnknownDamageRemainUnchanged() {
        MagicResistanceService service = new MagicResistanceService(0.35D);

        assertEquals(10.0F, service.reduceDamage(10.0F, 1.0F, PHYSICAL), 0.0001F);
        assertEquals(10.0F, service.reduceDamage(10.0F, 1.0F, UNKNOWN), 0.0001F);
    }

    @Test
    void configuredResistanceCanBeMeaningfulButNeverImmunity() {
        MagicResistanceService maximum = new MagicResistanceService(MagicResistanceService.HARD_MAX_RESISTANCE);
        assertEquals(2.50F, maximum.reduceDamage(10.0F, 1.0F, MAGIC), 0.0001F);

        assertThrows(IllegalArgumentException.class, () -> new MagicResistanceService(1.0D));
        assertThrows(IllegalArgumentException.class, () -> new MagicResistanceService(Double.NaN));
    }

    @Test
    void invalidRuntimeInputsFailClosed() {
        MagicResistanceService service = new MagicResistanceService(0.35D);

        assertEquals(10.0F, service.reduceDamage(10.0F, Float.NaN, MAGIC), 0.0001F);
        assertEquals(10.0F, service.reduceDamage(10.0F, -1.0F, MAGIC), 0.0001F);
        assertEquals(0.0F, service.reduceDamage(-5.0F, 1.0F, MAGIC), 0.0001F);
    }
}
