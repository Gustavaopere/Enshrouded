package com.gustavaopere.enshrouded.performance;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LevelOnePerformanceBenchmarkRedTest {
    @Test
    void benchmarkCoversRequiredCoreScalesEntityStressAndPersistenceEvidence() throws Exception {
        LevelOnePerformanceBenchmark.Report report = LevelOnePerformanceBenchmark.run();

        assertEquals(List.of(1, 10, 50), report.coreScenarios().stream()
                .map(LevelOnePerformanceBenchmark.CoreScenario::cores)
                .toList());
        for (LevelOnePerformanceBenchmark.CoreScenario scenario : report.coreScenarios()) {
            assertTrue(scenario.processedEntries() <= scenario.globalBudget());
            assertTrue(scenario.maxProcessedPerCore() <= scenario.perCoreBudget());
            assertTrue(scenario.wallNanos() >= 0L);
        }

        assertEquals(10_000, report.entityScenario().samples());
        assertEquals(10_000, report.entityScenario().updates());
        assertTrue(report.entityScenario().wallNanos() >= 0L);

        assertEquals(50, report.persistenceScenario().cores());
        assertEquals(3_200, report.persistenceScenario().cells());
        assertTrue(report.persistenceScenario().compressedBytes() > 0L);
        assertTrue(report.persistenceScenario().compressedBytes() < 5_000_000L,
                "representative Level-1 SavedData fixture must remain bounded well below 5 MB compressed");
        assertTrue(report.persistenceScenario().observedHeapBytesBefore() > 0L);
        assertTrue(report.persistenceScenario().observedHeapBytesAfter() > 0L);

        Path evidence = Path.of("build", "reports", "level1-performance-benchmark.txt");
        assertTrue(Files.isRegularFile(evidence));
        String text = Files.readString(evidence);
        assertTrue(text.contains("core-scenario=1"));
        assertTrue(text.contains("core-scenario=10"));
        assertTrue(text.contains("core-scenario=50"));
        assertTrue(text.contains("entity-samples=10000"));
        assertTrue(text.contains("persistence-cores=50"));
        assertTrue(text.contains("persistence-cells=3200"));
        assertTrue(text.contains("compressed-bytes="));
        assertTrue(text.contains("heap-observed-before-bytes="));
        assertTrue(text.contains("heap-observed-after-bytes="));
    }
}
