package com.gustavaopere.enshrouded.exposure.redsludge;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedSludgeResourcesRedTest {
    @Test
    void deadlyMaterializationRuleIsExplicitAndReversible() throws Exception {
        JsonObject rule = json("data/enshrouded/shroud_corruption/red_sludge.json");
        assertEquals("enshrouded:red_sludge", rule.get("id").getAsString());
        assertEquals("enshrouded:red_sludge_sources", rule.get("source_tag").getAsString());
        assertEquals("enshrouded:red_sludge", rule.get("result").getAsString());
        assertEquals("minecraft:red_sand", rule.get("reversal").getAsString());
        assertEquals("deadly", rule.get("min_severity").getAsString());

        JsonObject sources = json("data/enshrouded/tags/block/red_sludge_sources.json");
        assertEquals(1, sources.getAsJsonArray("values").size(),
                "Red Sludge reversal must not guess among multiple source blocks");
        assertEquals("minecraft:red_sand", sources.getAsJsonArray("values").get(0).getAsString());
    }

    @Test
    void visualAndDataResourcesArePackaged() throws Exception {
        assertJson("assets/enshrouded/blockstates/red_sludge.json");
        assertJson("assets/enshrouded/models/block/red_sludge.json");
        assertJson("data/enshrouded/loot_table/blocks/red_sludge.json");
        assertJson("data/enshrouded/tags/fluid/red_sludge.json");

        assertBinary("assets/enshrouded/textures/block/red_sludge_still.png");
        assertBinary("assets/enshrouded/textures/block/red_sludge_flow.png");
    }

    private static void assertJson(String path) throws Exception {
        json(path);
    }

    private static JsonObject json(String path) throws Exception {
        try (InputStream stream = resource(path)) {
            String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return JsonParser.parseString(text).getAsJsonObject();
        }
    }

    private static void assertBinary(String path) throws Exception {
        try (InputStream stream = resource(path)) {
            byte[] bytes = stream.readAllBytes();
            assertTrue(bytes.length > 8, path + " must contain an actual texture");
            assertEquals((byte) 0x89, bytes[0], path + " must be a PNG");
            assertEquals((byte) 'P', bytes[1]);
            assertEquals((byte) 'N', bytes[2]);
            assertEquals((byte) 'G', bytes[3]);
        }
    }

    private static InputStream resource(String path) {
        InputStream stream = RedSludgeResourcesRedTest.class.getClassLoader().getResourceAsStream(path);
        assertNotNull(stream, "Missing required Red Sludge resource: " + path);
        return stream;
    }
}
