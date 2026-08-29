package com.gustavaopere.enshrouded.shroud.terrain;

import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorruptionRuleReloadListenerTest {
    @Test
    void reloadPublishesDecodedRegistryInResourceIdOrder() {
        ResourceLocation first = id("a_rule");
        ResourceLocation second = id("z_rule");
        CorruptionRuleReloadListener listener = new CorruptionRuleReloadListener();

        listener.apply(Map.of(
                second, json(second, "minecraft:stone", "minecraft:tuff"),
                first, json(first, "minecraft:stone", "minecraft:deepslate")
        ), null, null);

        var rules = CorruptionRuleReloadListener.currentRegistry().all();
        assertEquals(2, rules.size());
        assertEquals(first, rules.get(0).id());
        assertEquals(second, rules.get(1).id());
    }

    @Test
    void mismatchedEmbeddedIdFailsReloadWithoutPublishingPartialRegistry() {
        ResourceLocation stable = id("stable");
        CorruptionRuleReloadListener listener = new CorruptionRuleReloadListener();
        listener.apply(Map.of(stable, json(stable, "minecraft:stone", "minecraft:tuff")), null, null);
        CorruptionRuleRegistry before = CorruptionRuleReloadListener.currentRegistry();

        ResourceLocation fileId = id("file_id");
        ResourceLocation embeddedId = id("other_id");
        assertThrows(IllegalStateException.class, () -> listener.apply(
                Map.of(fileId, json(embeddedId, "minecraft:stone", "minecraft:deepslate")),
                null,
                null
        ));
        assertTrue(CorruptionRuleReloadListener.currentRegistry() == before);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("enshrouded", path);
    }

    private static com.google.gson.JsonElement json(
            ResourceLocation id,
            String reversal,
            String result) {
        return JsonParser.parseString("""
                {
                  "id": "%s",
                  "source_tag": "enshrouded:corruptible_safe",
                  "result": "%s",
                  "reversal": "%s",
                  "min_intensity": 0.25,
                  "safety": "safe"
                }
                """.formatted(id, result, reversal));
    }
}
