package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.FlameWardQuery;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.protection.DefaultMutationAuthority;
import com.gustavaopere.enshrouded.protection.MutationSafetyMode;
import com.gustavaopere.enshrouded.protection.ProtectedAreaService;
import com.gustavaopere.enshrouded.protection.ProtectionDecision;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TerrainSafetyGameTests {
    private TerrainSafetyGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void loadedWorldFactsDriveFailClosedMutationAuthority(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos stoneRelative = new BlockPos(1, 0, 1);
        BlockPos stonePos = helper.absolutePos(stoneRelative);
        helper.setBlock(stoneRelative, Blocks.STONE);

        DefaultMutationAuthority safe = new DefaultMutationAuthority(
                MutationSafetyMode.SAFE,
                FlameWardQuery.none(),
                ProtectedAreaService.none(),
                false,
                false
        );
        helper.assertTrue(
                safe.canMutate(level, stonePos, MutationKind.CORRUPTION),
                "SAFE authority must allow an explicitly safe-tagged natural stone block"
        );
        helper.assertTrue(
                !safe.canMutate(level, stonePos, MutationKind.CORE_PLACEMENT),
                "Core placement must reject a non-replaceable stone target"
        );

        DefaultMutationAuthority warded = new DefaultMutationAuthority(
                MutationSafetyMode.SAFE,
                (candidateLevel, candidatePos) -> candidatePos.equals(stonePos),
                ProtectedAreaService.none(),
                false,
                false
        );
        helper.assertTrue(
                !warded.canMutate(level, stonePos, MutationKind.CORRUPTION),
                "Sanctuary must veto new corruption"
        );
        helper.assertTrue(
                warded.canMutate(level, stonePos, MutationKind.PURIFICATION),
                "Sanctuary alone must not strand safe purification"
        );

        DefaultMutationAuthority protectedAuthority = new DefaultMutationAuthority(
                MutationSafetyMode.SAFE,
                FlameWardQuery.none(),
                (candidateLevel, candidatePos, kind) -> ProtectionDecision.PROTECTED,
                false,
                false
        );
        helper.assertTrue(
                !protectedAuthority.canMutate(level, stonePos, MutationKind.PURIFICATION),
                "Protected positions must veto every mutation kind"
        );

        BlockPos chestRelative = new BlockPos(1, 1, 1);
        BlockPos chestPos = helper.absolutePos(chestRelative);
        helper.setBlock(chestRelative, Blocks.CHEST);
        helper.assertTrue(level.getBlockEntity(chestPos) != null, "Chest fixture must expose a block entity");
        helper.assertTrue(
                !safe.canMutate(level, chestPos, MutationKind.PURIFICATION),
                "Block entities must fail closed without expert override"
        );

        BlockPos airRelative = new BlockPos(2, 1, 1);
        BlockPos airPos = helper.absolutePos(airRelative);
        helper.setBlock(airRelative, Blocks.AIR);
        helper.assertTrue(
                safe.canMutate(level, airPos, MutationKind.CORE_PLACEMENT),
                "Core placement must allow an unprotected replaceable target"
        );
        helper.succeed();
    }
}
