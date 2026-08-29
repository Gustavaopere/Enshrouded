package com.gustavaopere.enshrouded.shroud.terrain;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorruptionRuleCodecRedTest {
    @Test
    void codecAcceptsExplicitReversibleRuleAndRejectsUnsafeShapes() throws Exception {
        @SuppressWarnings("unchecked")
        Codec<CorruptionRule> codec = (Codec<CorruptionRule>) CorruptionRule.class.getField("CODEC").get(null);

        String valid = """
                {
                  "id": "enshrouded:test_stone",
                  "source_tag": "enshrouded:corruptible_safe",
                  "result": "minecraft:deepslate",
                  "reversal": "minecraft:stone",
                  "min_intensity": 0.25,
                  "safety": "safe"
                }
                """;
        CorruptionRule decoded = codec.parse(JsonOps.INSTANCE, JsonParser.parseString(valid))
                .result().orElseThrow();
        assertEquals(ResourceLocation.parse("minecraft:stone"), decoded.reversalBlock());
        assertEquals(CorruptionSafetyClass.SAFE, decoded.safetyClass());

        String missingReversal = """
                {
                  "id": "enshrouded:bad",
                  "source_tag": "enshrouded:corruptible_safe",
                  "result": "minecraft:deepslate",
                  "min_intensity": 0.25,
                  "safety": "safe"
                }
                """;
        assertTrue(codec.parse(JsonOps.INSTANCE, JsonParser.parseString(missingReversal)).result().isEmpty());

        String invalidIntensity = """
                {
                  "id": "enshrouded:bad_intensity",
                  "source_tag": "enshrouded:corruptible_safe",
                  "result": "minecraft:deepslate",
                  "reversal": "minecraft:stone",
                  "min_intensity": 1.25,
                  "safety": "safe"
                }
                """;
        assertTrue(codec.parse(JsonOps.INSTANCE, JsonParser.parseString(invalidIntensity)).result().isEmpty());
    }

    @Test
    void registryIsImmutableAndRejectsDuplicateRuleIds() throws Exception {
        CorruptionRule a = new CorruptionRule(
                ResourceLocation.parse("enshrouded:a"),
                ResourceLocation.parse("enshrouded:corruptible_safe"),
                ResourceLocation.parse("minecraft:deepslate"),
                ResourceLocation.parse("minecraft:stone"),
                0.25f,
                CorruptionSafetyClass.SAFE
        );
        CorruptionRule b = new CorruptionRule(
                ResourceLocation.parse("enshrouded:b"),
                ResourceLocation.parse("enshrouded:corruptible_safe"),
                ResourceLocation.parse("minecraft:tuff"),
                ResourceLocation.parse("minecraft:dirt"),
                0.5f,
                CorruptionSafetyClass.AGGRESSIVE
        );

        Constructor<CorruptionRuleRegistry> ctor = CorruptionRuleRegistry.class.getDeclaredConstructor(java.util.Collection.class);
        CorruptionRuleRegistry registry = ctor.newInstance(List.of(a, b));
        @SuppressWarnings("unchecked")
        Optional<CorruptionRule> found = (Optional<CorruptionRule>) CorruptionRuleRegistry.class
                .getMethod("rule", ResourceLocation.class)
                .invoke(registry, a.id());
        assertEquals(a, found.orElseThrow());

        @SuppressWarnings("unchecked")
        List<CorruptionRule> all = (List<CorruptionRule>) CorruptionRuleRegistry.class.getMethod("all").invoke(registry);
        assertEquals(List.of(a, b), all);
        assertThrows(UnsupportedOperationException.class, () -> all.add(a));

        InvocationTargetException duplicate = assertThrows(
                InvocationTargetException.class,
                () -> ctor.newInstance(List.of(a, a))
        );
        assertTrue(duplicate.getCause() instanceof IllegalArgumentException);
    }
}
