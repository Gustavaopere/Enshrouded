package com.gustavaopere.enshrouded.combat.magic;

import com.gustavaopere.enshrouded.api.combat.MagicDamageClassification;
import com.gustavaopere.enshrouded.api.combat.MagicDamageConfidence;
import com.gustavaopere.enshrouded.api.combat.MagicDamageKind;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class DefaultMagicDamageClassifierTest {
    @Test
    void standaloneTagsResolveIntoFoundationClassification() {
        MagicDamageClassification magical = DefaultMagicDamageClassifier.classifyTags(true, false);
        assertEquals(MagicDamageKind.GENERIC_MAGIC, magical.kind());
        assertEquals(MagicDamageConfidence.CERTAIN, magical.confidence());

        MagicDamageClassification bypass = DefaultMagicDamageClassifier.classifyTags(true, true);
        assertEquals(MagicDamageKind.NON_MAGIC, bypass.kind(),
                "explicit magic_bypass must prevent corrupted-mob resistance even when a source is also tagged magic");
        assertEquals(MagicDamageConfidence.CERTAIN, bypass.confidence());

        MagicDamageClassification unknown = DefaultMagicDamageClassifier.classifyTags(false, false);
        assertEquals(MagicDamageKind.UNKNOWN, unknown.kind());
        assertEquals(MagicDamageConfidence.UNKNOWN, unknown.confidence());
    }

    @Test
    void adapterEnrichmentProducesOneFoundationDecision() {
        MagicDamageClassification baseline = new MagicDamageClassification(
                MagicDamageKind.UNKNOWN,
                MagicDamageConfidence.UNKNOWN
        );
        MagicDamageClassification enriched = DefaultMagicDamageClassifier.resolveEnrichments(
                baseline,
                List.of(
                        new MagicDamageClassification(MagicDamageKind.ARCANE, MagicDamageConfidence.LIKELY),
                        new MagicDamageClassification(MagicDamageKind.ARCANE, MagicDamageConfidence.CERTAIN)
                )
        );

        assertEquals(MagicDamageKind.ARCANE, enriched.kind());
        assertEquals(MagicDamageConfidence.CERTAIN, enriched.confidence(),
                "adapters may enrich classification/confidence but must converge to one Foundation decision");
    }

    @Test
    void conflictingEqualConfidenceAdapterSignalsFailClosed() {
        MagicDamageClassification resolved = DefaultMagicDamageClassifier.resolveEnrichments(
                new MagicDamageClassification(MagicDamageKind.UNKNOWN, MagicDamageConfidence.UNKNOWN),
                List.of(
                        new MagicDamageClassification(MagicDamageKind.ARCANE, MagicDamageConfidence.CERTAIN),
                        new MagicDamageClassification(MagicDamageKind.NON_MAGIC, MagicDamageConfidence.CERTAIN)
                )
        );

        assertEquals(MagicDamageKind.UNKNOWN, resolved.kind());
        assertEquals(MagicDamageConfidence.UNKNOWN, resolved.confidence(),
                "equally strong contradictory adapter signals must not accidentally grant resistance");
    }
}
