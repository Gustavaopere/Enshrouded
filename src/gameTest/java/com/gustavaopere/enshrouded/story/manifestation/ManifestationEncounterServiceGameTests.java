package com.gustavaopere.enshrouded.story.manifestation;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import com.gustavaopere.enshrouded.story.boss.LichBossRuntime;
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
}
