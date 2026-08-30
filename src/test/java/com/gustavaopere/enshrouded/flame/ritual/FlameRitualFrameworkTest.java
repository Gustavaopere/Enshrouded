package com.gustavaopere.enshrouded.flame.ritual;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionState;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FlameRitualFrameworkTest {
    private static final ResourceLocation RITUAL_ID = ResourceLocation.fromNamespaceAndPath("enshrouded", "synthetic_level1_checkpoint");
    private static final ResourceLocation INTENT_ID = ResourceLocation.fromNamespaceAndPath("enshrouded", "synthetic_checkpoint_intent");
    private static final UUID PLAYER_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");

    @Test
    void registryUsesStableIdsAndRejectsDuplicates() {
        FlameRitual ritual = syntheticRitual(true, RitualOutcome.levelOneCheckpoint(), new ArrayList<>());
        FlameRitualRegistry registry = new FlameRitualRegistry();

        registry.register(ritual);

        assertSame(ritual, registry.find(RITUAL_ID).orElseThrow());
        assertThrows(IllegalArgumentException.class, () -> registry.register(
                syntheticRitual(true, RitualOutcome.levelOneCheckpoint(), new ArrayList<>())
        ));
    }

    @Test
    void executorEnforcesEligibilityAndConsumesOfferingOnlyOnce() {
        ProgressionOwner owner = ProgressionOwner.player(PLAYER_ID);
        MemoryStore store = new MemoryStore();
        CountingOffering offering = new CountingOffering();
        FlameRitualRegistry registry = registryWith(syntheticRitual(true, RitualOutcome.levelOneCheckpoint(), new ArrayList<>()));
        FlameRitualExecutor executor = new FlameRitualExecutor(playerId -> owner, registry, store);

        FlameRitualExecutor.ExecutionResult first = executor.invoke(PLAYER_ID, RITUAL_ID, INTENT_ID, offering);
        FlameRitualExecutor.ExecutionResult duplicate = executor.invoke(PLAYER_ID, RITUAL_ID, INTENT_ID, offering);

        assertEquals(FlameRitualExecutor.Status.APPLIED, first.status());
        assertEquals(FlameRitualExecutor.Status.ALREADY_COMPLETED, duplicate.status());
        assertEquals(1, offering.consumeCount());
        assertEquals(Set.of(RITUAL_ID), store.state().progression(owner).completedRituals());

        CountingOffering rejectedOffering = new CountingOffering();
        ResourceLocation rejectedId = ResourceLocation.fromNamespaceAndPath("enshrouded", "synthetic_rejected");
        FlameRitual rejected = syntheticRitual(rejectedId, false, RitualOutcome.levelOneCheckpoint(), new ArrayList<>());
        FlameRitualExecutor rejectedExecutor = new FlameRitualExecutor(
                playerId -> owner,
                registryWith(rejected),
                new MemoryStore()
        );

        FlameRitualExecutor.ExecutionResult ineligible = rejectedExecutor.invoke(PLAYER_ID, rejectedId, INTENT_ID, rejectedOffering);

        assertEquals(FlameRitualExecutor.Status.INELIGIBLE, ineligible.status());
        assertEquals(0, rejectedOffering.consumeCount());
    }

    @Test
    void ownerIsResolvedExactlyOnceAndRemainsImmutableDuringTransaction() {
        ProgressionOwner initialOwner = ProgressionOwner.team("ftb:alpha");
        ProgressionOwner changedOwner = ProgressionOwner.team("ftb:beta");
        AtomicReference<ProgressionOwner> currentOwner = new AtomicReference<>(initialOwner);
        AtomicInteger resolverCalls = new AtomicInteger();
        List<ProgressionOwner> ritualOwners = new ArrayList<>();

        ProgressionOwnerResolver resolver = playerId -> {
            resolverCalls.incrementAndGet();
            return currentOwner.get();
        };
        FlameRitual ritual = new FlameRitual() {
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
                ritualOwners.add(context.owner());
                currentOwner.set(changedOwner);
                return true;
            }

            @Override
            public OfferingContract offering() {
                return countingOfferingContract(ritualOwners);
            }

            @Override
            public RitualOutcome outcome(Context context) {
                ritualOwners.add(context.owner());
                return RitualOutcome.levelOneCheckpoint();
            }
        };
        MemoryStore store = new MemoryStore();
        FlameRitualExecutor executor = new FlameRitualExecutor(resolver, registryWith(ritual), store);

        FlameRitualExecutor.ExecutionResult result = executor.invoke(PLAYER_ID, RITUAL_ID, INTENT_ID, new CountingOffering());

        assertEquals(FlameRitualExecutor.Status.APPLIED, result.status());
        assertEquals(initialOwner, result.owner());
        assertEquals(1, resolverCalls.get());
        assertTrue(ritualOwners.stream().allMatch(initialOwner::equals));
        assertTrue(store.state().hasOwner(initialOwner));
        assertFalse(store.state().hasOwner(changedOwner));
    }

    @Test
    void levelOneCheckpointMarksStoryReadyWithoutGrantingPassageTwo() {
        ProgressionOwner owner = ProgressionOwner.player(PLAYER_ID);
        MemoryStore store = new MemoryStore();
        FlameRitualExecutor executor = new FlameRitualExecutor(
                playerId -> owner,
                registryWith(syntheticRitual(true, RitualOutcome.levelOneCheckpoint(), new ArrayList<>())),
                store
        );

        FlameRitualExecutor.ExecutionResult result = executor.invoke(PLAYER_ID, RITUAL_ID, INTENT_ID, new CountingOffering());
        FlameProgressionState.OwnerProgression progression = store.state().progression(owner);

        assertEquals(FlameRitualExecutor.Status.APPLIED, result.status());
        assertTrue(progression.nextLevelReady());
        assertEquals(1, progression.flameLevel());
        assertEquals(1, progression.passageLevel());
    }

    @Test
    void serverSideCallerCanInvokeRegisteredSyntheticRitualWithoutAltarClasses() {
        ProgressionOwner owner = ProgressionOwner.player(PLAYER_ID);
        MemoryStore store = new MemoryStore();
        CountingOffering offering = new CountingOffering();
        FlameRitualExecutor executor = new FlameRitualExecutor(
                playerId -> owner,
                registryWith(syntheticRitual(true, RitualOutcome.levelOneCheckpoint(), new ArrayList<>())),
                store
        );
        Function<FlameRitual.Offering, FlameRitualExecutor.ExecutionResult> serverCaller =
                suppliedOffering -> executor.invoke(PLAYER_ID, RITUAL_ID, INTENT_ID, suppliedOffering);

        assertEquals(FlameRitualExecutor.Status.APPLIED, serverCaller.apply(offering).status());
        assertEquals(FlameRitualExecutor.Status.ALREADY_COMPLETED, serverCaller.apply(offering).status());
        assertEquals(1, offering.consumeCount());
    }

    @Test
    void intentAndOfferingContractsFailClosedBeforeConsumption() {
        ProgressionOwner owner = ProgressionOwner.player(PLAYER_ID);
        CountingOffering offering = new CountingOffering(false);
        FlameRitualExecutor executor = new FlameRitualExecutor(
                playerId -> owner,
                registryWith(syntheticRitual(true, RitualOutcome.levelOneCheckpoint(), new ArrayList<>())),
                new MemoryStore()
        );

        FlameRitualExecutor.ExecutionResult wrongIntent = executor.invoke(
                PLAYER_ID,
                RITUAL_ID,
                ResourceLocation.fromNamespaceAndPath("enshrouded", "wrong_intent"),
                offering
        );
        FlameRitualExecutor.ExecutionResult wrongOffering = executor.invoke(PLAYER_ID, RITUAL_ID, INTENT_ID, offering);

        assertEquals(FlameRitualExecutor.Status.INTENT_MISMATCH, wrongIntent.status());
        assertEquals(FlameRitualExecutor.Status.OFFERING_REJECTED, wrongOffering.status());
        assertEquals(0, offering.consumeCount());
    }

    private static FlameRitualRegistry registryWith(FlameRitual ritual) {
        FlameRitualRegistry registry = new FlameRitualRegistry();
        registry.register(ritual);
        return registry;
    }

    private static FlameRitual syntheticRitual(
            boolean eligible,
            RitualOutcome outcome,
            List<ProgressionOwner> ownersSeen) {
        return syntheticRitual(RITUAL_ID, eligible, outcome, ownersSeen);
    }

    private static FlameRitual syntheticRitual(
            ResourceLocation ritualId,
            boolean eligible,
            RitualOutcome outcome,
            List<ProgressionOwner> ownersSeen) {
        return new FlameRitual() {
            @Override
            public ResourceLocation id() {
                return ritualId;
            }

            @Override
            public ResourceLocation intentId() {
                return INTENT_ID;
            }

            @Override
            public boolean isEligible(Context context) {
                ownersSeen.add(context.owner());
                return eligible;
            }

            @Override
            public OfferingContract offering() {
                return countingOfferingContract(ownersSeen);
            }

            @Override
            public RitualOutcome outcome(Context context) {
                ownersSeen.add(context.owner());
                return outcome;
            }
        };
    }

    private static FlameRitual.OfferingContract countingOfferingContract(List<ProgressionOwner> ownersSeen) {
        return new FlameRitual.OfferingContract() {
            @Override
            public boolean accepts(FlameRitual.Context context, FlameRitual.Offering offering) {
                ownersSeen.add(context.owner());
                return offering instanceof CountingOffering countingOffering && countingOffering.accepted();
            }

            @Override
            public void consume(FlameRitual.Context context, FlameRitual.Offering offering) {
                ownersSeen.add(context.owner());
                ((CountingOffering) offering).consume();
            }
        };
    }

    private static final class CountingOffering implements FlameRitual.Offering {
        private final boolean accepted;
        private int consumeCount;

        private CountingOffering() {
            this(true);
        }

        private CountingOffering(boolean accepted) {
            this.accepted = accepted;
        }

        private boolean accepted() {
            return accepted;
        }

        private void consume() {
            consumeCount++;
        }

        private int consumeCount() {
            return consumeCount;
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
