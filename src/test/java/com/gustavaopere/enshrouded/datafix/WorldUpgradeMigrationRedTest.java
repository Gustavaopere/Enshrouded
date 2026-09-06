package com.gustavaopere.enshrouded.datafix;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionSchema;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionCodec;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionSchema;
import com.gustavaopere.enshrouded.shroud.state.ShroudSchema;
import com.gustavaopere.enshrouded.shroud.state.ShroudStateCodec;
import com.gustavaopere.enshrouded.story.state.StoryCodec;
import com.gustavaopere.enshrouded.story.state.StorySchema;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class WorldUpgradeMigrationRedTest {
    @Test
    void allPersistentSubsystemsExposeExplicitVersionContracts() {
        assertEquals(2, ShroudSchema.CURRENT_VERSION);
        assertEquals(2, ExposureSchema.CURRENT_VERSION);
        assertEquals(2, EntityCorruptionSchema.CURRENT_VERSION);
        assertEquals(2, FlameProgressionSchema.CURRENT_VERSION);
        assertEquals(2, StorySchema.CURRENT_VERSION);
    }

    @Test
    void syntheticLegacyFixturesMigrateToExactCurrentValues() throws Exception {
        CompoundTag shroud = EnshroudedDataFixer.migrate(PersistentSubsystem.SHROUD, fixture("shroud-v1.snbt"));
        assertEquals(ShroudSchema.CURRENT_VERSION, shroud.getInt("schema_version"));
        var shroudState = ShroudStateCodec.decode(shroud);
        UUID coreId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        assertEquals(1, shroudState.cores().size());
        assertEquals(64, shroudState.cores().get(coreId).maxInfluenceRadius());
        assertEquals(1, shroudState.regions().size());

        CompoundTag exposure = EnshroudedDataFixer.migrate(PersistentSubsystem.EXPOSURE, fixture("exposure-v1.snbt"));
        assertEquals(ExposureSchema.CURRENT_VERSION, exposure.getInt("schema_version"));
        assertEquals(4321, exposure.getInt("remaining_ticks"));

        CompoundTag corruption = EnshroudedDataFixer.migrate(
                PersistentSubsystem.ENTITY_CORRUPTION,
                fixture("entity-corruption-v1.snbt")
        );
        assertEquals(EntityCorruptionSchema.CURRENT_VERSION, corruption.getInt("schema_version"));
        assertEquals(0.625F, corruption.getFloat("intensity"));

        CompoundTag flame = EnshroudedDataFixer.migrate(PersistentSubsystem.FLAME_PROGRESSION, fixture("flame-v1.snbt"));
        assertEquals(FlameProgressionSchema.CURRENT_VERSION, flame.getInt("schema_version"));
        assertFalse(flame.getList("owners", CompoundTag.TAG_COMPOUND).getCompound(0).getBoolean("next_level_ready"));
        var flameState = FlameProgressionCodec.decode(flame);
        ProgressionOwner owner = ProgressionOwner.player(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertEquals(2, flameState.progression(owner).flameLevel());
        assertEquals(2, flameState.progression(owner).passageLevel());
        assertEquals(1, flameState.progression(owner).completedRituals().size());

        CompoundTag story = EnshroudedDataFixer.migrate(PersistentSubsystem.STORY, fixture("story-v1.snbt"));
        assertEquals(StorySchema.CURRENT_VERSION, story.getInt("schema_version"));
        var storyState = StoryCodec.decode(story);
        UUID encounterId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        assertTrue(storyState.encounter(encounterId).orElseThrow().rewardIssued());
        assertTrue(storyState.issueReward(encounterId).isEmpty(), "migrated rewarded encounter must remain exactly-once");
    }

    @Test
    void futurePreVersionedAndMalformedSchemasFailClosedWithSubsystemDiagnostics() throws Exception {
        CompoundTag future = fixture("exposure-v1.snbt");
        future.putInt("schema_version", ExposureSchema.CURRENT_VERSION + 1);

        UnsupportedPersistentSchemaException futureFailure = assertThrows(
                UnsupportedPersistentSchemaException.class,
                () -> EnshroudedDataFixer.migrate(PersistentSubsystem.EXPOSURE, future)
        );
        assertEquals(PersistentSubsystem.EXPOSURE, futureFailure.subsystem());
        assertEquals(ExposureSchema.CURRENT_VERSION + 1, futureFailure.schemaVersion());
        assertTrue(futureFailure.getMessage().contains("exposure"));

        CompoundTag preVersioned = fixture("story-v1.snbt");
        preVersioned.putInt("schema_version", 0);
        UnsupportedPersistentSchemaException preVersionedFailure = assertThrows(
                UnsupportedPersistentSchemaException.class,
                () -> EnshroudedDataFixer.migrate(PersistentSubsystem.STORY, preVersioned)
        );
        assertEquals(0, preVersionedFailure.schemaVersion());

        PersistentDataFormatException malformedFailure = assertThrows(
                PersistentDataFormatException.class,
                () -> EnshroudedDataFixer.migrate(PersistentSubsystem.STORY, fixture("corrupt-missing-schema.snbt"))
        );
        assertEquals(PersistentSubsystem.STORY, malformedFailure.subsystem());
        assertTrue(malformedFailure.getMessage().contains("schema_version"));
    }

    private static CompoundTag fixture(String name) throws Exception {
        String path = "/world-upgrades/" + name;
        try (InputStream stream = WorldUpgradeMigrationRedTest.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IOException("missing fixture: " + path);
            }
            return TagParser.parseTag(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
        }
    }
}
