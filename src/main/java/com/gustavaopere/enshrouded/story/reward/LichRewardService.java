package com.gustavaopere.enshrouded.story.reward;

import com.gustavaopere.enshrouded.story.manifestation.ManifestationEncounterService;
import com.gustavaopere.enshrouded.story.state.EncounterOutcome;
import com.gustavaopere.enshrouded.story.state.EncounterRecord;
import com.gustavaopere.enshrouded.story.state.StorySavedData;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Sole authority for committing the first-manifestation reward exactly once.
 *
 * <p>Encounter validation, bounded delivery and rewardIssued mutation execute under one serialized
 * story-store transaction. The stored encounter owner is copied into the receipt; no owner resolver
 * is consulted here. The reward flag is committed only after the delivery callback reports success,
 * so a failed physical delivery remains retryable rather than manufacturing an issued reward.</p>
 */
public final class LichRewardService {
    private final RewardStore store;

    public LichRewardService(RewardStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    public static LichRewardService forLevel(ServerLevel level) {
        Objects.requireNonNull(level, "level");
        return new LichRewardService(new SavedDataRewardStore(StorySavedData.get(level)));
    }

    /**
     * Delivers and commits the reward once. Delivery must be bounded and must not mutate this story
     * store; returning false leaves the reward pending so a later replay can retry safely.
     */
    public Optional<RewardReceipt> issue(UUID encounterId, Predicate<RewardReceipt> delivery) {
        Objects.requireNonNull(encounterId, "encounterId");
        Objects.requireNonNull(delivery, "delivery");
        return store.transact(transaction -> {
            Optional<EncounterRecord> found = transaction.encounter(encounterId);
            if (found.isEmpty()) {
                return Optional.empty();
            }

            EncounterRecord record = found.orElseThrow();
            if (record.manifestationIndex() != ManifestationEncounterService.FIRST_MANIFESTATION_INDEX
                    || record.outcome() != EncounterOutcome.DEFEATED
                    || record.rewardIssued()) {
                return Optional.empty();
            }

            RewardReceipt receipt = new RewardReceipt(
                    record.owner(),
                    record.encounterId(),
                    record.manifestationIndex()
            );
            if (!delivery.test(receipt)) {
                return Optional.empty();
            }
            if (!transaction.issueReward(encounterId)) {
                throw new IllegalStateException("reward state changed during serialized delivery commit");
            }
            return Optional.of(receipt);
        });
    }

    /** Serialization boundary for one atomic story-reward transaction. */
    public interface RewardStore {
        <T> T transact(Function<RewardTransaction, T> transaction);
    }

    public interface RewardTransaction {
        Optional<EncounterRecord> encounter(UUID encounterId);

        boolean issueReward(UUID encounterId);
    }

    private static final class SavedDataRewardStore implements RewardStore {
        private final StorySavedData savedData;

        private SavedDataRewardStore(StorySavedData savedData) {
            this.savedData = Objects.requireNonNull(savedData, "savedData");
        }

        @Override
        public <T> T transact(Function<RewardTransaction, T> transaction) {
            Objects.requireNonNull(transaction, "transaction");
            synchronized (savedData) {
                return transaction.apply(new RewardTransaction() {
                    @Override
                    public Optional<EncounterRecord> encounter(UUID encounterId) {
                        return savedData.state().encounter(encounterId);
                    }

                    @Override
                    public boolean issueReward(UUID encounterId) {
                        return savedData.issueReward(encounterId);
                    }
                });
            }
        }
    }
}
