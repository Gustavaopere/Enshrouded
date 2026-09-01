package com.gustavaopere.enshrouded.integration.ironspells;

import com.gustavaopere.enshrouded.api.combat.MagicDamageClassification;
import com.gustavaopere.enshrouded.api.combat.MagicDamageClassifier;
import com.gustavaopere.enshrouded.api.combat.MagicDamageConfidence;
import com.gustavaopere.enshrouded.api.combat.MagicDamageKind;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;

import java.util.Objects;
import java.util.Set;

/**
 * Optional Iron's Spells 'n Spellbooks classification evidence without an Iron's class dependency.
 *
 * <p>Iron's 1.21.1 publishes the nine school damage types below into NeoForge's {@code is_magic}
 * tag. Entity/effect damage such as heartstop and blood cauldron is intentionally excluded. This
 * adapter only enriches Enshrouded's one canonical classification decision.</p>
 */
public final class IronSpellsMagicAdapter implements MagicDamageClassifier {
    private static final String NAMESPACE = "irons_spellbooks";

    public static final Set<ResourceLocation> KNOWN_MAGIC_DAMAGE_TYPES = Set.of(
            id("fire_magic"),
            id("ice_magic"),
            id("lightning_magic"),
            id("holy_magic"),
            id("ender_magic"),
            id("blood_magic"),
            id("evocation_magic"),
            id("eldritch_magic"),
            id("nature_magic")
    );

    @Override
    public MagicDamageClassification classify(DamageSource source) {
        Objects.requireNonNull(source, "source");
        return source.typeHolder().unwrapKey()
                .map(key -> classifyDamageType(key.location()))
                .orElseGet(IronSpellsMagicAdapter::unknown);
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
