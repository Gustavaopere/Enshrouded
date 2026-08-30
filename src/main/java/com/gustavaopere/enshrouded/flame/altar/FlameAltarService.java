package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.flame.ritual.FlameRitual;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualExecutor;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Physical Flame Altar adapter over the canonical ritual engine.
 *
 * <p>This service owns no progression state, ritual outcomes or eligibility rules. It captures the
 * authoritative server inventory and delegates every candidate to {@link FlameRitualExecutor}.</p>
 */
public final class FlameAltarService {
    public static final int OFFERING_SLOT = 0;

    private final FlameRitualRegistry registry;

    public FlameAltarService(FlameRitualRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    public ActivationResult activate(
            UUID playerId,
            IItemHandler inventory,
            FlameRitualExecutor executor) {
        Objects.requireNonNull(inventory, "inventory");
        if (inventory.getSlots() <= OFFERING_SLOT || inventory.getStackInSlot(OFFERING_SLOT).isEmpty()) {
            return ActivationResult.noMatchingRitual();
        }
        return activate(playerId, FlameAltarOffering.capture(inventory, OFFERING_SLOT), executor);
    }

    /**
     * Package-private seam used by the contract test so delegation can be verified without booting
     * Minecraft item registries. Production callers always use the inventory overload above.
     */
    ActivationResult activate(
            UUID playerId,
            FlameRitual.Offering offering,
            FlameRitualExecutor executor) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(offering, "offering");
        Objects.requireNonNull(executor, "executor");

        ResourceLocation completedRitual = null;
        for (FlameRitual ritual : registry.rituals()) {
            FlameRitualExecutor.ExecutionResult execution = executor.invoke(
                    playerId,
                    ritual.id(),
                    ritual.intentId(),
                    offering
            );

            switch (execution.status()) {
                case APPLIED -> {
                    return new ActivationResult(Status.APPLIED, Optional.of(ritual.id()));
                }
                case ALREADY_COMPLETED -> {
                    if (completedRitual == null) {
                        completedRitual = ritual.id();
                    }
                }
                case INELIGIBLE, OFFERING_REJECTED -> {
                    // Candidate did not match the authoritative current owner/offering state.
                }
                case UNKNOWN_RITUAL, INTENT_MISMATCH -> throw new IllegalStateException(
                        "altar registry/executor contract diverged for ritual " + ritual.id()
                );
            }
        }

        if (completedRitual != null) {
            return new ActivationResult(Status.ALREADY_COMPLETED, Optional.of(completedRitual));
        }
        return ActivationResult.noMatchingRitual();
    }

    public enum Status {
        APPLIED,
        ALREADY_COMPLETED,
        NO_MATCHING_RITUAL
    }

    public record ActivationResult(Status status, Optional<ResourceLocation> ritualId) {
        public ActivationResult {
            Objects.requireNonNull(status, "status");
            Objects.requireNonNull(ritualId, "ritualId");
        }

        public static ActivationResult noMatchingRitual() {
            return new ActivationResult(Status.NO_MATCHING_RITUAL, Optional.empty());
        }
    }
}
