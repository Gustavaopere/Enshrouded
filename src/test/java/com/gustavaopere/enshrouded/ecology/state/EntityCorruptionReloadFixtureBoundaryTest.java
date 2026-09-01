package com.gustavaopere.enshrouded.ecology.state;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class EntityCorruptionReloadFixtureBoundaryTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path FIXTURE = ROOT.resolve(
            "src/gameTest/java/com/gustavaopere/enshrouded/ecology/state/EntityCorruptionReloadGameTests.java");

    @Test
    void reloadFixturePollsForPersistedEntityInsteadOfTrustingOneFixedDelay() throws IOException {
        String source = Files.readString(FIXTURE);

        assertTrue(source.contains("ENTITY_LOAD_MAX_WAIT_TICKS"),
                "reload fixture needs a bounded wait deadline for asynchronous entity-section loading");
        assertTrue(source.contains("ENTITY_LOAD_POLL_INTERVAL_TICKS"),
                "reload fixture must poll rather than infer absence from one scheduled lookup");
        assertTrue(source.contains("pollForReloadOrCreate"),
                "reload fixture must retry the UUID lookup while the persisted entity section is becoming visible");
        assertFalse(source.contains("ENTITY_LOAD_SETTLE_TICKS"),
                "a single fixed settle delay already proved flaky and must not return");
    }
}
