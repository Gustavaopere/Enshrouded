package com.gustavaopere.enshrouded.combat.magic;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.combat.MagicDamageClassification;
import com.gustavaopere.enshrouded.api.combat.MagicDamageClassifier;
import com.gustavaopere.enshrouded.api.combat.MagicDamageConfidence;
import com.gustavaopere.enshrouded.api.combat.MagicDamageKind;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Standalone magic classification baseline backed by damage-type tags plus optional evidence. */
public final class DefaultMagicDamageClassifier implements MagicDamageClassifier {
    public static final TagKey<DamageType> MAGIC = tag("magic");
    public static final TagKey<DamageType> MAGIC_BYPASS = tag("magic_bypass");

    private final List<MagicDamageClassifier> enrichments;

    public DefaultMagicDamageClassifier() {
        this(List.of());
    }

    public DefaultMagicDamageClassifier(List<? extends MagicDamageClassifier> enrichments) {
        Objects.requireNonNull(enrichments, "enrichments");
        this.enrichments = List.copyOf(enrichments);
    }

    @Override
    public MagicDamageClassification classify(DamageSource source) {
        Objects.requireNonNull(source, "source");
        MagicDamageClassification baseline = classifyTags(source.is(MAGIC), source.is(MAGIC_BYPASS));
        if (enrichments.isEmpty() || baseline.kind() == MagicDamageKind.NON_MAGIC) {
            return baseline;
        }

        List<MagicDamageClassification> evidence = enrichments.stream()
                .map(classifier -> classifier.classify(source))
                .toList();
        return resolveEnrichments(baseline, evidence);
    }

    static MagicDamageClassification classifyTags(boolean magic, boolean bypass) {
        if (bypass) {
            return classification(MagicDamageKind.NON_MAGIC, MagicDamageConfidence.CERTAIN);
        }
        if (magic) {
            return classification(MagicDamageKind.GENERIC_MAGIC, MagicDamageConfidence.CERTAIN);
        }
        return unknown();
    }

    /**
     * Combines optional adapter evidence into one Foundation-owned classification. Adapters never
     * apply resistance themselves; contradictory strongest signals fail closed to UNKNOWN.
     */
    static MagicDamageClassification resolveEnrichments(
            MagicDamageClassification baseline,
            List<MagicDamageClassification> enrichments) {
        Objects.requireNonNull(baseline, "baseline");
        Objects.requireNonNull(enrichments, "enrichments");

        List<MagicDamageClassification> candidates = new ArrayList<>(enrichments.size() + 1);
        candidates.add(baseline);
        for (MagicDamageClassification enrichment : enrichments) {
            candidates.add(Objects.requireNonNull(enrichment, "enrichment"));
        }

        int strongestRank = candidates.stream()
                .mapToInt(classification -> rank(classification.confidence()))
                .max()
                .orElse(0);
        List<MagicDamageClassification> strongest = candidates.stream()
                .filter(classification -> rank(classification.confidence()) == strongestRank)
                .toList();

        MagicDamageConfidence confidence = strongest.getFirst().confidence();
        MagicDamageKind resolvedKind = resolveKinds(strongest);
        return resolvedKind == MagicDamageKind.UNKNOWN
                ? unknown()
                : classification(resolvedKind, confidence);
    }

    private static MagicDamageKind resolveKinds(List<MagicDamageClassification> strongest) {
        MagicDamageKind first = strongest.getFirst().kind();
        if (strongest.stream().allMatch(classification -> classification.kind() == first)) {
            return first;
        }

        boolean anyNonMagic = strongest.stream().anyMatch(classification -> classification.kind() == MagicDamageKind.NON_MAGIC);
        boolean anyMagical = strongest.stream().anyMatch(MagicDamageClassification::magical);
        if (anyNonMagic && anyMagical) {
            return MagicDamageKind.UNKNOWN;
        }

        List<MagicDamageKind> specificMagic = strongest.stream()
                .map(MagicDamageClassification::kind)
                .filter(MagicDamageKind::magical)
                .filter(kind -> kind != MagicDamageKind.GENERIC_MAGIC)
                .distinct()
                .toList();
        if (specificMagic.size() == 1 && strongest.stream().allMatch(classification ->
                classification.kind() == MagicDamageKind.GENERIC_MAGIC || classification.kind() == specificMagic.getFirst())) {
            return specificMagic.getFirst();
        }

        return MagicDamageKind.UNKNOWN;
    }

    private static int rank(MagicDamageConfidence confidence) {
        return switch (confidence) {
            case UNKNOWN -> 0;
            case LIKELY -> 1;
            case CERTAIN -> 2;
        };
    }

    private static MagicDamageClassification classification(MagicDamageKind kind, MagicDamageConfidence confidence) {
        return new MagicDamageClassification(kind, confidence);
    }

    private static MagicDamageClassification unknown() {
        return classification(MagicDamageKind.UNKNOWN, MagicDamageConfidence.UNKNOWN);
    }

    private static TagKey<DamageType> tag(String path) {
        return TagKey.create(
                Registries.DAMAGE_TYPE,
                ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, path)
        );
    }
}
