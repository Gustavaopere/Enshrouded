package com.gustavaopere.enshrouded.performance;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class PerformanceHotPathInstrumentationRedTest {
    @Test
    void canonicalQueryPublishesLocalQueryCountWithoutChunkEnumeration() throws IOException {
        String source = source("shroud/query/DefaultShroudQuery.java");
        assertTrue(source.contains("recordLocalQueries(1L)"), "canonical local query must publish one observation");
        assertFalse(source.contains("getAllChunks"));
        assertFalse(source.contains("getChunks()"));
        assertFalse(source.contains("getAllEntities"));
    }

    @Test
    void materializationPublishesDequeuedAttemptsAndSuccessfulMutations() throws IOException {
        String source = source("shroud/terrain/ShroudMaterializationService.java");
        assertTrue(source.contains("recordMaterialization(jobs.size(), mutations)"),
                "materialization tick must publish bounded dequeued jobs and successful mutations");
    }

    @Test
    void restorationPublishesAttemptsAndSuccessfulReversions() throws IOException {
        String source = source("shroud/purification/TerrainRestorationService.java");
        assertTrue(source.contains("recordRestoration(attempted, mutations)"),
                "cleanup tick must publish bounded attempts and successful reversions");
    }

    @Test
    void entityCorruptionPublishesPerEntitySamplesAndStateUpdatesWithoutWorldScan() throws IOException {
        String source = source("ecology/state/EntityCorruptionRuntime.java");
        assertTrue(source.contains("recordEntityUpdate(1L, stateUpdated ? 1L : 0L)"),
                "sampled entity update must publish one per-entity sample and whether state changed");
        assertFalse(source.contains("getAllEntities"));
        assertFalse(source.contains("getEntities("));
        assertFalse(source.contains("getAllChunks"));
    }

    @Test
    void particleControllerPublishesActuallyVisitedSamplesAndEmittedParticles() throws IOException {
        String source = source("client/effects/ShroudParticleController.java");
        assertTrue(source.contains("recordClientEffects(visitedSamples, emittedParticles)"),
                "client pulse must publish actually visited source samples and emitted particles");
        assertTrue(source.contains("MAX_SAMPLES_PER_PULSE = 192"), "particle sampling hard cap must remain explicit");
    }

    private static String source(String relative) throws IOException {
        return Files.readString(Path.of("src/main/java/com/gustavaopere/enshrouded", relative));
    }
}
