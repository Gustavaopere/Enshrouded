package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.FlameWardRuntimeBindings;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarBlockEntity;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarBraceBlock;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarFormationPhase;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarRuneBlock;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FlameAltarUnformGameTests {
    private static final String BATCH = "flameAltarUnform";

    private FlameAltarUnformGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void breakingRequiredSatelliteUnformsThenExplicitInteractionReformsWithoutConsumingOffering(
            GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ServerPlayer player = requireServerPlayer(helper);
        BlockPos centerRelative = new BlockPos(3, 1, 3);
        BlockPos center = helper.absolutePos(centerRelative);
        BlockPos southeastRelative = centerRelative.offset(1, 0, 1);

        placeCanonicalShell(helper, centerRelative);
        FlameAltarBlockEntity altar = requireAltar(helper, centerRelative);
        altar.inventory().setStackInSlot(0, new ItemStack(Items.DIRT, 3));

        helper.useBlock(centerRelative, player);
        helper.assertTrue(altar.isFormed(), "Precondition: canonical shell must form before break test");
        helper.assertTrue(FlameWardRuntimeBindings.query().suppresses(level, center),
                "Precondition: formed altar must own active Sanctuary");

        helper.destroyBlock(southeastRelative);

        helper.assertTrue(!altar.isFormed(),
                "Breaking any required satellite must deterministically revoke controller FORMED state");
        helper.assertTrue(altar.formationPhase() == FlameAltarFormationPhase.UNFORMED,
                "Required-part break must return the controller runtime phase to UNFORMED");
        helper.assertTrue(!FlameWardRuntimeBindings.query().suppresses(level, center),
                "UNFORM caused by structural break must deactivate the canonical Sanctuary provider");
        assertExistingShellPresentation(helper, centerRelative, false);
        helper.assertTrue(altar.inventory().getStackInSlot(0).getCount() == 3,
                "Structural unform must not consume the ritual offering");

        helper.setBlock(southeastRelative, ModBlocks.FLAME_ALTAR_RUNE.get());
        helper.assertTrue(!altar.isFormed(),
                "Replacing a missing satellite must not passively reform without explicit controller interaction");
        helper.assertTrue(!FlameWardRuntimeBindings.query().suppresses(level, center),
                "Replacing the part alone must not reactivate Sanctuary");

        helper.useBlock(centerRelative, player);
        helper.assertTrue(altar.isFormed(),
                "A repaired canonical shell must reform on a new explicit controller interaction");
        assertExistingShellPresentation(helper, centerRelative, true);
        helper.assertTrue(FlameWardRuntimeBindings.query().suppresses(level, center),
                "Explicit reformation must reactivate the same canonical Sanctuary provider");
        helper.assertTrue(altar.inventory().getStackInSlot(0).getCount() == 3,
                "Form-unform-reform lifecycle must not consume or duplicate the ritual offering");

        helper.destroyBlock(centerRelative);
        helper.succeed();
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void breakingControllerClearsSatellitePresentationAndSanctuary(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ServerPlayer player = requireServerPlayer(helper);
        BlockPos centerRelative = new BlockPos(3, 1, 3);
        BlockPos center = helper.absolutePos(centerRelative);

        placeCanonicalShell(helper, centerRelative);
        FlameAltarBlockEntity altar = requireAltar(helper, centerRelative);
        helper.useBlock(centerRelative, player);
        helper.assertTrue(altar.isFormed(), "Precondition: controller must be FORMED before removal");
        assertExistingShellPresentation(helper, centerRelative, true);
        helper.assertTrue(FlameWardRuntimeBindings.query().suppresses(level, center),
                "Precondition: formed controller must activate Sanctuary");

        helper.destroyBlock(centerRelative);

        helper.assertTrue(level.getBlockEntity(center) == null,
                "Breaking the controller must remove its authoritative BlockEntity");
        assertExistingShellPresentation(helper, centerRelative, false);
        helper.assertTrue(!FlameWardRuntimeBindings.query().suppresses(level, center),
                "Breaking the controller must deactivate Sanctuary and leave no orphan authority");
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

    private static void assertExistingShellPresentation(GameTestHelper helper, BlockPos center, boolean expected) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                BlockState state = helper.getBlockState(center.offset(dx, 0, dz));
                if (state.is(ModBlocks.FLAME_ALTAR_BRACE.get())) {
                    helper.assertTrue(state.getValue(FlameAltarBraceBlock.FORMED) == expected,
                            "Every surviving brace must mirror controller formation presentation");
                } else if (state.is(ModBlocks.FLAME_ALTAR_RUNE.get())) {
                    helper.assertTrue(state.getValue(FlameAltarRuneBlock.FORMED) == expected,
                            "Every surviving rune must mirror controller formation presentation");
                }
            }
        }
    }

    @SuppressWarnings("removal")
    private static ServerPlayer requireServerPlayer(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        helper.assertTrue(player != null, "Flame Altar unform GameTest requires a server-side mock player");
        return player;
    }

    private static FlameAltarBlockEntity requireAltar(GameTestHelper helper, BlockPos relative) {
        var blockEntity = GameTestBootstrap.requireServerLevel(helper).getBlockEntity(helper.absolutePos(relative));
        helper.assertTrue(blockEntity instanceof FlameAltarBlockEntity,
                "Placed enshrouded:flame_altar must create FlameAltarBlockEntity");
        return (FlameAltarBlockEntity) blockEntity;
    }
}
