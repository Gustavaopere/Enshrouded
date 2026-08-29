package com.gustavaopere.enshrouded.ecology.state;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreService;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EntityCorruptionGameTests {
    private static final BlockPos SPAWN = new BlockPos(0, 1, 0);
    private static final UUID CANONICAL_CORE_ID = UUID.fromString("71111111-2222-3333-4444-555555555555");
    private static final UUID CANONICAL_REGION_ID = UUID.fromString("72222222-3333-4444-5555-666666666666");
    private static final UUID TAMED_CORE_ID = UUID.fromString("75555555-6666-7777-8888-999999999999");
    private static final UUID TAMED_REGION_ID = UUID.fromString("76666666-7777-8888-9999-aaaaaaaaaaaa");
    private static final UUID OWNER_ID = UUID.fromString("73333333-4444-5555-6666-777777777777");

    private EntityCorruptionGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void canonicalShroudCorruptsVanillaExamplesWithoutReplacingIdentity(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        seedEffectiveShroud(level, helper.absolutePos(SPAWN), CANONICAL_CORE_ID, CANONICAL_REGION_ID);

        Cow cow = helper.spawnWithNoFreeWill(EntityType.COW, SPAWN);
        Wolf wolf = helper.spawnWithNoFreeWill(EntityType.WOLF, SPAWN.offset(1, 0, 0));
        Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, SPAWN.offset(0, 0, 1));

        EntityType<?> cowType = cow.getType();
        EntityType<?> wolfType = wolf.getType();
        EntityType<?> zombieType = zombie.getType();

        EntityCorruptionRuntime.advanceNow(cow);
        EntityCorruptionRuntime.advanceNow(wolf);
        EntityCorruptionRuntime.advanceNow(zombie);

        helper.assertTrue(cow.getData(EntityCorruptionAttachment.ENTITY_CORRUPTION.get()).intensity() > 0.0F,
                "cow in canonical Shroud must gain corruption");
        helper.assertTrue(wolf.getData(EntityCorruptionAttachment.ENTITY_CORRUPTION.get()).intensity() > 0.0F,
                "wolf in canonical Shroud must gain corruption");
        helper.assertTrue(zombie.getData(EntityCorruptionAttachment.ENTITY_CORRUPTION.get()).intensity() > 0.0F,
                "zombie in canonical Shroud must gain corruption");
        helper.assertTrue(cow.getType() == cowType && wolf.getType() == wolfType && zombie.getType() == zombieType,
                "entity corruption must preserve original entity types");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void tamedWolfPreservesOwnerAndTameState(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        seedEffectiveShroud(level, helper.absolutePos(SPAWN), TAMED_CORE_ID, TAMED_REGION_ID);

        Wolf wolf = helper.spawnWithNoFreeWill(EntityType.WOLF, SPAWN);
        wolf.setOwnerUUID(OWNER_ID);
        wolf.setTame(true, false);
        wolf.setInSittingPose(true);

        EntityCorruptionRuntime.advanceNow(wolf);

        helper.assertTrue(wolf.getData(EntityCorruptionAttachment.ENTITY_CORRUPTION.get()).intensity() > 0.0F,
                "eligible tamed wolf must gain corruption state");
        helper.assertTrue(OWNER_ID.equals(wolf.getOwnerUUID()), "corruption must preserve owner UUID");
        helper.assertTrue(wolf.isTame(), "corruption must preserve tame state");
        helper.assertTrue(wolf.isInSittingPose(), "corruption must preserve unrelated entity state");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void clearSpaceRegressesExistingCorruptionWithoutReplacement(GameTestHelper helper) {
        Cow cow = helper.spawnWithNoFreeWill(EntityType.COW, SPAWN);
        cow.setData(EntityCorruptionAttachment.ENTITY_CORRUPTION.get(),
                new EntityCorruptionAttachment(EntityCorruptionSchema.CURRENT_VERSION, 0.60F));
        EntityType<?> originalType = cow.getType();

        EntityCorruptionRuntime.advanceNow(cow);

        helper.assertTrue(cow.getData(EntityCorruptionAttachment.ENTITY_CORRUPTION.get()).intensity() < 0.60F,
                "clear space must regress existing corruption");
        helper.assertTrue(cow.getType() == originalType, "regression must never replace entity identity");
        helper.succeed();
    }

    private static void seedEffectiveShroud(
            ServerLevel level,
            BlockPos absolutePos,
            UUID coreId,
            UUID regionId) {
        ShroudSavedData data = ShroudSavedData.get(level);
        ShroudWorldState state = ShroudCoreService.registerDormant(
                data.state(), coreId, regionId, absolutePos, 1, 64, 0x041E17L
        ).state();
        state = ShroudCoreService.activate(state, coreId).state();

        ShroudCellPos cell = ShroudGridGeometry.levelOne().cellAt(absolutePos);
        LinkedHashMap<ShroudCellPos, ShroudCellState> cells = new LinkedHashMap<>();
        cells.put(cell, new ShroudCellState(cell, 1.0D, ShroudSeverity.SHROUD));
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>(state.regions());
        regions.put(regionId, new ShroudRegionState(regionId, coreId, Map.copyOf(cells)));
        data.replace(new ShroudWorldState(state.schemaVersion(), state.cores(), regions));
    }
}
