package com.gustavaopere.enshrouded.exposure.deadly;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.progression.FlamePassageQuery;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.exposure.DeadlyExposurePolicy;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.exposure.ExposureService;
import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import com.gustavaopere.enshrouded.exposure.ShroudExposureAttachment;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.Optional;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DeadlyShroudGameTests {
    private static final int MAX_RESERVE = 1_000;
    private static final int EMERGENCY_WINDOW = 100;
    private static final int RAPID_DRAIN = 20;
    private static final PassageRequirement LEVEL_TWO = new PassageRequirement(2);
    private static final ShroudSample DEADLY = new ShroudSample(
            1.0F,
            ShroudSeverity.DEADLY,
            Optional.empty(),
            false
    );

    private DeadlyShroudGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void levelOnePlayerInDeadlyShroudReachesFatalReserveRapidly(GameTestHelper helper) {
        ExposureService service = service(FlamePassageQuery.levelOneFallback());
        ExposureSnapshot snapshot = service.tick(
                UUID.randomUUID(),
                state(MAX_RESERVE),
                DEADLY,
                5
        );

        helper.assertTrue(snapshot.deadlyBarrierActive(),
                "standalone Level 1 must keep the Deadly passage barrier active");
        helper.assertTrue(snapshot.remainingTicks() == 0,
                "five ticks of configured rapid drain must exhaust the clamped emergency reserve");
        helper.assertTrue(snapshot.madnessStage().fatal(),
                "zero reserve produced by the Deadly barrier must feed the authoritative fatal Madness outcome");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void fakePassageLevelTwoPermitsDeadlyShroud(GameTestHelper helper) {
        ExposureService service = service(owner -> 2);
        ExposureSnapshot snapshot = service.tick(
                UUID.randomUUID(),
                state(600),
                DEADLY,
                5
        );

        helper.assertTrue(!snapshot.deadlyBarrierActive(),
                "passage level 2 must satisfy the configured Level-2 requirement");
        helper.assertTrue(snapshot.remainingTicks() == 595,
                "permitted Deadly passage must use ordinary reserve drain rather than emergency collapse");
        helper.assertTrue(snapshot.severity() == ShroudSeverity.DEADLY,
                "permitting traversal must not rewrite canonical DEADLY cell severity");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void edgeDancingDoesNotResetEmergencyWindow(GameTestHelper helper) {
        ExposureService service = service(FlamePassageQuery.levelOneFallback());
        UUID playerId = UUID.randomUUID();

        ExposureSnapshot firstEntry = service.tick(playerId, state(MAX_RESERVE), DEADLY, 1);
        ExposureSnapshot oneTickOutside = service.tick(
                playerId,
                firstEntry.attachmentState(),
                ShroudSample.clear(),
                1
        );
        ExposureSnapshot reentry = service.tick(
                playerId,
                oneTickOutside.attachmentState(),
                DEADLY,
                1
        );

        helper.assertTrue(firstEntry.remainingTicks() == 80,
                "first underleveled entry must clamp and rapidly drain the emergency reserve");
        helper.assertTrue(oneTickOutside.remainingTicks() == 81,
                "one safe tick may recover only the normal bounded recovery amount");
        helper.assertTrue(reentry.remainingTicks() == 61,
                "re-entry must continue from the actual reserve instead of resetting the emergency window");
        helper.assertTrue(reentry.remainingTicks() < firstEntry.remainingTicks(),
                "rapid Deadly drain must dominate short boundary dancing");
        helper.succeed();
    }

    private static ExposureService service(FlamePassageQuery passageQuery) {
        DeadlyExposurePolicy policy = new FlameGatedDeadlyExposurePolicy(
                ProgressionOwnerResolver.standalone(),
                passageQuery,
                LEVEL_TWO,
                EMERGENCY_WINDOW,
                RAPID_DRAIN
        );
        return new ExposureService(MAX_RESERVE, 1, 1, 100, policy);
    }

    private static ShroudExposureAttachment state(int remainingTicks) {
        return new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, remainingTicks);
    }
}
