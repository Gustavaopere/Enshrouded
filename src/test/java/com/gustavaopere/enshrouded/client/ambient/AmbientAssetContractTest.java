package com.gustavaopere.enshrouded.client.ambient;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AmbientAssetContractTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path JAVA = ROOT.resolve("src/main/java/com/gustavaopere/enshrouded");
    private static final Path ASSETS = ROOT.resolve("src/main/resources/assets/enshrouded");

    @Test
    void ambientSoundsUseOneCanonicalRegistryAndOriginalAssets() throws IOException {
        Path registry = JAVA.resolve("registry/ModSounds.java");
        assertTrue(Files.isRegularFile(registry), "07.03 must own one canonical SoundEvent registry");
        String registrySource = Files.readString(registry);
        assertTrue(registrySource.contains("SHROUD_AMBIENT"));
        assertTrue(registrySource.contains("DEADLY_SHROUD_AMBIENT"));

        String registrations = Files.readString(JAVA.resolve("registry/ModRegistries.java"));
        assertTrue(registrations.contains("ModSounds.register(modBus)"),
                "sound registry must be wired through the canonical common registry bootstrap");

        String controller = Files.readString(JAVA.resolve("client/ambient/ShroudAmbientController.java"));
        assertTrue(controller.contains("ModSounds.SHROUD_AMBIENT"));
        assertTrue(controller.contains("ModSounds.DEADLY_SHROUD_AMBIENT"));

        Path soundsJson = ASSETS.resolve("sounds.json");
        assertTrue(Files.isRegularFile(soundsJson));
        String sounds = Files.readString(soundsJson);
        assertTrue(sounds.contains("ambient/shroud"));
        assertTrue(sounds.contains("ambient/deadly_shroud"));

        assertOgg(ASSETS.resolve("sounds/ambient/shroud.ogg"));
        assertOgg(ASSETS.resolve("sounds/ambient/deadly_shroud.ogg"));

        Path provenance = ROOT.resolve("docs/assets/audio.md");
        assertTrue(Files.isRegularFile(provenance));
        String provenanceText = Files.readString(provenance, StandardCharsets.UTF_8);
        assertTrue(provenanceText.contains("shroud.ogg"));
        assertTrue(provenanceText.contains("deadly_shroud.ogg"));
        assertTrue(provenanceText.contains("CC0-1.0"),
                "original procedural ambience must have explicit redistribution provenance");
    }

    private static void assertOgg(Path path) throws IOException {
        assertTrue(Files.isRegularFile(path), "missing OGG asset: " + path);
        byte[] bytes = Files.readAllBytes(path);
        assertTrue(bytes.length > 256, "OGG asset is implausibly small: " + path);
        assertArrayEquals(new byte[]{'O', 'g', 'g', 'S'}, new byte[]{bytes[0], bytes[1], bytes[2], bytes[3]},
                "sound asset must be a real Ogg container");
    }
}
