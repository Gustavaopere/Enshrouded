package com.gustavaopere.enshrouded.flame.state;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class FlameProgressionLoadGuardTest {
    @TempDir
    Path tempDir;

    @Test
    void unreadableExistingDataFileFailsClosedInsteadOfCreatingBaselineState() throws Exception {
        Path dataFile = tempDir.resolve(FlameProgressionSavedData.DATA_NAME + ".dat");
        Files.writeString(dataFile, "synthetic-existing-data");

        assertThrows(
                IllegalStateException.class,
                () -> FlameProgressionSavedData.resolveLoadedOrAbsent(null, dataFile, FlameProgressionSavedData::create)
        );
    }

    @Test
    void absentDataFileMayCreateInitialState() {
        Path dataFile = tempDir.resolve(FlameProgressionSavedData.DATA_NAME + ".dat");
        FlameProgressionSavedData created = FlameProgressionSavedData.create();

        FlameProgressionSavedData resolved = FlameProgressionSavedData.resolveLoadedOrAbsent(
                null,
                dataFile,
                () -> created
        );

        assertSame(created, resolved);
    }

    @Test
    void successfullyLoadedStateWinsEvenWhenDataFileExists() throws Exception {
        Path dataFile = tempDir.resolve(FlameProgressionSavedData.DATA_NAME + ".dat");
        Files.writeString(dataFile, "synthetic-existing-data");
        FlameProgressionSavedData loaded = FlameProgressionSavedData.create();

        FlameProgressionSavedData resolved = FlameProgressionSavedData.resolveLoadedOrAbsent(
                loaded,
                dataFile,
                () -> {
                    throw new AssertionError("factory must not run when load succeeded");
                }
        );

        assertSame(loaded, resolved);
    }
}
