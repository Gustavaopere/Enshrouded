package com.gustavaopere.enshrouded.flame.ritual;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.api.progression.ProgressionRuntimeBindings;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionSavedData;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Server-side ritual execution seam with immutable owner snapshot semantics.
 *
 * <p>The owner resolver is called exactly once per invocation. Eligibility, offering validation,
 * offering consumption, result computation and progression mutation then use that same owner key.
 * Duplicate ritual checkpoints are rejected before offering consumption.</p>
 */
public final class FlameRitualExecutor {
    private final ProgressionOwnerResolver ownerResolver;
    private final FlameRitualRegistry registry;
    private final ProgressionStore progressionStore;

    public FlameRitualExecutor(
            ProgressionOwnerResolver ownerResolver,
            FlameRitualRegistry registry,
            ProgressionStore progressionStore) {
        this.ownerResolver = Objects.requireNonNull(ownerResolver, "ownerResolver");
        this.registry = Objects.requireNonNull(registry, "registry");
        this.progressionStore = Objects.requireNonNull(progressionStore, "progressionStore");
    }

    /** Production server adapter used later by physical altar/story callers. */
    public static FlameRitualExecutor forServer(MinecraftServer server, FlameRitualRegistry registry) {
        Objects.requireNonNull(server, "server");
        FlameProgressionSavedData savedData = FlameProgressionSavedData.get(server);
        return new FlameRitualExecutor(
                ProgressionRuntimeBindings.ownerResolver(),
                registry,
                new SavedDataStore(savedData)
        );
    }

    public ExecutionResult invoke(
            UUID invokerId,
            ResourceLocation ritualId,
            ResourceLocation intentId,
            FlameRitual.Offering offering) {
        Objects.requireNonNull(invokerId, "invokerId");
        Objects.requireNonNull(ritualId, "ritualId");
        Objects.requireNonNull(intentId, "intentId");
        Objects.requireNonNull(offering, "offering");

        // Contract: resolve exactly once at operation start and retain this immutable stable key.
        ProgressionOwner owner = Objects.requireNonNull(ownerResolver.resolve(invokerId), "resolved owner");
        Optional<FlameRitual> found = registry.find(ritualId);
        if (found.isEmpty()) {
            return new ExecutionResult(Status.UNKNOWN_RITUAL, owner, ritualId, Optional.empty());
        }

        FlameRitual ritual = found.orElseThrow();
        ResourceLocation expectedIntent = Objects.requireNonNull(ritual.intentId(), "ritual.intentId()");
        if (!expectedIntent.equals(intentId)) {
            return new ExecutionResult(Status.INTENT_MISMATCH, owner, ritualId, Optional.empty());
        }
        FlameRitual.OfferingContract offeringContract = Objects.requireNonNull(ritual.offering(), "ritual.offering()");

        return progressionStore.transact(owner, transaction -> {
            FlameProgressionState.OwnerProgression progression = transaction.progression();
            if (progression.completedRituals().contains(ritualId)) {
                return new ExecutionResult(Status.ALREADY_COMPLETED, owner, ritualId, Optional.empty());
            }

            FlameRitual.Context context = new FlameRitual.Context(invokerId, owner, progression, intentId);
            if (!ritual.isEligible(context)) {
                return new ExecutionResult(Status.INELIGIBLE, owner, ritualId, Optional.empty());
            }
            if (!offeringContract.accepts(context, offering)) {
                return new ExecutionResult(Status.OFFERING_REJECTED, owner, ritualId, Optional.empty());
            }

            RitualOutcome outcome = Objects.requireNonNull(ritual.outcome(context), "ritual.outcome()");
            boolean applied = transaction.applyCheckpoint(
                    ritualId,
                    outcome,
                    () -> offeringContract.consume(context, offering)
            );
            if (!applied) {
                return new ExecutionResult(Status.ALREADY_COMPLETED, owner, ritualId, Optional.empty());
            }
            return new ExecutionResult(Status.APPLIED, owner, ritualId, Optional.of(outcome));
        });
    }

    public enum Status {
        APPLIED,
        ALREADY_COMPLETED,
        UNKNOWN_RITUAL,
        INTENT_MISMATCH,
        INELIGIBLE,
        OFFERING_REJECTED
    }

    public record ExecutionResult(
            Status status,
            ProgressionOwner owner,
            ResourceLocation ritualId,
            Optional<RitualOutcome> outcome) {
        public ExecutionResult {
            Objects.requireNonNull(status, "status");
            Objects.requireNonNull(owner, "owner");
            Objects.requireNonNull(ritualId, "ritualId");
            Objects.requireNonNull(outcome, "outcome");
        }
    }

    /** Store boundary that serializes one owner-scoped ritual transaction. */
    public interface ProgressionStore {
        <T> T transact(ProgressionOwner owner, Function<RitualTransaction, T> transaction);
    }

    public interface RitualTransaction {
        FlameProgressionState.OwnerProgression progression();

        /**
         * Applies the checkpoint and runs offering consumption only when the ritual is still unique
         * and the resulting progression state has already been validated.
         * Implementations must keep duplicate check, validation, consumption and state mutation
         * under one store lock.
         */
        boolean applyCheckpoint(ResourceLocation ritualId, RitualOutcome outcome, Runnable consumeOffering);
    }

    private static final class SavedDataStore implements ProgressionStore {
        private final FlameProgressionSavedData savedData;

        private SavedDataStore(FlameProgressionSavedData savedData) {
            this.savedData = Objects.requireNonNull(savedData, "savedData");
        }

        @Override
        public <T> T transact(ProgressionOwner owner, Function<RitualTransaction, T> transaction) {
            Objects.requireNonNull(owner, "owner");
            Objects.requireNonNull(transaction, "transaction");
            synchronized (savedData) {
                return transaction.apply(new RitualTransaction() {
                    @Override
                    public FlameProgressionState.OwnerProgression progression() {
                        return savedData.progression(owner);
                    }

                    @Override
                    public boolean applyCheckpoint(
                            ResourceLocation ritualId,
                            RitualOutcome outcome,
                            Runnable consumeOffering) {
                        Objects.requireNonNull(ritualId, "ritualId");
                        Objects.requireNonNull(outcome, "outcome");
                        Objects.requireNonNull(consumeOffering, "consumeOffering");

                        Optional<FlameProgressionState> next = savedData.state().applyRitualCheckpoint(
                                owner,
                                ritualId,
                                outcome.flameLevel(),
                                outcome.passageLevel(),
                                outcome.nextLevelReady()
                        );
                        if (next.isEmpty()) {
                            return false;
                        }

                        // All progression validation has succeeded while holding the same store lock.
                        // If consumption itself fails, no progression state is committed.
                        consumeOffering.run();
                        savedData.replace(next.orElseThrow());
                        return true;
                    }
                });
            }
        }
    }
}
