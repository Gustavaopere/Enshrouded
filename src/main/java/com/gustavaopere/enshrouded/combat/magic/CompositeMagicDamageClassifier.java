package com.gustavaopere.enshrouded.combat.magic;

import com.gustavaopere.enshrouded.api.combat.MagicDamageClassification;
import com.gustavaopere.enshrouded.api.combat.MagicDamageClassifier;
import com.gustavaopere.enshrouded.api.combat.MagicDamageKind;
import net.minecraft.world.damagesource.DamageSource;

import java.util.List;
import java.util.Objects;

/**
 * Resolves the Foundation baseline and optional classification evidence into one final decision.
 * This class never mutates damage and never registers an event hook.
 */
public final class CompositeMagicDamageClassifier implements MagicDamageClassifier {
    private final MagicDamageClassifier baseline;
    private final List<MagicDamageClassifier> enrichments;

    public CompositeMagicDamageClassifier(
            MagicDamageClassifier baseline,
            List<? extends MagicDamageClassifier> enrichments) {
        this.baseline = Objects.requireNonNull(baseline, "baseline");
        Objects.requireNonNull(enrichments, "enrichments");
        this.enrichments = List.copyOf(enrichments);
    }

    @Override
    public MagicDamageClassification classify(DamageSource source) {
        Objects.requireNonNull(source, "source");
        MagicDamageClassification baselineClassification = baseline.classify(source);

        // An explicit NON_MAGIC baseline (notably enshrouded:magic_bypass) is authoritative.
        if (baselineClassification.kind() == MagicDamageKind.NON_MAGIC || enrichments.isEmpty()) {
            return baselineClassification;
        }

        List<MagicDamageClassification> evidence = enrichments.stream()
                .map(classifier -> classifier.classify(source))
                .toList();
        return resolveClassifications(baselineClassification, evidence);
    }

    static MagicDamageClassification resolveClassifications(
            MagicDamageClassification baseline,
            List<MagicDamageClassification> evidence) {
        return DefaultMagicDamageClassifier.resolveEnrichments(baseline, evidence);
    }
}
