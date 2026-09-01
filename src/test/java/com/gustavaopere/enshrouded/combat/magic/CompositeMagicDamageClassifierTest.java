package com.gustavaopere.enshrouded.combat.magic;

import com.gustavaopere.enshrouded.api.combat.MagicDamageClassification;
import com.gustavaopere.enshrouded.api.combat.MagicDamageConfidence;
import com.gustavaopere.enshrouded.api.combat.MagicDamageKind;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class CompositeMagicDamageClassifierTest {
    private static final MagicDamageClassification UNKNOWN = new MagicDamageClassification(
            MagicDamageKind.UNKNOWN,
            MagicDamageConfidence.UNKNOWN
    );
    private static final MagicDamageClassification GENERIC_MAGIC = new MagicDamageClassification(
            MagicDamageKind.GENERIC_MAGIC,
            MagicDamageConfidence.CERTAIN
    );
    private static final MagicDamageClassification NON_MAGIC = new MagicDamageClassification(
            MagicDamageKind.NON_MAGIC,
            MagicDamageConfidence.CERTAIN
    );

    @Test
    void multipleAdapterSignalsCollapseIntoOneFinalClassification() {
        MagicDamageClassification resolved = CompositeMagicDamageClassifier.resolveClassifications(
                UNKNOWN,
                List.of(GENERIC_MAGIC, GENERIC_MAGIC)
        );

        assertEquals(MagicDamageKind.GENERIC_MAGIC, resolved.kind());
        assertEquals(MagicDamageConfidence.CERTAIN, resolved.confidence());
        assertTrue(resolved.magical());
    }

    @Test
    void unknownAdapterEvidenceFallsBackToCoreBaseline() {
        MagicDamageClassification resolved = CompositeMagicDamageClassifier.resolveClassifications(
                GENERIC_MAGIC,
                List.of(UNKNOWN, UNKNOWN)
        );

        assertEquals(GENERIC_MAGIC, resolved);
    }

    @Test
    void contradictoryCertainEvidenceFailsClosed() {
        MagicDamageClassification resolved = CompositeMagicDamageClassifier.resolveClassifications(
                UNKNOWN,
                List.of(GENERIC_MAGIC, NON_MAGIC)
        );

        assertEquals(MagicDamageKind.UNKNOWN, resolved.kind());
        assertEquals(MagicDamageConfidence.UNKNOWN, resolved.confidence());
        assertFalse(resolved.magical());
    }

    @Test
    void finalClassificationIsReducedExactlyOnceAfterComposition() {
        MagicDamageClassification resolved = CompositeMagicDamageClassifier.resolveClassifications(
                UNKNOWN,
                List.of(GENERIC_MAGIC, GENERIC_MAGIC)
        );
        MagicResistanceService service = new MagicResistanceService(0.35D);

        float reducedOnce = service.reduceDamage(10.0F, 1.0F, resolved);
        assertEquals(6.50F, reducedOnce, 0.0001F);

        float hypotheticalDoubleReduction = service.reduceDamage(reducedOnce, 1.0F, resolved);
        assertEquals(4.225F, hypotheticalDoubleReduction, 0.0001F);
        assertFalse(Math.abs(reducedOnce - hypotheticalDoubleReduction) < 0.0001F,
                "multiple adapter evidence must collapse before the one runtime reduction, not trigger repeated reduction");
    }
}
