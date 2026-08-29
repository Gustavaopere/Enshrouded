package com.gustavaopere.enshrouded.exposure;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.client.state.ClientExposureState;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExposureRuntimeContractRedTest {
    @Test
    void cadenceProcessesInitialSnapshotThenAtMostOncePerSampleInterval() {
        ExposureSamplingCadence cadence = new ExposureSamplingCadence(20);
        UUID playerId = UUID.fromString("13149eb0-89a1-4441-98ce-664a6ca20566");

        assertEquals(0, cadence.elapsedTicks(playerId, 100L).orElseThrow());
        assertFalse(cadence.elapsedTicks(playerId, 119L).isPresent());
        assertEquals(20, cadence.elapsedTicks(playerId, 120L).orElseThrow());
        assertFalse(cadence.elapsedTicks(playerId, 121L).isPresent());
        assertEquals(40, cadence.elapsedTicks(playerId, 160L).orElseThrow());

        cadence.forget(playerId);
        assertEquals(0, cadence.elapsedTicks(playerId, 500L).orElseThrow(),
                "logout forgets cadence only; attachment persistence is independent");
    }

    @Test
    void clientRejectsStaleExposureSnapshots() {
        ClientExposureState client = new ClientExposureState();
        ExposureSnapshot firstSnapshot = snapshot(5800);
        ExposureSnapshot secondSnapshot = snapshot(5780);

        assertTrue(client.accept(ExposurePayload.fromSnapshot(3L, firstSnapshot)));
        assertFalse(client.accept(ExposurePayload.fromSnapshot(2L, secondSnapshot)));
        assertEquals(firstSnapshot, client.snapshot());
        assertTrue(client.accept(ExposurePayload.fromSnapshot(4L, secondSnapshot)));
        assertEquals(secondSnapshot, client.snapshot());
    }

    @Test
    void exposureNetworkAndRuntimeRemainServerAuthoritative() throws Exception {
        String networking = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/network/ModNetworking.java"));
        assertTrue(networking.contains("ExposurePayload.TYPE"));
        assertTrue(networking.contains("ClientExposureState.INSTANCE.accept(payload)"));
        assertFalse(networking.contains("playToServer("));
        assertFalse(networking.contains("playBidirectional("));
        assertFalse(networking.contains("commonToServer("));
        assertFalse(networking.contains("commonBidirectional("));

        Class<?> runtime = Class.forName("com.gustavaopere.enshrouded.exposure.ExposureRuntime");
        assertEquals(20, runtime.getField("SAMPLE_INTERVAL_TICKS").getInt(null));
        runtime.getMethod("register");

        String source = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/exposure/ExposureRuntime.java"));
        assertTrue(source.contains("PlayerTickEvent.Post"));
        assertTrue(source.contains("PlayerLoggedOutEvent"));
        assertTrue(source.contains("ShroudExposureAttachment.PLAYER_EXPOSURE.get()"));
        assertTrue(source.contains("PacketDistributor.sendToPlayer"));
    }

    private static ExposureSnapshot snapshot(int remainingTicks) {
        return new ExposureSnapshot(
                ExposureSchema.CURRENT_VERSION,
                remainingTicks,
                ExposureSchema.DEFAULT_MAX_RESERVE_TICKS,
                0.5F,
                ShroudSeverity.SHROUD,
                false,
                false
        );
    }
}
