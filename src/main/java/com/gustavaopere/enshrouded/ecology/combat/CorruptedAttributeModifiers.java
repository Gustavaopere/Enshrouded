package com.gustavaopere.enshrouded.ecology.combat;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Objects;

/** Applies one stable transient modifier per corrupted stat and never stacks duplicate Enshrouded modifiers. */
public final class CorruptedAttributeModifiers {
    public static final ResourceLocation MAX_HEALTH_ID = id("corruption_max_health");
    public static final ResourceLocation ATTACK_DAMAGE_ID = id("corruption_attack_damage");
    public static final ResourceLocation MOVEMENT_SPEED_ID = id("corruption_movement_speed");
    public static final ResourceLocation KNOCKBACK_RESISTANCE_ID = id("corruption_knockback_resistance");

    private CorruptedAttributeModifiers() {
    }

    public static void synchronize(LivingEntity entity, CorruptionAttributeProfile profile) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(profile, "profile");

        reconcileIfPresent(
                entity.getAttribute(Attributes.MAX_HEALTH),
                MAX_HEALTH_ID,
                profile.maxHealthMultiplier(),
                CorruptionCombatPolicy.MAX_HEALTH_CAP,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
        reconcileIfPresent(
                entity.getAttribute(Attributes.ATTACK_DAMAGE),
                ATTACK_DAMAGE_ID,
                profile.attackDamageMultiplier(),
                CorruptionCombatPolicy.ATTACK_DAMAGE_CAP,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
        reconcileIfPresent(
                entity.getAttribute(Attributes.MOVEMENT_SPEED),
                MOVEMENT_SPEED_ID,
                profile.movementSpeedMultiplier(),
                CorruptionCombatPolicy.MOVEMENT_SPEED_CAP,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
        reconcileIfPresent(
                entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE),
                KNOCKBACK_RESISTANCE_ID,
                profile.knockbackResistanceBonus(),
                CorruptionCombatPolicy.KNOCKBACK_RESISTANCE_CAP,
                AttributeModifier.Operation.ADD_VALUE
        );

        if (entity.getHealth() > entity.getMaxHealth()) {
            entity.setHealth(entity.getMaxHealth());
        }
    }

    static void reconcile(
            AttributeInstance instance,
            ResourceLocation id,
            double desiredAmount,
            double cap,
            AttributeModifier.Operation operation) {
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(operation, "operation");
        if (!Double.isFinite(desiredAmount) || desiredAmount < 0.0D) {
            throw new IllegalArgumentException("desiredAmount must be finite and non-negative");
        }
        if (!Double.isFinite(cap) || cap < 0.0D) {
            throw new IllegalArgumentException("cap must be finite and non-negative");
        }

        double bounded = Math.min(desiredAmount, cap);
        AttributeModifier existing = instance.getModifier(id);
        if (bounded <= 0.0D) {
            if (existing != null) {
                instance.removeModifier(id);
            }
            return;
        }

        if (existing != null
                && Double.compare(existing.amount(), bounded) == 0
                && existing.operation() == operation) {
            return;
        }

        instance.addOrUpdateTransientModifier(new AttributeModifier(id, bounded, operation));
    }

    private static void reconcileIfPresent(
            AttributeInstance instance,
            ResourceLocation id,
            double desiredAmount,
            double cap,
            AttributeModifier.Operation operation) {
        if (instance != null) {
            reconcile(instance, id, desiredAmount, cap, operation);
        }
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, path);
    }
}
