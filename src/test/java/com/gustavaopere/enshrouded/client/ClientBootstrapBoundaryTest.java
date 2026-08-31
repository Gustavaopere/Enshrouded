package com.gustavaopere.enshrouded.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ClientBootstrapBoundaryTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path MAIN = ROOT.resolve("src/main/java/com/gustavaopere/enshrouded");

    @Test
    void clientConfigAndHudAreOwnedByClientOnlyEntrypoint() throws IOException {
        Path clientEntrypoint = MAIN.resolve("client/EnshroudedClient.java");
        assertTrue(Files.isRegularFile(clientEntrypoint),
                "Stage 07 must register presentation config/rendering from a physical client-only entrypoint");

        String clientSource = Files.readString(clientEntrypoint);
        assertTrue(clientSource.contains("dist = Dist.CLIENT"),
                "client entrypoint must be physically gated to Dist.CLIENT");
        assertTrue(clientSource.contains("ModConfig.Type.CLIENT"),
                "shared Stage 07 config must be registered as CLIENT config");
        assertTrue(clientSource.contains("EnshroudedClientConfig.CLIENT_SPEC"));
        assertTrue(clientSource.contains("ShroudHudOverlay"));

        String commonBootstrap = Files.readString(MAIN.resolve("Enshrouded.java"));
        assertFalse(commonBootstrap.contains("EnshroudedClientConfig"),
                "common bootstrap must not load Stage 07 client config on dedicated servers");
        assertFalse(commonBootstrap.contains("client.hud"),
                "common bootstrap must not load HUD implementation on dedicated servers");
    }
}
