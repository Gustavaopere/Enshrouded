package com.gustavaopere.enshrouded.story.state;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/** Immutable server-global story aggregate keyed by stable Foundation progression owners. */
public record LichStoryState(
        int schemaVersion,
        Map<ProgressionOwner, ManifestationRecord> manifestations,
        Map<UUID, EncounterRecord> encounters) {

    public LichStoryState {
        StorySchema.requireSupported(schemaVersion);
        Objects.requireNonNull(manifestations, "manifestations");
        Objects.requireNonNull(encounters, "encounters");

        LinkedHashMap<ProgressionOwner, ManifestationRecord> manifestationCopy = new LinkedHashMap<>();
        for (var entry : manifestations.entrySet()) {
            ProgressionOwner owner = Objects.requireNonNull(entry.getKey(), "manifestation owner");
            ManifestationRecord record = Objects.requireNonNull(entry.getValue(), "manifestation record");
            if (!owner.equals(record.owner())) {
                throw new IllegalArgumentException("manifestation map key does not match record owner: " + owner.stableKey());
            }
            manifestationCopy.put(owner, record);
        }

        LinkedHashMap<UUID, EncounterRecord> encounterCopy = new LinkedHashMap<>();
        for (var entry : encounters.entrySet()) {
            UUID encounterId = Objects.requireNonNull(entry.getKey(), "encounter id");
            EncounterRecord record = Objects.requireNonNull(entry.getValue(), "encounter record");
            if (!encounterId.equals(record.encounterId())) {
                throw new IllegalArgumentException("encounter map key does not match record id: " + encounterId);
            }
            if (!manifestationCopy.containsKey(record.owner())) {
                throw new IllegalArgumentException("encounter owner has no manifestation record: " + record.owner().stableKey());
            }
            encounterCopy.put(encounterId, record);
        }

        manifestations = Collections.unmodifiableMap(manifestationCopy);
        encounters = Collections.unmodifiableMap(encounterCopy);
    }

    public static LichStoryState empty() {
        return new LichStoryState(StorySchema.CURRENT_VERSION, Map.of(), Map.of());
    }

    public ManifestationRecord manifestation(ProgressionOwner owner) {
        Objects.requireNonNull(owner, "owner");
        return manifestations.getOrDefault(owner, ManifestationRecord.baseline(owner));
    }

    public Optional<EncounterRecord> encounter(UUID encounterId) {
        return Optional.ofNullable(encounters.get(Objects.requireNonNull(encounterId, "encounterId")));
    }

    public Optional<LichStoryState> createEncounter(
            ProgressionOwner owner,
            UUID encounterId,
            int manifestationIndex) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(encounterId, "encounterId");
        StorySchema.requireManifestationIndex(manifestationIndex);
        if (encounters.containsKey(encounterId)) {
            return Optional.empty();
        }

        ManifestationRecord manifestation = manifestation(owner);
        if (manifestation.currentManifestationIndex() != manifestationIndex
                || manifestation.isDefeated(manifestationIndex)
                || encounters.values().stream().anyMatch(record -> record.owner().equals(owner) && record.isOpen())) {
            return Optional.empty();
        }

        LinkedHashMap<ProgressionOwner, ManifestationRecord> nextManifestations = new LinkedHashMap<>(manifestations);
        nextManifestations.putIfAbsent(owner, manifestation);
        LinkedHashMap<UUID, EncounterRecord> nextEncounters = new LinkedHashMap<>(encounters);
        nextEncounters.put(encounterId, EncounterRecord.available(encounterId, manifestationIndex, owner));
        return Optional.of(new LichStoryState(StorySchema.CURRENT_VERSION, nextManifestations, nextEncounters));
    }

    public Optional<LichStoryState> activateEncounter(UUID encounterId, UUID physicalEntityId) {
        EncounterRecord current = encounters.get(Objects.requireNonNull(encounterId, "encounterId"));
        if (current == null) {
            return Optional.empty();
        }
        return current.activate(physicalEntityId).map(this::replaceEncounter);
    }

    public Optional<LichStoryState> defeatEncounter(UUID encounterId) {
        EncounterRecord current = encounters.get(Objects.requireNonNull(encounterId, "encounterId"));
        if (current == null) {
            return Optional.empty();
        }
        Optional<EncounterRecord> defeated = current.defeat();
        if (defeated.isEmpty()) {
            return Optional.empty();
        }
        ManifestationRecord manifestation = manifestations.get(current.owner());
        if (manifestation == null) {
            throw new IllegalStateException("encounter owner lost manifestation record: " + current.owner().stableKey());
        }
        ManifestationRecord nextManifestation = manifestation.markDefeated(current.manifestationIndex())
                .orElseThrow(() -> new IllegalStateException("ACTIVE encounter points at already-defeated manifestation"));

        LinkedHashMap<ProgressionOwner, ManifestationRecord> nextManifestations = new LinkedHashMap<>(manifestations);
        nextManifestations.put(current.owner(), nextManifestation);
        LinkedHashMap<UUID, EncounterRecord> nextEncounters = new LinkedHashMap<>(encounters);
        nextEncounters.put(encounterId, defeated.orElseThrow());
        return Optional.of(new LichStoryState(StorySchema.CURRENT_VERSION, nextManifestations, nextEncounters));
    }

    public Optional<LichStoryState> abortEncounter(UUID encounterId) {
        EncounterRecord current = encounters.get(Objects.requireNonNull(encounterId, "encounterId"));
        if (current == null) {
            return Optional.empty();
        }
        return current.abort().map(this::replaceEncounter);
    }

    public Optional<LichStoryState> issueReward(UUID encounterId) {
        EncounterRecord current = encounters.get(Objects.requireNonNull(encounterId, "encounterId"));
        if (current == null) {
            return Optional.empty();
        }
        return current.issueReward().map(this::replaceEncounter);
    }

    /**
     * Reconciles persisted ACTIVE encounters after restart. A missing/dead actor aborts the encounter,
     * never defeats it and therefore never becomes reward eligible.
     */
    public Optional<LichStoryState> reconcileActiveEncounters(Predicate<UUID> activeEntityAlive) {
        Objects.requireNonNull(activeEntityAlive, "activeEntityAlive");
        LinkedHashMap<UUID, EncounterRecord> nextEncounters = new LinkedHashMap<>(encounters);
        boolean changed = false;
        for (EncounterRecord encounter : encounters.values()) {
            if (encounter.outcome() != EncounterOutcome.ACTIVE) {
                continue;
            }
            UUID entityId = encounter.entityId().orElseThrow();
            if (activeEntityAlive.test(entityId)) {
                continue;
            }
            EncounterRecord aborted = encounter.abort().orElseThrow();
            nextEncounters.put(encounter.encounterId(), aborted);
            changed = true;
        }
        if (!changed) {
            return Optional.empty();
        }
        return Optional.of(new LichStoryState(StorySchema.CURRENT_VERSION, manifestations, nextEncounters));
    }

    private LichStoryState replaceEncounter(EncounterRecord replacement) {
        LinkedHashMap<UUID, EncounterRecord> next = new LinkedHashMap<>(encounters);
        next.put(replacement.encounterId(), replacement);
        return new LichStoryState(StorySchema.CURRENT_VERSION, manifestations, next);
    }
}
