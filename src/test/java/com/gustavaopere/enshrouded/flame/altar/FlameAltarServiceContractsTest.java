package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitual;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualExecutor;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualRegistry;
import com.gustavaopere.enshrouded.flame.ritual.RitualOutcome;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionState;
import net.minecraft.resources.ResourceLocation;
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

final class FlameAltarServiceContractsTest {
    private static final UUID PLAYER_ID = UUID.fromString("90000000-1111-2222-3333-444444444444");
    private static final ProgressionOwner OWNER = ProgressionOwner.player(PLAYER_ID);
    private static final ResourceLocation RITUAL_ID =
            ResourceLocation.fromNamespaceAndPath("enshrouded", "synthetic_altar_ritual");
    private static final ResourceLocation INTENT_ID =
            ResourceLocation.fromNamespaceAndPath("enshrouded", "synthetic_altar_intent");

    @Test
    void altarDelegatesOfferingValidationConsumptionAndCheckpointToMergedExecutor() {
        FlameRitualRegistry registry = new FlameRitualRegistry();
        registry.register(syntheticBlazePowderRitual());
        FlameAltarService service = new FlameAltarService(registry);
        MemoryStore store = new MemoryStore();
        FlameRitualExecutor executor = new FlameRitualExecutor(ignored -> OWNER, registry, store);
        ItemStackHandler inventory = singleSlot(Items.BLAZE_POWDER.getDefaultInstance());

        FlameAltarService.ActivationResult result = service.activate(PLAYER_ID, inventory, executor);

        assertEquals(FlameAltarService.Status.APPLIED, result.status());
        assertEquals(Optional.of(RITUAL_ID), result.ritualId());
        assertTrue(inventory.getStackInSlot(0).isEmpty());
        assertTrue(store.state().progression(OWNER).completedRituals().contains(RITUAL_ID));
        assertTrue(store.state().progression(OWNER).nextLevelReady());
        assertEquals(1, store.state().progression(OWNER).passageLevel());
    }

    @Test
    void forgedOrWrongServerInventoryCannotBypassRitualOfferingContract() {
        FlameRitualRegistry registry = new FlameRitualRegistry();
        registry.register(syntheticBlazePowderRitual());
        FlameAltarService service = new FlameAltarService(registry);
        MemoryStore store = new MemoryStore();
        FlameRitualExecutor executor = new FlameRitualExecutor(ignored -> OWNER, registry, store);
        ItemStackHandler inventory = singleSlot(Items.ROTTEN_FLESH.getDefaultInstance());

        FlameAltarService.ActivationResult result = service.activate(PLAYER_ID, inventory, executor);

        assertEquals(FlameAltarService.Status.NO_MATCHING_RITUAL, result.status());
        assertTrue(inventory.getStackInSlot(0).is(Items.ROTTEN_FLESH));
        assertFalse(store.state().hasOwner(OWNER));
    }

    @Test
    void duplicateActivationAcrossTheSameOrAnotherAltarCannotGrantOrConsumeTwice() {
        FlameRitualRegistry registry = new FlameRitualRegistry();
        registry.register(syntheticBlazePowderRitual());
        FlameAltarService service = new FlameAltarService(registry);
        MemoryStore store = new MemoryStore();
        FlameRitualExecutor executor = new FlameRitualExecutor(ignored -> OWNER, registry, store);
        ItemStackHandler firstAltar = singleSlot(Items.BLAZE_POWDER.getDefaultInstance());
        ItemStackHandler secondAltar = singleSlot(Items.BLAZE_POWDER.getDefaultInstance());

        FlameAltarService.ActivationResult first = service.activate(PLAYER_ID, firstAltar, executor);
        FlameAltarService.ActivationResult duplicate = service.activate(PLAYER_ID, secondAltar, executor);

        assertEquals(FlameAltarService.Status.APPLIED, first.status());
        assertEquals(FlameAltarService.Status.ALREADY_COMPLETED, duplicate.status());
        assertTrue(firstAltar.getStackInSlot(0).isEmpty());
        assertEquals(1, secondAltar.getStackInSlot(0).getCount());
        assertEquals(1, store.state().progression(OWNER).completedRituals().size());
    }

    @Test
    void offeringConsumptionFailsClosedIfInventoryChangedAfterValidation() {
        ItemStackHandler inventory = singleSlot(Items.BLAZE_POWDER.getDefaultInstance());
        FlameAltarOffering offering = FlameAltarOffering.capture(inventory, 0);

        inventory.setStackInSlot(0, Items.ROTTEN_FLESH.getDefaultInstance());

        assertFalse(offering.consumeOne());
        assertTrue(inventory.getStackInSlot(0).is(Items.ROTTEN_FLESH));
    }

    private static FlameRitual syntheticBlazePowderRitual() {
        return new FlameRitual() {
            @Override
            public ResourceLocation id() {
                return RITUAL_ID;
            }

            @Override
            public ResourceLocation intentId() {
                return INTENT_ID;
            }

            @Override
            public boolean isEligible(Context context) {
                return context.progression().flameLevel() == 1 && context.progression().passageLevel() == 1;
            }

            @Override
            public OfferingContract offering() {
                return new OfferingContract() {
                    @Override
                    public boolean accepts(Context context, Offering offering) {
                        return offering instanceof FlameAltarOffering altarOffering
                                && altarOffering.stack().is(Items.BLAZE_POWDER);
                    }

                    @Override
                    public void consume(Context context, Offering offering) {
                        if (!(offering instanceof FlameAltarOffering altarOffering) || !altarOffering.consumeOne()) {
                            throw new IllegalStateException("altar offering changed before transactional consumption");
                        }
                    }
                };
            }

            @Override
            public RitualOutcome outcome(Context context) {
                return RitualOutcome.levelOneCheckpoint();
            }
        };
    }

    private static ItemStackHandler singleSlot(ItemStack stack) {
        ItemStackHandler handler = new ItemStackHandler(1);
        handler.setStackInSlot(0, stack.copy());
        return handler;
    }

    private static final class MemoryStore implements FlameRitualExecutor.ProgressionStore {
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
