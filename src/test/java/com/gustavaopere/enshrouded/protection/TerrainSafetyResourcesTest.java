package com.gustavaopere.enshrouded.protection;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerrainSafetyResourcesTest {
    @Test
    void blockTagKeysAreStable() {
        assertEquals("enshrouded:corruptible_safe", TerrainSafetyTags.CORRUPTIBLE_SAFE.location().toString());
        assertEquals("enshrouded:corruptible_aggressive", TerrainSafetyTags.CORRUPTIBLE_AGGRESSIVE.location().toString());
    }

    @Test
    void datapackTagsKeepSafeAndAggressiveBoundariesExplicit() throws Exception {
        String safe = Files.readString(Path.of(
                "src/main/resources/data/enshrouded/tags/block/corruptible_safe.json"));
        String aggressive = Files.readString(Path.of(
                "src/main/resources/data/enshrouded/tags/block/corruptible_aggressive.json"));

        assertTrue(safe.contains("minecraft:stone"));
        assertTrue(safe.contains("#minecraft:base_stone_overworld"));
        assertTrue(aggressive.contains("#enshrouded:corruptible_safe"));
    }
}
