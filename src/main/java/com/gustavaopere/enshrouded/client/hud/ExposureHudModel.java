package com.gustavaopere.enshrouded.client.hud;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import com.gustavaopere.enshrouded.exposure.madness.MadnessStage;

import java.util.Locale;
import java.util.Objects;

/**
 * Pure presentation model derived only from a server-authored exposure snapshot.
 *
 * <p>Client interpolation may smooth the countdown between snapshots, but it is deliberately
 * clamped above zero until the server explicitly authors zero remaining ticks. The model never
 * recalculates Madness or passage eligibility.</p>
 */
public record ExposureHudModel(
        boolean visible,
        int presentedRemainingTicks,
        int maxReserveTicks,
        ZoneKind zoneKind,
        MadnessStage madnessStage,
        boolean passageWarning,
        String zoneTranslationKey,
        String warningTranslationKey) {

    public static ExposureHudModel fromSnapshot(ExposureSnapshot snapshot, int elapsedClientTicks) {
        Objects.requireNonNull(snapshot, "snapshot");

        boolean visible = snapshot.severity() != ShroudSeverity.CLEAR && !snapshot.sanctuarySuppressed();
        ZoneKind zoneKind = snapshot.severity() == ShroudSeverity.DEADLY
                ? ZoneKind.DEADLY
                : ZoneKind.ORDINARY;
        boolean passageWarning = snapshot.severity() == ShroudSeverity.DEADLY
                && snapshot.deadlyBarrierActive();

        int remainingTicks = snapshot.remainingTicks();
        int presentedTicks;
        if (remainingTicks == 0) {
            presentedTicks = 0;
        } else if (!visible) {
            presentedTicks = remainingTicks;
        } else {
            presentedTicks = Math.max(1, remainingTicks - Math.max(0, elapsedClientTicks));
        }

        return new ExposureHudModel(
                visible,
                presentedTicks,
                snapshot.maxReserveTicks(),
                zoneKind,
                snapshot.madnessStage(),
                passageWarning,
                zoneKind.translationKey(),
                passageWarning ? "hud.enshrouded.passage_blocked" : ""
        );
    }

    public String countdownText() {
        int totalSeconds = presentedRemainingTicks == 0
                ? 0
                : (presentedRemainingTicks + 19) / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds);
    }

    public enum ZoneKind {
        ORDINARY("hud.enshrouded.shroud"),
        DEADLY("hud.enshrouded.deadly_shroud");

        private final String translationKey;

        ZoneKind(String translationKey) {
            this.translationKey = translationKey;
        }

        public String translationKey() {
            return translationKey;
        }
    }
}
