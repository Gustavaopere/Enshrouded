package com.gustavaopere.enshrouded.shroud.terrain;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterializationRulesRedTest {
    @Test
    void materializationDomainAndDatapackContractExist() throws Exception {
        Class.forName("com.gustavaopere.enshrouded.shroud.terrain.CorruptionRule");
        Class.forName("com.gustavaopere.enshrouded.shroud.terrain.CorruptionRuleRegistry");
        Class.forName("com.gustavaopere.enshrouded.shroud.terrain.ShroudMutationJob");
        Class.forName("com.gustavaopere.enshrouded.shroud.terrain.ShroudMaterializationService");

        assertTrue(
                Files.isDirectory(Path.of("src/main/resources/data/enshrouded/shroud_corruption")),
                "materialization rules must be data-driven under data/enshrouded/shroud_corruption"
        );
    }
}
