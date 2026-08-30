package com.gustavaopere.enshrouded.story.state;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Persistent encounter identity/state; the physical entity UUID is only valid while ACTIVE. */
public record EncounterRecord(
        UUID encounterId,
        int manifestationIndex,
        ProgressionOwner owner,
        EncounterOutcome outcome,
        boolean rewardIssued,
        Optional<UUID> entityId) {

    public EncounterRecord {
        encounterId = Objects.requireNonNull(encounterId, "encounterId");
        StorySchema.requireManifestationIndex(manifestationIndex);
        owner = Objects.requireNonNull(owner, "owner");
        outcome = Objects.requireNonNull(outcome, "outcome");
        entityId = Objects.requireNonNull(entityId, "entityId").map(Objects::requireNonNull);
        if (rewardIssued && !outcome.rewardEligible()) {
            throw new IllegalArgumentException("rewardIssued requires a DEFEATED encounter");
        }
        if (outcome == EncounterOutcome.ACTIVE && entityId.isEmpty()) {
            throw new IllegalArgumentException("ACTIVE encounter requires a physical entity UUID");
        }
        if (outcome != EncounterOutcome.ACTIVE && entityId.isPresent()) {
            throw new IllegalArgumentException("physical entity UUID is only valid while encounter is ACTIVE");
        }
    }

    public static EncounterRecord available(UUID encounterId, int manifestationIndex, ProgressionOwner owner) {
        return new EncounterRecord(
                encounterId,
                manifestationIndex,
                owner,
                EncounterOutcome.AVAILABLE,
                false,
                Optional.empty()
        );
    }

    public Optional<EncounterRecord> activate(UUID physicalEntityId) {
        Objects.requireNonNull(physicalEntityId, "physicalEntityId");
        if (outcome != EncounterOutcome.AVAILABLE) {
            return Optional.empty();
        }
        return Optional.of(new EncounterRecord(
                encounterId,
                manifestationIndex,
                owner,
                EncounterOutcome.ACTIVE,
                false,
                Optional.of(physicalEntityId)
        ));
    }

    public Optional<EncounterRecord> defeat() {
        if (outcome != EncounterOutcome.ACTIVE) {
            return Optional.empty();
        }
        return Optional.of(new EncounterRecord(
                encounterId,
                manifestationIndex,
                owner,
                EncounterOutcome.DEFEATED,
                false,
                Optional.empty()
        ));
    }

    public Optional<EncounterRecord> abort() {
        if (outcome != EncounterOutcome.ACTIVE) {
            return Optional.empty();
        }
        return Optional.of(new EncounterRecord(
                encounterId,
                manifestationIndex,
                owner,
                EncounterOutcome.ABORTED,
                false,
                Optional.empty()
        ));
    }

    public Optional<EncounterRecord> issueReward() {
        if (outcome != EncounterOutcome.DEFEATED || rewardIssued) {
            return Optional.empty();
        }
        return Optional.of(new EncounterRecord(
                encounterId,
                manifestationIndex,
                owner,
                outcome,
                true,
                Optional.empty()
        ));
    }

    public boolean isOpen() {
        return outcome == EncounterOutcome.AVAILABLE || outcome == EncounterOutcome.ACTIVE;
    }
}
