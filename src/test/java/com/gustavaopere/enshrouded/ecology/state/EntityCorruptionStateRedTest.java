package com.gustavaopere.enshrouded.ecology.state;

import com.google.gson.JsonParser;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class EntityCorruptionStateRedTest {
    @Test
    void attachmentRoundTripsAndRejectsUnknownFutureSchema() {
        EntityCorruptionAttachment state = new EntityCorruptionAttachment(
                EntityCorruptionSchema.CURRENT_VERSION,
                0.625F
        );

        var encoded = EntityCorruptionAttachment.CODEC.encodeStart(JsonOps.INSTANCE, state)
                .result()
                .orElseThrow();
        EntityCorruptionAttachment decoded = EntityCorruptionAttachment.CODEC.parse(JsonOps.INSTANCE, encoded)
                .result()
                .orElseThrow();
        assertEquals(state, decoded);
        assertEquals(CorruptionStage.CORRUPTED, decoded.stage());

        var future = JsonParser.parseString("{\"schema_version\":999,\"intensity\":0.75}");
        var futureResult = EntityCorruptionAttachment.CODEC.parse(JsonOps.INSTANCE, future);
        assertTrue(futureResult.error().isPresent(), "future schema must fail closed");
        assertTrue(futureResult.error().orElseThrow().message().contains("unsupported entity corruption schema version"));
    }

    @Test
    void serviceAccumulatesOnlyInEffectiveShroudAndRegressesInSafeSpace() {
        EntityCorruptionService service = new EntityCorruptionService(0.01F, 0.02F, 100);
        EntityCorruptionAttachment clean = EntityCorruptionAttachment.clean();
        ShroudSample shroud = new ShroudSample(1.0F, ShroudSeverity.SHROUD, Optional.empty(), false);

        EntityCorruptionAttachment exposed = service.tick(clean, shroud, 20);
        assertEquals(0.20F, exposed.intensity(), 0.0001F);
        assertEquals(CorruptionStage.TAINTED, exposed.stage());

        ShroudSample sanctuary = new ShroudSample(1.0F, ShroudSeverity.DEADLY, Optional.empty(), true);
        EntityCorruptionAttachment regressed = service.tick(exposed, sanctuary, 5);
        assertEquals(0.10F, regressed.intensity(), 0.0001F);

        EntityCorruptionAttachment cleared = service.tick(regressed, ShroudSample.clear(), 20);
        assertEquals(0.0F, cleared.intensity(), 0.0001F);
        assertEquals(CorruptionStage.CLEAR, cleared.stage());
    }

    @Test
    void reducerClampsElapsedWorkAndIntensity() {
        EntityCorruptionService service = new EntityCorruptionService(0.05F, 0.05F, 20);
        ShroudSample deadly = new ShroudSample(1.0F, ShroudSeverity.DEADLY, Optional.empty(), false);

        EntityCorruptionAttachment saturated = service.tick(EntityCorruptionAttachment.clean(), deadly, 10_000);
        assertEquals(1.0F, saturated.intensity(), 0.0001F);
        assertEquals(CorruptionStage.CORRUPTED, saturated.stage());
    }

    @Test
    void eligibilityIsAllowlistedAndFailClosed() {
        assertTrue(CorruptionEligibility.isEligible(false, true, false, false));
        assertFalse(CorruptionEligibility.isEligible(true, true, false, false), "players use Exposure instead");
        assertFalse(CorruptionEligibility.isEligible(false, false, false, false), "unknown entity types fail closed");
        assertFalse(CorruptionEligibility.isEligible(false, true, true, false), "immune tag wins");
        assertFalse(CorruptionEligibility.isEligible(false, true, false, true), "boss exclusion tag wins");
    }
}
