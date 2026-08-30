package com.gustavaopere.enshrouded.flame.state;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionRuntimeBindings;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FlameProgressionGameTests {
    private static final String FLAME_STATE_BATCH = "flameState";
    private static final UUID RELOAD_OWNER_ID = UUID.fromString("5f05a111-1aa1-4e51-9c31-05f05a111111");
    private static final ProgressionOwner RELOAD_OWNER = ProgressionOwner.player(RELOAD_OWNER_ID);
    private static final ResourceLocation RELOAD_RITUAL =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "flame_state_reload_sentinel");

    private FlameProgressionGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = FLAME_STATE_BATCH)
    public static void standalonePlayersRemainIndependentAndStorageIsServerGlobal(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ServerLevel nether = level.getServer().getLevel(Level.NETHER);
        helper.assertTrue(nether != null, "GameTest server must expose the Nether for global Flame-state verification");

        FlameProgressionSavedData current = FlameProgressionSavedData.get(level);
        FlameProgressionSavedData fromNether = FlameProgressionSavedData.get(nether);
        helper.assertTrue(current == fromNether,
                "Flame progression must be server-global rather than accidentally split by dimension");

        Player firstPlayer = GameTestBootstrap.makeMockPlayer(helper, GameType.SURVIVAL);
        Player secondPlayer = GameTestBootstrap.makeMockPlayer(helper, GameType.SURVIVAL);
        helper.assertTrue(!firstPlayer.getUUID().equals(secondPlayer.getUUID()),
                "GameTest must provide distinct player UUIDs for owner isolation");

        var resolver = new DefaultProgressionOwnerResolver();
        ProgressionOwner firstOwner = resolver.resolve(firstPlayer.getUUID());
        ProgressionOwner secondOwner = resolver.resolve(secondPlayer.getUUID());
        helper.assertTrue(!firstOwner.equals(secondOwner),
                "Standalone resolver must keep distinct players in distinct progression owners");

        ResourceLocation ritual = ResourceLocation.fromNamespaceAndPath(
                Enshrouded.MOD_ID,
                "owner_isolation_" + firstPlayer.getUUID().toString().replace("-", "")
        );
        helper.assertTrue(current.applyRitualCheckpoint(firstOwner, ritual, 1, 2),
                "First owner must accept its first synthetic progression checkpoint");
        helper.assertTrue(current.progression(firstOwner).passageLevel() == 2,
                "First owner must observe its persisted passage advancement");
        helper.assertTrue(current.progression(secondOwner).passageLevel() == 1,
                "Second owner must remain at the Level-1 baseline");

        ProgressionOwner boundFirst = ProgressionRuntimeBindings.ownerResolver().resolve(firstPlayer.getUUID());
        ProgressionOwner boundSecond = ProgressionRuntimeBindings.ownerResolver().resolve(secondPlayer.getUUID());
        helper.assertTrue(boundFirst.equals(firstOwner) && boundSecond.equals(secondOwner),
                "Runtime owner binding must preserve standalone player ownership");
        helper.assertTrue(ProgressionRuntimeBindings.passageQuery().passageLevel(boundFirst) == 2,
                "Runtime passage binding must read the persistent Stage-05 provider, not the Level-1 fallback");
        helper.assertTrue(ProgressionRuntimeBindings.passageQuery().passageLevel(boundSecond) == 1,
                "Runtime passage binding must preserve owner isolation");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty", batch = FLAME_STATE_BATCH)
    public static void flameProgressionSurvivesRealTwoBootSaveReload(GameTestHelper helper) {
        FlameProgressionSavedData data = FlameProgressionSavedData.get(GameTestBootstrap.requireServerLevel(helper));
        FlameProgressionState.OwnerProgression progression = data.progression(RELOAD_OWNER);

        if (!progression.completedRituals().contains(RELOAD_RITUAL)) {
            helper.assertTrue(data.applyRitualCheckpoint(RELOAD_OWNER, RELOAD_RITUAL, 1, 2),
                    "First boot must install the Flame progression reload sentinel exactly once");
            GameTestBootstrap.forceSaveForReload(helper);
            System.out.println("ENSHROUDED_FLAME_PROGRESSION_CREATED");
        } else {
            helper.assertTrue(progression.flameLevel() == 1,
                    "Reloaded Flame sentinel must preserve Flame Level 1 exactly");
            helper.assertTrue(progression.passageLevel() == 2,
                    "Reloaded Flame sentinel must preserve passage level exactly");
            helper.assertTrue(progression.completedRituals().contains(RELOAD_RITUAL),
                    "Reloaded Flame sentinel must preserve the completed ritual checkpoint");
            System.out.println("ENSHROUDED_FLAME_PROGRESSION_RELOADED");
        }
        helper.succeed();
    }
}
