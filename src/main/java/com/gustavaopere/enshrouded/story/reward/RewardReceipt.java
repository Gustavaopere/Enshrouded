package com.gustavaopere.enshrouded.story.reward;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;

import java.util.Objects;
import java.util.UUID;

/** Proof of one committed story-reward issuance, independent of physical delivery mechanics. */
public record RewardReceipt(
        ProgressionOwner owner,
        UUID encounterId,
        int manifestationIndex) {

    public RewardReceipt {
        owner = Objects.requireNonNull(owner, "owner");
        encounterId = Objects.requireNonNull(encounterId, "encounterId");
        if (manifestationIndex < 1) {
            throw new IllegalArgumentException("manifestationIndex must be >= 1");
        }
    }

    public LichSkullIdentity skullIdentity() {
        return new LichSkullIdentity(encounterId, manifestationIndex);
    }
}
