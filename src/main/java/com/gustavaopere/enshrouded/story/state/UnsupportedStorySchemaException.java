package com.gustavaopere.enshrouded.story.state;

public final class UnsupportedStorySchemaException extends IllegalStateException {
    private final int schemaVersion;

    public UnsupportedStorySchemaException(int schemaVersion) {
        super("Unsupported Enshrouded story schema version: " + schemaVersion
                + " (supported " + StorySchema.FIRST_VERSION + ".." + StorySchema.CURRENT_VERSION + ")");
        this.schemaVersion = schemaVersion;
    }

    public int schemaVersion() {
        return schemaVersion;
    }
}
