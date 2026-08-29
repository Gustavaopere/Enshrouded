package com.gustavaopere.enshrouded.exposure.redsludge;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionRule;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionSafetyClass;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class RedSludgeMaterializationRedTest {
    @Test
    void deadlyOnlyRuleRejectsOrdinaryShroudWhileLegacyRuleStillAllowsIt() throws Exception {
        CorruptionRule deadlyOnly = CorruptionRule.CODEC.parse(
                JsonOps.INSTANCE,
                JsonParser.parseString("""
                        {
                          "id": "enshrouded:red_sludge_test",
                          "source_tag": "enshrouded:red_sludge_sources",
                          "result": "enshrouded:red_sludge",
                          "reversal": "minecraft:water",
                          "min_intensity": 0.0,
                          "min_severity": "deadly",
                          "safety": "safe"
                        }
                        """)
        ).result().orElseThrow();

        CorruptionRule legacy = new CorruptionRule(
                ResourceLocation.parse("enshrouded:legacy"),
                ResourceLocation.parse("enshrouded:corruptible_safe"),
                ResourceLocation.parse("minecraft:deepslate"),
                ResourceLocation.parse("minecraft:stone"),
                0.25F,
                CorruptionSafetyClass.SAFE
        );

        Method appliesTo = CorruptionRule.class.getMethod("appliesTo", ShroudSample.class);
        ShroudSample ordinary = new ShroudSample(1.0F, ShroudSeverity.SHROUD, Optional.empty(), false);
        ShroudSample deadly = new ShroudSample(1.0F, ShroudSeverity.DEADLY, Optional.empty(), false);

        assertFalse((boolean) appliesTo.invoke(deadlyOnly, ordinary),
                "Red Sludge materialization must never match an ordinary SHROUD sample");
        assertTrue((boolean) appliesTo.invoke(deadlyOnly, deadly),
                "The same rule must match a canonical DEADLY sample");
        assertTrue((boolean) appliesTo.invoke(legacy, ordinary),
                "Existing six-argument Stage-02 rules must retain SHROUD-compatible behavior");
    }
}
