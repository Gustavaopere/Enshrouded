package com.gustavaopere.enshrouded.exposure;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
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
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FlameWardExposureGameTests {
    private static final ShroudGridGeometry GEOMETRY = ShroudGridGeometry.levelOne();

    private FlameWardExposureGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void altarMasksLogicalShroudAndRemovalRevealsSameThreat(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos altarRelative = new BlockPos(2, 1, 2);
        BlockPos altarPos = helper.absolutePos(altarRelative);
        ShroudSavedData data = ShroudSavedData.get(level);
        ShroudWorldState original = data.state();
        UUID coreId = UUID.randomUUID();
        ShroudWorldState injected = withLogicalCell(original, altarPos, coreId);
        data.replace(injected);

        DefaultShroudQuery query = DefaultShroudQuery.levelOne(GEOMETRY);
        ShroudSample latent = query.sample(level, altarPos, null);
        helper.assertTrue(!latent.sanctuarySuppressed(), "Precondition: logical Shroud must initially be effective");
        helper.assertTrue(latent.severity() != ShroudSeverity.CLEAR, "Precondition: test position must contain logical Shroud");
        helper.assertTrue(latent.sourceId().orElseThrow().equals(coreId), "Precondition: expected test core must own the logical cell");

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

        data.replace(original);
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    @SuppressWarnings("removal")
    public static void playerStandingInLogicalShroudInsideWardRecoversExposure(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos altarRelative = new BlockPos(2, 1, 2);
        BlockPos altarPos = helper.absolutePos(altarRelative);
        BlockPos playerPos = altarPos.offset(1, 0, 0);
        ShroudSavedData data = ShroudSavedData.get(level);
        ShroudWorldState original = data.state();
        data.replace(withLogicalCell(original, playerPos, UUID.randomUUID()));
        helper.setBlock(altarRelative, ModBlocks.FLAME_ALTAR.get());

        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        helper.assertTrue(player != null, "Sanctuary GameTest requires a server-side mock player");
        player.setPos(playerPos.getX() + 0.5D, playerPos.getY(), playerPos.getZ() + 0.5D);
        int maxReserve = com.gustavaopere.enshrouded.config.EnshroudedConfig.exposureMaxReserveTicks();
        int startingReserve = Math.max(1, maxReserve - 100);
        player.setData(
                ShroudExposureAttachment.PLAYER_EXPOSURE.get(),
                new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, startingReserve)
        );

        ExposureSnapshot snapshot = ExposureRuntime.process(player, ExposureRuntime.SAMPLE_INTERVAL_TICKS);

        helper.assertTrue(snapshot.sanctuarySuppressed(), "Exposure runtime must observe the physical altar Sanctuary");
        helper.assertTrue(snapshot.severity() != ShroudSeverity.CLEAR,
                "Sanctuary must retain the latent logical Shroud severity in the authoritative sample");
        helper.assertTrue(snapshot.remainingTicks() > startingReserve,
                "Player standing in logical Shroud inside Sanctuary must recover exposure reserve");

        helper.destroyBlock(altarRelative);
        data.replace(original);
        ExposureRuntime.forget(player);
        helper.succeed();
    }

    private static ShroudWorldState withLogicalCell(ShroudWorldState existing, BlockPos pos, UUID coreId) {
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
        LinkedHashMap<UUID, ShroudCoreState> cores = new LinkedHashMap<>(existing.cores());
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>(existing.regions());
        cores.put(coreId, core);
        regions.put(regionId, region);
        return new ShroudWorldState(existing.schemaVersion(), cores, regions);
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
