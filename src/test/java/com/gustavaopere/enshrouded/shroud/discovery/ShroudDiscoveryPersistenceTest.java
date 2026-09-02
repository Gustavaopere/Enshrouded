package com.gustavaopere.enshrouded.shroud.discovery;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShroudDiscoveryPersistenceTest {
    @Test
    void roundTripsOwnerScopedKnowledgeAcrossDimensionsAndLifecycles() {
        ProgressionOwner player = ProgressionOwner.player(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        ProgressionOwner team = ProgressionOwner.team("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        DiscoveredCore active = new DiscoveredCore(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                "minecraft:overworld", new BlockPos(12, 70, -8), CoreLifecycleState.ACTIVE);
        DiscoveredCore destroyed = new DiscoveredCore(
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                "minecraft:the_nether", new BlockPos(-32, 64, 48), CoreLifecycleState.DESTROYED);
        DiscoveredCore purified = new DiscoveredCore(
                UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
                "minecraft:overworld", new BlockPos(96, 80, 24), CoreLifecycleState.PURIFIED);

        ShroudDiscoveryState original = ShroudDiscoveryState.empty()
                .discover(player, active)
                .discover(player, destroyed)
                .discover(team, purified);

        CompoundTag encoded = ShroudDiscoveryCodec.encode(original);
        assertEquals(ShroudDiscoverySchema.CURRENT_VERSION, encoded.getInt("schema_version"));
        assertEquals(original, ShroudDiscoveryCodec.decode(encoded));
        assertEquals(encoded, ShroudDiscoveryCodec.encode(ShroudDiscoveryCodec.decode(encoded)));
    }

    @Test
    void rejectsUnknownFutureSchemaInsteadOfGuessing() {
        CompoundTag tag = ShroudDiscoveryCodec.encode(ShroudDiscoveryState.empty());
        tag.putInt("schema_version", ShroudDiscoverySchema.CURRENT_VERSION + 1);

        assertThrows(UnsupportedShroudDiscoverySchemaException.class, () -> ShroudDiscoveryCodec.decode(tag));
    }

    @Test
    void rejectsInvalidOwnerStableKeyAndDuplicateCoreEntries() {
        DiscoveredCore core = new DiscoveredCore(
                UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"),
                "minecraft:overworld", BlockPos.ZERO, CoreLifecycleState.ACTIVE);
        ShroudDiscoveryState state = ShroudDiscoveryState.fromStableKeys(Map.of(
                ProgressionOwner.team("team-one").stableKey(), Map.of(core.coreId(), core)));
        CompoundTag encoded = ShroudDiscoveryCodec.encode(state);
        ListTag owners = encoded.getList("owners", CompoundTag.TAG_COMPOUND);
        CompoundTag owner = owners.getCompound(0);

        CompoundTag invalidOwner = encoded.copy();
        invalidOwner.getList("owners", CompoundTag.TAG_COMPOUND).getCompound(0).putString("owner", "not-an-owner");
        assertThrows(IllegalArgumentException.class, () -> ShroudDiscoveryCodec.decode(invalidOwner));

        CompoundTag duplicate = encoded.copy();
        ListTag cores = duplicate.getList("owners", CompoundTag.TAG_COMPOUND).getCompound(0)
                .getList("cores", CompoundTag.TAG_COMPOUND);
        cores.add(cores.getCompound(0).copy());
        assertThrows(IllegalArgumentException.class, () -> ShroudDiscoveryCodec.decode(duplicate));
    }
}
