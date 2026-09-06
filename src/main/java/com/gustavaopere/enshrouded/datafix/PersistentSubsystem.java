package com.gustavaopere.enshrouded.datafix;

import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionSchema;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionSchema;
import com.gustavaopere.enshrouded.shroud.state.ShroudSchema;
import com.gustavaopere.enshrouded.story.state.StorySchema;

/** Persistent Level-1 subsystem formats owned by the centralized Stage 09.03 migration layer. */
public enum PersistentSubsystem {
    SHROUD("shroud", 0, ShroudSchema.CURRENT_VERSION),
    EXPOSURE("exposure", 0, ExposureSchema.CURRENT_VERSION),
    ENTITY_CORRUPTION("entity_corruption", 0, EntityCorruptionSchema.CURRENT_VERSION),
    FLAME_PROGRESSION("flame_progression", FlameProgressionSchema.FIRST_VERSION, FlameProgressionSchema.CURRENT_VERSION),
    STORY("story", 0, StorySchema.CURRENT_VERSION);

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
