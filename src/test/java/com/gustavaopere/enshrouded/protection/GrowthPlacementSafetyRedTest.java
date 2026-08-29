package com.gustavaopere.enshrouded.protection;

import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrowthPlacementSafetyRedTest {
    @Test
    void growthPlacementKindIsThreatIntroducingAndRequiresReplaceableTarget() throws Exception {
        MutationKind growth = MutationKind.valueOf("GROWTH_PLACEMENT");
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

        assertTrue(invoke(allows, safe, growth, ProtectionDecision.UNPROTECTED,
                false, false, false, false, true));
        assertFalse(invoke(allows, safe, growth, ProtectionDecision.UNPROTECTED,
                false, false, false, false, false));
        assertFalse(invoke(allows, safe, growth, ProtectionDecision.UNPROTECTED,
                true, false, false, false, true));
        assertFalse(invoke(allows, safe, growth, ProtectionDecision.PROTECTED,
                false, false, false, false, true));
        assertFalse(invoke(allows, safe, growth, ProtectionDecision.UNPROTECTED,
                false, true, false, false, true));
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
        return (boolean) method.invoke(policy, kind, decision, warded, blockEntity,
                safeTagged, aggressiveTagged, replaceable);
    }
}
