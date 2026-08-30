package com.gustavaopere.enshrouded.story.state;

/** Version contract for persisted Lich/story state. */
public final class StorySchema {
    public static final int FIRST_VERSION = 1;
    public static final int CURRENT_VERSION = 1;

    private StorySchema() {
    }

    static void requireSupported(int schemaVersion) {
        if (schemaVersion < FIRST_VERSION || schemaVersion > CURRENT_VERSION) {
            throw new UnsupportedStorySchemaException(schemaVersion);
        }
    }

    static void requireManifestationIndex(int manifestationIndex) {
        if (manifestationIndex < 1) {
            throw new IllegalArgumentException("manifestationIndex must be >= 1");
        }
    }
}
