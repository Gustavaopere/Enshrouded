package com.gustavaopere.enshrouded.client.accessibility;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class AccessibilityConfigCacheBoundaryTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path MAIN = ROOT.resolve("src/main/java/com/gustavaopere/enshrouded");

    @Test
    void effectivePresetBundleIsCachedOutsideRenderHotPathsAndInvalidatedOnConfigEvents() throws IOException {
        String config = Files.readString(MAIN.resolve("config/EnshroudedClientConfig.java"));
        assertTrue(config.contains("cachedResolvedSettings"),
                "effective Stage 07 preset settings must be cached instead of rebuilding all sections per getter/frame");
        assertTrue(config.contains("invalidateResolvedSettings()"),
                "shared client config must expose an explicit cache invalidation seam");

        String controller = Files.readString(MAIN.resolve("client/accessibility/AccessibilityPresetController.java"));
        assertTrue(controller.contains("EnshroudedClientConfig.invalidateResolvedSettings()"),
                "config loading/reloading must invalidate the effective settings cache before presentation resumes");
    }
}
