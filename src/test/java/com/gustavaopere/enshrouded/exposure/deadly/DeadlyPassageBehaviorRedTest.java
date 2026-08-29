package com.gustavaopere.enshrouded.exposure.deadly;

import com.gustavaopere.enshrouded.api.progression.FlamePassageQuery;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.exposure.DeadlyExposurePolicy;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.exposure.ExposureService;
import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import com.gustavaopere.enshrouded.exposure.ShroudExposureAttachment;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeadlyPassageBehaviorRedTest {
    private static final int MAX_RESERVE = 1_000;
    private static final int EMERGENCY_WINDOW = 100;
    private static final int RAPID_DRAIN = 20;
    private static final PassageRequirement LEVEL_TWO = new PassageRequirement(2);

    @Test
    void passageRequirementUsesRequiredLevelComparison() {
        assertFalse(LEVEL_TWO.isMetBy(1));
        assertTrue(LEVEL_TWO.isMetBy(2));
        assertTrue(LEVEL_TWO.isMetBy(3));
    }

    @Test
    void levelOneFallbackRemainsUnderleveledAndCollapsesReserve() {
        DeadlyExposurePolicy policy = new FlameGatedDeadlyExposurePolicy(
                ProgressionOwnerResolver.standalone(),
                FlamePassageQuery.levelOneFallback(),
                LEVEL_TWO,
                EMERGENCY_WINDOW,
                RAPID_DRAIN
        );

        DeadlyExposurePolicy.Decision decision = policy.evaluate(
                UUID.randomUUID(),
                state(MAX_RESERVE),
                1,
                MAX_RESERVE
        );

        assertTrue(decision.barrierActive());
        assertEquals(80, decision.remainingTicks());
    }

    @Test
    void passageLevelTwoPermitsDeadlyZoneWithoutChangingCellData() {
        DeadlyExposurePolicy policy = new FlameGatedDeadlyExposurePolicy(
                ProgressionOwnerResolver.standalone(),
                owner -> 2,
                LEVEL_TWO,
                EMERGENCY_WINDOW,
                RAPID_DRAIN
        );

        DeadlyExposurePolicy.Decision decision = policy.evaluate(
                UUID.randomUUID(),
                state(600),
                5,
                MAX_RESERVE
        );

        assertFalse(decision.barrierActive(),
                "meeting the passage requirement must remove the Deadly progression barrier");
        assertEquals(595, decision.remainingTicks(),
                "permitted Deadly exposure must continue the ordinary one-tick-per-tick reserve drain");
    }

    @Test
    void resolverOrPassageLookupFailureFailsClosed() {
        DeadlyExposurePolicy resolverFailure = new FlameGatedDeadlyExposurePolicy(
                playerId -> { throw new IllegalStateException("resolver unavailable"); },
                owner -> 99,
                LEVEL_TWO,
                EMERGENCY_WINDOW,
                RAPID_DRAIN
        );
        DeadlyExposurePolicy passageFailure = new FlameGatedDeadlyExposurePolicy(
                ProgressionOwnerResolver.standalone(),
                owner -> { throw new IllegalStateException("passage unavailable"); },
                LEVEL_TWO,
                EMERGENCY_WINDOW,
                RAPID_DRAIN
        );

        DeadlyExposurePolicy.Decision resolverDecision = resolverFailure.evaluate(
                UUID.randomUUID(), state(MAX_RESERVE), 1, MAX_RESERVE);
        DeadlyExposurePolicy.Decision passageDecision = passageFailure.evaluate(
                UUID.randomUUID(), state(MAX_RESERVE), 1, MAX_RESERVE);

        assertTrue(resolverDecision.barrierActive());
        assertEquals(80, resolverDecision.remainingTicks());
        assertTrue(passageDecision.barrierActive());
        assertEquals(80, passageDecision.remainingTicks());
    }

    @Test
    void edgeDancingCannotResetEmergencyWindow() {
        DeadlyExposurePolicy policy = new FlameGatedDeadlyExposurePolicy(
                ProgressionOwnerResolver.standalone(),
                FlamePassageQuery.levelOneFallback(),
                LEVEL_TWO,
                EMERGENCY_WINDOW,
                RAPID_DRAIN
        );
        ExposureService service = new ExposureService(MAX_RESERVE, 1, 1, 20, policy);
        UUID playerId = UUID.randomUUID();
        ShroudSample deadly = new ShroudSample(1.0F, ShroudSeverity.DEADLY, Optional.empty(), false);

        ExposureSnapshot firstEntry = service.tick(playerId, state(MAX_RESERVE), deadly, 1);
        ExposureSnapshot oneTickOutside = service.tick(
                playerId,
                firstEntry.attachmentState(),
                ShroudSample.clear(),
                1
        );
        ExposureSnapshot reentry = service.tick(
                playerId,
                oneTickOutside.attachmentState(),
                deadly,
                1
        );

        assertEquals(80, firstEntry.remainingTicks());
        assertEquals(81, oneTickOutside.remainingTicks());
        assertEquals(61, reentry.remainingTicks());
        assertTrue(reentry.remainingTicks() < firstEntry.remainingTicks(),
                "rapid Deadly drain must dominate short boundary recovery instead of resetting the window");
    }

    private static ShroudExposureAttachment state(int remainingTicks) {
        return new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, remainingTicks);
    }
}
