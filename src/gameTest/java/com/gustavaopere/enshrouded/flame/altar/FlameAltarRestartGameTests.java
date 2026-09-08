package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.FlameWardRuntimeBindings;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import com.gustavaopere.enshrouded.protection.ProtectionRuntimeBindings;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Real two-boot persistence sentinel for the player-built Flame Altar complex.
 *
 * <p>The sentinel lives outside the disposable GameTest template so the external reload harness can
 * restart the same world. The first boot forms the canonical 3x3 structure through the real
 * controller boundary; the second boot proves block + BlockEntity NBT + bounded post-load recovery
 * + Sanctuary restoration without a second SavedData authority.</p>
 */
@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FlameAltarRestartGameTests {
    private static final BlockPos SENTINEL_CENTER = new BlockPos(392, 96, 392);
    private static final int OFFERING_COUNT = 3;
    private static final int SENTINEL_TICKET_DISTANCE = 3;

    private FlameAltarRestartGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void formedFlameAltarSurvivesRealServerRestart(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ChunkPos sentinelChunk = new ChunkPos(SENTINEL_CENTER);

        // The sentinel intentionally lives far outside the disposable GameTest template. Keep only
        // that chunk alive while this test observes the normal restart lifecycle; otherwise the
        // GameTest runner may evict it between the deferred recovery tick and the assertion, causing
        // requireAltar() to lazily materialize the same persisted NBT a second time. This ticket is
        // test-only, is always removed below, and does not change the production no-forced-chunks
        // contract of FlameAltarChunkRecoveryEvents.
        level.getChunkSource().addRegionTicket(
                TicketType.FORCED,
                sentinelChunk,
                SENTINEL_TICKET_DISTANCE,
                sentinelChunk
        );
        level.getChunkAt(SENTINEL_CENTER);

        if (!level.getBlockState(SENTINEL_CENTER).is(ModBlocks.FLAME_ALTAR.get())) {
            try {
                createFirstBootSentinel(helper, level);
            } finally {
                level.getChunkSource().removeRegionTicket(
                        TicketType.FORCED,
                        sentinelChunk,
                        SENTINEL_TICKET_DISTANCE,
                        sentinelChunk
                );
            }
            return;
        }

        // NeoForge invokes BlockEntity#onLoad before the first block-entity tick. Recovery itself is
        // deferred to the bounded post-server-tick path; the test ticket merely prevents an unrelated
        // eviction from resetting the observation window while that real path settles.
        helper.startSequence()
                .thenExecuteAfter(10, () -> {
                    try {
                        assertReloadedSentinel(helper, level);
                    } finally {
                        level.getChunkSource().removeRegionTicket(
                                TicketType.FORCED,
                                sentinelChunk,
                                SENTINEL_TICKET_DISTANCE,
                                sentinelChunk
                        );
                    }
                })
                .thenSucceed();
    }

    private static void createFirstBootSentinel(GameTestHelper helper, ServerLevel level) {
        placeCanonicalShell(level, SENTINEL_CENTER);
        FlameAltarBlockEntity altar = requireAltar(helper, level, SENTINEL_CENTER);
        altar.inventory().setStackInSlot(0, new ItemStack(Items.DIRT, OFFERING_COUNT));

        FlameAltarStructureValidator.Result result = altar.requestFormation(level);
        helper.assertTrue(result.status() == FlameAltarStructureValidator.Status.VALID,
                "First boot must form the exact canonical 3x3 Flame Altar sentinel");
        helper.assertTrue(altar.isFormed(),
                "First boot Flame Altar sentinel must become FORMED before persistence");
        assertShellPresentation(helper, level, true);
        helper.assertTrue(FlameWardRuntimeBindings.query().suppresses(level, SENTINEL_CENTER),
                "First boot FORMED sentinel must activate the canonical Sanctuary provider");

        GameTestBootstrap.forceSaveForReload(helper);
        System.out.println("ENSHROUDED_FLAME_ALTAR_FORMED_CREATED");
        helper.succeed();
    }

    private static void assertReloadedSentinel(GameTestHelper helper, ServerLevel level) {
        FlameAltarBlockEntity altar = requireAltar(helper, level, SENTINEL_CENTER);
        FlameAltarStructureValidator.Result diagnosticValidation = new FlameAltarStructureValidator(
                ProtectionRuntimeBindings.protectedAreas()
        ).validate(level, SENTINEL_CENTER);
        CompoundTag diagnosticPersisted = altar.saveWithoutMetadata(level.registryAccess());
        CompoundTag diagnosticFormation = diagnosticPersisted.getCompound("Formation");

        helper.assertTrue(altar.isFormed(),
                "Second boot must recover persisted FORMED state through the bounded post-load retry path"
                        + " [pending=" + altar.hasPendingFormationRecovery()
                        + ", phase=" + altar.formationPhase()
                        + ", validator=" + diagnosticValidation.status()
                        + ", problemPos=" + diagnosticValidation.problemPos()
                        + ", persistedSchema=" + diagnosticFormation.getInt("SchemaVersion")
                        + ", persistedFormed=" + diagnosticFormation.getBoolean("Formed")
                        + "]");
        helper.assertTrue(altar.formationPhase() == FlameAltarFormationPhase.FORMED,
                "Second boot recovery must settle in FORMED rather than transient VALIDATING/UNFORMED");
        helper.assertTrue(altar.inventory().getStackInSlot(0).is(Items.DIRT)
                        && altar.inventory().getStackInSlot(0).getCount() == OFFERING_COUNT,
                "Real restart must preserve the ritual inventory without consumption or duplication");
        assertShellPresentation(helper, level, true);
        helper.assertTrue(FlameWardRuntimeBindings.query().suppresses(level, SENTINEL_CENTER),
                "Second boot recovered altar must restore the canonical Sanctuary provider");

        CompoundTag persisted = altar.saveWithoutMetadata(level.registryAccess());
        CompoundTag formation = persisted.getCompound("Formation");
        helper.assertTrue(formation.getInt("SchemaVersion") == FlameAltarFormationState.CURRENT_SCHEMA_VERSION,
                "Restarted Flame Altar must retain the current formation schema version");
        helper.assertTrue(formation.getBoolean("Formed"),
                "Restarted Flame Altar must persist recovered FORMED state for subsequent restarts");

        System.out.println("ENSHROUDED_FLAME_ALTAR_FORMED_RELOADED");
    }

    private static void placeCanonicalShell(ServerLevel level, BlockPos center) {
        level.setBlock(center, ModBlocks.FLAME_ALTAR.get().defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(center.offset(0, 0, -1), ModBlocks.FLAME_ALTAR_BRACE.get().defaultBlockState()
                .setValue(FlameAltarBraceBlock.FACING, Direction.SOUTH), Block.UPDATE_ALL);
        level.setBlock(center.offset(1, 0, 0), ModBlocks.FLAME_ALTAR_BRACE.get().defaultBlockState()
                .setValue(FlameAltarBraceBlock.FACING, Direction.WEST), Block.UPDATE_ALL);
        level.setBlock(center.offset(0, 0, 1), ModBlocks.FLAME_ALTAR_BRACE.get().defaultBlockState()
                .setValue(FlameAltarBraceBlock.FACING, Direction.NORTH), Block.UPDATE_ALL);
        level.setBlock(center.offset(-1, 0, 0), ModBlocks.FLAME_ALTAR_BRACE.get().defaultBlockState()
                .setValue(FlameAltarBraceBlock.FACING, Direction.EAST), Block.UPDATE_ALL);
        level.setBlock(center.offset(-1, 0, -1), ModBlocks.FLAME_ALTAR_RUNE.get().defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(center.offset(1, 0, -1), ModBlocks.FLAME_ALTAR_RUNE.get().defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(center.offset(-1, 0, 1), ModBlocks.FLAME_ALTAR_RUNE.get().defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(center.offset(1, 0, 1), ModBlocks.FLAME_ALTAR_RUNE.get().defaultBlockState(), Block.UPDATE_ALL);
    }

    private static void assertShellPresentation(GameTestHelper helper, ServerLevel level, boolean expected) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                BlockState state = level.getBlockState(SENTINEL_CENTER.offset(dx, 0, dz));
                if (state.is(ModBlocks.FLAME_ALTAR_BRACE.get())) {
                    helper.assertTrue(state.getValue(FlameAltarBraceBlock.FORMED) == expected,
                            "Every persisted Flame Altar brace must mirror controller formation presentation");
                } else if (state.is(ModBlocks.FLAME_ALTAR_RUNE.get())) {
                    helper.assertTrue(state.getValue(FlameAltarRuneBlock.FORMED) == expected,
                            "Every persisted Flame Altar rune must mirror controller formation presentation");
                } else {
                    helper.fail("Persisted Flame Altar shell lost a required component at "
                            + SENTINEL_CENTER.offset(dx, 0, dz));
                }
            }
        }
    }

    private static FlameAltarBlockEntity requireAltar(
            GameTestHelper helper,
            ServerLevel level,
            BlockPos pos) {
        var blockEntity = level.getBlockEntity(pos);
        helper.assertTrue(blockEntity instanceof FlameAltarBlockEntity,
                "Persistent Flame Altar sentinel must retain its controller BlockEntity");
        return (FlameAltarBlockEntity) blockEntity;
    }
}
