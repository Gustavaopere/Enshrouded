package com.gustavaopere.enshrouded.exposure.redsludge;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

final class RedSludgeSurfaceRedTest {
    @Test
    void plannedRedSludgeTypesExist() throws Exception {
        assertNotNull(Class.forName("com.gustavaopere.enshrouded.content.fluid.RedSludgeFluid"));
        assertNotNull(Class.forName("com.gustavaopere.enshrouded.content.fluid.RedSludgeBlock"));
        assertNotNull(Class.forName("com.gustavaopere.enshrouded.exposure.redsludge.RedSludgeExposureHandler"));
    }
}
