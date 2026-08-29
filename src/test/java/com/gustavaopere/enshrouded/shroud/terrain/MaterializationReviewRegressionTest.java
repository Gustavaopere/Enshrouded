package com.gustavaopere.enshrouded.shroud.terrain;

import com.gustavaopere.enshrouded.api.shroud.MutationAuthority;
import com.gustavaopere.enshrouded.api.shroud.ShroudQuery;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterializationReviewRegressionTest {
    @Test
    void queuedJobsRevalidateAuthoritativeShroudBeforeMutation() throws Exception {
        var constructor = ShroudMaterializationService.class.getDeclaredConstructor(
                CorruptionRuleRegistry.class,
                MutationAuthority.class,
                ShroudQuery.class,
                int.class
        );
        assertNotNull(constructor);

        String source = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/shroud/terrain/ShroudMaterializationService.java"
        ));
        int apply = source.indexOf("private boolean apply");
        int resample = source.indexOf("shroudQuery.sample(", apply);
        int authority = source.indexOf("canMutate(", apply);
        int mutation = source.indexOf("setBlock(", apply);

        assertTrue(apply >= 0, "materialization must have a single apply sink");
        assertTrue(resample > apply, "queued work must re-sample canonical logical Shroud before applying");
        assertTrue(authority > resample, "logical Shroud must be revalidated before MutationAuthority");
        assertTrue(mutation > authority, "MutationAuthority must remain ahead of setBlock");
    }

    @Test
    void ruleSafetyClassParticipatesInCentralSourceEligibility() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/shroud/terrain/ShroudMaterializationService.java"
        ));

        assertTrue(source.contains("rule.safetyClass()"), "materialization must enforce the rule safety classification");
        assertTrue(source.contains("TerrainSafetyTags.CORRUPTIBLE_SAFE"), "SAFE rules must align with the central safe tag");
        assertTrue(source.contains("TerrainSafetyTags.CORRUPTIBLE_AGGRESSIVE"), "AGGRESSIVE rules must align with the central aggressive tag");
    }
}
