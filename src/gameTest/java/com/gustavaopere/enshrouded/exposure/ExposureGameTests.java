package com.gustavaopere.enshrouded.exposure;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ExposureGameTests {
    private static final UUID FIXTURE_CORE_ID = UUID.fromString("846bb382-d998-4980-8f4a-35b6860c2ab1");
    private static final UUID FIXTURE_REGION_ID = UUID.fromString("930bedaf-9846-4fd6-a754-b3b41cfb0cd8");
    private static final UUID CADENCE_PLAYER_ID = UUID.fromString("64995828-ec9d-49c7-9e75-37a3c2c3bcc8");
    private static final BlockPos SHROUD_POS = new BlockPos(2048, 96, 2048);
    private static final BlockPos CLEAR_POS = new BlockPos(2064, 96, 2048);

    private ExposureGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void crossingZoneBoundariesChangesExposureOncePerSampledInterval(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ShroudGridGeometry geometry = ShroudGridGeometry.levelOne();
        ShroudCellPos shroudCell = geometry.cellAt(SHROUD_POS);
        ShroudSavedData data = ShroudSavedData.get(level);
        data.replace(withExposureFixture(data.state(), shroudCell));

        DefaultShroudQuery query = DefaultShroudQuery.levelOne(geometry);
        ShroudSample shroud = query.sample(level, SHROUD_POS, null);
        ShroudSample clear = query.sample(level, CLEAR_POS, null);
        helper.assertTrue(shroud.severity() == ShroudSeverity.SHROUD,
                "Fixture position must resolve through the canonical query as SHROUD");
        helper.assertTrue(clear.severity() == ShroudSeverity.CLEAR,
                "Adjacent clear fixture position must resolve through the canonical query as CLEAR");

        ExposureSamplingCadence cadence = new ExposureSamplingCadence(20);
        ExposureService service = new ExposureService(100, 1, 1, 100, DeadlyExposurePolicy.levelOneBarrier());
        ShroudExposureAttachment state = ShroudExposureAttachment.full(100);

        OptionalInt initialDelta = cadence.elapsedTicks(CADENCE_PLAYER_ID, 100L);
        helper.assertTrue(initialDelta.isPresent() && initialDelta.getAsInt() == 0,
                "First sample must publish a zero-delta baseline");
        state = service.tick(CADENCE_PLAYER_ID, state, shroud, initialDelta.getAsInt()).attachmentState();
        helper.assertTrue(state.remainingTicks() == 100, "Zero-delta baseline must not drain reserve");

        helper.assertTrue(cadence.elapsedTicks(CADENCE_PLAYER_ID, 119L).isEmpty(),
                "Exposure must not process before the 20-tick sample interval");
        OptionalInt shroudDelta = cadence.elapsedTicks(CADENCE_PLAYER_ID, 120L);
        helper.assertTrue(shroudDelta.isPresent() && shroudDelta.getAsInt() == 20,
                "The first completed sample interval must be exactly 20 ticks");
        state = service.tick(CADENCE_PLAYER_ID, state, shroud, shroudDelta.getAsInt()).attachmentState();
        helper.assertTrue(state.remainingTicks() == 80,
                "One SHROUD interval must drain exactly one 20-tick work unit");

        helper.assertTrue(cadence.elapsedTicks(CADENCE_PLAYER_ID, 121L).isEmpty(),
                "Repeated ticks inside the same sample interval must not double-drain");
        OptionalInt clearDelta = cadence.elapsedTicks(CADENCE_PLAYER_ID, 140L);
        helper.assertTrue(clearDelta.isPresent() && clearDelta.getAsInt() == 20,
                "Crossing to CLEAR must still respect the same sampling interval");
        state = service.tick(CADENCE_PLAYER_ID, state, clear, clearDelta.getAsInt()).attachmentState();
        helper.assertTrue(state.remainingTicks() == 100,
                "One CLEAR interval must recover the same bounded reserve without exceeding max");

        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void disconnectSaveReloadPreservesExposureAttachment(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player first = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertTrue(first != null, "GameTest must create the first mock player");

        var attachmentType = ShroudExposureAttachment.PLAYER_EXPOSURE.get();
        ShroudExposureAttachment persisted = new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, 4321);
        first.setData(attachmentType, persisted);
        helper.assertTrue(first.getData(attachmentType).equals(persisted),
                "Initial player must own the persisted exposure attachment");

        var serialized = first.serializeNBT(level.registryAccess());
        Player reloaded = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertTrue(reloaded != null, "GameTest must create the reload target player");
        reloaded.deserializeNBT(level.registryAccess(), serialized);

        helper.assertTrue(reloaded.hasData(attachmentType),
                "Serialized player data must restore the exposure attachment after reconnect/reload");
        ShroudExposureAttachment restored = reloaded.getData(attachmentType);
        helper.assertTrue(restored.equals(persisted),
                "Reconnect/reload must preserve the exact exposure reserve instead of resetting it");

        ExposureService service = new ExposureService(
                ExposureSchema.DEFAULT_MAX_RESERVE_TICKS,
                1,
                1,
                100,
                DeadlyExposurePolicy.levelOneBarrier()
        );
        ShroudSample unsafe = new ShroudSample(0.5F, ShroudSeverity.SHROUD, java.util.Optional.empty(), false);
        ShroudExposureAttachment continued = service.tick(
                reloaded.getUUID(),
                restored,
                unsafe,
                20
        ).attachmentState();
        helper.assertTrue(continued.remainingTicks() == 4301,
                "Unsafe exposure after reload must continue from persisted reserve, not a fresh baseline");

        Player freshRespawn = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertTrue(!freshRespawn.hasData(attachmentType),
                "Fresh player entity must not inherit another entity's pre-death exposure attachment");
        helper.assertTrue(
                freshRespawn.getData(attachmentType).remainingTicks() == ExposureSchema.DEFAULT_MAX_RESERVE_TICKS,
                "Fresh respawn baseline must initialize safely at the configured Level-1 default"
        );

        helper.succeed();
    }

    private static ShroudWorldState withExposureFixture(ShroudWorldState existing, ShroudCellPos shroudCell) {
        LinkedHashMap<UUID, ShroudCoreState> cores = new LinkedHashMap<>(existing.cores());
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>(existing.regions());

        ShroudCoreState core = new ShroudCoreState(
                FIXTURE_CORE_ID,
                SHROUD_POS,
                1,
                CoreLifecycleState.DORMANT,
                64,
                0x4558504F53555245L,
                0L,
                FIXTURE_REGION_ID
        );
        ShroudCellState cell = new ShroudCellState(shroudCell, 0.5D, ShroudSeverity.SHROUD);
        ShroudRegionState region = new ShroudRegionState(
                FIXTURE_REGION_ID,
                FIXTURE_CORE_ID,
                Map.of(shroudCell, cell)
        );
        cores.put(FIXTURE_CORE_ID, core);
        regions.put(FIXTURE_REGION_ID, region);
        return new ShroudWorldState(existing.schemaVersion(), cores, regions);
    }
}
