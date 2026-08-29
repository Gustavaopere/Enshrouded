package com.gustavaopere.enshrouded.ecology.combat;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorruptedAttributeModifiersRedTest {
    private static final double EPSILON = 1.0E-9D;

    @Test
    void repeatedReconcileIsIdempotentAndHardCapped() {
        AttributeInstance health = new AttributeInstance(
                Holder.direct(new RangedAttribute("attribute.enshrouded.test_health", 20.0D, 1.0D, 1024.0D)),
                ignored -> { }
        );
        double baseValue = health.getValue();

        CorruptedAttributeModifiers.reconcile(
                health,
                CorruptedAttributeModifiers.MAX_HEALTH_ID,
                5.0D,
                CorruptionCombatPolicy.MAX_HEALTH_CAP,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
        double once = health.getValue();
        CorruptedAttributeModifiers.reconcile(
                health,
                CorruptedAttributeModifiers.MAX_HEALTH_ID,
                5.0D,
                CorruptionCombatPolicy.MAX_HEALTH_CAP,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
        double twice = health.getValue();

        assertTrue(health.hasModifier(CorruptedAttributeModifiers.MAX_HEALTH_ID));
        assertEquals(baseValue * (1.0D + CorruptionCombatPolicy.MAX_HEALTH_CAP), once, EPSILON);
        assertEquals(once, twice, EPSILON, "Reapplication must update one stable modifier instead of stacking duplicates");

        CorruptedAttributeModifiers.reconcile(
                health,
                CorruptedAttributeModifiers.MAX_HEALTH_ID,
                0.0D,
                CorruptionCombatPolicy.MAX_HEALTH_CAP,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
        assertFalse(health.hasModifier(CorruptedAttributeModifiers.MAX_HEALTH_ID));
        assertEquals(baseValue, health.getValue(), EPSILON);
    }
}
