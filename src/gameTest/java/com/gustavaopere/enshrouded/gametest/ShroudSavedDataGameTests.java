package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.gustavaopere.enshrouded.shroud.state.ShroudSchema;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ShroudSavedDataGameTests {
    private static final UUID CORE_ID = UUID.fromString("0f8fad5b-d9cb-469f-a165-70867728950e");
    private static final UUID REGION_ID = UUID.fromString("7c9e6679-7425-40de-944b-e07fc1f90ae7");
    private static final ShroudCellPos CELL_POS = new ShroudCellPos(3, 1, -2);
    private static final BlockPos GROWTH_RELOAD_SENTINEL_POS = new BlockPos(320, 96, 320);

    private ShroudSavedDataGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void shroudSavedDataIsLevelScopedAndRestartRecognizable(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ShroudSavedData data = ShroudSavedData.get(level);
        helper.assertTrue(data == ShroudSavedData.get(level), "Same ServerLevel must return the same SavedData instance");

        // The external two-boot harness deliberately reuses the same GameTest world. Keep the
        // growth sentinel outside test-structure cleanup so the second boot proves real block persistence.
        level.getChunkAt(GROWTH_RELOAD_SENTINEL_POS);
        if (!data.state().cores().containsKey(CORE_ID)) {
            data.replace(withSentinel(data.state()));
            helper.assertTrue(data.isDirty(), "Installing the Shroud reload sentinel must mark SavedData dirty");
            helper.assertTrue(
                    level.setBlock(
                            GROWTH_RELOAD_SENTINEL_POS,
                            ModBlocks.SHROUD_GROWTH.get().defaultBlockState(),
                            Block.UPDATE_ALL
                    ),
                    "First boot must place the Shroud growth persistence sentinel"
            );
            GameTestBootstrap.forceSaveForReload(helper);
            System.out.println("ENSHROUDED_SHROUD_SAVEDDATA_CREATED");
            System.out.println("ENSHROUDED_GROWTH_BLOCK_CREATED");
        } else {
            assertSentinelPreserved(helper, data.state());
            helper.assertTrue(
                    level.getBlockState(GROWTH_RELOAD_SENTINEL_POS).is(ModBlocks.SHROUD_GROWTH.get()),
                    "Second boot must reload the persisted Shroud growth block"
            );
            System.out.println("ENSHROUDED_SHROUD_SAVEDDATA_RELOADED");
            System.out.println("ENSHROUDED_GROWTH_BLOCK_RELOADED");
        }

        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void shroudSavedDataDoesNotAliasAcrossDimensions(GameTestHelper helper) {
        ServerLevel current = GameTestBootstrap.requireServerLevel(helper);
        ServerLevel nether = current.getServer().getLevel(Level.NETHER);
        helper.assertTrue(nether != null, "GameTest server must expose the Nether for dimension-isolation verification");

        ShroudSavedData currentData = ShroudSavedData.get(current);
        ShroudSavedData netherData = ShroudSavedData.get(nether);
        helper.assertTrue(currentData != netherData, "Distinct ServerLevel data storages must not share ShroudSavedData instances");

        if (current.dimension() != Level.NETHER) {
            helper.assertTrue(netherData.state().cores().isEmpty(), "A Shroud core stored in the GameTest level must not leak into Nether storage");
        }
        helper.succeed();
    }

    private static ShroudWorldState withSentinel(ShroudWorldState existing) {
        ShroudWorldState sentinel = sentinelState();
        if (existing.regions().containsKey(REGION_ID)) {
            throw new IllegalStateException("Shroud reload sentinel region id collided with pre-existing state");
        }

        LinkedHashMap<UUID, ShroudCoreState> cores = new LinkedHashMap<>(existing.cores());
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>(existing.regions());
        cores.put(CORE_ID, sentinel.cores().get(CORE_ID));
        regions.put(REGION_ID, sentinel.regions().get(REGION_ID));
        return new ShroudWorldState(existing.schemaVersion(), cores, regions);
    }

    private static void assertSentinelPreserved(GameTestHelper helper, ShroudWorldState actual) {
        ShroudWorldState sentinel = sentinelState();
        helper.assertTrue(
                sentinel.cores().get(CORE_ID).equals(actual.cores().get(CORE_ID)),
                "Reloaded Shroud SavedData must preserve the sentinel core exactly"
        );
        helper.assertTrue(
                sentinel.regions().get(REGION_ID).equals(actual.regions().get(REGION_ID)),
                "Reloaded Shroud SavedData must preserve the sentinel region/cell data exactly"
        );
    }

    private static ShroudWorldState sentinelState() {
        // Keep the persistence sentinel inert. ACTIVE cores are intentionally advanced by the
        // runtime scheduler after load, which would make an exact codec/reload assertion invalid.
        ShroudCoreState core = new ShroudCoreState(
                CORE_ID,
                new BlockPos(96, 64, -48),
                1,
                CoreLifecycleState.DORMANT,
                128,
                0x51A0D5EEDL,
                11L,
                REGION_ID
        );
        ShroudCellState cell = new ShroudCellState(CELL_POS, 0.625D, ShroudSeverity.SHROUD);
        ShroudRegionState region = new ShroudRegionState(REGION_ID, CORE_ID, Map.of(CELL_POS, cell));
        return new ShroudWorldState(
                ShroudSchema.CURRENT_VERSION,
                Map.of(CORE_ID, core),
                Map.of(REGION_ID, region)
        );
    }
}
