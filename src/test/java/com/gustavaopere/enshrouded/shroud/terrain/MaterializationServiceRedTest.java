package com.gustavaopere.enshrouded.shroud.terrain;

import com.gustavaopere.enshrouded.api.shroud.MutationAuthority;
import com.gustavaopere.enshrouded.api.shroud.ShroudQuery;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterializationServiceRedTest {
    @Test
    void serviceRequiresAuthorityQueryAndExposesBoundedLoadedWorldRuntime() throws Exception {
        var constructor = ShroudMaterializationService.class.getDeclaredConstructor(
                CorruptionRuleRegistry.class,
                MutationAuthority.class,
                ShroudQuery.class,
                int.class
        );
        assertNotNull(constructor);
        assertNotNull(ShroudMaterializationService.class.getMethod(
                "schedule",
                ServerLevel.class,
                BlockPos.class,
                ShroudSample.class
        ));
        assertNotNull(ShroudMaterializationService.class.getMethod(
                "tick",
                ServerLevel.class,
                int.class,
                int.class
        ));
        assertNotNull(ShroudMaterializationService.class.getMethod("pendingWork"));
    }

    @Test
    void sourceKeepsLogicalShroudAndMutationAuthorityAheadOfEveryWorldMutationSink() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/shroud/terrain/ShroudMaterializationService.java"
        ));
        int shroudSample = source.indexOf("shroudQuery.sample(");
        int authorityCall = source.indexOf("canMutate(");
        int mutationSink = source.indexOf("setBlock(");

        assertTrue(shroudSample >= 0, "materialization must revalidate canonical logical Shroud");
        assertTrue(authorityCall > shroudSample, "materialization must consult MutationAuthority after logical Shroud revalidation");
        assertTrue(source.contains("MutationKind.CORRUPTION"), "materialization must use Foundation CORRUPTION kind");
        assertTrue(mutationSink > authorityCall, "MutationAuthority must be consulted before setBlock");
    }
}
