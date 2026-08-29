package com.gustavaopere.enshrouded.exposure.deadly;

import com.gustavaopere.enshrouded.exposure.DeadlyExposurePolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeadlyPassageSurfaceRedTest {
    @Test
    void plannedDeadlyPassageTypesExistBehindExistingPolicySeam() throws Exception {
        Class<?> requirement = Class.forName(
                "com.gustavaopere.enshrouded.exposure.deadly.PassageRequirement");
        Class<?> policy = Class.forName(
                "com.gustavaopere.enshrouded.exposure.deadly.FlameGatedDeadlyExposurePolicy");

        assertTrue(requirement.isRecord(), "PassageRequirement must be an immutable value contract");
        assertTrue(DeadlyExposurePolicy.class.isAssignableFrom(policy),
                "Deadly passage implementation must substitute the existing Task 01 policy seam");
    }
}
