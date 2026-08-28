package com.gustavaopere.enshrouded.shroud.worldgen;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShroudCoreSeedingRuntimeRedTest {
    private static final Path BLOCK_ENTITY = Path.of(
            "src/main/java/com/gustavaopere/enshrouded/shroud/core/ShroudCoreBlockEntity.java");

    @Test
    void coreOnLoadQueuesRegistrationInsteadOfWritingCanonicalStateDirectly() throws Exception {
        String source = Files.readString(BLOCK_ENTITY);
        int onLoadStart = source.indexOf("public void onLoad()");
        int retireStart = source.indexOf("CoreMutationResult retirePhysicalCore", onLoadStart);
        assertTrue(onLoadStart >= 0 && retireStart > onLoadStart, "Could not isolate ShroudCoreBlockEntity.onLoad");

        String onLoadBody = source.substring(onLoadStart, retireStart);
        assertTrue(onLoadBody.contains("ShroudCoreRegistrationQueue"),
                "Core onLoad must enqueue canonical registration for server-thread draining");
        assertFalse(onLoadBody.contains("ShroudSavedData.get"),
                "Core onLoad must not write canonical SavedData directly");
        assertFalse(onLoadBody.contains("ShroudCoreService.registerDormant"),
                "Core onLoad must not call canonical lifecycle mutation directly");
    }

    @Test
    void registrationQueueAndAdminCommandExist() throws Exception {
        ClassLoader loader = ShroudCoreSeedingRuntimeRedTest.class.getClassLoader();
        Class.forName("com.gustavaopere.enshrouded.shroud.core.ShroudCoreRegistrationQueue", false, loader);
        Class.forName("com.gustavaopere.enshrouded.command.ShroudCoreCommand", false, loader);
    }
}
