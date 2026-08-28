package com.gustavaopere.enshrouded.network;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkDirectionContractTest {
    @Test
    void shroudPresentationPayloadHasNoServerboundRegistrationPath() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/network/ModNetworking.java"));

        assertTrue(source.contains("playToClient("));
        assertFalse(source.contains("playToServer("));
        assertFalse(source.contains("playBidirectional("));
        assertFalse(source.contains("commonToServer("));
        assertFalse(source.contains("commonBidirectional("));
    }
}
