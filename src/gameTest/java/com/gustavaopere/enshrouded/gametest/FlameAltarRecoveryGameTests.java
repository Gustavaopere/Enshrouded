package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.FlameWardRuntimeBindings;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarBlockEntity;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarBraceBlock;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarFormationPhase;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarFormationState;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarRecoveryGameTestAccess;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarRuneBlock;
import com.gustavaopere.enshrouded.flame.ward.FlameWardGameTestAccess;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FlameAltarRecoveryGameTests {
    private static final String BATCH = "flameAltarRecovery";

    private FlameAltarRecoveryGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void validPersistedFormedIntentRecoversAfterIndexedChunkLoadAndRestoresSanctuary(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos centerRelative = new BlockPos(3, 1, 3);
        BlockPos center = helper.absolutePos(centerRelative);
        ChunkPos recoveryChunk = new ChunkPos(center);

        FlameWardGameTestAccess.clear();
        try {
            placeCanonicalShell(helper, centerRelative);
            FlameAltarBlockEntity altar = requireAltar(helper, centerRelative);
            altar.inventory().setStackInSlot(0, new ItemStack(Items.DIRT, 3));
            loadPersistedFormedIntent(level, altar);

            helper.assertTrue(!altar.isFormed(),
                    "Persisted FORMED intent must stay fail-closed before indexed recovery runs");
            helper.assertTrue(altar.formationPhase() == FlameAltarFormationPhase.UNFORMED,
                    "Reloaded controller must begin runtime recovery in UNFORMED phase");

            FlameAltarRecoveryGameTestAccess.waitForChunk(level, center, recoveryChunk);
            FlameAltarRecoveryGameTestAccess.recoverWaitingForLoadedChunk(level, recoveryChunk);

            helper.assertTrue(altar.isFormed(),
                    "A physically valid persisted altar must recover FORMED at its indexed chunk-load boundary");
            helper.assertTrue(altar.formationPhase() == FlameAltarFormationPhase.FORMED,
                    "Successful recovery must finish in FORMED rather than leave transient VALIDATING state");
            assertShellPresentation(helper, centerRelative, true);
            helper.assertTrue(FlameWardRuntimeBindings.query().suppresses(level, center),
                    "Recovered FORMED altar must restore the canonical Sanctuary provider");
            helper.assertTrue(altar.inventory().getStackInSlot(0).getCount() == 3,
                    "Formation recovery must not consume or duplicate the ritual offering");
            helper.succeed();
        } finally {
            FlameWardGameTestAccess.clear();
        }
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void deterministicInvalidShellRevokesPersistedIntentAtIndexedChunkRecovery(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos centerRelative = new BlockPos(3, 1, 3);
        BlockPos center = helper.absolutePos(centerRelative);
        BlockPos northwestRelative = centerRelative.offset(-1, 0, -1);
        ChunkPos recoveryChunk = new ChunkPos(center);

        FlameWardGameTestAccess.clear();
        try {
            placeCanonicalShell(helper, centerRelative);
            FlameAltarBlockEntity altar = requireAltar(helper, centerRelative);
            helper.setBlock(northwestRelative, Blocks.AIR);
            loadPersistedFormedIntent(level, altar);

            CompoundTag beforeRecovery = altar.saveWithoutMetadata(level.registryAccess());
            helper.assertTrue(beforeRecovery.getCompound("Formation").getBoolean("Formed"),
                    "Persisted intent must remain until indexed recovery evaluates deterministic world evidence");

            FlameAltarRecoveryGameTestAccess.waitForChunk(level, center, recoveryChunk);
            FlameAltarRecoveryGameTestAccess.recoverWaitingForLoadedChunk(level, recoveryChunk);

            helper.assertTrue(!altar.isFormed(),
                    "Persisted FORMED intent must not survive recovery when a required component is physically missing");
            helper.assertTrue(altar.formationPhase() == FlameAltarFormationPhase.UNFORMED,
                    "Deterministic invalid recovery must settle in UNFORMED");
            helper.assertTrue(!FlameWardRuntimeBindings.query().suppresses(level, center),
                    "Invalid recovered shell must never activate Sanctuary");
            assertShellPresentation(helper, centerRelative, false);

            CompoundTag reserialized = altar.saveWithoutMetadata(level.registryAccess());
            CompoundTag formation = reserialized.getCompound("Formation");
            helper.assertTrue(!formation.getBoolean("Formed"),
                    "A deterministic structural invalidation must revoke persisted FORMED recovery intent");
            helper.succeed();
        } finally {
            FlameWardGameTestAccess.clear();
        }
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void unrelatedChunkLoadDoesNotWakeIndexedPendingAltar(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos centerRelative = new BlockPos(3, 1, 3);
        BlockPos center = helper.absolutePos(centerRelative);
        ChunkPos subscribedChunk = new ChunkPos(center);
        ChunkPos unrelatedChunk = new ChunkPos(subscribedChunk.x + 2, subscribedChunk.z + 2);

        FlameWardGameTestAccess.clear();
        try {
            placeCanonicalShell(helper, centerRelative);
            FlameAltarBlockEntity altar = requireAltar(helper, centerRelative);
            loadPersistedFormedIntent(level, altar);
            FlameAltarRecoveryGameTestAccess.waitForChunk(level, center, subscribedChunk);

            FlameAltarRecoveryGameTestAccess.recoverWaitingForLoadedChunk(level, unrelatedChunk);
            helper.assertTrue(!altar.isFormed(),
                    "An unrelated chunk load must not wake or validate an indexed pending Flame Altar");
            helper.assertTrue(!FlameWardRuntimeBindings.query().suppresses(level, center),
                    "An unrelated chunk load must not activate Sanctuary");

            FlameAltarRecoveryGameTestAccess.recoverWaitingForLoadedChunk(level, subscribedChunk);
            helper.assertTrue(altar.isFormed(),
                    "Only the exact subscribed chunk may wake the pending Flame Altar recovery");
            helper.assertTrue(FlameWardRuntimeBindings.query().suppresses(level, center),
                    "The indexed recovery wakeup must restore Sanctuary after successful validation");
            helper.succeed();
        } finally {
            FlameWardGameTestAccess.clear();
        }
    }

    private static void loadPersistedFormedIntent(ServerLevel level, FlameAltarBlockEntity altar) {
        CompoundTag persisted = altar.saveWithoutMetadata(level.registryAccess());
        CompoundTag formation = new CompoundTag();
        formation.putInt("SchemaVersion", FlameAltarFormationState.CURRENT_SCHEMA_VERSION);
        formation.putBoolean("Formed", true);
        persisted.put("Formation", formation);
        altar.loadWithComponents(persisted, level.registryAccess());
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

    private static void assertShellPresentation(GameTestHelper helper, BlockPos center, boolean expected) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                BlockState state = helper.getBlockState(center.offset(dx, 0, dz));
                if (state.is(ModBlocks.FLAME_ALTAR_BRACE.get())) {
                    helper.assertTrue(state.getValue(FlameAltarBraceBlock.FORMED) == expected,
                            "Every surviving brace must mirror recovered controller formation presentation");
                } else if (state.is(ModBlocks.FLAME_ALTAR_RUNE.get())) {
                    helper.assertTrue(state.getValue(FlameAltarRuneBlock.FORMED) == expected,
                            "Every surviving rune must mirror recovered controller formation presentation");
                }
            }
        }
    }

    private static FlameAltarBlockEntity requireAltar(GameTestHelper helper, BlockPos relative) {
        var blockEntity = GameTestBootstrap.requireServerLevel(helper).getBlockEntity(helper.absolutePos(relative));
        helper.assertTrue(blockEntity instanceof FlameAltarBlockEntity,
                "Placed enshrouded:flame_altar must create FlameAltarBlockEntity");
        return (FlameAltarBlockEntity) blockEntity;
    }
}
