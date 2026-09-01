package com.gustavaopere.enshrouded.integration.arsnouveau;

import com.gustavaopere.enshrouded.api.combat.MagicDamageClassification;
import com.gustavaopere.enshrouded.api.combat.MagicDamageClassifier;
import com.gustavaopere.enshrouded.api.combat.MagicDamageConfidence;
import com.gustavaopere.enshrouded.api.combat.MagicDamageKind;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;

import java.util.Objects;
import java.util.Set;

/**
 * Optional Ars Nouveau classification evidence without an Ars Nouveau class dependency.
 *
 * <p>Ars Nouveau 1.21.1 publishes these exact damage types into NeoForge's {@code is_magic}
 * tag already. This adapter is therefore a narrow fallback/evidence source for the same canonical
 * Enshrouded classification pipeline, never a damage reducer or event hook.</p>
 */
public final class ArsNouveauMagicAdapter implements MagicDamageClassifier {
    private static final String NAMESPACE = "ars_nouveau";

    public static final Set<ResourceLocation> KNOWN_MAGIC_DAMAGE_TYPES = Set.of(
            id("spell"),
            id("frost"),
            id("flare"),
            id("crush"),
            id("windshear")
    );

    @Override
    public MagicDamageClassification classify(DamageSource source) {
        Objects.requireNonNull(source, "source");
        return source.typeHolder().unwrapKey()
                .map(key -> classifyDamageType(key.location()))
                .orElseGet(ArsNouveauMagicAdapter::unknown);
    }

    public MagicDamageClassification classifyDamageType(ResourceLocation damageTypeId) {
        Objects.requireNonNull(damageTypeId, "damageTypeId");
        return KNOWN_MAGIC_DAMAGE_TYPES.contains(damageTypeId)
                ? magical()
                : unknown();
    }

    private static MagicDamageClassification magical() {
        return new MagicDamageClassification(MagicDamageKind.GENERIC_MAGIC, MagicDamageConfidence.CERTAIN);
    }

    private static MagicDamageClassification unknown() {
        return new MagicDamageClassification(MagicDamageKind.UNKNOWN, MagicDamageConfidence.UNKNOWN);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }
}
