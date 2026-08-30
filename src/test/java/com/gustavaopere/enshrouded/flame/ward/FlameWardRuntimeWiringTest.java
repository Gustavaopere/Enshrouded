package com.gustavaopere.enshrouded.flame.ward;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FlameWardRuntimeWiringTest {
    private static final Path SHROUD_QUERY = Path.of(
            "src/main/java/com/gustavaopere/enshrouded/shroud/query/DefaultShroudQuery.java");
    private static final Path PURIFICATION = Path.of(
            "src/main/java/com/gustavaopere/enshrouded/shroud/purification/ShroudPurificationRuntime.java");
    private static final Path CORE_FEATURE = Path.of(
            "src/main/java/com/gustavaopere/enshrouded/shroud/worldgen/ShroudCoreFeature.java");

    @Test
    void exposureAndPurificationConsumeTheStableFoundationWardHandle() throws Exception {
        String query = Files.readString(SHROUD_QUERY);
        String purification = Files.readString(PURIFICATION);

        assertTrue(query.contains("FlameWardRuntimeBindings.query()"),
                "Default Shroud query must consume the stable Flame ward runtime handle");
        assertTrue(purification.contains("FlameWardRuntimeBindings.query()"),
                "Purification authority must consume the same stable Flame ward runtime handle");
        assertFalse(query.contains("FlameWardQuery.none()"),
                "Production Shroud sampling must not silently fall back to a permanent no-ward provider");
        assertFalse(purification.contains("FlameWardQuery.none()"),
                "Production purification wiring must not silently fall back to a permanent no-ward provider");
    }

    @Test
    void physicalCoreWorldgenTraversesMutationAuthority() throws Exception {
        String feature = Files.readString(CORE_FEATURE);

        assertTrue(feature.contains("MutationAuthority"),
                "Shroud core feature must route physical placement through MutationAuthority");
        assertTrue(feature.contains("MutationKind.CORE_PLACEMENT"),
                "Shroud core feature must classify its mutation as CORE_PLACEMENT");
        assertTrue(feature.contains("mutationAuthority.canMutate"),
                "Shroud core feature must ask the central authority before setBlock");
    }
}
