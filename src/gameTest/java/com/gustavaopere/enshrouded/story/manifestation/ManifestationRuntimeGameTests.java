package com.gustavaopere.enshrouded.story.manifestation;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.exposure.ExposureRuntime;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import com.gustavaopere.enshrouded.story.state.EncounterOutcome;
import com.gustavaopere.enshrouded.story.state.LichStoryState;
import com.gustavaopere.enshrouded.story.state.StorySavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
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
        StorySavedData storySavedData = StorySavedData.get(level);
        LichStoryState storyBefore = storySavedData.state();
        ShroudSavedData shroudSavedData = ShroudSavedData.get(level);
        ShroudWorldState shroudBefore = shroudSavedData.state();
        storySavedData.replace(LichStoryState.empty());
        shroudSavedData.replace(ShroudWorldState.empty());

        UUID playerId = UUID.fromString("60603004-0000-4000-8000-000000000001");
        ProgressionOwner expectedOwner = ProgressionOwner.player(playerId);
        ManifestationEncounterService.ActiveEncounter active = null;
        try {
            active = ManifestationRuntime.service()
                    .start(level, playerId, helper.absolutePos(new BlockPos(1, 1, 1)))
                    .orElseThrow(() -> new AssertionError("runtime encounter must start through the canonical provider director"));
            BlockPos arenaCenter = active.entity().blockPosition();

            var activeArenaSample = ExposureRuntime.shroudQuery().sample(level, arenaCenter, active.entity());
            helper.assertTrue(activeArenaSample.severity() == ShroudSeverity.SHROUD,
                    "runtime start must install the Level-1 arena overlay in the authoritative exposure query");
            helper.assertTrue(activeArenaSample.sourceId().orElseThrow().equals(active.encounterId()),
                    "runtime exposure query must attribute the temporary arena overlay to the stable encounter UUID");

            active.entity().setHealth(1.0F);
            active.entity().hurt(level.damageSources().generic(), 100.0F);

            helper.assertTrue(!active.entity().isAlive(),
                    "fixture must cause a real living death rather than manually calling the encounter service");
            helper.assertTrue(storySavedData.state().encounter(active.encounterId()).orElseThrow().outcome() == EncounterOutcome.DEFEATED,
                    "LivingDeathEvent must route the marked actor to the canonical encounter defeat service");
            helper.assertTrue(storySavedData.state().manifestation(expectedOwner).isDefeated(1),
                    "automatic death routing must advance manifestation 1 for the owner captured at runtime start");
            helper.assertTrue(!storySavedData.state().encounter(active.encounterId()).orElseThrow().rewardIssued(),
                    "automatic 06.03 defeat routing must not issue the 06.04 concrete reward");

            var afterDefeatSample = ExposureRuntime.shroudQuery().sample(level, arenaCenter, null);
            helper.assertTrue(afterDefeatSample.sourceId().filter(active.encounterId()::equals).isEmpty(),
                    "valid defeat must remove only the temporary arena overlay from the authoritative exposure query");
            helper.assertTrue(shroudSavedData.state().equals(ShroudWorldState.empty()),
                    "runtime arena lifecycle must not create persistent Shroud state as a side effect");

            helper.succeed();
        } finally {
            if (active != null && !active.entity().isRemoved()) {
                active.entity().discard();
            }
            storySavedData.replace(storyBefore);
            shroudSavedData.replace(shroudBefore);
        }
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void canceledLivingDeathDoesNotDefeatOrCleanTheEncounter(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        StorySavedData storySavedData = StorySavedData.get(level);
        LichStoryState storyBefore = storySavedData.state();
        ShroudSavedData shroudSavedData = ShroudSavedData.get(level);
        ShroudWorldState shroudBefore = shroudSavedData.state();
        storySavedData.replace(LichStoryState.empty());
        shroudSavedData.replace(ShroudWorldState.empty());

        UUID playerId = UUID.fromString("60603006-0000-4000-8000-000000000001");
        ManifestationEncounterService.ActiveEncounter active = null;
        try {
            active = ManifestationRuntime.service()
                    .start(level, playerId, helper.absolutePos(new BlockPos(1, 1, 1)))
                    .orElseThrow(() -> new AssertionError("runtime encounter must start"));
            BlockPos arenaCenter = active.entity().blockPosition();
            active.entity().setHealth(0.0F);

            LivingDeathEvent canceled = new LivingDeathEvent(active.entity(), level.damageSources().generic());
            canceled.setCanceled(true);
            ManifestationRuntime.onLivingDeath(canceled);

            helper.assertTrue(storySavedData.state().encounter(active.encounterId()).orElseThrow().outcome() == EncounterOutcome.ACTIVE,
                    "a canceled provider death transition must not become a narrative boss defeat");
            helper.assertTrue(ExposureRuntime.shroudQuery().sample(level, arenaCenter, active.entity())
                            .sourceId().filter(active.encounterId()::equals).isPresent(),
                    "canceled death must retain the active encounter arena overlay");

            helper.succeed();
        } finally {
            if (active != null) {
                ManifestationRuntime.service().defeatFromActor(active.entity());
                if (!active.entity().isRemoved()) {
                    active.entity().discard();
                }
            }
            storySavedData.replace(storyBefore);
            shroudSavedData.replace(shroudBefore);
        }
    }
}
