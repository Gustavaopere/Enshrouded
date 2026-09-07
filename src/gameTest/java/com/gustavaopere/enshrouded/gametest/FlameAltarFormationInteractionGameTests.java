package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.FlameWardRuntimeBindings;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarBlockEntity;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarBraceBlock;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarMenu;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarRuneBlock;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FlameAltarFormationInteractionGameTests {
    private static final String BATCH = "flameAltarFormationInteraction";

    private FlameAltarFormationInteractionGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void explicitControllerInteractionFormsCanonicalShellBeforeOpeningRitualMenu(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ServerPlayer player = requireServerPlayer(helper);
        BlockPos centerRelative = new BlockPos(3, 1, 3);
        BlockPos center = helper.absolutePos(centerRelative);

        placeCanonicalShell(helper, centerRelative);
        FlameAltarBlockEntity altar = requireAltar(helper, centerRelative);

        helper.assertTrue(!altar.isFormed(),
                "A fully placed Flame Altar shell must remain UNFORMED until explicit controller interaction");
        assertShellFormed(helper, centerRelative, false);
        helper.assertTrue(!FlameWardRuntimeBindings.query().suppresses(level, center),
                "An UNFORMED shell must not activate Sanctuary");
        helper.assertTrue(player.containerMenu == player.inventoryMenu,
                "Formation GameTest must begin with no ritual menu open");

        helper.useBlock(centerRelative, player);

        helper.assertTrue(altar.isFormed(),
                "First explicit controller interaction on a valid shell must commit FORMED server-side");
        assertShellFormed(helper, centerRelative, true);
        helper.assertTrue(FlameWardRuntimeBindings.query().suppresses(level, center),
                "Successful formation must activate the existing canonical FlameWard provider");
        helper.assertTrue(player.containerMenu == player.inventoryMenu,
                "The formation interaction must not also open/execute the ritual UI in the same click");

        helper.useBlock(centerRelative, player);
        helper.assertTrue(player.containerMenu instanceof FlameAltarMenu,
                "A subsequent interaction on an already FORMED altar must open the canonical ritual menu");
        helper.assertTrue(altar.isFormed(),
                "Repeated interaction after formation must be idempotent and keep the altar FORMED");
        assertShellFormed(helper, centerRelative, true);

        player.closeContainer();
        helper.destroyBlock(centerRelative);
        helper.succeed();
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void invalidShellInteractionFailsClosedWithoutMenuOrSanctuary(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ServerPlayer player = requireServerPlayer(helper);
        BlockPos centerRelative = new BlockPos(3, 1, 3);
        BlockPos center = helper.absolutePos(centerRelative);

        placeCanonicalShell(helper, centerRelative);
        helper.setBlock(centerRelative.offset(1, 0, 1), net.minecraft.world.level.block.Blocks.AIR);
        FlameAltarBlockEntity altar = requireAltar(helper, centerRelative);

        helper.useBlock(centerRelative, player);

        helper.assertTrue(!altar.isFormed(),
                "Explicit activation of an invalid shell must remain UNFORMED");
        helper.assertTrue(!FlameWardRuntimeBindings.query().suppresses(level, center),
                "Invalid formation must not activate Sanctuary");
        helper.assertTrue(player.containerMenu == player.inventoryMenu,
                "Invalid formation must fail closed instead of opening the ritual menu");
        assertExistingShellComponentsUnformed(helper, centerRelative);

        helper.destroyBlock(centerRelative);
        helper.succeed();
    }

    private static void placeCanonicalShell(GameTestHelper helper, BlockPos center) {
        helper.setBlock(center, ModBlocks.FLAME_ALTAR.get());
        helper.setBlock(center.offset(0, 0, -1), ModBlocks.FLAME_ALTAR_BRACE.get().defaultBlockState()
                .setValue(FlameAltarBraceBlock.FACING, Direction.SOUTH));
        helper.setBlock(center.offset(1, 0, 0), ModBlocks.FLAME_ALTAR_BRACE.get().defaultBlockState()
                .setValue(FlameAltarBraceBlock.FACING, Direction.WEST));
        helper.setBlock(center.offset(0, 0, 1), ModBlocks.FLAME_ALTAR_BRACE.get().defaultBlockState()
                .setValue(FlameAltarBraceBlock.FACING, Direction.NORTH));
        helper.setBlock(center.offset(-1, 0, 0), ModBlocks.FLAME_ALTAR_BRACE.get().defaultBlockState()
                .setValue(FlameAltarBraceBlock.FACING, Direction.EAST));
        helper.setBlock(center.offset(-1, 0, -1), ModBlocks.FLAME_ALTAR_RUNE.get());
        helper.setBlock(center.offset(1, 0, -1), ModBlocks.FLAME_ALTAR_RUNE.get());
        helper.setBlock(center.offset(-1, 0, 1), ModBlocks.FLAME_ALTAR_RUNE.get());
        helper.setBlock(center.offset(1, 0, 1), ModBlocks.FLAME_ALTAR_RUNE.get());
    }

    private static void assertShellFormed(GameTestHelper helper, BlockPos center, boolean expected) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                BlockState state = helper.getBlockState(center.offset(dx, 0, dz));
                if (state.is(ModBlocks.FLAME_ALTAR_BRACE.get())) {
                    helper.assertTrue(state.getValue(FlameAltarBraceBlock.FORMED) == expected,
                            "Every brace must mirror the authoritative controller FORMED presentation state");
                } else if (state.is(ModBlocks.FLAME_ALTAR_RUNE.get())) {
                    helper.assertTrue(state.getValue(FlameAltarRuneBlock.FORMED) == expected,
                            "Every rune must mirror the authoritative controller FORMED presentation state");
                } else {
                    helper.fail("Canonical shell contains an unexpected component at " + center.offset(dx, 0, dz));
                }
            }
        }
    }

    private static void assertExistingShellComponentsUnformed(GameTestHelper helper, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                BlockState state = helper.getBlockState(center.offset(dx, 0, dz));
                if (state.is(ModBlocks.FLAME_ALTAR_BRACE.get())) {
                    helper.assertTrue(!state.getValue(FlameAltarBraceBlock.FORMED),
                            "Failed validation must not partially form a brace");
                } else if (state.is(ModBlocks.FLAME_ALTAR_RUNE.get())) {
                    helper.assertTrue(!state.getValue(FlameAltarRuneBlock.FORMED),
                            "Failed validation must not partially form a rune");
                }
            }
        }
    }

    @SuppressWarnings("removal")
    private static ServerPlayer requireServerPlayer(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        helper.assertTrue(player != null, "Formation interaction GameTest requires a server-side mock player");
        return player;
    }

    private static FlameAltarBlockEntity requireAltar(GameTestHelper helper, BlockPos relative) {
        var blockEntity = GameTestBootstrap.requireServerLevel(helper).getBlockEntity(helper.absolutePos(relative));
        helper.assertTrue(blockEntity instanceof FlameAltarBlockEntity,
                "Placed enshrouded:flame_altar must create FlameAltarBlockEntity");
        return (FlameAltarBlockEntity) blockEntity;
    }
}
