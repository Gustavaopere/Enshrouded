package com.gustavaopere.enshrouded.story.manifestation;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.story.boss.ManifestationDirector;
import com.gustavaopere.enshrouded.story.state.StorySavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Coordinates the explicit Level-1 manifestation start over the canonical Story State and boss provider director.
 */
public final class ManifestationEncounterService {
    public static final int FIRST_MANIFESTATION_INDEX = 1;

    private final ProgressionOwnerResolver ownerResolver;
    private final ManifestationDirector manifestationDirector;

    public ManifestationEncounterService(
            ProgressionOwnerResolver ownerResolver,
            ManifestationDirector manifestationDirector) {
        this.ownerResolver = Objects.requireNonNull(ownerResolver, "ownerResolver");
        this.manifestationDirector = Objects.requireNonNull(manifestationDirector, "manifestationDirector");
    }

    public Optional<ActiveEncounter> start(ServerLevel level, UUID playerId, BlockPos origin) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(origin, "origin");

        ProgressionOwner owner;
        try {
            owner = ownerResolver.resolve(playerId);
        } catch (RuntimeException resolutionFailure) {
            return Optional.empty();
        }
        if (owner == null) {
            return Optional.empty();
        }

        UUID encounterId = UUID.randomUUID();
        EncounterContext context = new EncounterContext(
                encounterId,
                origin,
                FIRST_MANIFESTATION_INDEX,
                seed(level, encounterId)
        );
        Optional<ManifestationDirector.ActiveManifestation> spawned = manifestationDirector.spawn(level, context);
        if (spawned.isEmpty()) {
            return Optional.empty();
        }

        ManifestationDirector.ActiveManifestation manifestation = spawned.orElseThrow();
        StorySavedData savedData = StorySavedData.get(level);
        boolean activated;
        synchronized (savedData) {
            activated = savedData.createEncounter(owner, encounterId, FIRST_MANIFESTATION_INDEX)
                    && savedData.activateEncounter(encounterId, manifestation.entity().getUUID());
        }
        if (!activated) {
            manifestation.entity().discard();
            return Optional.empty();
        }

        return Optional.of(new ActiveEncounter(owner, encounterId, manifestation));
    }

    private static long seed(ServerLevel level, UUID encounterId) {
        return level.getSeed()
                ^ encounterId.getMostSignificantBits()
                ^ encounterId.getLeastSignificantBits();
    }

    public record ActiveEncounter(
            ProgressionOwner owner,
            UUID encounterId,
            ManifestationDirector.ActiveManifestation manifestation) {
        public ActiveEncounter {
            owner = Objects.requireNonNull(owner, "owner");
            encounterId = Objects.requireNonNull(encounterId, "encounterId");
            manifestation = Objects.requireNonNull(manifestation, "manifestation");
        }

        public LivingEntity entity() {
            return manifestation.entity();
        }
    }
}
