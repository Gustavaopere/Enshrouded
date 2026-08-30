package com.gustavaopere.enshrouded.story.state;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Persistent story progression for one stable Foundation progression owner. */
public record ManifestationRecord(
        ProgressionOwner owner,
        int currentManifestationIndex,
        Set<Integer> defeatedManifestationIndices) {

    public ManifestationRecord {
        owner = Objects.requireNonNull(owner, "owner");
        StorySchema.requireManifestationIndex(currentManifestationIndex);
        Objects.requireNonNull(defeatedManifestationIndices, "defeatedManifestationIndices");
        LinkedHashSet<Integer> copy = new LinkedHashSet<>();
        for (Integer index : defeatedManifestationIndices) {
            if (index == null) {
                throw new IllegalArgumentException("defeated manifestation index must not be null");
            }
            StorySchema.requireManifestationIndex(index);
            copy.add(index);
        }
        defeatedManifestationIndices = Collections.unmodifiableSet(copy);
    }

    public static ManifestationRecord baseline(ProgressionOwner owner) {
        return new ManifestationRecord(owner, 1, Set.of());
    }

    public boolean isDefeated(int manifestationIndex) {
        return defeatedManifestationIndices.contains(manifestationIndex);
    }

    public Optional<ManifestationRecord> markDefeated(int manifestationIndex) {
        StorySchema.requireManifestationIndex(manifestationIndex);
        if (defeatedManifestationIndices.contains(manifestationIndex)) {
            return Optional.empty();
        }
        LinkedHashSet<Integer> next = new LinkedHashSet<>(defeatedManifestationIndices);
        next.add(manifestationIndex);
        return Optional.of(new ManifestationRecord(owner, currentManifestationIndex, next));
    }
}
