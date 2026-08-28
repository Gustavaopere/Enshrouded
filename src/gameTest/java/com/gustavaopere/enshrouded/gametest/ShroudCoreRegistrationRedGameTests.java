package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ShroudCoreRegistrationRedGameTests {
    private ShroudCoreRegistrationRedGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void shroudCoreBlockMustExistInRegistry(GameTestHelper helper) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "shroud_core");
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);

        helper.assertTrue(BuiltInRegistries.BLOCK.containsKey(key),
                "Expected registered block enshrouded:shroud_core before core lifecycle runtime can be accepted");
        helper.succeed();
    }
}
