package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionAttachment;
import com.gustavaopere.enshrouded.exposure.ShroudExposureAttachment;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionSavedData;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.gustavaopere.enshrouded.story.state.StorySavedData;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WorldUpgradeGameTests {
    private static final String BATCH = "worldUpgrade";
    private static final UUID CORE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID REGION_ID = UUID.fromString("00000000-0000-0000-0000-000000000101");
    private static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ENCOUNTER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private WorldUpgradeGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH, timeoutTicks = 40)
    public static void legacyPersistenceMigratesAndCurrentReloadStaysIdempotent(GameTestHelper helper) {
        var level = GameTestBootstrap.requireServerLevel(helper);
        var registries = level.registryAccess();

        ShroudSavedData shroud = ShroudSavedData.load(legacyShroud(), registries);
        helper.assertTrue(shroud.state().cores().size() == 1, "legacy Shroud must migrate one exact core");
        helper.assertTrue(shroud.state().regions().size() == 1, "legacy Shroud must migrate one exact region");
        helper.assertTrue(shroud.state().cores().get(CORE_ID).regionId().equals(REGION_ID),
                "legacy Shroud migration must preserve core-region identity");
        CompoundTag shroudCurrent = shroud.save(new CompoundTag(), registries);
        ShroudSavedData shroudReloaded = ShroudSavedData.load(shroudCurrent, registries);
        helper.assertTrue(shroudReloaded.state().cores().size() == 1,
                "current reload after migration must not duplicate Shroud core identity");

        ShroudExposureAttachment exposure = ShroudExposureAttachment.CODEC
                .parse(NbtOps.INSTANCE, legacyExposure()).getOrThrow();
        helper.assertTrue(exposure.remainingTicks() == 4321,
                "legacy exposure migration must preserve exact unsafe reserve");
        var exposureCurrent = ShroudExposureAttachment.CODEC.encodeStart(NbtOps.INSTANCE, exposure).getOrThrow();
        ShroudExposureAttachment exposureReloaded = ShroudExposureAttachment.CODEC
                .parse(NbtOps.INSTANCE, exposureCurrent).getOrThrow();
        helper.assertTrue(exposureReloaded.remainingTicks() == 4321,
                "current exposure reload must preserve exact migrated reserve");

        EntityCorruptionAttachment corruption = EntityCorruptionAttachment.CODEC
                .parse(NbtOps.INSTANCE, legacyEntityCorruption()).getOrThrow();
        helper.assertTrue(Math.abs(corruption.intensity() - 0.625F) < 0.0001F,
                "legacy entity corruption migration must preserve exact intensity");
        var corruptionCurrent = EntityCorruptionAttachment.CODEC.encodeStart(NbtOps.INSTANCE, corruption).getOrThrow();
        EntityCorruptionAttachment corruptionReloaded = EntityCorruptionAttachment.CODEC
                .parse(NbtOps.INSTANCE, corruptionCurrent).getOrThrow();
        helper.assertTrue(Math.abs(corruptionReloaded.intensity() - 0.625F) < 0.0001F,
                "current entity corruption reload must preserve migrated identity/state");

        ProgressionOwner owner = ProgressionOwner.player(OWNER_ID);
        FlameProgressionSavedData flame = FlameProgressionSavedData.load(legacyFlame(), registries);
        helper.assertTrue(flame.progression(owner).flameLevel() == 2 && flame.progression(owner).passageLevel() == 2,
                "legacy Flame migration must preserve exact progression levels");
        helper.assertTrue(flame.progression(owner).completedRituals().size() == 1,
                "legacy Flame migration must preserve exactly one ritual identity");
        helper.assertTrue(!flame.progression(owner).nextLevelReady(),
                "legacy Flame v1 migration must use fail-safe readiness default false");
        CompoundTag flameCurrent = flame.save(new CompoundTag(), registries);
        FlameProgressionSavedData flameReloaded = FlameProgressionSavedData.load(flameCurrent, registries);
        helper.assertTrue(flameReloaded.progression(owner).completedRituals().size() == 1,
                "current Flame reload after migration must not duplicate ritual identity");

        StorySavedData story = StorySavedData.load(legacyStory(), registries);
        helper.assertTrue(story.state().encounter(ENCOUNTER_ID).orElseThrow().rewardIssued(),
                "legacy Story migration must preserve already-issued reward state");
        helper.assertTrue(!story.issueReward(ENCOUNTER_ID),
                "migrated rewarded encounter must reject duplicate reward issuance");
        CompoundTag storyCurrent = story.save(new CompoundTag(), registries);
        StorySavedData storyReloaded = StorySavedData.load(storyCurrent, registries);
        helper.assertTrue(storyReloaded.state().encounter(ENCOUNTER_ID).orElseThrow().rewardIssued(),
                "current Story reload must preserve reward-issued identity");
        helper.assertTrue(!storyReloaded.issueReward(ENCOUNTER_ID),
                "current Story reload must remain exactly-once after migration");

        System.out.println("ENSHROUDED_WORLD_UPGRADE_PASSED");
        helper.succeed();
    }

    private static CompoundTag legacyShroud() {
        CompoundTag root = new CompoundTag();
        root.putInt("schema_version", 1);

        CompoundTag core = new CompoundTag();
        core.putString("id", CORE_ID.toString());
        core.putInt("center_x", 8);
        core.putInt("center_y", 64);
        core.putInt("center_z", 8);
        core.putInt("tier", 1);
        core.putString("lifecycle_state", "active");
        core.putInt("max_influence_radius", 64);
        core.putLong("expansion_seed", 123L);
        core.putLong("expansion_epoch", 2L);
        core.putString("region_id", REGION_ID.toString());
        ListTag cores = new ListTag();
        cores.add(core);
        root.put("cores", cores);

        CompoundTag cell = new CompoundTag();
        cell.putInt("x", 1);
        cell.putInt("y", 8);
        cell.putInt("z", 1);
        cell.putDouble("intensity", 0.75D);
        cell.putString("severity", "shroud");
        ListTag cells = new ListTag();
        cells.add(cell);

        CompoundTag region = new CompoundTag();
        region.putString("id", REGION_ID.toString());
        region.putString("core_id", CORE_ID.toString());
        region.put("cells", cells);
        ListTag regions = new ListTag();
        regions.add(region);
        root.put("regions", regions);
        return root;
    }

    private static CompoundTag legacyExposure() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("schema_version", 1);
        tag.putInt("remaining_ticks", 4321);
        return tag;
    }

    private static CompoundTag legacyEntityCorruption() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("schema_version", 1);
        tag.putFloat("intensity", 0.625F);
        return tag;
    }

    private static CompoundTag legacyFlame() {
        CompoundTag root = new CompoundTag();
        root.putInt("schema_version", 1);
        CompoundTag owner = new CompoundTag();
        owner.putString("owner", ProgressionOwner.player(OWNER_ID).stableKey());
        owner.putInt("flame_level", 2);
        owner.putInt("passage_level", 2);
        ListTag rituals = new ListTag();
        rituals.add(StringTag.valueOf("enshrouded:legacy_level_one"));
        owner.put("completed_rituals", rituals);
        ListTag owners = new ListTag();
        owners.add(owner);
        root.put("owners", owners);
        return root;
    }

    private static CompoundTag legacyStory() {
        CompoundTag root = new CompoundTag();
        root.putInt("schema_version", 1);
        String ownerKey = ProgressionOwner.player(OWNER_ID).stableKey();

        CompoundTag manifestation = new CompoundTag();
        manifestation.putString("owner", ownerKey);
        manifestation.putInt("current_manifestation_index", 1);
        ListTag defeated = new ListTag();
        defeated.add(IntTag.valueOf(1));
        manifestation.put("defeated_manifestations", defeated);
        ListTag manifestations = new ListTag();
        manifestations.add(manifestation);
        root.put("manifestations", manifestations);

        CompoundTag encounter = new CompoundTag();
        encounter.putString("encounter_id", ENCOUNTER_ID.toString());
        encounter.putInt("manifestation_index", 1);
        encounter.putString("owner", ownerKey);
        encounter.putString("outcome", "defeated");
        encounter.putBoolean("reward_issued", true);
        ListTag encounters = new ListTag();
        encounters.add(encounter);
        root.put("encounters", encounters);
        return root;
    }
}
