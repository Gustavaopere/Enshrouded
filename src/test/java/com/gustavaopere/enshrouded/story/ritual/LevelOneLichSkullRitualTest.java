package com.gustavaopere.enshrouded.story.ritual;

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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LevelOneLichSkullRitualTest {
    private static final UUID PLAYER_ID = UUID.fromString("60604005-0000-4000-8000-000000000001");
    private static final ProgressionOwner OWNER = ProgressionOwner.player(PLAYER_ID);

    @Test
    void concreteBindingUsesGenericExecutorExactlyOnceAndPreservesPassageOne() {
        AtomicInteger consumes = new AtomicInteger();
        TestOffering accepted = new TestOffering(true);
        LevelOneLichSkullRitual ritual = new LevelOneLichSkullRitual(new LevelOneLichSkullRitual.OfferingPolicy() {
            @Override
            public boolean accepts(FlameRitual.Offering offering) {
                return offering instanceof TestOffering testOffering && testOffering.accepted();
            }

            @Override
            public void consume(FlameRitual.Offering offering) {
                consumes.incrementAndGet();
            }
        });
        FlameRitualRegistry registry = new FlameRitualRegistry();
        registry.register(ritual);
        MemoryProgressionStore store = new MemoryProgressionStore();
        FlameRitualExecutor executor = new FlameRitualExecutor(ignored -> OWNER, registry, store);

        FlameRitualExecutor.ExecutionResult first = executor.invoke(
                PLAYER_ID,
                LevelOneLichSkullRitual.RITUAL_ID,
                LevelOneLichSkullRitual.INTENT_ID,
                accepted
        );
        FlameRitualExecutor.ExecutionResult replay = executor.invoke(
                PLAYER_ID,
                LevelOneLichSkullRitual.RITUAL_ID,
                LevelOneLichSkullRitual.INTENT_ID,
                accepted
        );
        FlameProgressionState.OwnerProgression progression = store.state.progression(OWNER);

        assertEquals(FlameRitualExecutor.Status.APPLIED, first.status());
        assertEquals(FlameRitualExecutor.Status.ALREADY_COMPLETED, replay.status());
        assertEquals(1, consumes.get());
        assertTrue(progression.completedRituals().contains(LevelOneLichSkullRitual.RITUAL_ID));
        assertTrue(progression.nextLevelReady());
        assertEquals(1, progression.flameLevel());
        assertEquals(1, progression.passageLevel());
    }

    @Test
    void rejectedOfferingFailsClosedBeforeConsumption() {
        AtomicInteger consumes = new AtomicInteger();
        LevelOneLichSkullRitual ritual = new LevelOneLichSkullRitual(new LevelOneLichSkullRitual.OfferingPolicy() {
            @Override
            public boolean accepts(FlameRitual.Offering offering) {
                return false;
            }

            @Override
            public void consume(FlameRitual.Offering offering) {
                consumes.incrementAndGet();
            }
        });
        FlameRitualRegistry registry = new FlameRitualRegistry();
        registry.register(ritual);
        MemoryProgressionStore store = new MemoryProgressionStore();
        FlameRitualExecutor executor = new FlameRitualExecutor(ignored -> OWNER, registry, store);

        FlameRitualExecutor.ExecutionResult result = executor.invoke(
                PLAYER_ID,
                LevelOneLichSkullRitual.RITUAL_ID,
                LevelOneLichSkullRitual.INTENT_ID,
                new TestOffering(false)
        );

        assertEquals(FlameRitualExecutor.Status.OFFERING_REJECTED, result.status());
        assertEquals(0, consumes.get());
        assertTrue(store.state.progression(OWNER).completedRituals().isEmpty());
    }

    private record TestOffering(boolean accepted) implements FlameRitual.Offering {
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
    }
}
