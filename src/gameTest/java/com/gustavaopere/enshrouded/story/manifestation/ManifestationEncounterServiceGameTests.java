package com.gustavaopere.enshrouded.story.manifestation;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import com.gustavaopere.enshrouded.story.boss.LichBossRuntime;
import com.gustavaopere.enshrouded.story.boss.ManifestationDirector;
import com.gustavaopere.enshrouded.story.state.EncounterOutcome;
import com.gustavaopere.enshrouded.story.state.LichStoryState;
import com.gustavaopere.enshrouded.story.state.StorySavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ManifestationEncounterServiceGameTests {
    private static final String BATCH = "firstManifestationEncounter";

    private ManifestationEncounterServiceGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void startsOneEncounterForResolvedOwnerAndRejectsConcurrentDuplicate(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        StorySavedData savedData = StorySavedData.get(level);
        LichStoryState before = savedData.state();
        savedData.replace(LichStoryState.empty());

        UUID playerId = UUID.fromString("60603001-0000-4000-8000-000000000001");
        ProgressionOwner owner = ProgressionOwner.player(
                UUID.fromString("60603001-0000-4000-8000-000000000101")
        );
        ProgressionOwnerResolver resolver = ignored -> owner;
        ManifestationEncounterService service = new ManifestationEncounterService(
                resolver,
                LichBossRuntime.director()
        );

        ManifestationEncounterService.ActiveEncounter active = null;
        try {
            active = service.start(level, playerId, helper.absolutePos(new BlockPos(1, 1, 1)))
                    .orElseThrow(() -> new AssertionError("first explicit encounter start must succeed"));

            helper.assertTrue(active.owner().equals(owner),
                    "encounter must retain the ProgressionOwner resolved at start");
            helper.assertTrue(savedData.state().encounter(active.encounterId()).orElseThrow().owner().equals(owner),
                    "persisted encounter must use the exact same owner snapshot");
            helper.assertTrue(savedData.state().encounter(active.encounterId()).orElseThrow().outcome() == EncounterOutcome.ACTIVE,
                    "successful encounter start must atomically reach ACTIVE with a physical actor");
            helper.assertTrue(ManifestationDirector.encounterId(active.entity()).orElseThrow().equals(active.encounterId()),
                    "accepted actor must retain the stable encounter UUID marker");
            helper.assertTrue(ManifestationDirector.manifestationId(active.entity()).orElseThrow()
                            == ManifestationEncounterService.FIRST_MANIFESTATION_INDEX,
                    "accepted actor must retain the manifestation identity independently from its provider/entity type");

            helper.assertTrue(service.start(level, playerId, helper.absolutePos(new BlockPos(3, 1, 3))).isEmpty(),
                    "a concurrent second encounter for the same resolved owner must be rejected");
            helper.assertTrue(savedData.state().encounters().values().stream()
                            .filter(record -> record.owner().equals(owner) && record.isOpen())
                            .count() == 1L,
                    "duplicate start must not create a second open encounter record");

            helper.succeed();
        } finally {
            if (active != null) {
                active.entity().discard();
            }
            savedData.replace(before);
        }
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void unrelatedUnmarkedLivingDeathCannotDefeatTheActiveEncounter(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        StorySavedData savedData = StorySavedData.get(level);
        LichStoryState before = savedData.state();
        savedData.replace(LichStoryState.empty());

        UUID playerId = UUID.fromString("60603002-0000-4000-8000-000000000001");
        ProgressionOwner owner = ProgressionOwner.player(
                UUID.fromString("60603002-0000-4000-8000-000000000101")
        );
        ManifestationEncounterService service = new ManifestationEncounterService(
                ignored -> owner,
                LichBossRuntime.director()
        );

        ManifestationEncounterService.ActiveEncounter active = null;
        Zombie unrelated = null;
        try {
            active = service.start(level, playerId, helper.absolutePos(new BlockPos(1, 1, 1)))
                    .orElseThrow(() -> new AssertionError("fixture encounter must start"));
            unrelated = EntityType.ZOMBIE.create(level);
            if (unrelated == null) {
                throw new AssertionError("vanilla zombie fixture must be creatable");
            }
            unrelated.moveTo(helper.absolutePos(new BlockPos(4, 1, 4)).getCenter());
            helper.assertTrue(level.addFreshEntity(unrelated), "unrelated zombie fixture must enter the level");
            unrelated.setHealth(0.0F);

            helper.assertTrue(service.defeatFromActor(unrelated).isEmpty(),
                    "an unmarked living entity death must not be accepted as the story boss defeat");
            helper.assertTrue(savedData.state().encounter(active.encounterId()).orElseThrow().outcome() == EncounterOutcome.ACTIVE,
                    "unrelated death must leave the marked encounter ACTIVE");
            helper.assertTrue(!savedData.state().manifestation(owner).isDefeated(1),
                    "unrelated death must not advance manifestation story progress");

            helper.succeed();
        } finally {
            if (unrelated != null && !unrelated.isRemoved()) {
                unrelated.discard();
            }
            if (active != null) {
                active.entity().discard();
            }
            savedData.replace(before);
        }
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void markedActorDefeatUsesStoredOwnerAndTransitionsExactlyOnce(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        StorySavedData savedData = StorySavedData.get(level);
        LichStoryState before = savedData.state();
        savedData.replace(LichStoryState.empty());

        UUID playerId = UUID.fromString("60603003-0000-4000-8000-000000000001");
        ProgressionOwner ownerAtStart = ProgressionOwner.player(
                UUID.fromString("60603003-0000-4000-8000-000000000101")
        );
        ProgressionOwner laterOwner = ProgressionOwner.player(
                UUID.fromString("60603003-0000-4000-8000-000000000202")
        );
        AtomicReference<ProgressionOwner> currentOwner = new AtomicReference<>(ownerAtStart);
        AtomicInteger resolveCalls = new AtomicInteger();
        ProgressionOwnerResolver mutableResolver = ignored -> {
            resolveCalls.incrementAndGet();
            return currentOwner.get();
        };
        ManifestationEncounterService service = new ManifestationEncounterService(
                mutableResolver,
                LichBossRuntime.director()
        );

        ManifestationEncounterService.ActiveEncounter active = null;
        try {
            active = service.start(level, playerId, helper.absolutePos(new BlockPos(1, 1, 1)))
                    .orElseThrow(() -> new AssertionError("fixture encounter must start"));
            currentOwner.set(laterOwner);
            active.entity().setHealth(0.0F);

            ManifestationEncounterService.DefeatResult defeated = service.defeatFromActor(active.entity())
                    .orElseThrow(() -> new AssertionError("the marked physical actor death must defeat its encounter"));

            helper.assertTrue(defeated.encounterId().equals(active.encounterId()),
                    "defeat result must retain the stable encounter UUID");
            helper.assertTrue(defeated.owner().equals(ownerAtStart),
                    "defeat authority must come from the persisted encounter owner snapshot, not a later resolver result");
            helper.assertTrue(resolveCalls.get() == 1,
                    "owner resolver must be called exactly once at encounter start and never again at defeat");
            helper.assertTrue(savedData.state().encounter(active.encounterId()).orElseThrow().outcome() == EncounterOutcome.DEFEATED,
                    "valid marked actor death must transition the encounter to DEFEATED");
            helper.assertTrue(savedData.state().manifestation(ownerAtStart).isDefeated(1),
                    "valid defeat must advance manifestation 1 for the stored owner");
            helper.assertTrue(!savedData.state().manifestation(laterOwner).isDefeated(1),
                    "changing the resolver after start must not transfer story progress to another owner");
            helper.assertTrue(!savedData.state().encounter(active.encounterId()).orElseThrow().rewardIssued(),
                    "06.03 defeat must not pre-issue the concrete 06.04 Lich Skull reward");
            helper.assertTrue(service.defeatFromActor(active.entity()).isEmpty(),
                    "duplicate death callbacks must not defeat the same encounter twice");

            helper.succeed();
        } finally {
            if (active != null && !active.entity().isRemoved()) {
                active.entity().discard();
            }
            savedData.replace(before);
        }
    }
}
