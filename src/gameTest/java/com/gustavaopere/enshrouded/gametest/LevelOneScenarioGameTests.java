package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionAttachment;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionRuntime;
import com.gustavaopere.enshrouded.exposure.DeadlyExposurePolicy;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.exposure.ExposureService;
import com.gustavaopere.enshrouded.exposure.ShroudExposureAttachment;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarService;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualExecutor;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualRegistry;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionSavedData;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreBlockEntity;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreService;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudExpansionScheduler;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudPropagationPolicy;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudWorkBudget;
import com.gustavaopere.enshrouded.shroud.purification.PurificationPolicy;
import com.gustavaopere.enshrouded.shroud.purification.ShroudRegressionScheduler;
import com.gustavaopere.enshrouded.shroud.query.DefaultShroudQuery;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import com.gustavaopere.enshrouded.story.manifestation.ManifestationEncounterService;
import com.gustavaopere.enshrouded.story.manifestation.ManifestationRuntime;
import com.gustavaopere.enshrouded.story.reward.LichSkullItem;
import com.gustavaopere.enshrouded.story.ritual.LevelOneLichSkullRitual;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class LevelOneScenarioGameTests {
    private static final String BATCH = "levelOneScenario";
    private static final ShroudGridGeometry GEOMETRY = ShroudGridGeometry.levelOne();
    private static final UUID EXPANSION_RELOAD_CORE_ID = UUID.fromString("71090101-0000-4000-8000-000000000001");
    private static final UUID EXPANSION_RELOAD_REGION_ID = UUID.fromString("71090101-0000-4000-8000-000000000002");
    private static final BlockPos EXPANSION_RELOAD_CENTER = new BlockPos(28672, 80, 28672);
    private static final UUID EXPOSURE_RELOAD_PLAYER_ID = UUID.fromString("71090101-0000-4000-8000-000000000003");
    private static final String EXPOSURE_RELOAD_PLAYER_NAME = "Enshrouded09Exposure";

    private LevelOneScenarioGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH, timeoutTicks = 120)
    public static void completeStandaloneLevelOneVerticalSlice(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ShroudSavedData shroudData = ShroudSavedData.get(level);
        BlockPos coreRelative = new BlockPos(1, 1, 1);
        BlockPos coreAbsolute = helper.absolutePos(coreRelative);
        UUID playerId = UUID.randomUUID();

        helper.setBlock(coreRelative, ModBlocks.SHROUD_CORE.get());
        helper.assertTrue(level.getBlockEntity(coreAbsolute) instanceof ShroudCoreBlockEntity,
                "Level-1 scenario must start from a physical Shroud Core");
        ((ShroudCoreBlockEntity) level.getBlockEntity(coreAbsolute)).requestAutomaticActivation();

        helper.runAtTickTime(6L, () -> {
            var core = shroudData.state().cores().values().stream()
                    .filter(candidate -> candidate.center().equals(coreAbsolute))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("automatic core seed must register canonically"));
            helper.assertTrue(core.lifecycleState() == CoreLifecycleState.ACTIVE,
                    "seeded Level-1 core must be ACTIVE before player exposure");
            ShroudRegionState region = shroudData.state().regions().get(core.regionId());
            helper.assertTrue(region != null && !region.cells().isEmpty(),
                    "active core must have begun canonical logical expansion");

            DefaultShroudQuery query = DefaultShroudQuery.levelOne(GEOMETRY);
            ShroudSample shroud = query.sample(level, coreAbsolute, null);
            helper.assertTrue(shroud.severity() == ShroudSeverity.SHROUD,
                    "core field must expose ordinary SHROUD through the canonical query");

            int reserve = ExposureSchema.DEFAULT_MAX_RESERVE_TICKS;
            ExposureService exposure = new ExposureService(reserve, 1, 1, 100, DeadlyExposurePolicy.levelOneBarrier());
            ShroudExposureAttachment drained = exposure.tick(
                    playerId,
                    ShroudExposureAttachment.full(reserve),
                    shroud,
                    20
            ).attachmentState();
            helper.assertTrue(drained.remainingTicks() < reserve,
                    "ordinary Shroud must drain the canonical exposure reserve");

            Cow cow = EntityType.COW.create(level);
            helper.assertTrue(cow != null, "Level-1 scenario must construct a corruption target");
            if (cow == null) {
                return;
            }
            cow.moveTo(coreAbsolute.getX() + 0.5D, coreAbsolute.getY(), coreAbsolute.getZ() + 0.5D);
            helper.assertTrue(level.addFreshEntity(cow), "corruption target must enter the server level");
            EntityCorruptionRuntime.advanceNow(cow);
            helper.assertTrue(cow.getData(EntityCorruptionAttachment.ENTITY_CORRUPTION.get()).intensity() > 0.0F,
                    "mob inside the same canonical Shroud field must gain corruption");
            cow.discard();

            BlockPos clearPos = new BlockPos(-28672, 80, -28672);
            level.getChunkAt(clearPos);
            ShroudSample clear = query.sample(level, clearPos, null);
            helper.assertTrue(clear.severity() == ShroudSeverity.CLEAR,
                    "escape fixture must sample canonical CLEAR space");
            ShroudExposureAttachment recovered = exposure.tick(playerId, drained, clear, 20).attachmentState();
            helper.assertTrue(recovered.remainingTicks() > drained.remainingTicks(),
                    "escaping ordinary Shroud must recover exposure reserve");

            helper.destroyBlock(coreRelative);
            var destroyed = shroudData.state().cores().get(core.id());
            helper.assertTrue(destroyed != null && destroyed.lifecycleState() == CoreLifecycleState.DESTROYED,
                    "destroying the physical core must transition the logical core to DESTROYED");

            ShroudRegionState destroyedRegion = shroudData.state().regions().get(core.regionId());
            helper.assertTrue(destroyedRegion != null && !destroyedRegion.cells().isEmpty(),
                    "destroyed core must retain logical cells until bounded regression processes them");
            ShroudWorldState isolatedDestroyed = new ShroudWorldState(
                    shroudData.state().schemaVersion(),
                    Map.of(core.id(), destroyed),
                    Map.of(core.regionId(), destroyedRegion)
            );
            ShroudRegressionScheduler.TickResult regression = new ShroudRegressionScheduler(
                    GEOMETRY,
                    PurificationPolicy.levelOne()
            ).tick(isolatedDestroyed, 1, 1);
            helper.assertTrue(!regression.regressedCells().isEmpty(),
                    "destroyed Level-1 field must perform bounded logical regression");
            helper.assertTrue(regression.regressedCells().getFirst().currentIntensity()
                            < regression.regressedCells().getFirst().previousIntensity(),
                    "regression must reduce or clear Shroud intensity instead of re-expanding it");

            ManifestationEncounterService.ActiveEncounter active = ManifestationRuntime.service().start(
                    level,
                    playerId,
                    coreAbsolute.offset(4, 0, 0)
            ).orElseThrow(() -> new AssertionError("Level-1 manifestation must start after core lifecycle exercise"));
            AABB dropSearch = active.entity().getBoundingBox().inflate(4.0D);
            active.entity().setHealth(0.0F);
            NeoForge.EVENT_BUS.post(new LivingDeathEvent(active.entity(), level.damageSources().generic()));

            List<ItemEntity> skullDrops = level.getEntitiesOfClass(
                    ItemEntity.class,
                    dropSearch,
                    item -> LichSkullItem.isAuthenticLevelOne(item.getItem())
                            && LichSkullItem.encounterId(item.getItem()).filter(active.encounterId()::equals).isPresent()
            );
            helper.assertTrue(skullDrops.size() == 1,
                    "valid manifestation defeat must emit exactly one authentic Level-1 Lich skull");

            ItemEntity skullDrop = skullDrops.getFirst();
            ItemStackHandler altarInventory = new ItemStackHandler(1);
            altarInventory.setStackInSlot(0, skullDrop.getItem().copy());
            skullDrop.discard();
            FlameRitualRegistry ritualRegistry = new FlameRitualRegistry();
            ritualRegistry.register(new LevelOneLichSkullRitual());
            FlameAltarService altar = new FlameAltarService(ritualRegistry);
            FlameAltarService.ActivationResult altarResult = altar.activate(
                    playerId,
                    altarInventory,
                    FlameRitualExecutor.forServer(level.getServer(), ritualRegistry)
            );
            var progression = FlameProgressionSavedData.get(level).progression(active.owner());
            helper.assertTrue(altarResult.status() == FlameAltarService.Status.APPLIED,
                    "authentic skull must complete the concrete Level-1 Flame ritual");
            helper.assertTrue(progression.completedRituals().contains(LevelOneLichSkullRitual.RITUAL_ID),
                    "Level-1 completion must persist the concrete manifestation ritual ID");
            helper.assertTrue(progression.nextLevelReady(),
                    "complete Level-1 vertical slice must end at next-level readiness");
            helper.assertTrue(progression.flameLevel() == 1 && progression.passageLevel() == 1,
                    "Level-1 completion must not silently grant Flame/Passage Level 2");
            if (!active.entity().isRemoved()) {
                active.entity().discard();
            }
            helper.succeed();
        });
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void expansionFrontierRebuildsFromPersistedStateAfterRealRestart(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ShroudSavedData data = ShroudSavedData.get(level);
        if (!data.state().cores().containsKey(EXPANSION_RELOAD_CORE_ID)) {
            ShroudWorldState state = ShroudCoreService.registerDormant(
                    data.state(),
                    EXPANSION_RELOAD_CORE_ID,
                    EXPANSION_RELOAD_REGION_ID,
                    EXPANSION_RELOAD_CENTER,
                    1,
                    128,
                    0x0901EADL
            ).state();
            state = ShroudCoreService.activate(state, EXPANSION_RELOAD_CORE_ID).state();
            ShroudCellPos centerCell = GEOMETRY.cellAt(EXPANSION_RELOAD_CENTER);
            LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>(state.regions());
            regions.put(EXPANSION_RELOAD_REGION_ID, new ShroudRegionState(
                    EXPANSION_RELOAD_REGION_ID,
                    EXPANSION_RELOAD_CORE_ID,
                    Map.of(centerCell, new ShroudCellState(centerCell, 1.0D, ShroudSeverity.SHROUD))
            ));
            data.replace(new ShroudWorldState(state.schemaVersion(), state.cores(), regions));
            GameTestBootstrap.forceSaveForReload(helper);
            System.out.println("ENSHROUDED_EXPANSION_MID_CREATED");
            helper.succeed();
            return;
        }

        var core = data.state().cores().get(EXPANSION_RELOAD_CORE_ID);
        ShroudRegionState before = data.state().regions().get(EXPANSION_RELOAD_REGION_ID);
        helper.assertTrue(core.lifecycleState() == CoreLifecycleState.ACTIVE,
                "second boot must retain the active mid-expansion core");
        helper.assertTrue(before != null && !before.cells().isEmpty(),
                "second boot must retain persisted logical cells used to rebuild runtime frontier");

        ShroudExpansionScheduler scheduler = new ShroudExpansionScheduler(
                GEOMETRY,
                ShroudPropagationPolicy.levelOne(),
                256
        );
        ShroudExpansionScheduler.TickResult advanced = scheduler.tick(
                data.state(),
                new ShroudWorkBudget(16, 16)
        );
        ShroudRegionState after = advanced.state().regions().get(EXPANSION_RELOAD_REGION_ID);
        helper.assertTrue(advanced.appliedCells() > 0,
                "fresh runtime scheduler must rebuild frontier from persisted cells after restart");
        helper.assertTrue(after.cells().size() > before.cells().size(),
                "reloaded active field must resume expansion rather than stall or reset");
        System.out.println("ENSHROUDED_EXPANSION_MID_RELOADED");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void exposureAttachmentSurvivesRealPlayerDataRestart(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ServerPlayer player = new ServerPlayer(
                level.getServer(),
                level,
                new GameProfile(EXPOSURE_RELOAD_PLAYER_ID, EXPOSURE_RELOAD_PLAYER_NAME),
                ClientInformation.createDefault()
        );
        var attachmentType = ShroudExposureAttachment.PLAYER_EXPOSURE.get();
        ShroudExposureAttachment sentinel = new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, 4321);

        if (!player.hasData(attachmentType)) {
            player.setData(attachmentType, sentinel);
            level.getServer().getPlayerList().save(player);
            GameTestBootstrap.forceSaveForReload(helper);
            System.out.println("ENSHROUDED_EXPOSURE_MID_CREATED");
            helper.succeed();
            return;
        }

        level.getServer().getPlayerList().load(player);
        helper.assertTrue(player.hasData(attachmentType),
                "second boot must load the persisted player exposure attachment from playerdata");
        ShroudExposureAttachment restored = player.getData(attachmentType);
        helper.assertTrue(restored.equals(sentinel),
                "real restart must preserve the exact exposure reserve instead of resetting it");
        ShroudSample unsafe = new ShroudSample(0.5F, ShroudSeverity.SHROUD, java.util.Optional.empty(), false);
        ExposureService service = new ExposureService(
                ExposureSchema.DEFAULT_MAX_RESERVE_TICKS,
                1,
                1,
                100,
                DeadlyExposurePolicy.levelOneBarrier()
        );
        ShroudExposureAttachment continued = service.tick(player.getUUID(), restored, unsafe, 20).attachmentState();
        helper.assertTrue(continued.remainingTicks() == 4301,
                "post-restart exposure must continue draining from persisted reserve");
        System.out.println("ENSHROUDED_EXPOSURE_MID_RELOADED");
        helper.succeed();
    }
}
