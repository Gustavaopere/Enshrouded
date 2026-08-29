package com.gustavaopere.enshrouded.shroud.purification;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class TerrainRestorationContractTest {
    @Test
    void everyRestorationMutationSinkRoutesThroughPurificationAuthority() throws Exception {
        Path source = Path.of(System.getProperty("user.dir"))
                .resolve("src/main/java/com/gustavaopere/enshrouded/shroud/purification/TerrainRestorationService.java");
        String content = Files.readString(source);

        assertTrue(content.contains("mutationAuthority.canMutate(level, pos, MutationKind.PURIFICATION)"),
                "Terrain restoration must authorize PURIFICATION before mutating the world");
        assertTrue(content.contains("level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL)"),
                "Native growth cleanup sink must remain explicit and reviewable");
        assertTrue(content.contains("level.setBlock(pos, reversal.defaultBlockState(), Block.UPDATE_ALL)"),
                "Terrain reversal sink must remain explicit and reviewable");
    }
}
