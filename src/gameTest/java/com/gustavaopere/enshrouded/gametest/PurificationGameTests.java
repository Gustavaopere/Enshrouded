package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.protection.DefaultMutationAuthority;
import com.gustavaopere.enshrouded.protection.MutationSafetyMode;
import com.gustavaopere.enshrouded.protection.ProtectedAreaService;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.purification.TerrainRestorationService;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionRule;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionRuleRegistry;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionSafetyClass;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PurificationGameTests {
    private PurificationGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void playerModifiedCorruptedBlockIsNotOverwritten(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos relative = new BlockPos(1, 0, 1);
        BlockPos absolute = helper.absolutePos(relative);
        helper.setBlock(relative, Blocks.DEEPSLATE);
        helper.setBlock(relative, Blocks.GOLD_BLOCK);

        AtomicInteger authorityCalls = new AtomicInteger();
        TerrainRestorationService service = service((candidateLevel, pos, kind) -> {
            authorityCalls.incrementAndGet();
            return true;
        });

        helper.assertFalse(service.tryRestore(level, absolute), "Player-edited block must fail closed instead of being overwritten");
        helper.assertBlockPresent(Blocks.GOLD_BLOCK, relative);
        helper.assertValueEqual(authorityCalls.get(), 0, "Unknown/player-edited state must be rejected before MutationAuthority");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void nativeGrowthCleanupRequiresPurificationAuthority(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos allowedRelative = new BlockPos(1, 0, 3);
        BlockPos deniedRelative = new BlockPos(3, 0, 3);
        helper.setBlock(allowedRelative, ModBlocks.SHROUD_GROWTH.get());
        helper.setBlock(deniedRelative, ModBlocks.SHROUD_GROWTH.get());

        AtomicInteger purificationCalls = new AtomicInteger();
        TerrainRestorationService allowed = service((candidateLevel, pos, kind) -> {
            helper.assertTrue(kind == MutationKind.PURIFICATION, "Growth cleanup must route through PURIFICATION authority");
            purificationCalls.incrementAndGet();
            return true;
        });
        helper.assertTrue(allowed.tryRestore(level, helper.absolutePos(allowedRelative)), "Authorized native growth must be removed after its logical cell clears");
        helper.assertBlockPresent(Blocks.AIR, allowedRelative);

        TerrainRestorationService denied = service((candidateLevel, pos, kind) -> false);
        helper.assertFalse(denied.tryRestore(level, helper.absolutePos(deniedRelative)), "Denied purification must leave native growth intact");
        helper.assertBlockPresent(ModBlocks.SHROUD_GROWTH.get(), deniedRelative);
        helper.assertValueEqual(purificationCalls.get(), 1, "Authorized cleanup must make exactly one authority decision");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void wardAllowsPurificationButStillVetoesNewCorruption(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos relative = new BlockPos(5, 0, 3);
        BlockPos absolute = helper.absolutePos(relative);
        helper.setBlock(relative, Blocks.STONE);

        DefaultMutationAuthority wardedAuthority = new DefaultMutationAuthority(
                MutationSafetyMode.SAFE,
                (candidateLevel, pos) -> true,
                ProtectedAreaService.none(),
                false,
                false
        );
        helper.assertFalse(
                wardedAuthority.canMutate(level, absolute, MutationKind.CORRUPTION),
                "Active ward must continue to veto new corruption"
        );

        helper.setBlock(relative, Blocks.DEEPSLATE);
        TerrainRestorationService restoration = service(wardedAuthority);
        helper.assertTrue(
                restoration.tryRestore(level, absolute),
                "Ward must not strand an already-cleared corrupted visual"
        );
        helper.assertBlockPresent(Blocks.STONE, relative);
        helper.succeed();
    }

    private static TerrainRestorationService service(com.gustavaopere.enshrouded.api.shroud.MutationAuthority authority) {
        return new TerrainRestorationService(
                new CorruptionRuleRegistry(List.of(stoneToDeepslateRule())),
                authority,
                ShroudGridGeometry.levelOne(),
                64
        );
    }

    private static CorruptionRule stoneToDeepslateRule() {
        return new CorruptionRule(
                ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "purification_test_stone"),
                ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "corruptible_safe"),
                ResourceLocation.withDefaultNamespace("deepslate"),
                ResourceLocation.withDefaultNamespace("stone"),
                0.25F,
                CorruptionSafetyClass.SAFE
        );
    }
}
