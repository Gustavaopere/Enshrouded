package com.gustavaopere.enshrouded.story.manifestation;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudQuery;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.Map;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class LichArenaRuleGameTests {
    private static final String BATCH = "firstManifestationArena";

    private LichArenaRuleGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void cleanupRemovesOnlyTemporaryArenaOverlayAndPreservesPreExistingShroud(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ShroudSavedData savedData = ShroudSavedData.get(level);
        ShroudWorldState original = savedData.state();

        UUID coreId = UUID.fromString("60603005-0000-4000-8000-000000000001");
        UUID regionId = UUID.fromString("60603005-0000-4000-8000-000000000002");
        ShroudCoreState core = new ShroudCoreState(
                coreId,
                helper.absolutePos(new BlockPos(12, 1, 12)),
                1,
                CoreLifecycleState.ACTIVE,
                32,
                60603005L,
                0L,
                regionId
        );
        ShroudRegionState region = new ShroudRegionState(regionId, coreId, Map.of());
        ShroudWorldState preExisting = new ShroudWorldState(
                original.schemaVersion(),
                Map.of(coreId, core),
                Map.of(regionId, region)
        );
        savedData.replace(preExisting);

        ShroudQuery baseQuery = (ignoredLevel, ignoredPos, ignoredEntity) -> ShroudSample.clear();
        FirstManifestationDefinition definition = FirstManifestationDefinition.levelOne();
        LichArenaRule arena = new LichArenaRule(baseQuery, definition);
        UUID encounterId = UUID.fromString("60603005-0000-4000-8000-000000000101");
        BlockPos center = helper.absolutePos(new BlockPos(2, 1, 2));

        try {
            helper.assertTrue(arena.activate(level, encounterId, center),
                    "first activation must install one temporary arena overlay");
            ShroudSample inside = arena.sample(level, center, null);
            helper.assertTrue(inside.intensity() >= definition.arenaIntensity(),
                    "active arena must intensify local Shroud without persisting a region");
            helper.assertTrue(inside.severity() == ShroudSeverity.SHROUD,
                    "Level-1 arena overlay must remain ordinary Shroud rather than fabricate Deadly ownership");
            helper.assertTrue(inside.sourceId().orElseThrow().equals(encounterId),
                    "temporary overlay source must be the encounter UUID, not a fake persistent core/region UUID");
            helper.assertTrue(savedData.state().equals(preExisting),
                    "activating the arena must not mutate the canonical persistent Shroud field");

            helper.assertTrue(arena.cleanup(level, encounterId),
                    "cleanup must remove the installed temporary overlay exactly once");
            helper.assertTrue(arena.sample(level, center, null).equals(ShroudSample.clear()),
                    "after cleanup the decorated query must return the unchanged base Shroud sample");
            helper.assertTrue(savedData.state().equals(preExisting),
                    "arena cleanup must preserve the complete pre-existing Shroud field");
            helper.assertTrue(!arena.cleanup(level, encounterId),
                    "duplicate cleanup must be idempotent and report that no overlay remained");

            helper.succeed();
        } finally {
            arena.cleanup(level, encounterId);
            savedData.replace(original);
        }
    }
}
