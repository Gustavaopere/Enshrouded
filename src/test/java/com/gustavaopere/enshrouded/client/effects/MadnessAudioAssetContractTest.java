package com.gustavaopere.enshrouded.client.effects;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class MadnessAudioAssetContractTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path JAVA = ROOT.resolve("src/main/java/com/gustavaopere/enshrouded");
    private static final Path ASSETS = ROOT.resolve("src/main/resources/assets/enshrouded");

    @Test
    void madnessUsesProjectOwnedOriginalAudioAsset() throws IOException {
        String soundsRegistry = Files.readString(JAVA.resolve("registry/ModSounds.java"));
        assertTrue(soundsRegistry.contains("MADNESS_WHISPER"));

        String soundsJson = Files.readString(ASSETS.resolve("sounds.json"));
        assertTrue(soundsJson.contains("madness_whisper"));
        assertTrue(soundsJson.contains("madness/whisper"));

        Path ogg = ASSETS.resolve("sounds/madness/whisper.ogg");
        assertTrue(Files.isRegularFile(ogg));
        byte[] bytes = Files.readAllBytes(ogg);
        assertTrue(bytes.length > 256);
        assertArrayEquals(new byte[]{'O', 'g', 'g', 'S'}, new byte[]{bytes[0], bytes[1], bytes[2], bytes[3]});

        String provenance = Files.readString(ROOT.resolve("docs/assets/audio.md"));
        assertTrue(provenance.contains("madness/whisper.ogg"));
        assertTrue(provenance.contains("CC0-1.0"));
    }
}
