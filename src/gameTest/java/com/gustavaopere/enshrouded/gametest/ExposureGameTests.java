package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.exposure.DeadlyExposurePolicy;
import com.gustavaopere.enshrouded.exposure.ExposureSamplingCadence;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.exposure.ExposureService;
import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import com.gustavaopere.enshrouded.exposure.ShroudExposureAttachment;
import com.mojang.authlib.GameProfile;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ExposureGameTests {
    private static final int MAX_RESERVE = ExposureSchema.DEFAULT_MAX_RESERVE_TICKS;
    private static final UUID CADENCE_PLAYER_ID = UUID.fromString("f2ab828d-dc84-46f5-9d32-11a6c60762ba");
    private static final UUID PERSISTENCE_PLAYER_ID = UUID.fromString("46c0d33f-d417-47d8-b333-22b31509d0b6");

    private ExposureGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void crossingZoneBoundaryAppliesExposureExactlyOncePerSampleInterval(GameTestHelper helper) {
        ExposureSamplingCadence cadence = new ExposureSamplingCadence(20);
        ExposureService service = new ExposureService(
                MAX_RESERVE,
                1,
                1,
                100,
                DeadlyExposurePolicy.levelOneBarrier()
        );
        ShroudExposureAttachment state = ShroudExposureAttachment.full(MAX_RESERVE);

        OptionalInt initial = cadence.elapsedTicks(CADENCE_PLAYER_ID, 100L);
        helper.assertTrue(initial.isPresent() && initial.getAsInt() == 0,
                "First session observation must emit a zero-delta sample");
        ExposureSnapshot initialSnapshot = service.tick(CADENCE_PLAYER_ID, state, shroud(), initial.getAsInt());
        helper.assertTrue(initialSnapshot.remainingTicks() == MAX_RESERVE,
                "Zero-delta first sample must not drain exposure");

        helper.assertTrue(cadence.elapsedTicks(CADENCE_PLAYER_ID, 119L).isEmpty(),
                "Exposure must not be applied again before the sample interval");

        OptionalInt shroudInterval = cadence.elapsedTicks(CADENCE_PLAYER_ID, 120L);
        helper.assertTrue(shroudInterval.isPresent() && shroudInterval.getAsInt() == 20,
                "Exactly one twenty-tick interval must become authoritative work");
        ExposureSnapshot drained = service.tick(
                CADENCE_PLAYER_ID,
                new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, initialSnapshot.remainingTicks()),
                shroud(),
                shroudInterval.getAsInt()
        );
        helper.assertTrue(drained.remainingTicks() == MAX_RESERVE - 20,
                "Ordinary Shroud must drain exactly once for the sampled interval");

        helper.assertTrue(cadence.elapsedTicks(CADENCE_PLAYER_ID, 121L).isEmpty(),
                "Crossing into clear space must not manufacture an extra same-interval update");

        OptionalInt clearInterval = cadence.elapsedTicks(CADENCE_PLAYER_ID, 140L);
        helper.assertTrue(clearInterval.isPresent() && clearInterval.getAsInt() == 20,
                "The next clear-space update must occur only at the next sample boundary");
        ExposureSnapshot recovered = service.tick(
                CADENCE_PLAYER_ID,
                new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, drained.remainingTicks()),
                ShroudSample.clear(),
                clearInterval.getAsInt()
        );
        helper.assertTrue(recovered.remainingTicks() == MAX_RESERVE,
                "Clear space must recover the same bounded interval without double application");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void disconnectSaveReloadPreservesExposureWhileSessionCadenceResets(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        GameProfile profile = new GameProfile(PERSISTENCE_PLAYER_ID, "ExposurePersistence");
        FakePlayer player = FakePlayerFactory.get(level, profile);
        var attachmentType = ShroudExposureAttachment.PLAYER_EXPOSURE.get();
        ShroudExposureAttachment persisted = new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, 1234);
        player.setData(attachmentType, persisted);

        CompoundTag saved = player.serializeNBT(level.registryAccess());
        player.setData(attachmentType, ShroudExposureAttachment.full(MAX_RESERVE));
        player.deserializeNBT(level.registryAccess(), saved);

        helper.assertTrue(player.getData(attachmentType).equals(persisted),
                "NeoForge entity serialization must preserve the versioned exposure attachment");

        ExposureSamplingCadence cadence = new ExposureSamplingCadence(20);
        OptionalInt beforeDisconnect = cadence.elapsedTicks(PERSISTENCE_PLAYER_ID, 500L);
        helper.assertTrue(beforeDisconnect.isPresent() && beforeDisconnect.getAsInt() == 0,
                "First connected observation must establish the session cadence");
        helper.assertTrue(cadence.elapsedTicks(PERSISTENCE_PLAYER_ID, 520L).orElseThrow() == 20,
                "Connected session must accumulate the configured exposure interval");

        cadence.forget(PERSISTENCE_PLAYER_ID);
        OptionalInt afterReconnect = cadence.elapsedTicks(PERSISTENCE_PLAYER_ID, 900L);
        helper.assertTrue(afterReconnect.isPresent() && afterReconnect.getAsInt() == 0,
                "Disconnect must reset only ephemeral cadence so offline time cannot drain exposure");
        helper.assertTrue(player.getData(attachmentType).remainingTicks() == 1234,
                "Reconnect/session reset must not refill the persisted unsafe reserve");
        helper.succeed();
    }

    private static ShroudSample shroud() {
        return new ShroudSample(0.75F, ShroudSeverity.SHROUD, Optional.empty(), false);
    }
}
