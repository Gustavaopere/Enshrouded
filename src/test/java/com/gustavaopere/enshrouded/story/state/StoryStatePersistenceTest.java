package com.gustavaopere.enshrouded.story.state;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class StoryStatePersistenceTest {
    private static final ProgressionOwner PLAYER = ProgressionOwner.player(
            UUID.fromString("11111111-1111-1111-1111-111111111111"));
    private static final ProgressionOwner TEAM = ProgressionOwner.team("ftb:story_team");
    private static final ProgressionOwner WORLD = ProgressionOwner.world("minecraft:overworld");

    @Test
    void roundTripsVersionedStoryStateForPlayerTeamAndWorldOwners() {
        UUID playerEncounter = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID teamEncounter = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        UUID worldEncounter = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        UUID activeEntity = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

        LichStoryState state = LichStoryState.empty()
                .createEncounter(PLAYER, playerEncounter, 1).orElseThrow()
                .activateEncounter(playerEncounter, activeEntity).orElseThrow()
                .createEncounter(TEAM, teamEncounter, 1).orElseThrow()
                .activateEncounter(teamEncounter, UUID.randomUUID()).orElseThrow()
                .defeatEncounter(teamEncounter).orElseThrow()
                .createEncounter(WORLD, worldEncounter, 1).orElseThrow();

        CompoundTag encoded = StoryCodec.encode(state);
        LichStoryState decoded = StoryCodec.decode(encoded);

        assertEquals(StorySchema.CURRENT_VERSION, encoded.getInt("schema_version"));
        assertEquals(state, decoded);
        assertEquals(encoded, StoryCodec.encode(decoded));
        assertEquals(Set.of(), decoded.manifestation(PLAYER).defeatedManifestationIndices());
        assertEquals(Set.of(1), decoded.manifestation(TEAM).defeatedManifestationIndices());
        assertEquals(EncounterOutcome.ACTIVE, decoded.encounter(playerEncounter).orElseThrow().outcome());
        assertEquals(activeEntity, decoded.encounter(playerEncounter).orElseThrow().entityId().orElseThrow());
        assertEquals(EncounterOutcome.AVAILABLE, decoded.encounter(worldEncounter).orElseThrow().outcome());
    }

    @Test
    void futureOrPreVersionedSchemaFailsClosedInsteadOfResettingStory() {
        CompoundTag future = StoryCodec.encode(LichStoryState.empty());
        future.putInt("schema_version", StorySchema.CURRENT_VERSION + 1);
        UnsupportedStorySchemaException futureFailure = assertThrows(
                UnsupportedStorySchemaException.class,
                () -> StoryCodec.decode(future)
        );
        assertEquals(StorySchema.CURRENT_VERSION + 1, futureFailure.schemaVersion());

        CompoundTag invalid = StoryCodec.encode(LichStoryState.empty());
        invalid.putInt("schema_version", 0);
        assertThrows(UnsupportedStorySchemaException.class, () -> StoryCodec.decode(invalid));
    }

    @Test
    void legalEncounterTransitionsAreOneWayAndRewardIsExactlyOnce() {
        UUID encounterId = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
        UUID entityId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

        LichStoryState available = LichStoryState.empty()
                .createEncounter(PLAYER, encounterId, 1).orElseThrow();
        assertEquals(EncounterOutcome.AVAILABLE, available.encounter(encounterId).orElseThrow().outcome());
        assertTrue(available.createEncounter(PLAYER, UUID.randomUUID(), 1).isEmpty(),
                "One owner must not receive a concurrent open encounter");
        assertTrue(available.defeatEncounter(encounterId).isEmpty(),
                "AVAILABLE must not skip directly to DEFEATED");

        LichStoryState active = available.activateEncounter(encounterId, entityId).orElseThrow();
        assertEquals(EncounterOutcome.ACTIVE, active.encounter(encounterId).orElseThrow().outcome());
        assertTrue(active.activateEncounter(encounterId, UUID.randomUUID()).isEmpty(),
                "ACTIVE must not be activated twice");
        assertTrue(active.issueReward(encounterId).isEmpty(),
                "ACTIVE encounters are not reward eligible");

        LichStoryState defeated = active.defeatEncounter(encounterId).orElseThrow();
        EncounterRecord terminal = defeated.encounter(encounterId).orElseThrow();
        assertEquals(EncounterOutcome.DEFEATED, terminal.outcome());
        assertTrue(terminal.entityId().isEmpty(),
                "Physical entity UUID must remain transient encounter linkage, not terminal Lich identity");
        assertEquals(Set.of(1), defeated.manifestation(PLAYER).defeatedManifestationIndices());
        assertTrue(defeated.defeatEncounter(encounterId).isEmpty(),
                "Duplicate defeat must be idempotently rejected");

        LichStoryState rewarded = defeated.issueReward(encounterId).orElseThrow();
        assertTrue(rewarded.encounter(encounterId).orElseThrow().rewardIssued());
        assertTrue(rewarded.issueReward(encounterId).isEmpty(),
                "A valid encounter reward can be issued exactly once");
    }

    @Test
    void abortedEncounterNeverBecomesDefeatedOrRewardEligible() {
        UUID encounterId = UUID.randomUUID();
        LichStoryState active = LichStoryState.empty()
                .createEncounter(TEAM, encounterId, 1).orElseThrow()
                .activateEncounter(encounterId, UUID.randomUUID()).orElseThrow();

        LichStoryState aborted = active.abortEncounter(encounterId).orElseThrow();

        assertEquals(EncounterOutcome.ABORTED, aborted.encounter(encounterId).orElseThrow().outcome());
        assertTrue(aborted.encounter(encounterId).orElseThrow().entityId().isEmpty());
        assertFalse(aborted.manifestation(TEAM).defeatedManifestationIndices().contains(1));
        assertTrue(aborted.defeatEncounter(encounterId).isEmpty());
        assertTrue(aborted.issueReward(encounterId).isEmpty());
    }

    @Test
    void activeEncounterWithMissingActorReconcilesToAbortedWithoutChangingOwner() {
        UUID encounterId = UUID.randomUUID();
        UUID missingActor = UUID.randomUUID();
        LichStoryState active = LichStoryState.empty()
                .createEncounter(WORLD, encounterId, 1).orElseThrow()
                .activateEncounter(encounterId, missingActor).orElseThrow();

        LichStoryState reconciled = active.reconcileActiveEncounters(entityId -> false).orElseThrow();
        EncounterRecord encounter = reconciled.encounter(encounterId).orElseThrow();

        assertEquals(WORLD, encounter.owner());
        assertEquals(EncounterOutcome.ABORTED, encounter.outcome());
        assertFalse(encounter.rewardIssued());
        assertTrue(encounter.entityId().isEmpty());
        assertTrue(reconciled.reconcileActiveEncounters(entityId -> false).isEmpty(),
                "Reconciliation must be idempotent after ACTIVE becomes ABORTED");
    }
}
