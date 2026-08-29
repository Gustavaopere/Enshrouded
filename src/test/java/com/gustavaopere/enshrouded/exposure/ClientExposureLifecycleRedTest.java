package com.gustavaopere.enshrouded.exposure;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class ClientExposureLifecycleRedTest {
    @Test
    void clientDisconnectResetsExposureSequenceEpoch() throws Exception {
        Class.forName("com.gustavaopere.enshrouded.client.state.ClientExposureLifecycle");

        String source = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/client/state/ClientExposureLifecycle.java"));
        assertTrue(source.contains("ClientPlayerNetworkEvent.LoggingOut"),
                "Client lifecycle must observe logical-client logout");
        assertTrue(source.contains("ClientExposureState.INSTANCE.reset()"),
                "Logout must reset the client sequence epoch before the next connection");
        assertTrue(source.contains("Dist.CLIENT"),
                "Client lifecycle subscriber must be distribution-gated away from dedicated server classloading");
    }
}
