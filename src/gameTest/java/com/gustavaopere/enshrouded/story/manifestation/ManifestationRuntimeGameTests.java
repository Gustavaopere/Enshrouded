package com.gustavaopere.enshrouded.story.manifestation;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import com.gustavaopere.enshrouded.story.state.EncounterOutcome;
import com.gustavaopere.enshrouded.story.state.LichStoryState;
import com.gustavaopere.enshrouded.story.state.StorySavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ManifestationRuntimeGameTests {
    private static final String BATCH = "manifestationRuntime";

    private ManifestationRuntimeGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void realLivingDeathEventDefeatsOnlyTheRuntimeStartedMarkedActor(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        StorySavedData savedData = StorySavedData.get(level);
        LichStoryState before = savedData.state();
        savedData.replace(LichStoryState.empty());

        UUID playerId = UUID.fromString("60603004-0000-4000-8000-000000000001");
        ProgressionOwner expectedOwner = ProgressionOwner.player(playerId);
        ManifestationEncounterService.ActiveEncounter active = null;
        try {
            active = ManifestationRuntime.service()
                    .start(level, playerId, helper.absolutePos(new BlockPos(1, 1, 1)))
                    .orElseThrow(() -> new AssertionError("runtime encounter must start through the canonical provider director"));

            active.entity().setHealth(1.0F);
            active.entity().hurt(level.damageSources().generic(), 100.0F);

            helper.assertTrue(!active.entity().isAlive(),
                    "fixture must cause a real living death rather than manually calling the encounter service");
            helper.assertTrue(savedData.state().encounter(active.encounterId()).orElseThrow().outcome() == EncounterOutcome.DEFEATED,
                    "LivingDeathEvent must route the marked actor to the canonical encounter defeat service");
            helper.assertTrue(savedData.state().manifestation(expectedOwner).isDefeated(1),
                    "automatic death routing must advance manifestation 1 for the owner captured at runtime start");
            helper.assertTrue(!savedData.state().encounter(active.encounterId()).orElseThrow().rewardIssued(),
                    "automatic 06.03 defeat routing must not issue the 06.04 concrete reward");

            helper.succeed();
        } finally {
            if (active != null && !active.entity().isRemoved()) {
                active.entity().discard();
            }
            savedData.replace(before);
        }
    }
}
