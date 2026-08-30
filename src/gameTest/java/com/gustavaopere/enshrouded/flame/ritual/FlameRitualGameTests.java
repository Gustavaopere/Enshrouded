package com.gustavaopere.enshrouded.flame.ritual;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionRuntimeBindings;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionSavedData;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.concurrent.atomic.AtomicInteger;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FlameRitualGameTests {
    private static final String FLAME_RITUAL_BATCH = "flameRitual";
    private static final ResourceLocation RITUAL_ID =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "synthetic_level1_server_checkpoint");
    private static final ResourceLocation INTENT_ID =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "synthetic_level1_server_intent");

    private FlameRitualGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = FLAME_RITUAL_BATCH)
    public static void serverCallerInvokesSyntheticRitualExactlyOnceWithoutAltarClasses(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        Player player = GameTestBootstrap.makeMockPlayer(helper, GameType.SURVIVAL);
        ProgressionOwner expectedOwner = ProgressionRuntimeBindings.ownerResolver().resolve(player.getUUID());
        AtomicInteger consumed = new AtomicInteger();

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
                return context.owner().equals(expectedOwner)
                        && context.progression().flameLevel() == 1
                        && context.progression().passageLevel() == 1;
            }

            @Override
            public OfferingContract offering() {
                return new OfferingContract() {
                    @Override
                    public boolean accepts(Context context, Offering offering) {
                        return context.owner().equals(expectedOwner) && offering instanceof SyntheticOffering;
                    }

                    @Override
                    public void consume(Context context, Offering offering) {
                        if (!(offering instanceof SyntheticOffering)) {
                            throw new IllegalStateException("unexpected synthetic offering type");
                        }
                        consumed.incrementAndGet();
                    }
                };
            }

            @Override
            public RitualOutcome outcome(Context context) {
                return RitualOutcome.levelOneCheckpoint();
            }
        };

        FlameRitualRegistry registry = new FlameRitualRegistry();
        registry.register(ritual);
        FlameRitualExecutor executor = FlameRitualExecutor.forServer(level.getServer(), registry);
        SyntheticOffering offering = new SyntheticOffering();

        FlameRitualExecutor.ExecutionResult first = executor.invoke(player.getUUID(), RITUAL_ID, INTENT_ID, offering);
        FlameRitualExecutor.ExecutionResult duplicate = executor.invoke(player.getUUID(), RITUAL_ID, INTENT_ID, offering);

        helper.assertTrue(first.status() == FlameRitualExecutor.Status.APPLIED,
                "First server-side synthetic ritual invocation must apply");
        helper.assertTrue(duplicate.status() == FlameRitualExecutor.Status.ALREADY_COMPLETED,
                "Duplicate server-side invocation must be idempotently rejected");
        helper.assertTrue(first.owner().equals(expectedOwner) && duplicate.owner().equals(expectedOwner),
                "Server caller must retain the same resolved progression owner");
        helper.assertTrue(consumed.get() == 1,
                "Synthetic offering must be consumed exactly once across duplicate invocations");

        var progression = FlameProgressionSavedData.get(level).progression(expectedOwner);
        helper.assertTrue(progression.completedRituals().contains(RITUAL_ID),
                "Successful server ritual must persist its stable ritual checkpoint");
        helper.assertTrue(progression.nextLevelReady(),
                "Level-1 checkpoint must persist story readiness");
        helper.assertTrue(progression.flameLevel() == 1 && progression.passageLevel() == 1,
                "Level-1 ritual completion must not grant Flame or Passage Level 2");
        helper.succeed();
    }

    private static final class SyntheticOffering implements FlameRitual.Offering {
    }
}
