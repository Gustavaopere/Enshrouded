package com.gustavaopere.enshrouded.client.hud;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShroudHudOverlayContractTest {
    private static final Path SOURCE = Path.of(
            System.getProperty("user.dir"),
            "src/main/java/com/gustavaopere/enshrouded/client/hud/ShroudHudOverlay.java"
    );

    @Test
    void overlayDoesNotPredictServerExposureFromWallClockTime() throws IOException {
        String source = Files.readString(SOURCE);

        assertFalse(source.contains("System.nanoTime()"),
                "the HUD must not drain authoritative exposure from wall-clock time during pause or server lag");
        assertFalse(source.contains("NANOS_PER_TICK"),
                "wall-clock tick conversion is not a valid server exposure clock");
        assertTrue(source.contains("ExposureHudModel.fromSnapshot(state.snapshot(), 0)"),
                "the seconds HUD should present the latest server-authored reserve without local death prediction");
    }

    @Test
    void passageWarningUsesBoundedPanelWidthInsteadOfFixedRightEdgeOffset() throws IOException {
        String source = Files.readString(SOURCE);

        assertFalse(source.contains("x + 82, y + 17"),
                "a fixed right-side warning origin can truncate localized text on right-anchored HUDs");
        assertTrue(source.contains("graphics.drawWordWrap"),
                "the passage warning must be constrained to an explicit width inside the HUD panel");
        assertTrue(source.contains("PANEL_WIDTH - 12"),
                "warning wrapping must reserve symmetric panel padding");
    }
}
