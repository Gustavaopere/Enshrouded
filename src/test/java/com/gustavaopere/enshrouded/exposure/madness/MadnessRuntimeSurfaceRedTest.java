package com.gustavaopere.enshrouded.exposure.madness;

import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import net.minecraft.server.level.ServerPlayer;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MadnessRuntimeSurfaceRedTest {
    @Test
    void authoritativeRuntimeAndFatalDamagePresentationSurfaceExist() throws Exception {
        Class<?> runtime = Class.forName("com.gustavaopere.enshrouded.exposure.madness.MadnessRuntime");
        assertNotNull(runtime.getDeclaredMethod("apply", ServerPlayer.class, ExposureSnapshot.class));

        ClassLoader loader = MadnessRuntimeSurfaceRedTest.class.getClassLoader();
        assertNotNull(loader.getResource("data/minecraft/tags/damage_type/bypasses_cooldown.json"));
        var language = loader.getResourceAsStream("assets/enshrouded/lang/en_us.json");
        assertNotNull(language);
        String text = new String(language.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(text.contains("death.attack.madness"));
        assertTrue(text.toLowerCase().contains("madness") && text.toLowerCase().contains("shroud"));
    }
}
