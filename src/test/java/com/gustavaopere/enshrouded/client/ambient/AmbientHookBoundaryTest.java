package com.gustavaopere.enshrouded.client.ambient;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AmbientHookBoundaryTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path MAIN = ROOT.resolve("src/main/java/com/gustavaopere/enshrouded");

    @Test
    void ambientRuntimeIsRegisteredOnlyFromPhysicalClientAndResetsOnLogout() throws IOException {
        Path controller = MAIN.resolve("client/ambient/ShroudAmbientController.java");
        assertTrue(Files.isRegularFile(controller), "07.03 must provide one client-only ambient controller");
        String controllerSource = Files.readString(controller);
        assertTrue(controllerSource.contains("ClientTickEvent.Post"),
                "ambient cooldown/emission must advance at most once per client tick");
        assertTrue(controllerSource.contains("ClientExposureState.INSTANCE.snapshot()"),
                "ambient presentation must consume synchronized server-authored Exposure state");
        assertFalse(controllerSource.contains("getEntities"), "ambient runtime must not scan world entities");
        assertFalse(controllerSource.contains("getAllEntities"), "ambient runtime must not scan world entities");

        String clientBootstrap = Files.readString(MAIN.resolve("client/EnshroudedClient.java"));
        assertTrue(clientBootstrap.contains("ShroudAmbientController.register(NeoForge.EVENT_BUS)"),
                "ambient runtime must register only from the physical Dist.CLIENT entrypoint");

        String lifecycle = Files.readString(MAIN.resolve("client/state/ClientExposureLifecycle.java"));
        assertTrue(lifecycle.contains("ShroudAmbientController.reset()"),
                "ambient cooldown state must reset when the client connection ends");

        String commonBootstrap = Files.readString(MAIN.resolve("Enshrouded.java"));
        assertFalse(commonBootstrap.contains("client.ambient"),
                "common bootstrap must not load ambient client classes on dedicated servers");
    }
}
