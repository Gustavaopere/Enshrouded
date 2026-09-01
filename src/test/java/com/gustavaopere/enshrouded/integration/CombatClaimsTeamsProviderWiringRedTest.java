package com.gustavaopere.enshrouded.integration;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatClaimsTeamsProviderWiringRedTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));

    @Test
    void productionRuntimeSelectsInstalledProvidersAndBindsThemToCanonicalAuthorities() throws IOException {
        String runtime = source("src/main/java/com/gustavaopere/enshrouded/integration/CombatClaimsTeamsIntegrationRuntime.java");

        assertTrue(runtime.contains("ModList.get().isLoaded(\"ftbteams\")"));
        assertTrue(runtime.contains("ModList.get().isLoaded(\"ftbchunks\")"));
        assertTrue(runtime.contains("ModList.get().isLoaded(\"minecolonies\")"));
        assertTrue(runtime.contains("ModList.get().isLoaded(\"epicfight\")"));
        assertTrue(runtime.contains("new FtbTeamsOwnerResolver"));
        assertTrue(runtime.contains("new FtbChunksProtectionAdapter"));
        assertTrue(runtime.contains("new MineColoniesProtectionAdapter"));
        assertTrue(runtime.contains("ProtectionRuntimeBindings.install(providers)"));
    }

    @Test
    void providerQueriesUseDirectPublicApiLookupsWithoutWorldWideScans() throws IOException {
        String runtime = source("src/main/java/com/gustavaopere/enshrouded/integration/CombatClaimsTeamsIntegrationRuntime.java");

        assertTrue(runtime.contains("dev.ftb.mods.ftbteams.api.FTBTeamsAPI"));
        assertTrue(runtime.contains("getTeamForPlayerID"));
        assertTrue(runtime.contains("isPlayerTeam"));
        assertTrue(runtime.contains("getId"));

        assertTrue(runtime.contains("dev.ftb.mods.ftbchunks.api.FTBChunksAPI"));
        assertTrue(runtime.contains("isManagerLoaded"));
        assertTrue(runtime.contains("getOwningTeam"));

        assertTrue(runtime.contains("com.minecolonies.api.colony.IColonyManager"));
        assertTrue(runtime.contains("getIColony"));

        assertFalse(runtime.contains("getColonies("));
        assertFalse(runtime.contains("getTeams("));
        assertFalse(runtime.contains("LivingDamageEvent"));
        assertFalse(runtime.contains("setNewDamage"));
    }

    private static String source(String relative) throws IOException {
        return Files.readString(ROOT.resolve(relative));
    }
}
