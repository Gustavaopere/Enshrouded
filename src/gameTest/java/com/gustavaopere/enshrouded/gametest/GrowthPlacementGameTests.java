package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.protection.MutationSafetyMode;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionRuleRegistry;
import com.gustavaopere.enshrouded.shroud.terrain.GrowthPlacementService;
import com.gustavaopere.enshrouded.shroud.terrain.ShroudMaterializationService;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GrowthPlacementGameTests {
    private GrowthPlacementGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void growthPlacementRequiresExposedAuthorizedLogicalShroud(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos supportRelative = new BlockPos(1, 0, 1);
        BlockPos supportPos = helper.absolutePos(supportRelative);
        BlockPos targetRelative = supportRelative.above();
        BlockPos targetPos = helper.absolutePos(targetRelative);
        helper.setBlock(supportRelative, Blocks.STONE);
        helper.setBlock(targetRelative, Blocks.AIR);

        AtomicInteger authorityCalls = new AtomicInteger();
        GrowthPlacementService allowed = new GrowthPlacementService(
                (candidateLevel, candidatePos, kind) -> {
                    authorityCalls.incrementAndGet();
                    return true;
                },
                (candidateLevel, candidatePos, player) -> shroud(1.0F, ShroudSeverity.SHROUD)
        );

        helper.assertTrue(
                allowed.tryPlaceOnTop(
                        level,
                        supportPos,
                        ModBlocks.SHROUD_GROWTH.get(),
                        ShroudSeverity.SHROUD,
                        1.0F,
                        17L
                ),
                "Authorized exposed surface inside logical Shroud must accept a common growth"
        );
        helper.assertBlockPresent(ModBlocks.SHROUD_GROWTH.get(), targetRelative);
        helper.assertTrue(authorityCalls.get() == 2, "Growth placement must authorize both support corruption and target placement");

        BlockPos deniedSupportRelative = new BlockPos(3, 0, 1);
        BlockPos deniedSupportPos = helper.absolutePos(deniedSupportRelative);
        helper.setBlock(deniedSupportRelative, Blocks.STONE);
        helper.setBlock(deniedSupportRelative.above(), Blocks.AIR);
        GrowthPlacementService denied = new GrowthPlacementService(
                (candidateLevel, candidatePos, kind) -> false,
                (candidateLevel, candidatePos, player) -> shroud(1.0F, ShroudSeverity.SHROUD)
        );
        helper.assertTrue(
                !denied.tryPlaceOnTop(
                        level,
                        deniedSupportPos,
                        ModBlocks.SHROUD_GROWTH.get(),
                        ShroudSeverity.SHROUD,
                        1.0F,
                        17L
                ),
                "MutationAuthority denial must veto growth placement"
        );
        helper.assertTrue(
                !level.getBlockState(helper.absolutePos(deniedSupportRelative.above())).is(ModBlocks.SHROUD_GROWTH.get()),
                "Denied target must remain free of the requested growth"
        );

        int authorityCallsBeforeInvalidFace = authorityCalls.get();
        helper.setBlock(deniedSupportRelative, Blocks.AIR);
        helper.assertTrue(
                !allowed.tryPlaceOnTop(
                        level,
                        deniedSupportPos,
                        ModBlocks.SHROUD_GROWTH.get(),
                        ShroudSeverity.SHROUD,
                        1.0F,
                        17L
                ),
                "Growth placement must reject a support without a sturdy exposed top face"
        );
        helper.assertTrue(
                authorityCalls.get() == authorityCallsBeforeInvalidFace,
                "Invalid surface geometry must be rejected before invoking mutation authority"
        );

        BlockPos clearSupportRelative = new BlockPos(5, 0, 1);
        BlockPos clearSupportPos = helper.absolutePos(clearSupportRelative);
        helper.setBlock(clearSupportRelative, Blocks.STONE);
        helper.setBlock(clearSupportRelative.above(), Blocks.AIR);
        GrowthPlacementService clear = new GrowthPlacementService(
                (candidateLevel, candidatePos, kind) -> true,
                (candidateLevel, candidatePos, player) -> ShroudSample.clear()
        );
        helper.assertTrue(
                !clear.tryPlaceOnTop(
                        level,
                        clearSupportPos,
                        ModBlocks.SHROUD_GROWTH.get(),
                        ShroudSeverity.SHROUD,
                        1.0F,
                        17L
                ),
                "A world position outside the logical Shroud must never gain a growth"
        );
        helper.assertTrue(
                !level.getBlockState(helper.absolutePos(clearSupportRelative.above())).is(ModBlocks.SHROUD_GROWTH.get()),
                "Clear logical cells must remain free of Shroud growths"
        );
        helper.assertTrue(level.getBlockState(targetPos).is(ModBlocks.SHROUD_GROWTH.get()), "Placed growth must remain in-world for the test duration");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void deadlyGrowthRequiresDeadlyLogicalSeverity(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos ordinaryRelative = new BlockPos(1, 0, 3);
        BlockPos ordinaryPos = helper.absolutePos(ordinaryRelative);
        helper.setBlock(ordinaryRelative, Blocks.STONE);
        helper.setBlock(ordinaryRelative.above(), Blocks.AIR);

        GrowthPlacementService ordinary = new GrowthPlacementService(
                (candidateLevel, candidatePos, kind) -> true,
                (candidateLevel, candidatePos, player) -> shroud(1.0F, ShroudSeverity.SHROUD)
        );
        helper.assertTrue(
                !ordinary.tryPlaceOnTop(
                        level,
                        ordinaryPos,
                        ModBlocks.WITHERED_GROWTH.get(),
                        ShroudSeverity.DEADLY,
                        1.0F,
                        31L
                ),
                "Deadly/Red growth must not materialize in ordinary Shroud severity"
        );
        helper.assertTrue(
                !level.getBlockState(helper.absolutePos(ordinaryRelative.above())).is(ModBlocks.WITHERED_GROWTH.get()),
                "Ordinary Shroud must remain free of the deadly visual family"
        );

        BlockPos deadlyRelative = new BlockPos(3, 0, 3);
        BlockPos deadlyPos = helper.absolutePos(deadlyRelative);
        helper.setBlock(deadlyRelative, Blocks.STONE);
        helper.setBlock(deadlyRelative.above(), Blocks.AIR);
        GrowthPlacementService deadly = new GrowthPlacementService(
                (candidateLevel, candidatePos, kind) -> true,
                (candidateLevel, candidatePos, player) -> shroud(1.0F, ShroudSeverity.DEADLY)
        );
        helper.assertTrue(
                deadly.tryPlaceOnTop(
                        level,
                        deadlyPos,
                        ModBlocks.WITHERED_GROWTH.get(),
                        ShroudSeverity.DEADLY,
                        1.0F,
                        31L
                ),
                "Deadly logical severity must permit the deadly visual family when authorized"
        );
        helper.assertBlockPresent(ModBlocks.WITHERED_GROWTH.get(), deadlyRelative.above());
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void materializationBudgetsGrowthAttemptsPerChunk(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ShroudMaterializationService materialization = new ShroudMaterializationService(
                new CorruptionRuleRegistry(List.of()),
                (candidateLevel, candidatePos, kind) -> true,
                (candidateLevel, candidatePos, player) -> shroud(1.0F, ShroudSeverity.SHROUD),
                MutationSafetyMode.SAFE,
                8
        );

        // Keep all support positions in the exact same chunk regardless of the randomized
        // absolute GameTest template origin. Varying X here made this test flaky whenever
        // the template landed near a chunk boundary.
        BlockPos firstRelative = new BlockPos(1, 0, 5);
        BlockPos secondRelative = new BlockPos(1, 2, 5);
        BlockPos thirdRelative = new BlockPos(1, 4, 5);
        for (BlockPos relative : List.of(firstRelative, secondRelative, thirdRelative)) {
            helper.setBlock(relative, Blocks.STONE);
            helper.setBlock(relative.above(), Blocks.AIR);
            helper.assertTrue(
                    materialization.scheduleGrowth(
                            level,
                            helper.absolutePos(relative),
                            ModBlocks.SHROUD_VEIN.get(),
                            ShroudSeverity.SHROUD,
                            1.0F,
                            47L
                    ),
                    "Loaded unique support positions must enter the bounded growth queue"
            );
        }

        helper.assertTrue(materialization.pendingGrowthWork() == 3, "Three growth jobs must be queued");
        int firstPlacements = materialization.tickGrowths(level, 3, 1);
        helper.assertTrue(firstPlacements <= 1, "A one-attempt per-chunk budget cannot produce more than one placement");
        helper.assertTrue(materialization.pendingGrowthWork() == 2, "Exactly one growth job must be attempted on the first bounded tick");
        int secondPlacements = materialization.tickGrowths(level, 3, 1);
        helper.assertTrue(secondPlacements <= 1, "The per-chunk budget must remain bounded on the next tick");
        helper.assertTrue(materialization.pendingGrowthWork() == 1, "Exactly one growth job must be attempted on each bounded tick");
        helper.succeed();
    }

    private static ShroudSample shroud(float intensity, ShroudSeverity severity) {
        return new ShroudSample(intensity, severity, Optional.empty(), false);
    }
}
