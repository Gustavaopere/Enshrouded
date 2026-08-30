package com.gustavaopere.enshrouded.story.manifestation;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.story.boss.ManifestationDirector;
import com.gustavaopere.enshrouded.story.state.EncounterOutcome;
import com.gustavaopere.enshrouded.story.state.EncounterRecord;
import com.gustavaopere.enshrouded.story.state.StorySavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Coordinates the explicit Level-1 manifestation lifecycle over canonical Story State and the boss provider director.
 */
public final class ManifestationEncounterService {
    public static final int FIRST_MANIFESTATION_INDEX = 1;

    private final ProgressionOwnerResolver ownerResolver;
    private final ManifestationDirector manifestationDirector;
    private final LichArenaRule arenaRule;

    public ManifestationEncounterService(
            ProgressionOwnerResolver ownerResolver,
            ManifestationDirector manifestationDirector) {
        this(ownerResolver, manifestationDirector, null);
    }

    public ManifestationEncounterService(
            ProgressionOwnerResolver ownerResolver,
            ManifestationDirector manifestationDirector,
            @Nullable LichArenaRule arenaRule) {
        this.ownerResolver = Objects.requireNonNull(ownerResolver, "ownerResolver");
        this.manifestationDirector = Objects.requireNonNull(manifestationDirector, "manifestationDirector");
        this.arenaRule = arenaRule;
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

        if (arenaRule != null && !arenaRule.activate(level, encounterId, origin)) {
            synchronized (savedData) {
                savedData.abortEncounter(encounterId);
            }
            manifestation.entity().discard();
            return Optional.empty();
        }

        return Optional.of(new ActiveEncounter(owner, encounterId, manifestation));
    }

    /**
     * Accepts only the actual dead physical actor bound to an ACTIVE encounter. The owner is read from
     * the persisted encounter record and is never re-resolved from a player or team at defeat time.
     */
    public Optional<DefeatResult> defeatFromActor(LivingEntity actor) {
        Objects.requireNonNull(actor, "actor");
        if (actor.isAlive() || !(actor.level() instanceof ServerLevel level)) {
            return Optional.empty();
        }

        Optional<UUID> taggedEncounterId = ManifestationDirector.encounterId(actor);
        if (taggedEncounterId.isEmpty()) {
            return Optional.empty();
        }

        UUID encounterId = taggedEncounterId.orElseThrow();
        StorySavedData savedData = StorySavedData.get(level);
        DefeatResult result;
        synchronized (savedData) {
            Optional<EncounterRecord> recordOptional = savedData.state().encounter(encounterId);
            if (recordOptional.isEmpty()) {
                return Optional.empty();
            }

            EncounterRecord record = recordOptional.orElseThrow();
            if (record.outcome() != EncounterOutcome.ACTIVE
                    || record.entityId().filter(actor.getUUID()::equals).isEmpty()) {
                return Optional.empty();
            }

            ProgressionOwner storedOwner = record.owner();
            int manifestationIndex = record.manifestationIndex();
            if (!savedData.defeatEncounter(encounterId)) {
                return Optional.empty();
            }
            result = new DefeatResult(storedOwner, encounterId, manifestationIndex);
        }

        if (arenaRule != null) {
            arenaRule.cleanup(level, encounterId);
        }
        return Optional.of(result);
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

    public record DefeatResult(ProgressionOwner owner, UUID encounterId, int manifestationIndex) {
        public DefeatResult {
            owner = Objects.requireNonNull(owner, "owner");
            encounterId = Objects.requireNonNull(encounterId, "encounterId");
            if (manifestationIndex < 1) {
                throw new IllegalArgumentException("manifestationIndex must be >= 1");
            }
        }
    }
}
