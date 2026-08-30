package com.gustavaopere.enshrouded.flame.ward;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.FlameWardRuntimeBindings;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import com.gustavaopere.enshrouded.protection.DefaultMutationAuthority;
import com.gustavaopere.enshrouded.protection.ProtectedAreaService;
import com.gustavaopere.enshrouded.protection.ProtectionDecision;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FlameWardMutationGameTests {
    private static final String WARD_MUTATION_BATCH = "flameWardMutation";

    private FlameWardMutationGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = WARD_MUTATION_BATCH)
    public static void wardVetoesThreatMutationButNotSafePurification(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos altarRelative = new BlockPos(2, 1, 2);
        BlockPos center = helper.absolutePos(altarRelative);

        // This batch verifies one deterministic ward. Remove loaded-altar state left by prior
        // GameTest batches so their spatial layout cannot redefine this fixture's "outside".
        FlameWardRuntime.service().clear();

        try {
            helper.setBlock(altarRelative, ModBlocks.FLAME_ALTAR.get());

            int radius = EnshroudedConfig.flameWardRadius();
            BlockPos insideCore = center.offset(1, 0, 0);
            BlockPos outsideCore = center.offset(radius + 1, 0, 0);
            BlockPos insideCorruption = center.offset(0, 0, 1);
            BlockPos outsideCorruption = center.offset(0, 0, radius + 1);
            level.setBlock(insideCore, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            level.setBlock(outsideCore, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            level.setBlock(insideCorruption, Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
            level.setBlock(outsideCorruption, Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);

            DefaultMutationAuthority authority = DefaultMutationAuthority.fromConfig(
                    FlameWardRuntimeBindings.query(),
                    ProtectedAreaService.none()
            );

            helper.assertTrue(!authority.canMutate(level, insideCore, MutationKind.CORE_PLACEMENT),
                    "Sanctuary must veto new Shroud core placement inside the ward");
            helper.assertTrue(authority.canMutate(level, outsideCore, MutationKind.CORE_PLACEMENT),
                    "Equivalent core placement just outside the ward must remain eligible");
            helper.assertTrue(!authority.canMutate(level, insideCorruption, MutationKind.CORRUPTION),
                    "Sanctuary must veto new terrain corruption inside the ward");
            helper.assertTrue(authority.canMutate(level, outsideCorruption, MutationKind.CORRUPTION),
                    "Safe-tagged terrain just outside the ward must remain corruptible");
            helper.assertTrue(authority.canMutate(level, insideCorruption, MutationKind.PURIFICATION),
                    "Sanctuary must not trap safe purification cleanup inside the ward");

            DefaultMutationAuthority protectedAuthority = DefaultMutationAuthority.fromConfig(
                    FlameWardRuntimeBindings.query(),
                    (queryLevel, pos, kind) -> pos.equals(insideCorruption)
                            ? ProtectionDecision.PROTECTED
                            : ProtectionDecision.UNPROTECTED
            );
            helper.assertTrue(!protectedAuthority.canMutate(level, insideCorruption, MutationKind.PURIFICATION),
                    "Protected/player-owned targets must remain untouched even when Sanctuary permits cleanup");

            helper.succeed();
        } finally {
            helper.destroyBlock(altarRelative);
            FlameWardRuntime.service().clear();
        }
    }
}
