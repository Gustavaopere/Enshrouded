package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudExpansionScheduler;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudFrontierEntry;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudPropagationPolicy;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudWorkBudget;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSchema;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PerformanceGameTests {
    private static final String BATCH = "performanceStress";
    private static final ShroudGridGeometry GEOMETRY = new ShroudGridGeometry(8);
    private static final int ENTRIES_PER_CORE = 64;
    private static final int GLOBAL_BUDGET = 256;
    private static final int PER_CORE_BUDGET = 8;

    private static final ShroudPropagationPolicy APPLY_NO_NEIGHBORS = new ShroudPropagationPolicy() {
        @Override
        public double intensity(ShroudCoreState core, ShroudGridGeometry geometry, ShroudCellPos candidate) {
            return 1.0D;
        }

        @Override
        public List<ShroudCellPos> neighbors(ShroudCellPos source) {
            return List.of();
        }
    };

    private PerformanceGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH, timeoutTicks = 40)
    public static void boundedSchedulerStressRunsInsideGameTestServer(GameTestHelper helper) {
        GameTestBootstrap.requireServerLevel(helper);

        for (int cores : List.of(1, 10, 50)) {
            ShroudWorldState state = activeFixture(cores);
            ShroudExpansionScheduler scheduler = new ShroudExpansionScheduler(
                    GEOMETRY,
                    APPLY_NO_NEIGHBORS,
                    ENTRIES_PER_CORE + 8);

            for (int coreIndex = 0; coreIndex < cores; coreIndex++) {
                UUID coreId = coreId(coreIndex);
                for (int entry = 0; entry < ENTRIES_PER_CORE; entry++) {
                    boolean accepted = scheduler.enqueue(
                            coreId,
                            new ShroudFrontierEntry(
                                    new ShroudCellPos(coreIndex * 1000 + entry, 0, 0),
                                    0L,
                                    entry));
                    helper.assertTrue(accepted, "stress fixture must enqueue every deterministic frontier entry");
                }
            }

            long started = System.nanoTime();
            ShroudExpansionScheduler.TickResult result = scheduler.tick(
                    state,
                    new ShroudWorkBudget(GLOBAL_BUDGET, PER_CORE_BUDGET));
            long wallNanos = System.nanoTime() - started;
            int expectedProcessed = Math.min(GLOBAL_BUDGET, cores * PER_CORE_BUDGET);
            int maxPerCore = result.processedPerCore().values().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);

            helper.assertTrue(result.processedEntries() == expectedProcessed,
                    "server stress scheduler must consume exactly the bounded deterministic allowance");
            helper.assertTrue(result.processedEntries() <= GLOBAL_BUDGET,
                    "server stress scheduler must never exceed global budget");
            helper.assertTrue(maxPerCore <= PER_CORE_BUDGET,
                    "server stress scheduler must never exceed per-core budget");
            helper.assertTrue(result.appliedCells() <= result.processedEntries(),
                    "applied cells cannot exceed processed frontier entries");

            System.out.printf(Locale.ROOT,
                    "ENSHROUDED_LEVEL1_SERVER_PERF cores=%d processed=%d applied=%d maxPerCore=%d wallNanos=%d wallMs=%.3f%n",
                    cores,
                    result.processedEntries(),
                    result.appliedCells(),
                    maxPerCore,
                    wallNanos,
                    wallNanos / 1_000_000.0D);
        }

        System.out.println("ENSHROUDED_LEVEL1_SERVER_PERF_PASSED");
        helper.succeed();
    }

    private static ShroudWorldState activeFixture(int cores) {
        LinkedHashMap<UUID, ShroudCoreState> coreStates = new LinkedHashMap<>();
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>();
        for (int index = 0; index < cores; index++) {
            UUID coreId = coreId(index);
            UUID regionId = regionId(index);
            coreStates.put(coreId, new ShroudCoreState(
                    coreId,
                    new BlockPos(index * 8000, 4, 4),
                    1,
                    CoreLifecycleState.ACTIVE,
                    2_000_000,
                    index + 1L,
                    0L,
                    regionId));
            regions.put(regionId, new ShroudRegionState(regionId, coreId, Map.of()));
        }
        return new ShroudWorldState(ShroudSchema.CURRENT_VERSION, coreStates, regions);
    }

    private static UUID coreId(int index) {
        return new UUID(0x0902000000000010L, index + 1L);
    }

    private static UUID regionId(int index) {
        return new UUID(0x0902000000000011L, index + 1L);
    }
}
