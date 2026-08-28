package com.gustavaopere.enshrouded.shroud.worldgen;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShroudCoreWorldgenContractRedTest {
    private static final Path CONFIGURED_FEATURE = Path.of(
            "src/main/resources/data/enshrouded/worldgen/configured_feature/shroud_core.json");
    private static final Path PLACED_FEATURE = Path.of(
            "src/main/resources/data/enshrouded/worldgen/placed_feature/shroud_core.json");
    private static final Path BIOME_MODIFIER = Path.of(
            "src/main/resources/data/enshrouded/neoforge/biome_modifier/shroud_core.json");

    @Test
    void featureAndRegistryBootstrapExist() throws Exception {
        Class.forName("com.gustavaopere.enshrouded.shroud.worldgen.ShroudCoreFeature");
        Class.forName("com.gustavaopere.enshrouded.shroud.worldgen.ShroudCoreWorldgenRegistry");
    }

    @Test
    void configuredPlacedAndBiomeModifierDataExist() {
        assertTrue(Files.isRegularFile(CONFIGURED_FEATURE), CONFIGURED_FEATURE::toString);
        assertTrue(Files.isRegularFile(PLACED_FEATURE), PLACED_FEATURE::toString);
        assertTrue(Files.isRegularFile(BIOME_MODIFIER), BIOME_MODIFIER::toString);
    }
}
