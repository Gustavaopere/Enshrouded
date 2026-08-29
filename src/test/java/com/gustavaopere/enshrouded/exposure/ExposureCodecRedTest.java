package com.gustavaopere.enshrouded.exposure;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ExposureCodecRedTest {
    @Test
    void codecRoundTripsVersionedReserveAndRejectsFutureSchema() throws Exception {
        try {
            Field codecField = ShroudExposureAttachment.class.getField("CODEC");
            @SuppressWarnings("unchecked")
            Codec<ShroudExposureAttachment> codec = (Codec<ShroudExposureAttachment>) codecField.get(null);

            ShroudExposureAttachment state = new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, 4321);
            var encoded = codec.encodeStart(JsonOps.INSTANCE, state).getOrThrow();
            JsonObject object = encoded.getAsJsonObject();
            assertEquals(ExposureSchema.CURRENT_VERSION, object.get("schema_version").getAsInt());
            assertEquals(4321, object.get("remaining_ticks").getAsInt());
            assertEquals(state, codec.parse(JsonOps.INSTANCE, object).getOrThrow());

            JsonObject future = new JsonObject();
            future.addProperty("schema_version", ExposureSchema.CURRENT_VERSION + 1);
            future.addProperty("remaining_ticks", 4000);
            var rejected = codec.parse(JsonOps.INSTANCE, future);
            assertTrue(rejected.error().isPresent(), "unknown future exposure schema must fail closed");
            assertTrue(
                    rejected.error().orElseThrow().message().contains("unsupported exposure schema version"),
                    "future-schema rejection must carry a diagnostic"
            );
        } catch (NoSuchFieldException exception) {
            fail("Exposure persistence CODEC is not implemented yet");
        }
    }
}
