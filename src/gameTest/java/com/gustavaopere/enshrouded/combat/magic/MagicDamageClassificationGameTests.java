package com.gustavaopere.enshrouded.combat.magic;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.combat.MagicDamageKind;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class MagicDamageClassificationGameTests {
    private MagicDamageClassificationGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void vanillaMagicFlowsThroughCanonicalEnshroudedTag(GameTestHelper helper) {
        var source = helper.getLevel().damageSources().source(DamageTypes.MAGIC);
        var classification = new DefaultMagicDamageClassifier().classify(source);

        helper.assertTrue(classification.magical(),
                "minecraft:magic must flow through enshrouded:magic via the canonical NeoForge magic tag");
        helper.assertTrue(classification.kind() == MagicDamageKind.GENERIC_MAGIC,
                "vanilla magic must use the Foundation GENERIC_MAGIC classification");
        helper.succeed();
    }
}
