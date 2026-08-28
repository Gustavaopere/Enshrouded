package com.gustavaopere.enshrouded.shroud.state;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class ShroudStatePersistenceTest {
    @Test
    void roundTripsSparseVersionedWorldState() {
        UUID coreId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID regionId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        ShroudCoreState core = new ShroudCoreState(
                coreId,
                new BlockPos(128, 64, -32),
                1,
                "dormant",
                192,
                0x5EEDL,
                7L,
                regionId
        );
        ShroudCellState first = new ShroudCellState(new ShroudCellPos(4, 2, -1), 0.35D, ShroudSeverity.SHROUD);
        ShroudCellState second = new ShroudCellState(new ShroudCellPos(9, 3, 5), 0.90D, ShroudSeverity.DEADLY);
        ShroudRegionState region = new ShroudRegionState(
                regionId,
                coreId,
                Map.of(first.position(), first, second.position(), second)
        );
        ShroudWorldState original = new ShroudWorldState(
                ShroudSchema.CURRENT_VERSION,
                Map.of(coreId, core),
                Map.of(regionId, region)
        );

        CompoundTag encoded = ShroudStateCodec.encode(original);
        assertEquals(ShroudSchema.CURRENT_VERSION, encoded.getInt("schema_version"));
        assertEquals(original, ShroudStateCodec.decode(encoded));
    }

    @Test
    void emptyWorldRoundTripIsIdempotent() {
        ShroudWorldState empty = ShroudWorldState.empty();

        CompoundTag once = ShroudStateCodec.encode(empty);
        ShroudWorldState loaded = ShroudStateCodec.decode(once);
        CompoundTag twice = ShroudStateCodec.encode(loaded);

        assertEquals(empty, loaded);
        assertEquals(once, twice);
    }

    @Test
    void rejectsUnknownFutureSchemaInsteadOfGuessing() {
        CompoundTag tag = ShroudStateCodec.encode(ShroudWorldState.empty());
        tag.putInt("schema_version", ShroudSchema.CURRENT_VERSION + 1);

        assertThrows(UnsupportedShroudSchemaException.class, () -> ShroudStateCodec.decode(tag));
    }

    @Test
    void rejectsDuplicateCoreIdsFromCorruptedInput() {
        UUID coreId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID regionId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        ShroudCoreState core = new ShroudCoreState(
                coreId,
                new BlockPos(0, 64, 0),
                1,
                "active",
                64,
                4L,
                0L,
                regionId
        );
        ShroudWorldState state = new ShroudWorldState(
                ShroudSchema.CURRENT_VERSION,
                Map.of(coreId, core),
                Map.of()
        );
        CompoundTag corrupted = ShroudStateCodec.encode(state);
        ListTag cores = corrupted.getList("cores", CompoundTag.TAG_COMPOUND);
        cores.add(cores.getCompound(0).copy());

        assertThrows(IllegalArgumentException.class, () -> ShroudStateCodec.decode(corrupted));
    }

    @Test
    void rejectsImpossibleCoreRadiusAndInvalidCellIntensity() {
        UUID coreId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        UUID regionId = UUID.fromString("66666666-6666-6666-6666-666666666666");

        assertThrows(IllegalArgumentException.class, () -> new ShroudCoreState(
                coreId,
                BlockPos.ZERO,
                1,
                "active",
                0,
                1L,
                0L,
                regionId
        ));
        assertThrows(IllegalArgumentException.class, () -> new ShroudCellState(
                new ShroudCellPos(0, 0, 0),
                Double.NaN,
                ShroudSeverity.SHROUD
        ));
    }

    @Test
    void independentWorldStatesCanReuseLocalCoordinatesWithoutSharingState() {
        ShroudCellPos local = new ShroudCellPos(2, 1, 2);
        UUID firstCore = UUID.fromString("77777777-7777-7777-7777-777777777777");
        UUID secondCore = UUID.fromString("88888888-8888-8888-8888-888888888888");
        UUID firstRegion = UUID.fromString("99999999-9999-9999-9999-999999999999");
        UUID secondRegion = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        ShroudWorldState overworldLike = new ShroudWorldState(
                ShroudSchema.CURRENT_VERSION,
                Map.of(firstCore, new ShroudCoreState(firstCore, BlockPos.ZERO, 1, "active", 32, 1L, 0L, firstRegion)),
                Map.of(firstRegion, new ShroudRegionState(firstRegion, firstCore,
                        Map.of(local, new ShroudCellState(local, 0.25D, ShroudSeverity.SHROUD))))
        );
        ShroudWorldState netherLike = new ShroudWorldState(
                ShroudSchema.CURRENT_VERSION,
                Map.of(secondCore, new ShroudCoreState(secondCore, BlockPos.ZERO, 1, "active", 32, 2L, 0L, secondRegion)),
                Map.of(secondRegion, new ShroudRegionState(secondRegion, secondCore,
                        Map.of(local, new ShroudCellState(local, 0.75D, ShroudSeverity.DEADLY))))
        );

        assertNotEquals(overworldLike.regions().get(firstRegion), netherLike.regions().get(secondRegion));
        assertEquals(0.25D, overworldLike.regions().get(firstRegion).cells().get(local).intensity());
        assertEquals(0.75D, netherLike.regions().get(secondRegion).cells().get(local).intensity());
    }
}
