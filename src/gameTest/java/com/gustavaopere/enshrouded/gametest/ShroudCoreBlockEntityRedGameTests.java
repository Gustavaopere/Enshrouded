package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ShroudCoreBlockEntityRedGameTests {
    private ShroudCoreBlockEntityRedGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void shroudCoreBlockEntityTypeMustExistInRegistry(GameTestHelper helper) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "shroud_core");
        ResourceKey<BlockEntityType<?>> key = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, id);

        helper.assertTrue(BuiltInRegistries.BLOCK_ENTITY_TYPE.containsKey(key),
                "Expected registered block entity type enshrouded:shroud_core");
        helper.succeed();
    }
}
