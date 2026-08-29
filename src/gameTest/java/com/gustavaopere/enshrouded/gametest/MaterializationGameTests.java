package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.protection.MutationSafetyMode;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionRule;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionRuleRegistry;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionSafetyClass;
import com.gustavaopere.enshrouded.shroud.terrain.ShroudMaterializationService;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class MaterializationGameTests {
    private static final ResourceLocation SAFE_TAG = id("corruptible_safe");

    private MaterializationGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void loadedPatchMaterializesGraduallyWithinBudget(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos firstRelative = new BlockPos(0, 0, 1);
        BlockPos secondRelative = new BlockPos(1, 0, 1);
        helper.setBlock(firstRelative, Blocks.STONE);
        helper.setBlock(secondRelative, Blocks.STONE);

        ShroudSample sample = activeSample();
        AtomicInteger authorityCalls = new AtomicInteger();
        ShroudMaterializationService service = new ShroudMaterializationService(
                new CorruptionRuleRegistry(List.of(stoneToDeepslateRule())),
                (candidateLevel, pos, kind) -> {
                    helper.assertTrue(candidateLevel == level, "authority must receive the GameTest level");
                    helper.assertTrue(kind == MutationKind.CORRUPTION, "materialization must use CORRUPTION authority");
                    authorityCalls.incrementAndGet();
                    return true;
                },
                (candidateLevel, pos, entity) -> sample,
                MutationSafetyMode.SAFE,
                8
        );

        helper.assertTrue(service.schedule(level, helper.absolutePos(firstRelative), sample), "first stone should queue");
        helper.assertTrue(service.schedule(level, helper.absolutePos(secondRelative), sample), "second stone should queue");
        helper.assertValueEqual(service.pendingWork(), 2, "pending materialization work");

        helper.assertValueEqual(service.tick(level, 1, 1), 1, "first budgeted mutation count");
        helper.assertBlockPresent(Blocks.DEEPSLATE, firstRelative);
        helper.assertBlockPresent(Blocks.STONE, secondRelative);
        helper.assertValueEqual(service.pendingWork(), 1, "deferred materialization work");

        helper.assertValueEqual(service.tick(level, 1, 1), 1, "second budgeted mutation count");
        helper.assertBlockPresent(Blocks.DEEPSLATE, secondRelative);
        helper.assertValueEqual(authorityCalls.get(), 2, "authority calls");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void deniedAndUnknownPositionsRemainUntouched(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos protectedRelative = new BlockPos(0, 0, 1);
        BlockPos unknownRelative = new BlockPos(1, 0, 1);
        helper.setBlock(protectedRelative, Blocks.STONE);
        helper.setBlock(unknownRelative, Blocks.OBSIDIAN);

        ShroudSample sample = activeSample();
        AtomicInteger authorityCalls = new AtomicInteger();
        ShroudMaterializationService service = new ShroudMaterializationService(
                new CorruptionRuleRegistry(List.of(stoneToDeepslateRule())),
                (candidateLevel, pos, kind) -> {
                    authorityCalls.incrementAndGet();
                    return false;
                },
                (candidateLevel, pos, entity) -> sample,
                MutationSafetyMode.SAFE,
                8
        );

        helper.assertTrue(service.schedule(level, helper.absolutePos(protectedRelative), sample), "safe stone should queue before authority decision");
        helper.assertFalse(service.schedule(level, helper.absolutePos(unknownRelative), sample), "unknown block must fail closed");
        helper.assertValueEqual(service.tick(level, 8, 8), 0, "denied mutation count");
        helper.assertBlockPresent(Blocks.STONE, protectedRelative);
        helper.assertBlockPresent(Blocks.OBSIDIAN, unknownRelative);
        helper.assertValueEqual(authorityCalls.get(), 1, "authority calls");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void unloadedPositionIsNotForcedIntoMemory(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos farAway = new BlockPos(1_000_000, 64, 1_000_000);
        helper.assertFalse(level.hasChunkAt(farAway), "far-away chunk must begin unloaded");

        ShroudSample sample = activeSample();
        ShroudMaterializationService service = new ShroudMaterializationService(
                new CorruptionRuleRegistry(List.of(stoneToDeepslateRule())),
                (candidateLevel, pos, kind) -> true,
                (candidateLevel, pos, entity) -> sample,
                MutationSafetyMode.SAFE,
                8
        );

        helper.assertFalse(service.schedule(level, farAway, sample), "unloaded positions must not schedule");
        helper.assertFalse(level.hasChunkAt(farAway), "materialization must not force-load the chunk");
        helper.assertValueEqual(service.pendingWork(), 0, "unloaded work queue size");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void staleQueuedWorkCannotOutliveLogicalShroud(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos relative = new BlockPos(0, 0, 1);
        BlockPos absolute = helper.absolutePos(relative);
        helper.setBlock(relative, Blocks.STONE);

        AtomicReference<ShroudSample> currentSample = new AtomicReference<>(activeSample());
        AtomicInteger authorityCalls = new AtomicInteger();
        ShroudMaterializationService service = new ShroudMaterializationService(
                new CorruptionRuleRegistry(List.of(stoneToDeepslateRule())),
                (candidateLevel, pos, kind) -> {
                    authorityCalls.incrementAndGet();
                    return true;
                },
                (candidateLevel, pos, entity) -> currentSample.get(),
                MutationSafetyMode.SAFE,
                8
        );

        helper.assertTrue(service.schedule(level, absolute, currentSample.get()), "active Shroud should queue stone");
        currentSample.set(new ShroudSample(0.0F, ShroudSeverity.CLEAR, Optional.empty(), false));

        helper.assertValueEqual(service.tick(level, 8, 8), 0, "stale job mutation count");
        helper.assertBlockPresent(Blocks.STONE, relative);
        helper.assertValueEqual(authorityCalls.get(), 0, "stale logical Shroud must fail before MutationAuthority");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void aggressiveRuleCannotRunInSafeMode(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos relative = new BlockPos(0, 0, 1);
        BlockPos absolute = helper.absolutePos(relative);
        helper.setBlock(relative, Blocks.STONE);

        ShroudSample sample = activeSample();
        ShroudMaterializationService service = new ShroudMaterializationService(
                new CorruptionRuleRegistry(List.of(aggressiveStoneToDeepslateRule())),
                (candidateLevel, pos, kind) -> true,
                (candidateLevel, pos, entity) -> sample,
                MutationSafetyMode.SAFE,
                8
        );

        helper.assertFalse(service.schedule(level, absolute, sample), "AGGRESSIVE rule must not queue in SAFE mode");
        helper.assertValueEqual(service.pendingWork(), 0, "disabled aggressive rule queue size");
        helper.assertBlockPresent(Blocks.STONE, relative);
        helper.succeed();
    }

    private static ShroudSample activeSample() {
        return new ShroudSample(1.0F, ShroudSeverity.SHROUD, Optional.empty(), false);
    }

    private static CorruptionRule stoneToDeepslateRule() {
        return new CorruptionRule(
                id("test_stone_to_deepslate"),
                SAFE_TAG,
                ResourceLocation.withDefaultNamespace("deepslate"),
                ResourceLocation.withDefaultNamespace("stone"),
                0.25F,
                CorruptionSafetyClass.SAFE
        );
    }

    private static CorruptionRule aggressiveStoneToDeepslateRule() {
        return new CorruptionRule(
                id("test_aggressive_stone_to_deepslate"),
                SAFE_TAG,
                ResourceLocation.withDefaultNamespace("deepslate"),
                ResourceLocation.withDefaultNamespace("stone"),
                0.25F,
                CorruptionSafetyClass.AGGRESSIVE
        );
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, path);
    }
}
