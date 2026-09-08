package com.gustavaopere.enshrouded.flame.altar;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class FlameAltarShellAcquisitionResourcesTest {
    @Test
    void survivalRecipesProduceTheFullRequiredShellSets() throws Exception {
        String braceRecipe = Files.readString(Path.of(
                "src/main/resources/data/enshrouded/recipe/flame_altar_brace.json"));
        String runeRecipe = Files.readString(Path.of(
                "src/main/resources/data/enshrouded/recipe/flame_altar_rune.json"));

        assertTrue(braceRecipe.contains("\"id\": \"enshrouded:flame_altar_brace\""));
        assertTrue(braceRecipe.contains("\"count\": 4"));
        assertTrue(runeRecipe.contains("\"id\": \"enshrouded:flame_altar_rune\""));
        assertTrue(runeRecipe.contains("\"count\": 4"));
    }

    @Test
    void shellComponentsDropThemselvesInsteadOfBecomingCommandOnlyAfterPlacement() throws Exception {
        String braceLoot = Files.readString(Path.of(
                "src/main/resources/data/enshrouded/loot_table/blocks/flame_altar_brace.json"));
        String runeLoot = Files.readString(Path.of(
                "src/main/resources/data/enshrouded/loot_table/blocks/flame_altar_rune.json"));

        assertTrue(braceLoot.contains("\"name\": \"enshrouded:flame_altar_brace\""));
        assertTrue(runeLoot.contains("\"name\": \"enshrouded:flame_altar_rune\""));
    }
}
