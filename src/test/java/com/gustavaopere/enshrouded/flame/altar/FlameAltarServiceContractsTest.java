package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitual;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualExecutor;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualRegistry;
import com.gustavaopere.enshrouded.flame.ritual.RitualOutcome;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionState;
import net.minecraft.resources.ResourceLocation;
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
        registry.register(syntheticOfferingRitual());
        FlameAltarService service = new FlameAltarService(registry);
        MemoryStore store = new MemoryStore();
        FlameRitualExecutor executor = new FlameRitualExecutor(ignored -> OWNER, registry, store);
        TestOffering offering = new TestOffering(true);

        FlameAltarService.ActivationResult result = service.activate(PLAYER_ID, offering, executor);

        assertEquals(FlameAltarService.Status.APPLIED, result.status());
        assertEquals(Optional.of(RITUAL_ID), result.ritualId());
        assertTrue(offering.consumed());
        assertTrue(store.state().progression(OWNER).completedRituals().contains(RITUAL_ID));
        assertTrue(store.state().progression(OWNER).nextLevelReady());
        assertEquals(1, store.state().progression(OWNER).passageLevel());
    }

    @Test
    void forgedOrWrongOfferingCannotBypassRitualContract() {
        FlameRitualRegistry registry = new FlameRitualRegistry();
        registry.register(syntheticOfferingRitual());
        FlameAltarService service = new FlameAltarService(registry);
        MemoryStore store = new MemoryStore();
        FlameRitualExecutor executor = new FlameRitualExecutor(ignored -> OWNER, registry, store);
        TestOffering offering = new TestOffering(false);

        FlameAltarService.ActivationResult result = service.activate(PLAYER_ID, offering, executor);

        assertEquals(FlameAltarService.Status.NO_MATCHING_RITUAL, result.status());
        assertFalse(offering.consumed());
        assertFalse(store.state().hasOwner(OWNER));
    }

    @Test
    void duplicateActivationAcrossTheSameOrAnotherAltarCannotGrantOrConsumeTwice() {
        FlameRitualRegistry registry = new FlameRitualRegistry();
        registry.register(syntheticOfferingRitual());
        FlameAltarService service = new FlameAltarService(registry);
        MemoryStore store = new MemoryStore();
        FlameRitualExecutor executor = new FlameRitualExecutor(ignored -> OWNER, registry, store);
        TestOffering firstOffering = new TestOffering(true);
        TestOffering secondOffering = new TestOffering(true);

        FlameAltarService.ActivationResult first = service.activate(PLAYER_ID, firstOffering, executor);
        FlameAltarService.ActivationResult duplicate = service.activate(PLAYER_ID, secondOffering, executor);

        assertEquals(FlameAltarService.Status.APPLIED, first.status());
        assertEquals(FlameAltarService.Status.ALREADY_COMPLETED, duplicate.status());
        assertTrue(firstOffering.consumed());
        assertFalse(secondOffering.consumed());
        assertEquals(1, store.state().progression(OWNER).completedRituals().size());
    }

    private static FlameRitual syntheticOfferingRitual() {
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
                        return offering instanceof TestOffering testOffering && testOffering.valid();
                    }

                    @Override
                    public void consume(Context context, Offering offering) {
                        if (!(offering instanceof TestOffering testOffering) || !testOffering.consume()) {
                            throw new IllegalStateException("synthetic altar offering changed before consumption");
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

    private static final class TestOffering implements FlameRitual.Offering {
        private final boolean valid;
        private boolean consumed;

        private TestOffering(boolean valid) {
            this.valid = valid;
        }

        private boolean valid() {
            return valid;
        }

        private boolean consumed() {
            return consumed;
        }

        private boolean consume() {
            if (consumed) {
                return false;
            }
            consumed = true;
            return true;
        }
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
