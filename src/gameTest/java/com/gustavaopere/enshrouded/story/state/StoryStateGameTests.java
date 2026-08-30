package com.gustavaopere.enshrouded.story.state;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class StoryStateGameTests {
    private static final String STORY_STATE_BATCH = "storyState";
    private static final ProgressionOwner RELOAD_OWNER = ProgressionOwner.player(
            UUID.fromString("60601001-0000-4000-8000-000000000001"));
    private static final UUID RELOAD_ENCOUNTER = UUID.fromString("60601001-0000-4000-8000-000000000002");
    private static final UUID MISSING_ACTOR = UUID.fromString("60601001-0000-4000-8000-000000000003");

    private StoryStateGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = STORY_STATE_BATCH)
    public static void storyStorageIsServerGlobalAcrossDimensions(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ServerLevel nether = level.getServer().getLevel(Level.NETHER);
        helper.assertTrue(nether != null, "GameTest server must expose the Nether for global Story-state verification");

        StorySavedData overworldData = StorySavedData.get(level);
        StorySavedData netherData = StorySavedData.get(nether);

        helper.assertTrue(overworldData == netherData,
                "Lich story state must be server-global rather than split by dimension");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty", batch = STORY_STATE_BATCH)
    public static void activeEncounterWithoutActorAbortsAcrossRealTwoBootReload(GameTestHelper helper) {
        StorySavedData data = StorySavedData.get(GameTestBootstrap.requireServerLevel(helper));
        var existing = data.state().encounter(RELOAD_ENCOUNTER);

        if (existing.isEmpty()) {
            LichStoryState active = data.state()
                    .createEncounter(RELOAD_OWNER, RELOAD_ENCOUNTER, 1).orElseThrow()
                    .activateEncounter(RELOAD_ENCOUNTER, MISSING_ACTOR).orElseThrow();
            data.replace(active);
            GameTestBootstrap.forceSaveForReload(helper);
            System.out.println("ENSHROUDED_STORY_CREATED");
        } else {
            EncounterRecord encounter = existing.orElseThrow();
            helper.assertTrue(encounter.owner().equals(RELOAD_OWNER),
                    "Reload reconciliation must preserve the immutable encounter owner snapshot");
            helper.assertTrue(encounter.outcome() == EncounterOutcome.ABORTED,
                    "An ACTIVE encounter whose tracked actor is absent after restart must reconcile to ABORTED");
            helper.assertTrue(encounter.entityId().isEmpty(),
                    "ABORTED reconciliation must discard the transient physical actor UUID");
            helper.assertTrue(!encounter.rewardIssued(),
                    "Missing-actor recovery must never manufacture a defeat reward");
            helper.assertTrue(!data.state().manifestation(RELOAD_OWNER).defeatedManifestationIndices().contains(1),
                    "Missing-actor recovery must not mark manifestation 1 defeated");
            System.out.println("ENSHROUDED_STORY_RELOADED");
        }
        helper.succeed();
    }
}
