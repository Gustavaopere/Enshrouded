package com.gustavaopere.enshrouded.shroud.discovery;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShroudDiscoveryRuntimeContractTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path MAIN = ROOT.resolve("src/main/java/com/gustavaopere/enshrouded");

    @Test
    void canonicalSampleDrivesDiscoveryAndNetworkingWithoutASecondOmniscientScan() throws IOException {
        Path runtime = MAIN.resolve("shroud/discovery/ShroudDiscoveryRuntime.java");
        assertTrue(Files.isRegularFile(runtime), "Stage 08.04 requires a server discovery runtime");

        String runtimeSource = Files.readString(runtime);
        assertTrue(runtimeSource.contains("ShroudDiscoveryObservation.observe"));
        assertTrue(runtimeSource.contains("ProgressionRuntimeBindings.ownerResolver()"));
        assertTrue(runtimeSource.contains("ShroudDiscoverySavedData.get"));
        assertTrue(runtimeSource.contains("ShroudDiscoverySyncTracker"));
        assertTrue(runtimeSource.contains("PacketDistributor.sendToPlayer"));
        assertTrue(runtimeSource.contains("ShroudCoreDestroyedEvent"));
        assertTrue(runtimeSource.contains("ShroudCorePurifiedEvent"));

        String syncRuntime = Files.readString(MAIN.resolve("network/ShroudSyncRuntime.java"));
        assertTrue(syncRuntime.contains("syncWithSample"),
                "discovery must reuse the exact canonical sample already queried for presentation sync");
        assertTrue(syncRuntime.contains("ShroudDiscoveryRuntime.observe"));

        String networking = Files.readString(MAIN.resolve("network/ModNetworking.java"));
        assertTrue(networking.contains("ShroudDiscoveryPayload.TYPE"));
        assertTrue(networking.contains("ShroudDiscoveryPayload.STREAM_CODEC"));

        String purification = Files.readString(MAIN.resolve("shroud/purification/ShroudPurificationRuntime.java"));
        assertTrue(purification.contains("ShroudCorePurifiedEvent"),
                "purification must publish an explicit lifecycle transition instead of discovery polling all cores");
    }
}
