package com.gustavaopere.enshrouded.exposure.madness;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MadnessSurfaceRedTest {
    @Test
    void levelOneMadnessSurfaceAndFatalDamageResourcesExist() {
        assertDoesNotThrow(() -> Class.forName("com.gustavaopere.enshrouded.exposure.madness.MadnessStage"));
        assertDoesNotThrow(() -> Class.forName("com.gustavaopere.enshrouded.exposure.madness.MadnessService"));
        assertDoesNotThrow(() -> Class.forName("com.gustavaopere.enshrouded.exposure.madness.ModDamageTypes"));

        ClassLoader loader = MadnessSurfaceRedTest.class.getClassLoader();
        assertNotNull(loader.getResource("data/enshrouded/damage_type/madness.json"));
        assertNotNull(loader.getResource("data/minecraft/tags/damage_type/bypasses_armor.json"));
        assertNotNull(loader.getResource("data/minecraft/tags/damage_type/bypasses_resistance.json"));
        assertNotNull(loader.getResource("data/minecraft/tags/damage_type/bypasses_enchantments.json"));
    }
}
