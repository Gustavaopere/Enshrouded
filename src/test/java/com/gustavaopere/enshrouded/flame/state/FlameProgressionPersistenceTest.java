package com.gustavaopere.enshrouded.flame.state;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class FlameProgressionPersistenceTest {
    @Test
    void roundTripsSchemaOwnersLevelsAndStableRitualIds() {
        ProgressionOwner player = ProgressionOwner.player(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        ProgressionOwner team = ProgressionOwner.team("ftb:builders");
        ResourceLocation firstRitual = ResourceLocation.fromNamespaceAndPath("enshrouded", "synthetic_first");
        ResourceLocation secondRitual = ResourceLocation.fromNamespaceAndPath("enshrouded", "synthetic_second");

        FlameProgressionState state = FlameProgressionState.empty()
                .applyRitualCheckpoint(player, firstRitual, 2, 3).orElseThrow()
                .applyRitualCheckpoint(team, secondRitual, 4, 5).orElseThrow();

        CompoundTag encoded = FlameProgressionCodec.encode(state);

        assertEquals(FlameProgressionSchema.CURRENT_VERSION, encoded.getInt("schema_version"));
        assertEquals(state, FlameProgressionCodec.decode(encoded));
        assertEquals(encoded, FlameProgressionCodec.encode(FlameProgressionCodec.decode(encoded)));
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

    @Test
    void missingOwnersListFailsClosedInsteadOfDecodingAsEmptyProgression() {
        CompoundTag encoded = FlameProgressionCodec.encode(FlameProgressionState.empty());
        encoded.remove("owners");

        assertThrows(IllegalArgumentException.class, () -> FlameProgressionCodec.decode(encoded));
    }

    @Test
    void mistypedOwnersListFailsClosedInsteadOfDecodingAsEmptyProgression() {
        CompoundTag encoded = FlameProgressionCodec.encode(FlameProgressionState.empty());
        ListTag wrongType = new ListTag();
        wrongType.add(StringTag.valueOf("not-an-owner-record"));
        encoded.put("owners", wrongType);

        assertThrows(IllegalArgumentException.class, () -> FlameProgressionCodec.decode(encoded));
    }

    @Test
    void missingCompletedRitualListFailsClosedInsteadOfErasingOwnerHistory() {
        ProgressionOwner owner = ProgressionOwner.player(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        ResourceLocation ritual = ResourceLocation.fromNamespaceAndPath("enshrouded", "synthetic_history");
        CompoundTag encoded = FlameProgressionCodec.encode(
                FlameProgressionState.empty().applyRitualCheckpoint(owner, ritual, 1, 1).orElseThrow()
        );
        encoded.getList("owners", CompoundTag.TAG_COMPOUND).getCompound(0).remove("completed_rituals");

        assertThrows(IllegalArgumentException.class, () -> FlameProgressionCodec.decode(encoded));
    }
}
