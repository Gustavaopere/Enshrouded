package com.gustavaopere.enshrouded.story.state;

import java.util.Locale;
import java.util.Optional;

public enum EncounterOutcome {
    AVAILABLE,
    ACTIVE,
    DEFEATED,
    ABORTED;

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public boolean isTerminal() {
        return this == DEFEATED || this == ABORTED;
    }

    public boolean rewardEligible() {
        return this == DEFEATED;
    }

    public static Optional<EncounterOutcome> fromId(String id) {
        if (id == null) {
            return Optional.empty();
        }
        for (EncounterOutcome outcome : values()) {
            if (outcome.id().equals(id)) {
                return Optional.of(outcome);
            }
        }
        return Optional.empty();
    }
}
