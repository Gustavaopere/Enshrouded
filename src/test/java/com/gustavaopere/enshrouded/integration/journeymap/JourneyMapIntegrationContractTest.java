package com.gustavaopere.enshrouded.integration.journeymap;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class JourneyMapIntegrationContractTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path MAIN = ROOT.resolve("src/main/java/com/gustavaopere/enshrouded");

    @Test
    void journeymapIsAClientOnlySoftDependencyDrivenOnlyByAuthorizedSnapshots() throws IOException {
        String build = Files.readString(ROOT.resolve("build.gradle"));
        assertTrue(build.contains("https://maven.blamejared.com"));
        assertTrue(build.contains("journeymap-api-neoforge"));
        assertTrue(build.contains("2.0.0-1.21.1"));
        assertTrue(build.contains("compileOnly"));

        Path plugin = MAIN.resolve("integration/journeymap/EnshroudedJourneyMapPlugin.java");
        Path adapter = MAIN.resolve("integration/journeymap/JourneyMapAdapter.java");
        assertTrue(Files.isRegularFile(plugin));
        assertTrue(Files.isRegularFile(adapter));

        String pluginSource = Files.readString(plugin);
        assertTrue(pluginSource.contains("@JourneyMapPlugin(apiVersion = \"2.0.0\")"));
        assertTrue(pluginSource.contains("implements IClientPlugin"));
        assertTrue(pluginSource.contains("ClientEventRegistry.MAPPING_EVENT.subscribe"));
        assertTrue(pluginSource.contains("ClientShroudDiscoveryState.INSTANCE.addListener"));

        String adapterSource = Files.readString(adapter);
        assertTrue(adapterSource.contains("WaypointFactory.createWaypoint"));
        assertTrue(adapterSource.contains("addWaypoint"));
        assertTrue(adapterSource.contains("removeWaypoint"));
        assertTrue(adapterSource.contains("persistent=false") || adapterSource.contains("false)"));
        assertFalse(adapterSource.contains("ShroudSavedData"),
                "JourneyMap must never read authoritative world SavedData on the client");

        String commonBootstrap = Files.readString(MAIN.resolve("Enshrouded.java"));
        assertFalse(commonBootstrap.contains("EnshroudedJourneyMapPlugin"),
                "common bootstrap must never classload the optional JourneyMap plugin");
        assertFalse(commonBootstrap.contains("JourneyMapAdapter"));

        String clientBootstrap = Files.readString(MAIN.resolve("client/EnshroudedClient.java"));
        assertTrue(clientBootstrap.contains("ClientPlayerNetworkEvent.LoggingOut"));
        assertTrue(clientBootstrap.contains("ClientShroudDiscoveryState.INSTANCE.reset()"));

        String modsToml = Files.readString(ROOT.resolve("src/main/resources/META-INF/neoforge.mods.toml"));
        assertFalse(modsToml.contains("modId=\"journeymap\""),
                "JourneyMap absence must remain a supported standalone configuration");
    }
}
