package com.gustavaopere.enshrouded.api;

import com.gustavaopere.enshrouded.api.combat.MagicDamageClassification;
import com.gustavaopere.enshrouded.api.combat.MagicDamageConfidence;
import com.gustavaopere.enshrouded.api.combat.MagicDamageKind;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.api.story.EncounterContext;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class DomainContractsTest {
    @Test
    void shroudSeverityUsesStableIds() {
        assertEquals("clear", ShroudSeverity.CLEAR.id());
        assertEquals("shroud", ShroudSeverity.SHROUD.id());
        assertEquals("deadly", ShroudSeverity.DEADLY.id());
        assertEquals(Optional.of(ShroudSeverity.DEADLY), ShroudSeverity.fromId("deadly"));
        assertEquals(Optional.empty(), ShroudSeverity.fromId("future-tier"));
        assertEquals(Optional.empty(), ShroudSeverity.fromId(null));
    }

    @Test
    void progressionOwnerRoundTripsWithoutExternalTeamTypes() {
        UUID player = UUID.fromString("b23f4057-68c3-4a2f-839c-04998bd4ddda");
        ProgressionOwner owner = ProgressionOwner.player(player);
        assertEquals("player:b23f4057-68c3-4a2f-839c-04998bd4ddda", owner.stableKey());
        assertEquals(owner, ProgressionOwner.parse(owner.stableKey()).orElseThrow());

        ProgressionOwner team = ProgressionOwner.team("ftb:builders");
        assertEquals("team:ftb:builders", team.stableKey());
        assertEquals(team, ProgressionOwner.parse(team.stableKey()).orElseThrow());

        ProgressionOwner world = ProgressionOwner.world("minecraft:overworld");
        assertEquals("world:minecraft:overworld", world.stableKey());
        assertEquals(world, ProgressionOwner.parse(world.stableKey()).orElseThrow());

        assertEquals(Optional.empty(), ProgressionOwner.parse("unknown:value"));
        assertEquals(Optional.empty(), ProgressionOwner.parse("player:not-a-uuid"));
        assertEquals(Optional.empty(), ProgressionOwner.parse("team:"));
        assertEquals(Optional.empty(), ProgressionOwner.parse(null));
    }

    @Test
    void progressionOwnerRejectsInvalidConstruction() {
        assertThrows(IllegalArgumentException.class, () -> ProgressionOwner.team("  "));
        assertThrows(IllegalArgumentException.class, () -> ProgressionOwner.world(""));
        assertThrows(IllegalArgumentException.class,
                () -> new ProgressionOwner(ProgressionOwner.Kind.PLAYER, "not-a-uuid"));
    }

    @Test
    void progressionOwnerUsesOneCanonicalPlayerUuidRepresentation() {
        UUID player = UUID.fromString("b23f4057-68c3-4a2f-839c-04998bd4ddda");
        ProgressionOwner uppercase = new ProgressionOwner(
                ProgressionOwner.Kind.PLAYER,
                "B23F4057-68C3-4A2F-839C-04998BD4DDDA");

        assertEquals(player.toString(), uppercase.id());
        assertEquals("player:" + player, uppercase.stableKey());
        assertThrows(IllegalArgumentException.class,
                () -> new ProgressionOwner(ProgressionOwner.Kind.PLAYER, "1-1-1-1-1"));
        assertEquals(Optional.empty(), ProgressionOwner.parse("player:1-1-1-1-1"));
    }

    @Test
    void shroudSampleRejectsImpossibleIntensity() {
        UUID core = UUID.fromString("6745ac8a-04b4-45dd-8d56-7d1ec73c3f18");
        ShroudSample sample = new ShroudSample(0.75f, ShroudSeverity.SHROUD, Optional.of(core), false);
        assertEquals(0.75f, sample.intensity());
        assertThrows(IllegalArgumentException.class,
                () -> new ShroudSample(-0.01f, ShroudSeverity.SHROUD, Optional.empty(), false));
        assertThrows(IllegalArgumentException.class,
                () -> new ShroudSample(1.01f, ShroudSeverity.DEADLY, Optional.empty(), false));
        assertThrows(IllegalArgumentException.class,
                () -> new ShroudSample(Float.NaN, ShroudSeverity.SHROUD, Optional.empty(), false));
        assertThrows(IllegalArgumentException.class,
                () -> new ShroudSample(Float.POSITIVE_INFINITY, ShroudSeverity.DEADLY, Optional.empty(), false));
    }

    @Test
    void magicClassificationKeepsConfidenceExplicit() {
        MagicDamageClassification classification = new MagicDamageClassification(
                MagicDamageKind.NECROTIC,
                MagicDamageConfidence.CERTAIN);
        assertTrue(classification.magical());
        assertEquals(MagicDamageKind.NECROTIC, classification.kind());

        MagicDamageClassification unknown = new MagicDamageClassification(
                MagicDamageKind.UNKNOWN,
                MagicDamageConfidence.UNKNOWN);
        assertFalse(unknown.magical(), "Unknown damage must fail safe and not gain magic resistance treatment");
    }

    @Test
    void encounterContextRejectsNonPositiveManifestationLevels() {
        EncounterContext context = new EncounterContext(
                UUID.fromString("b4094537-860e-4e6d-ab11-d88942dbaae1"),
                1,
                42L);
        assertEquals(1, context.manifestationLevel());
        assertThrows(IllegalArgumentException.class,
                () -> new EncounterContext(UUID.randomUUID(), 0, 42L));
    }

    @Test
    void encounterContextSnapshotsMutableOrigin() {
        BlockPos.MutableBlockPos mutableOrigin = new BlockPos.MutableBlockPos(4, 5, 6);
        EncounterContext context = new EncounterContext(UUID.randomUUID(), mutableOrigin, 1, 42L);

        mutableOrigin.set(40, 50, 60);

        assertEquals(new BlockPos(4, 5, 6), context.origin());
        assertNotSame(mutableOrigin, context.origin(), "Encounter origin must not retain a mutable BlockPos reference");
    }
}
