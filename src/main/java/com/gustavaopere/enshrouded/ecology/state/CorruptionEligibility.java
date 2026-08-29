package com.gustavaopere.enshrouded.ecology.state;

public final class CorruptionEligibility {
    private CorruptionEligibility() {
    }

    static boolean isEligible(boolean isPlayer, boolean isAllowlisted, boolean isImmune, boolean isExcludedBoss) {
        return !isPlayer && isAllowlisted && !isImmune && !isExcludedBoss;
    }
}
