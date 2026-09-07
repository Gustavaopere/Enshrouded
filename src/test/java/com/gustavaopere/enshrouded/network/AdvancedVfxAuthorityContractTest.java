package com.gustavaopere.enshrouded.network;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AdvancedVfxAuthorityContractTest {
    @Test
    void advancedVfxNetworkingIsClientboundOnlyAndPresentationOnly() throws IOException {
        String networking = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/network/ModNetworking.java"));
        String emitter = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/presentation/AdvancedVfxServerEmitter.java"));

        assertTrue(networking.contains("AdvancedVfxPayload.TYPE"));
        assertTrue(networking.contains("ClientAdvancedVfxState.INSTANCE.accept(payload)"));
        assertFalse(networking.contains("playToServer("));
        assertTrue(emitter.contains("PacketDistributor.sendToPlayer"));
        assertTrue(emitter.contains("cue.serverAuthored()"));
        assertFalse(emitter.contains("sendParticles("));
        assertFalse(emitter.contains("forceChunk"));
        assertFalse(emitter.contains("getChunk("));
    }

    @Test
    void discreteCuesAreDownstreamOfCanonicalSuccessPoints() throws IOException {
        String core = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/shroud/core/ShroudCoreBlockEntity.java"));
        String altar = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/flame/altar/FlameAltarMenu.java"));
        String lichPresentation = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/story/manifestation/LichManifestationPresentation.java"));

        String coreCue = "AdvancedVfxServerEmitter.emit(serverLevel, worldPosition, AdvancedVfxCue.CORE_DESTROYED);";
        assertTrue(core.contains(coreCue));
        assertTrue(core.indexOf(coreCue) > core.indexOf("NeoForge.EVENT_BUS.post(new ShroudCoreDestroyedEvent"));

        String altarCue = "AdvancedVfxServerEmitter.emit(serverPlayer.serverLevel(), altar.getBlockPos(), AdvancedVfxCue.FLAME_RITUAL_SUCCESS);";
        assertTrue(altar.contains(altarCue));
        assertTrue(altar.indexOf(altarCue) > altar.indexOf("result.status() == FlameAltarService.Status.APPLIED"));

        String lichCue = "AdvancedVfxServerEmitter.emit(level, actor.blockPosition(), AdvancedVfxCue.LICH_MANIFESTATION);";
        assertTrue(lichPresentation.contains(lichCue));
        assertFalse(lichPresentation.substring(lichPresentation.indexOf("public static void onSpawned"),
                lichPresentation.indexOf("public static void onDefeated")).contains("sendParticles("));
    }
}
