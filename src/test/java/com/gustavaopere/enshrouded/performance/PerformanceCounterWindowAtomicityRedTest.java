package com.gustavaopere.enshrouded.performance;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class PerformanceCounterWindowAtomicityRedTest {

    @Test
    void pairedRecordersAndSnapshotsShareMethodLevelSynchronization() throws Exception {
        assertSynchronized("recordExpansion", long.class, long.class);
        assertSynchronized("recordRegression", long.class, long.class);
        assertSynchronized("recordMaterialization", long.class, long.class);
        assertSynchronized("recordRestoration", long.class, long.class);
        assertSynchronized("recordEntityUpdate", long.class, long.class);
        assertSynchronized("recordClientEffects", long.class, long.class);
        assertSynchronized("snapshot");
        assertSynchronized("snapshotAndReset");
    }

    private static void assertSynchronized(String name, Class<?>... parameterTypes) throws Exception {
        Method method = PerformanceCounters.class.getDeclaredMethod(name, parameterTypes);
        assertTrue(
                Modifier.isSynchronized(method.getModifiers()),
                () -> name + " must share the PerformanceCounters monitor so paired values cannot split across snapshot windows");
    }
}
