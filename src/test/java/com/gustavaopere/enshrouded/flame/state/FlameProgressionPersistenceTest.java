package com.gustavaopere.enshrouded.flame.state;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FlameProgressionPersistenceTest {
    @Test
    void roundTripsSchemaOwnersLevelsReadinessAndStableRitualIds() {
        ProgressionOwner player = ProgressionOwner.player(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        ProgressionOwner team = ProgressionOwner.team("ftb:builders");
        ResourceLocation firstRitual = ResourceLocation.fromNamespaceAndPath("enshrouded", "synthetic_first");
        ResourceLocation secondRitual = ResourceLocation.fromNamespaceAndPath("enshrouded", "synthetic_second");

        FlameProgressionState state = FlameProgressionState.empty()
                .applyRitualCheckpoint(player, firstRitual, 2, 3, true).orElseThrow()
                .applyRitualCheckpoint(team, secondRitual, 4, 5).orElseThrow();

        CompoundTag encoded = FlameProgressionCodec.encode(state);

        assertEquals(FlameProgressionSchema.CURRENT_VERSION, encoded.getInt("schema_version"));
        assertEquals(state, FlameProgressionCodec.decode(encoded));
        assertEquals(encoded, FlameProgressionCodec.encode(FlameProgressionCodec.decode(encoded)));
        assertTrue(FlameProgressionCodec.decode(encoded).progression(player).nextLevelReady());
        assertFalse(FlameProgressionCodec.decode(encoded).progression(team).nextLevelReady());
    }

    @Test
    void schemaOneMigratesToCurrentWithSafeReadinessDefault() {
        ProgressionOwner owner = ProgressionOwner.player(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        ResourceLocation ritual = ResourceLocation.fromNamespaceAndPath("enshrouded", "legacy_schema_one");
        FlameProgressionState current = FlameProgressionState.empty()
                .applyRitualCheckpoint(owner, ritual, 1, 1, true)
                .orElseThrow();
        CompoundTag legacy = FlameProgressionCodec.encode(current);
        legacy.putInt("schema_version", 1);
        legacy.getList("owners", CompoundTag.TAG_COMPOUND).getCompound(0).remove("next_level_ready");

        FlameProgressionState migrated = FlameProgressionCodec.decode(legacy);

        assertEquals(FlameProgressionSchema.CURRENT_VERSION, migrated.schemaVersion());
        assertFalse(migrated.progression(owner).nextLevelReady());
        assertEquals(1, migrated.progression(owner).passageLevel());
        assertEquals(1, migrated.progression(owner).flameLevel());
    }

    @Test
    void futureSchemaFailsClosedInsteadOfResettingProgression() {
        CompoundTag encoded = FlameProgressionCodec.encode(FlameProgressionState.empty());
        encoded.putInt("schema_version", FlameProgressionSchema.CURRENT_VERSION + 1);

        UnsupportedFlameProgressionSchemaException failure = assertThrows(
                UnsupportedFlameProgressionSchemaException.class,
                () -> FlameProgressionCodec.decode(encoded)
        );

        assertEquals(FlameProgressionSchema.CURRENT_VERSION + 1, failure.schemaVersion());
    }

    @Test
    void preVersionedOrInvalidSchemaFailsClosed() {
        CompoundTag encoded = FlameProgressionCodec.encode(FlameProgressionState.empty());
        encoded.putInt("schema_version", 0);

        assertThrows(UnsupportedFlameProgressionSchemaException.class, () -> FlameProgressionCodec.decode(encoded));
    }

    @Test
    void duplicateOwnerEntriesFromCorruptedInputAreRejected() {
        ProgressionOwner owner = ProgressionOwner.player(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        ResourceLocation ritual = ResourceLocation.fromNamespaceAndPath("enshrouded", "synthetic_duplicate_owner");
        FlameProgressionState state = FlameProgressionState.empty()
                .applyRitualCheckpoint(owner, ritual, 1, 1).orElseThrow();
        CompoundTag encoded = FlameProgressionCodec.encode(state);
        var owners = encoded.getList("owners", CompoundTag.TAG_COMPOUND);
        owners.add(owners.getCompound(0).copy());

        assertThrows(IllegalArgumentException.class, () -> FlameProgressionCodec.decode(encoded));
    }
}
