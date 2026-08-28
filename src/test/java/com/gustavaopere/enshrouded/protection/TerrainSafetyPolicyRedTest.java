package com.gustavaopere.enshrouded.protection;

import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerrainSafetyPolicyRedTest {
    @Test
    void policyMatrixIsFailClosedAndMutationKindAware() throws Exception {
        Class<?> policyType = Class.forName("com.gustavaopere.enshrouded.protection.MutationSafetyPolicy");
        Constructor<?> constructor = policyType.getDeclaredConstructor(MutationSafetyMode.class, boolean.class, boolean.class);
        constructor.setAccessible(true);
        Method allows = policyType.getDeclaredMethod(
                "allows",
                MutationKind.class,
                ProtectionDecision.class,
                boolean.class,
                boolean.class,
                boolean.class,
                boolean.class,
                boolean.class
        );
        allows.setAccessible(true);

        Object safe = constructor.newInstance(MutationSafetyMode.SAFE, false, false);
        Object aggressive = constructor.newInstance(MutationSafetyMode.AGGRESSIVE, false, false);
        Object expert = constructor.newInstance(MutationSafetyMode.SAFE, true, true);

        assertTrue(invoke(allows, safe, MutationKind.CORRUPTION, ProtectionDecision.UNPROTECTED,
                false, false, true, false, false));
        assertFalse(invoke(allows, safe, MutationKind.CORRUPTION, ProtectionDecision.UNPROTECTED,
                false, false, false, true, false));
        assertTrue(invoke(allows, aggressive, MutationKind.CORRUPTION, ProtectionDecision.UNPROTECTED,
                false, false, false, true, false));

        assertFalse(invoke(allows, safe, MutationKind.CORRUPTION, ProtectionDecision.UNPROTECTED,
                true, false, true, false, false));
        assertFalse(invoke(allows, safe, MutationKind.CORE_PLACEMENT, ProtectionDecision.UNPROTECTED,
                true, false, false, false, true));
        assertTrue(invoke(allows, safe, MutationKind.PURIFICATION, ProtectionDecision.UNPROTECTED,
                true, false, false, false, false));
        assertTrue(invoke(allows, safe, MutationKind.RITUAL_STRUCTURE, ProtectionDecision.UNPROTECTED,
                true, false, false, false, false));

        assertFalse(invoke(allows, safe, MutationKind.PURIFICATION, ProtectionDecision.PROTECTED,
                false, false, false, false, false));
        assertFalse(invoke(allows, safe, MutationKind.PURIFICATION, ProtectionDecision.INDETERMINATE,
                false, false, false, false, false));
        assertTrue(invoke(allows, expert, MutationKind.PURIFICATION, ProtectionDecision.INDETERMINATE,
                false, false, false, false, false));

        assertFalse(invoke(allows, safe, MutationKind.PURIFICATION, ProtectionDecision.UNPROTECTED,
                false, true, false, false, false));
        assertTrue(invoke(allows, expert, MutationKind.PURIFICATION, ProtectionDecision.UNPROTECTED,
                false, true, false, false, false));

        assertTrue(invoke(allows, safe, MutationKind.CORE_PLACEMENT, ProtectionDecision.UNPROTECTED,
                false, false, false, false, true));
        assertFalse(invoke(allows, safe, MutationKind.CORE_PLACEMENT, ProtectionDecision.UNPROTECTED,
                false, false, false, false, false));
    }

    private static boolean invoke(
            Method method,
            Object policy,
            MutationKind kind,
            ProtectionDecision decision,
            boolean warded,
            boolean blockEntity,
            boolean safeTagged,
            boolean aggressiveTagged,
            boolean replaceable) throws Exception {
        return (boolean) method.invoke(
                policy,
                kind,
                decision,
                warded,
                blockEntity,
                safeTagged,
                aggressiveTagged,
                replaceable
        );
    }
}
