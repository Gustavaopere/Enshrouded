package com.gustavaopere.enshrouded.exposure.redsludge;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

final class RedSludgeSurfaceRedTest {
    @Test
    void plannedRedSludgeTypesExist() throws Exception {
        ClassLoader loader = RedSludgeSurfaceRedTest.class.getClassLoader();
        assertNotNull(Class.forName("com.gustavaopere.enshrouded.content.fluid.RedSludgeFluid", false, loader));
        assertNotNull(Class.forName("com.gustavaopere.enshrouded.content.fluid.RedSludgeBlock", false, loader));
        assertNotNull(Class.forName("com.gustavaopere.enshrouded.exposure.redsludge.RedSludgeExposureHandler", false, loader));
    }
}
