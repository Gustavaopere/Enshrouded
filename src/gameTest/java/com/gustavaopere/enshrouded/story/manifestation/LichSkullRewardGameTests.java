package com.gustavaopere.enshrouded.story.manifestation;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarService;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualExecutor;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualRegistry;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionSavedData;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionState;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import com.gustavaopere.enshrouded.story.reward.LichSkullItem;
import com.gustavaopere.enshrouded.story.ritual.LevelOneLichSkullRitual;
import com.gustavaopere.enshrouded.story.state.LichStoryState;
import com.gustavaopere.enshrouded.story.state.StorySavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class LichSkullRewardGameTests {
    private static final String BATCH = "lichSkullReward";

    private LichSkullRewardGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void validMarkedDeathEmitsOneAuthenticSkullAndReplayEmitsNone(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        StorySavedData storyData = StorySavedData.get(level);
        LichStoryState storyBefore = storyData.state();
        storyData.replace(LichStoryState.empty());

        UUID playerId = UUID.fromString("60604002-0000-4000-8000-000000000001");
        ManifestationEncounterService.ActiveEncounter active = null;
        AABB dropSearch = null;
        try {
            active = ManifestationRuntime.service().start(
                    level,
                    playerId,
                    helper.absolutePos(new BlockPos(1, 1, 1))
            ).orElseThrow(() -> new AssertionError("first manifestation fixture must start"));
            dropSearch = active.entity().getBoundingBox().inflate(3.0D);
            active.entity().setHealth(0.0F);

            ManifestationRuntime.onLivingDeath(new LivingDeathEvent(
                    active.entity(),
                    level.damageSources().generic()
            ));

            List<ItemEntity> firstDrops = authenticDrops(level, dropSearch, active.encounterId());
            helper.assertTrue(firstDrops.size() == 1,
                    "valid marked death must emit exactly one authentic Enshrouded Lich skull");
            helper.assertTrue(storyData.state().encounter(active.encounterId()).orElseThrow().rewardIssued(),
                    "successful reward emission must persist rewardIssued=true on the defeated encounter");

            ManifestationRuntime.onLivingDeath(new LivingDeathEvent(
                    active.entity(),
                    level.damageSources().generic()
            ));
            helper.assertTrue(authenticDrops(level, dropSearch, active.encounterId()).size() == 1,
                    "replayed death callback must not emit a second Lich skull");

            helper.succeed();
        } finally {
            if (dropSearch != null) {
                level.getEntitiesOfClass(ItemEntity.class, dropSearch).forEach(ItemEntity::discard);
            }
            if (active != null && !active.entity().isRemoved()) {
                active.entity().discard();
            }
            storyData.replace(storyBefore);
        }
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void defeatToSkullToAltarClosesLevelOneCheckpointWithoutPassageTwo(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        StorySavedData storyData = StorySavedData.get(level);
        FlameProgressionSavedData flameData = FlameProgressionSavedData.get(level);
        LichStoryState storyBefore = storyData.state();
        FlameProgressionState flameBefore = flameData.state();
        storyData.replace(LichStoryState.empty());
        flameData.replace(FlameProgressionState.empty());

        UUID playerId = UUID.fromString("60604003-0000-4000-8000-000000000001");
        ManifestationEncounterService.ActiveEncounter active = null;
        AABB dropSearch = null;
        try {
            active = ManifestationRuntime.service().start(
                    level,
                    playerId,
                    helper.absolutePos(new BlockPos(1, 1, 1))
            ).orElseThrow(() -> new AssertionError("first manifestation fixture must start"));
            dropSearch = active.entity().getBoundingBox().inflate(3.0D);
            active.entity().setHealth(0.0F);

            ManifestationRuntime.onLivingDeath(new LivingDeathEvent(
                    active.entity(),
                    level.damageSources().generic()
            ));
            ItemEntity droppedSkull = authenticDrops(level, dropSearch, active.encounterId()).stream()
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("defeat must produce the authentic ritual offering"));

            ItemStackHandler altarInventory = new ItemStackHandler(1);
            altarInventory.setStackInSlot(0, droppedSkull.getItem().copy());
            droppedSkull.discard();

            FlameRitualRegistry registry = new FlameRitualRegistry();
            registry.register(new LevelOneLichSkullRitual());
            FlameAltarService altar = new FlameAltarService(registry);
            FlameAltarService.ActivationResult result = altar.activate(
                    playerId,
                    altarInventory,
                    FlameRitualExecutor.forServer(level.getServer(), registry)
            );
            FlameProgressionState.OwnerProgression progression = flameData.progression(active.owner());

            helper.assertTrue(result.status() == FlameAltarService.Status.APPLIED,
                    "authentic first-manifestation skull must delegate through the Stage-05 altar/executor and apply");
            helper.assertTrue(altarInventory.getStackInSlot(0).isEmpty(),
                    "successful ritual must consume exactly one authentic skull offering");
            helper.assertTrue(progression.completedRituals().contains(LevelOneLichSkullRitual.RITUAL_ID),
                    "successful offering must persist the concrete lich_manifestation_1 checkpoint exactly once");
            helper.assertTrue(progression.nextLevelReady(),
                    "Level-1 story loop completion must mark the next-level readiness checkpoint");
            helper.assertTrue(progression.flameLevel() == 1,
                    "Level-1 skull ritual must not advance Flame Level in this release");
            helper.assertTrue(progression.passageLevel() == 1,
                    "Level-1 skull ritual must not grant Passage Level 2");

            helper.succeed();
        } finally {
            if (dropSearch != null) {
                level.getEntitiesOfClass(ItemEntity.class, dropSearch).forEach(ItemEntity::discard);
            }
            if (active != null && !active.entity().isRemoved()) {
                active.entity().discard();
            }
            flameData.replace(flameBefore);
            storyData.replace(storyBefore);
        }
    }

    private static List<ItemEntity> authenticDrops(ServerLevel level, AABB bounds, UUID encounterId) {
        return level.getEntitiesOfClass(
                ItemEntity.class,
                bounds,
                entity -> LichSkullItem.isAuthenticLevelOne(entity.getItem())
                        && LichSkullItem.encounterId(entity.getItem()).filter(encounterId::equals).isPresent()
        );
    }
}
