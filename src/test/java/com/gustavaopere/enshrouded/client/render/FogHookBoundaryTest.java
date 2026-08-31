package com.gustavaopere.enshrouded.client.render;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FogHookBoundaryTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path MAIN = ROOT.resolve("src/main/java/com/gustavaopere/enshrouded");

    @Test
    void fogControllerUsesSupportedClientHooksAndGameTimeDelta() throws IOException {
        Path controller = MAIN.resolve("client/render/ShroudFogController.java");
        assertTrue(Files.isRegularFile(controller), "Stage 07.02 must provide the client fog controller");

        String source = Files.readString(controller);
        assertTrue(source.contains("ViewportEvent.RenderFog"));
        assertTrue(source.contains("ViewportEvent.ComputeFogColor"));
        assertTrue(source.contains("FogType.NONE"), "enhanced Shroud fog must not override underwater/lava/powder-snow fog");
        assertTrue(source.contains("setCanceled(true)"), "NeoForge 1.21.1 requires cancelling RenderFog for plane changes");
        assertTrue(source.contains("getGameTimeDeltaTicks()"), "visual transition must follow game time instead of wall clock");
        assertTrue(source.contains("EnshroudedClientConfig.fogSettings()"), "runtime hook must read the shared live client config seam");
        assertTrue(source.contains("ClientExposureState.INSTANCE"), "fog target must consume synchronized server-authored state");
        assertTrue(source.contains("settings.intensity() <= 0.0D"),
                "zero fog intensity must leave vanilla/other-mod fog uncancelled instead of becoming an invisible override");
    }

    @Test
    void fogControllerIsRegisteredOnlyFromPhysicalClientBootstrap() throws IOException {
        String clientSource = Files.readString(MAIN.resolve("client/EnshroudedClient.java"));
        assertTrue(clientSource.contains("ShroudFogController"));

        String commonSource = Files.readString(MAIN.resolve("Enshrouded.java"));
        assertFalse(commonSource.contains("client.render"), "dedicated-server bootstrap must not import fog/render classes");
        assertFalse(commonSource.contains("ShroudFogController"));
    }
}
