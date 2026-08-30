package com.gustavaopere.enshrouded.story.reward;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarOffering;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualExecutor;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualRegistry;
import com.gustavaopere.enshrouded.flame.ritual.RitualOutcome;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionState;
import com.gustavaopere.enshrouded.story.ritual.LevelOneLichSkullRitual;
import com.gustavaopere.enshrouded.story.state.EncounterRecord;
import com.gustavaopere.enshrouded.story.state.LichStoryState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LichRewardContractTest {
    private static final UUID ENCOUNTER_ID = UUID.fromString("60604001-0000-4000-8000-000000000001");
    private static final UUID ENTITY_ID = UUID.fromString("60604001-0000-4000-8000-000000000002");
    private static final UUID PLAYER_ID = UUID.fromString("60604001-0000-4000-8000-000000000003");
    private static final ProgressionOwner OWNER = ProgressionOwner.player(
            UUID.fromString("60604001-0000-4000-8000-000000000101")
    );

    @Test
    void authenticSkullUsesPersistentComponentIdentityAndRejectsLookalikes() {
        LichSkullItem skullItem = new LichSkullItem(new Item.Properties().stacksTo(1));
        ItemStack authentic = LichSkullItem.createAuthentic(skullItem, ENCOUNTER_ID, 1);
        ItemStack unstampedEnshroudedSkull = new ItemStack(skullItem);
        ItemStack vanillaLookalike = new ItemStack(Items.WITHER_SKELETON_SKULL);

        assertTrue(LichSkullItem.isAuthenticLevelOne(authentic));
        assertEquals(ENCOUNTER_ID, LichSkullItem.encounterId(authentic).orElseThrow());
        assertEquals(1, LichSkullItem.manifestationIndex(authentic).orElseThrow());
        assertFalse(LichSkullItem.isAuthenticLevelOne(unstampedEnshroudedSkull));
        assertFalse(LichSkullItem.isAuthenticLevelOne(vanillaLookalike));
    }

    @Test
    void rewardReceiptIsIssuedExactlyOnceFromDefeatedEncounterAndRetainsStoredOwner() {
        MemoryRewardStore store = new MemoryRewardStore(defeatedState());
        LichSkullItem skullItem = new LichSkullItem(new Item.Properties().stacksTo(1));
        LichRewardService service = new LichRewardService(
                store,
                (encounterId, manifestationIndex) ->
                        LichSkullItem.createAuthentic(skullItem, encounterId, manifestationIndex)
        );

        RewardReceipt first = service.issue(ENCOUNTER_ID).orElseThrow();
        Optional<RewardReceipt> replay = service.issue(ENCOUNTER_ID);

        assertEquals(OWNER, first.owner());
        assertEquals(ENCOUNTER_ID, first.encounterId());
        assertEquals(1, first.manifestationIndex());
        assertTrue(LichSkullItem.isAuthenticLevelOne(first.reward()));
        assertTrue(replay.isEmpty());
        assertTrue(store.state().encounter(ENCOUNTER_ID).orElseThrow().rewardIssued());
    }

    @Test
    void concreteSkullRitualUsesStageFiveExecutorExactlyOnceAndLeavesPassageAtOne() {
        LichSkullItem skullItem = new LichSkullItem(new Item.Properties().stacksTo(1));
        ItemStack authentic = LichSkullItem.createAuthentic(skullItem, ENCOUNTER_ID, 1);
        ItemStackHandler inventory = new ItemStackHandler(1);
        inventory.setStackInSlot(0, authentic);

        LevelOneLichSkullRitual ritual = new LevelOneLichSkullRitual();
        FlameRitualRegistry registry = new FlameRitualRegistry();
        registry.register(ritual);
        MemoryProgressionStore progressionStore = new MemoryProgressionStore();
        FlameRitualExecutor executor = new FlameRitualExecutor(
                ignored -> OWNER,
                registry,
                progressionStore
        );
        FlameAltarOffering offering = FlameAltarOffering.capture(inventory, 0);

        FlameRitualExecutor.ExecutionResult first = executor.invoke(
                PLAYER_ID,
                LevelOneLichSkullRitual.RITUAL_ID,
                LevelOneLichSkullRitual.INTENT_ID,
                offering
        );
        FlameRitualExecutor.ExecutionResult duplicate = executor.invoke(
                PLAYER_ID,
                LevelOneLichSkullRitual.RITUAL_ID,
                LevelOneLichSkullRitual.INTENT_ID,
                offering
        );
        FlameProgressionState.OwnerProgression progression = progressionStore.state().progression(OWNER);

        assertEquals(FlameRitualExecutor.Status.APPLIED, first.status());
        assertEquals(FlameRitualExecutor.Status.ALREADY_COMPLETED, duplicate.status());
        assertTrue(inventory.getStackInSlot(0).isEmpty());
        assertTrue(progression.completedRituals().contains(LevelOneLichSkullRitual.RITUAL_ID));
        assertTrue(progression.nextLevelReady());
        assertEquals(1, progression.flameLevel());
        assertEquals(1, progression.passageLevel());
    }

    @Test
    void concreteSkullRitualRejectsUnrelatedSkullLikeItems() {
        LevelOneLichSkullRitual ritual = new LevelOneLichSkullRitual();
        FlameRitualRegistry registry = new FlameRitualRegistry();
        registry.register(ritual);
        MemoryProgressionStore progressionStore = new MemoryProgressionStore();
        FlameRitualExecutor executor = new FlameRitualExecutor(ignored -> OWNER, registry, progressionStore);
        ItemStackHandler inventory = new ItemStackHandler(1);
        inventory.setStackInSlot(0, new ItemStack(Items.WITHER_SKELETON_SKULL));

        FlameRitualExecutor.ExecutionResult result = executor.invoke(
                PLAYER_ID,
                LevelOneLichSkullRitual.RITUAL_ID,
                LevelOneLichSkullRitual.INTENT_ID,
                FlameAltarOffering.capture(inventory, 0)
        );

        assertEquals(FlameRitualExecutor.Status.OFFERING_REJECTED, result.status());
        assertFalse(inventory.getStackInSlot(0).isEmpty());
        assertFalse(progressionStore.state().progression(OWNER).nextLevelReady());
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

    private static final class MemoryProgressionStore implements FlameRitualExecutor.ProgressionStore {
        private FlameProgressionState state = FlameProgressionState.empty();

        @Override
        public synchronized <T> T transact(
                ProgressionOwner owner,
                Function<FlameRitualExecutor.RitualTransaction, T> transaction) {
            return transaction.apply(new FlameRitualExecutor.RitualTransaction() {
                @Override
                public FlameProgressionState.OwnerProgression progression() {
                    return state.progression(owner);
                }

                @Override
                public boolean applyCheckpoint(
                        ResourceLocation ritualId,
                        RitualOutcome outcome,
                        Runnable consumeOffering) {
                    Optional<FlameProgressionState> next = state.applyRitualCheckpoint(
                            owner,
                            ritualId,
                            outcome.flameLevel(),
                            outcome.passageLevel(),
                            outcome.nextLevelReady()
                    );
                    if (next.isEmpty()) {
                        return false;
                    }
                    consumeOffering.run();
                    state = next.orElseThrow();
                    return true;
                }
            });
        }

        private FlameProgressionState state() {
            return state;
        }
    }
}
