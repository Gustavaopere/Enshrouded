package com.gustavaopere.enshrouded.story.reward;

import com.gustavaopere.enshrouded.registry.ModItems;
import com.gustavaopere.enshrouded.story.manifestation.ManifestationEncounterService;
import com.gustavaopere.enshrouded.story.state.EncounterOutcome;
import com.gustavaopere.enshrouded.story.state.EncounterRecord;
import com.gustavaopere.enshrouded.story.state.StorySavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Sole authority for the concrete first-manifestation trophy.
 *
 * <p>Encounter validation and rewardIssued mutation execute under one story-store transaction. The
 * stored encounter owner is copied into the receipt; no owner resolver is consulted here.</p>
 */
public final class LichRewardService {
    private final RewardStore store;
    private final BiFunction<UUID, Integer, ItemStack> rewardFactory;

    public LichRewardService(
            RewardStore store,
            BiFunction<UUID, Integer, ItemStack> rewardFactory) {
        this.store = Objects.requireNonNull(store, "store");
        this.rewardFactory = Objects.requireNonNull(rewardFactory, "rewardFactory");
    }

    public static LichRewardService forLevel(ServerLevel level) {
        Objects.requireNonNull(level, "level");
        StorySavedData savedData = StorySavedData.get(level);
        return new LichRewardService(
                new SavedDataRewardStore(savedData),
                (encounterId, manifestationIndex) -> LichSkullItem.createAuthentic(
                        ModItems.LICH_SKULL_MANIFESTATION_1.get(),
                        encounterId,
                        manifestationIndex
                )
        );
    }

    /** Returns a receipt only for the single transition from reward-pending to reward-issued. */
    public Optional<RewardReceipt> issue(UUID encounterId) {
        Objects.requireNonNull(encounterId, "encounterId");
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

            ItemStack reward = Objects.requireNonNull(
                    rewardFactory.apply(record.encounterId(), record.manifestationIndex()),
                    "rewardFactory result"
            );
            if (reward.isEmpty() || reward.getCount() != 1 || !LichSkullItem.isAuthenticLevelOne(reward)) {
                throw new IllegalStateException("reward factory produced an invalid first-manifestation skull");
            }
            if (!transaction.issueReward(encounterId)) {
                return Optional.empty();
            }
            return Optional.of(new RewardReceipt(
                    record.owner(),
                    record.encounterId(),
                    record.manifestationIndex(),
                    reward
            ));
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
