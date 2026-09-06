package com.gustavaopere.enshrouded.command;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class RecoveryCommandContractRedTest {
    @Test
    void recoveryCommandsStayOperatorOnlyLoadedChunkAndNarrow() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/command/RecoveryCommand.java"
        ));

        assertTrue(source.contains("hasPermission(2)"), "recovery must remain operator-only");
        assertTrue(source.contains("hasChunkAt"), "core recovery must reject unloaded chunks");
        assertTrue(source.contains("enqueueRecoveryRegistration"),
                "physical-core recovery must replay the canonical registration queue");
        assertTrue(source.contains("reconcileActiveEncounters"),
                "story recovery must reuse the canonical orphan reconciliation path");
        assertFalse(source.contains("getAllEntities"), "recovery must not enumerate all entities");
        assertFalse(source.contains("getAllChunks"), "recovery must not enumerate all chunks");
        assertFalse(source.contains("getChunks()"), "recovery must not enumerate loaded chunks");
        assertFalse(source.contains("resetAll"), "recovery must not expose a reset-all operation");
        assertFalse(source.contains("deleteAll"), "recovery must not expose a delete-all operation");
    }
}
