package com.gustavaopere.enshrouded.flame.state;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Immutable server-global Flame progression snapshot keyed by stable progression owner. */
public record FlameProgressionState(
        int schemaVersion,
        Map<ProgressionOwner, OwnerProgression> owners) {

    public FlameProgressionState {
        if (schemaVersion != FlameProgressionSchema.CURRENT_VERSION) {
            throw new UnsupportedFlameProgressionSchemaException(schemaVersion);
        }
        Objects.requireNonNull(owners, "owners");
        LinkedHashMap<ProgressionOwner, OwnerProgression> copy = new LinkedHashMap<>();
        owners.forEach((owner, progression) -> {
            Objects.requireNonNull(owner, "owner");
            Objects.requireNonNull(progression, "progression");
            if (copy.put(owner, progression) != null) {
                throw new IllegalArgumentException("duplicate progression owner: " + owner.stableKey());
            }
        });
        owners = Map.copyOf(copy);
    }

    public static FlameProgressionState empty() {
        return new FlameProgressionState(FlameProgressionSchema.CURRENT_VERSION, Map.of());
    }

    /** Reads the owner baseline without materializing or dirtying persistent state. */
    public OwnerProgression progression(ProgressionOwner owner) {
        Objects.requireNonNull(owner, "owner");
        return owners.getOrDefault(owner, OwnerProgression.initial());
    }

    public boolean hasOwner(ProgressionOwner owner) {
        return owners.containsKey(Objects.requireNonNull(owner, "owner"));
    }

    /** Backward-compatible checkpoint API for callers that do not change story readiness. */
    public Optional<FlameProgressionState> applyRitualCheckpoint(
            ProgressionOwner owner,
            ResourceLocation ritualId,
            int flameLevel,
            int passageLevel) {
        return applyRitualCheckpoint(owner, ritualId, flameLevel, passageLevel, false);
    }

    /**
     * Atomically records one ritual checkpoint together with its resulting progression levels and
     * monotonic story-readiness flag. Duplicate ritual IDs return empty and cannot grant a second
     * advancement.
     */
    public Optional<FlameProgressionState> applyRitualCheckpoint(
            ProgressionOwner owner,
            ResourceLocation ritualId,
            int flameLevel,
            int passageLevel,
            boolean nextLevelReady) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(ritualId, "ritualId");
        OwnerProgression current = progression(owner);
        if (current.completedRituals().contains(ritualId)) {
            return Optional.empty();
        }
        if (flameLevel < current.flameLevel() || passageLevel < current.passageLevel()) {
            throw new IllegalArgumentException("ritual checkpoint cannot regress Flame or passage level");
        }

        LinkedHashSet<ResourceLocation> rituals = new LinkedHashSet<>(current.completedRituals());
        rituals.add(ritualId);
        OwnerProgression nextOwner = new OwnerProgression(
                flameLevel,
                passageLevel,
                current.nextLevelReady() || nextLevelReady,
                rituals
        );
        LinkedHashMap<ProgressionOwner, OwnerProgression> nextOwners = new LinkedHashMap<>(owners);
        nextOwners.put(owner, nextOwner);
        return Optional.of(new FlameProgressionState(schemaVersion, nextOwners));
    }

    public record OwnerProgression(
            int flameLevel,
            int passageLevel,
            boolean nextLevelReady,
            Set<ResourceLocation> completedRituals) {
        public OwnerProgression {
            FlameProgressionSchema.validateLevel(flameLevel, "flameLevel");
            FlameProgressionSchema.validateLevel(passageLevel, "passageLevel");
            Objects.requireNonNull(completedRituals, "completedRituals");
            LinkedHashSet<ResourceLocation> copy = new LinkedHashSet<>();
            for (ResourceLocation ritual : completedRituals) {
                if (!copy.add(Objects.requireNonNull(ritual, "ritual"))) {
                    throw new IllegalArgumentException("duplicate completed ritual: " + ritual);
                }
            }
            completedRituals = Set.copyOf(copy);
        }

        /** Source-compatible constructor for schema-v1/core callers. */
        public OwnerProgression(int flameLevel, int passageLevel, Set<ResourceLocation> completedRituals) {
            this(flameLevel, passageLevel, false, completedRituals);
        }

        public static OwnerProgression initial() {
            return new OwnerProgression(1, 1, false, Set.of());
        }
    }
}
