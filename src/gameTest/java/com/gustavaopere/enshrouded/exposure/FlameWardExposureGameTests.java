package com.gustavaopere.enshrouded.exposure;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.query.DefaultShroudQuery;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
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
public final class FlameWardExposureGameTests {
    private static final ShroudGridGeometry GEOMETRY = ShroudGridGeometry.levelOne();
    private static final String WARD_MASK_BATCH = "flameWardMask";
    private static final String WARD_EXPOSURE_BATCH = "flameWardExposure";

    private FlameWardExposureGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = WARD_MASK_BATCH)
    public static void altarMasksLogicalShroudAndRemovalRevealsSameThreat(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos altarRelative = new BlockPos(2, 1, 2);
        BlockPos altarPos = helper.absolutePos(altarRelative);
        ShroudSavedData data = ShroudSavedData.get(level);
        ShroudWorldState original = data.state();
        UUID coreId = UUID.randomUUID();
        ShroudWorldState injected = isolatedLogicalCellState(original.schemaVersion(), altarPos, coreId);
        data.replace(injected);

        try {
            DefaultShroudQuery query = DefaultShroudQuery.levelOne(GEOMETRY);
            ShroudSample latent = query.sample(level, altarPos, null);
            helper.assertTrue(!latent.sanctuarySuppressed(), "Precondition: logical Shroud must initially be effective");
            helper.assertTrue(latent.severity() != ShroudSeverity.CLEAR, "Precondition: test position must contain logical Shroud");
            helper.assertTrue(latent.sourceId().orElseThrow().equals(coreId), "Precondition: expected test core must own the isolated logical cell");

            helper.setBlock(altarRelative, ModBlocks.FLAME_ALTAR.get());
            ShroudSample warded = query.sample(level, altarPos, null);
            helper.assertTrue(warded.sanctuarySuppressed(), "Loaded Flame Altar must suppress effective Shroud inside its ward");
            assertLatentSamplePreserved(helper, latent, warded);

            helper.destroyBlock(altarRelative);
            ShroudSample revealed = query.sample(level, altarPos, null);
            helper.assertTrue(!revealed.sanctuarySuppressed(), "Removing the altar must reveal the still-present logical Shroud");
            assertLatentSamplePreserved(helper, latent, revealed);
            helper.assertTrue(data.state().equals(injected),
                    "Sanctuary activation/removal must not rewrite canonical logical Shroud state");
            helper.succeed();
        } finally {
            if (level.getBlockState(altarPos).is(ModBlocks.FLAME_ALTAR.get())) {
                level.destroyBlock(altarPos, false);
            }
            data.replace(original);
        }
    }

    @GameTest(template = "foundation_empty", batch = WARD_EXPOSURE_BATCH)
    public static void logicalShroudInsideWardRecoversExposureReserve(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos altarRelative = new BlockPos(2, 1, 2);
        BlockPos altarPos = helper.absolutePos(altarRelative);
        BlockPos playerPos = altarPos.offset(1, 0, 0);
        ShroudSavedData data = ShroudSavedData.get(level);
        ShroudWorldState original = data.state();
        ShroudWorldState injected = isolatedLogicalCellState(original.schemaVersion(), playerPos, UUID.randomUUID());
        data.replace(injected);

        try {
            helper.setBlock(altarRelative, ModBlocks.FLAME_ALTAR.get());

            ShroudSample sample = DefaultShroudQuery.levelOne(GEOMETRY).sample(level, playerPos, null);
            helper.assertTrue(sample.sanctuarySuppressed(),
                    "Exposure input must observe the physical Flame Altar Sanctuary");
            helper.assertTrue(sample.severity() != ShroudSeverity.CLEAR,
                    "Sanctuary must retain latent logical Shroud severity in the sample consumed by exposure");

            int maxReserve = EnshroudedConfig.exposureMaxReserveTicks();
            int startingReserve = Math.max(1, maxReserve - 100);
            ExposureService service = new ExposureService(
                    maxReserve,
                    1,
                    1,
                    ExposureRuntime.MAX_ELAPSED_TICKS,
                    DeadlyExposurePolicy.levelOneBarrier()
            );
            ExposureSnapshot snapshot = service.tick(
                    UUID.randomUUID(),
                    new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, startingReserve),
                    sample,
                    ExposureRuntime.SAMPLE_INTERVAL_TICKS
            );

            helper.assertTrue(snapshot.sanctuarySuppressed(),
                    "Exposure reducer must preserve Sanctuary suppression in its authoritative snapshot");
            helper.assertTrue(snapshot.remainingTicks() > startingReserve,
                    "Logical Shroud inside Sanctuary must recover exposure reserve instead of draining it");
            helper.succeed();
        } finally {
            if (level.getBlockState(altarPos).is(ModBlocks.FLAME_ALTAR.get())) {
                level.destroyBlock(altarPos, false);
            }
            data.replace(original);
        }
    }

    private static ShroudWorldState isolatedLogicalCellState(int schemaVersion, BlockPos pos, UUID coreId) {
        UUID regionId = UUID.randomUUID();
        ShroudCellPos cellPos = GEOMETRY.cellAt(pos);
        ShroudCoreState core = new ShroudCoreState(
                coreId,
                pos.immutable(),
                1,
                CoreLifecycleState.DORMANT,
                128,
                0x5A4C7A11L,
                0L,
                regionId
        );
        ShroudCellState cell = new ShroudCellState(cellPos, 0.60D, ShroudSeverity.SHROUD);
        ShroudRegionState region = new ShroudRegionState(regionId, coreId, Map.of(cellPos, cell));
        return new ShroudWorldState(schemaVersion, Map.of(coreId, core), Map.of(regionId, region));
    }

    private static void assertLatentSamplePreserved(GameTestHelper helper, ShroudSample expected, ShroudSample actual) {
        helper.assertTrue(Float.compare(expected.intensity(), actual.intensity()) == 0,
                "Sanctuary must not change canonical logical intensity");
        helper.assertTrue(expected.severity() == actual.severity(),
                "Sanctuary must not change canonical logical severity");
        helper.assertTrue(expected.sourceId().equals(actual.sourceId()),
                "Sanctuary must not change canonical source core provenance");
    }
}
