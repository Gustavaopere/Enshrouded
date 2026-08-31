package com.gustavaopere.enshrouded.story.reward;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.story.state.EncounterRecord;
import com.gustavaopere.enshrouded.story.state.LichStoryState;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LichRewardContractTest {
    private static final UUID ENCOUNTER_ID = UUID.fromString("60604001-0000-4000-8000-000000000001");
    private static final UUID ENTITY_ID = UUID.fromString("60604001-0000-4000-8000-000000000002");
    private static final ProgressionOwner OWNER = ProgressionOwner.player(
            UUID.fromString("60604001-0000-4000-8000-000000000101")
    );

    @Test
    void skullIdentityCodecRoundTripsAndRejectsMissingOrWrongFormatData() {
        LichSkullIdentity identity = new LichSkullIdentity(ENCOUNTER_ID, 1);

        assertEquals(identity, LichSkullIdentity.decode(identity.encode()).orElseThrow());
        assertTrue(LichSkullIdentity.decode(new CompoundTag()).isEmpty());

        CompoundTag wrongFormat = identity.encode();
        wrongFormat.putInt("Format", LichSkullIdentity.FORMAT_VERSION + 1);
        assertTrue(LichSkullIdentity.decode(wrongFormat).isEmpty());
    }

    @Test
    void rewardReceiptIsIssuedExactlyOnceFromDefeatedEncounterAndRetainsStoredOwner() {
        MemoryRewardStore store = new MemoryRewardStore(defeatedState());
        LichRewardService service = new LichRewardService(store);

        RewardReceipt first = service.issue(ENCOUNTER_ID, receipt -> true).orElseThrow();
        Optional<RewardReceipt> replay = service.issue(ENCOUNTER_ID, receipt -> true);

        assertEquals(OWNER, first.owner());
        assertEquals(ENCOUNTER_ID, first.encounterId());
        assertEquals(1, first.manifestationIndex());
        assertEquals(new LichSkullIdentity(ENCOUNTER_ID, 1), first.skullIdentity());
        assertTrue(replay.isEmpty());
        assertTrue(store.state().encounter(ENCOUNTER_ID).orElseThrow().rewardIssued());
    }

    @Test
    void failedDeliveryDoesNotCommitRewardAndRetryCanDeliverExactlyOnce() {
        MemoryRewardStore store = new MemoryRewardStore(defeatedState());
        LichRewardService service = new LichRewardService(store);

        Optional<RewardReceipt> failedDelivery = service.issue(ENCOUNTER_ID, receipt -> false);
        assertTrue(failedDelivery.isEmpty());
        assertTrue(!store.state().encounter(ENCOUNTER_ID).orElseThrow().rewardIssued());

        RewardReceipt delivered = service.issue(ENCOUNTER_ID, receipt -> true).orElseThrow();
        Optional<RewardReceipt> replay = service.issue(ENCOUNTER_ID, receipt -> true);

        assertEquals(OWNER, delivered.owner());
        assertTrue(store.state().encounter(ENCOUNTER_ID).orElseThrow().rewardIssued());
        assertTrue(replay.isEmpty());
    }

    private static LichStoryState defeatedState() {
        return LichStoryState.empty()
                .createEncounter(OWNER, ENCOUNTER_ID, 1).orElseThrow()
                .activateEncounter(ENCOUNTER_ID, ENTITY_ID).orElseThrow()
                .defeatEncounter(ENCOUNTER_ID).orElseThrow();
    }

    private static final class MemoryRewardStore implements LichRewardService.RewardStore {
        private LichStoryState state;

        private MemoryRewardStore(LichStoryState state) {
            this.state = state;
        }

        @Override
        public synchronized <T> T transact(Function<LichRewardService.RewardTransaction, T> transaction) {
            return transaction.apply(new LichRewardService.RewardTransaction() {
                @Override
                public Optional<EncounterRecord> encounter(UUID encounterId) {
                    return state.encounter(encounterId);
                }

                @Override
                public boolean issueReward(UUID encounterId) {
                    Optional<LichStoryState> next = state.issueReward(encounterId);
                    if (next.isEmpty()) {
                        return false;
                    }
                    state = next.orElseThrow();
                    return true;
                }
            });
        }

        private LichStoryState state() {
            return state;
        }
    }
}
