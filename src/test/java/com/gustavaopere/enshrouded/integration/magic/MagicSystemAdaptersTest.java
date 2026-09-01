package com.gustavaopere.enshrouded.integration.magic;

import com.gustavaopere.enshrouded.api.combat.MagicDamageClassification;
import com.gustavaopere.enshrouded.api.combat.MagicDamageConfidence;
import com.gustavaopere.enshrouded.api.combat.MagicDamageKind;
import com.gustavaopere.enshrouded.integration.arsnouveau.ArsNouveauMagicAdapter;
import com.gustavaopere.enshrouded.integration.irons.IronsSpellbooksMagicAdapter;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class MagicSystemAdaptersTest {
    private static ResourceLocation id(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    @Test
    void arsNouveauClassifiesOnlyKnownSpellDamageTypes() {
        ArsNouveauMagicAdapter adapter = new ArsNouveauMagicAdapter();
        for (String path : List.of("spell", "frost", "flare", "crush", "windshear")) {
            assertMagical(adapter.classifyDamageType(id("ars_nouveau", path)));
        }

        assertUnknown(adapter.classifyDamageType(id("ars_nouveau", "sourceberry_bush")));
        assertUnknown(adapter.classifyDamageType(id("ars_nouveau", "made_up_physical")));
        assertUnknown(adapter.classifyDamageType(id("minecraft", "player_attack")));
    }

    @Test
    void ironSpellsClassifiesOnlyKnownSchoolDamageTypes() {
        IronsSpellbooksMagicAdapter adapter = new IronsSpellbooksMagicAdapter();
        for (String path : List.of(
                "fire_magic", "ice_magic", "lightning_magic", "holy_magic", "ender_magic",
                "blood_magic", "evocation_magic", "eldritch_magic", "nature_magic")) {
            assertMagical(adapter.classifyDamageType(id("irons_spellbooks", path)));
        }

        for (String path : List.of(
                "heartstop", "blood_cauldron", "dragon_breath_pool", "fire_field", "poison_cloud")) {
            assertUnknown(adapter.classifyDamageType(id("irons_spellbooks", path)));
        }
        assertUnknown(adapter.classifyDamageType(id("irons_spellbooks", "made_up_physical")));
        assertUnknown(adapter.classifyDamageType(id("minecraft", "player_attack")));
    }

    @Test
    void adaptersExposeOnlyExactUpstreamMagicWhitelists() {
        assertEquals(5, ArsNouveauMagicAdapter.KNOWN_MAGIC_DAMAGE_TYPES.size());
        assertEquals(9, IronsSpellbooksMagicAdapter.KNOWN_MAGIC_DAMAGE_TYPES.size());
        assertTrue(ArsNouveauMagicAdapter.KNOWN_MAGIC_DAMAGE_TYPES.stream()
                .allMatch(id -> id.getNamespace().equals("ars_nouveau")));
        assertTrue(IronsSpellbooksMagicAdapter.KNOWN_MAGIC_DAMAGE_TYPES.stream()
                .allMatch(id -> id.getNamespace().equals("irons_spellbooks")));
    }

    private static void assertMagical(MagicDamageClassification classification) {
        assertEquals(MagicDamageKind.GENERIC_MAGIC, classification.kind());
        assertEquals(MagicDamageConfidence.CERTAIN, classification.confidence());
        assertTrue(classification.magical());
    }

    private static void assertUnknown(MagicDamageClassification classification) {
        assertEquals(MagicDamageKind.UNKNOWN, classification.kind());
        assertEquals(MagicDamageConfidence.UNKNOWN, classification.confidence());
        assertFalse(classification.magical());
    }
}
