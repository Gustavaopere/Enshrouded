package com.gustavaopere.enshrouded.ecology.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorruptionCombatPolicyRedTest {
    private static final double EPSILON = 1.0E-9D;

    @Test
    void aggressionStartsAtCorruptedStageThreshold() {
        CorruptionCombatPolicy policy = CorruptionCombatPolicy.levelOne();

        assertFalse(policy.shouldAcquirePlayerTarget(0.4999D));
        assertTrue(policy.shouldAcquirePlayerTarget(0.5D));
        assertTrue(policy.shouldAcquirePlayerTarget(1.0D));
    }

    @Test
    void attributeScalingIsMonotonicAndHardCapped() {
        CorruptionCombatPolicy policy = CorruptionCombatPolicy.levelOne();
        CorruptionAttributeProfile clean = policy.attributeProfile(0.0D);
        CorruptionAttributeProfile half = policy.attributeProfile(0.5D);
        CorruptionAttributeProfile overfull = policy.attributeProfile(5.0D);

        assertEquals(0.0D, clean.maxHealthMultiplier(), EPSILON);
        assertEquals(0.0D, clean.attackDamageMultiplier(), EPSILON);
        assertEquals(0.0D, clean.movementSpeedMultiplier(), EPSILON);
        assertEquals(0.0D, clean.knockbackResistanceBonus(), EPSILON);

        assertTrue(half.maxHealthMultiplier() > clean.maxHealthMultiplier());
        assertTrue(half.attackDamageMultiplier() > clean.attackDamageMultiplier());
        assertTrue(half.movementSpeedMultiplier() > clean.movementSpeedMultiplier());
        assertTrue(half.knockbackResistanceBonus() > clean.knockbackResistanceBonus());

        assertEquals(CorruptionCombatPolicy.MAX_HEALTH_CAP, overfull.maxHealthMultiplier(), EPSILON);
        assertEquals(CorruptionCombatPolicy.ATTACK_DAMAGE_CAP, overfull.attackDamageMultiplier(), EPSILON);
        assertEquals(CorruptionCombatPolicy.MOVEMENT_SPEED_CAP, overfull.movementSpeedMultiplier(), EPSILON);
        assertEquals(CorruptionCombatPolicy.KNOCKBACK_RESISTANCE_CAP, overfull.knockbackResistanceBonus(), EPSILON);
    }
}
