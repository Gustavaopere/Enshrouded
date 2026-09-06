package com.gustavaopere.enshrouded.datafix;

import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionSchema;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionSchema;
import com.gustavaopere.enshrouded.shroud.state.ShroudSchema;
import com.gustavaopere.enshrouded.story.state.StorySchema;

/** Persistent Level-1 subsystem formats owned by the centralized Stage 09.03 migration layer. */
public enum PersistentSubsystem {
    SHROUD("shroud", ShroudSchema.FIRST_VERSION, ShroudSchema.CURRENT_VERSION),
    EXPOSURE("exposure", ExposureSchema.FIRST_VERSION, ExposureSchema.CURRENT_VERSION),
    ENTITY_CORRUPTION("entity corruption", EntityCorruptionSchema.FIRST_VERSION, EntityCorruptionSchema.CURRENT_VERSION),
    FLAME_PROGRESSION("flame_progression", FlameProgressionSchema.FIRST_VERSION, FlameProgressionSchema.CURRENT_VERSION),
    STORY("story", StorySchema.FIRST_VERSION, StorySchema.CURRENT_VERSION);

    private final String id;
    private final int oldestMigratableVersion;
    private final int currentVersion;

    PersistentSubsystem(String id, int oldestMigratableVersion, int currentVersion) {
        this.id = id;
        this.oldestMigratableVersion = oldestMigratableVersion;
        this.currentVersion = currentVersion;
    }

    public String id() {
        return id;
    }

    public int oldestMigratableVersion() {
        return oldestMigratableVersion;
    }

    public int currentVersion() {
        return currentVersion;
    }
}
