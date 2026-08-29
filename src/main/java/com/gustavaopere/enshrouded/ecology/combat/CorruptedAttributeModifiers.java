package com.gustavaopere.enshrouded.ecology.combat;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Objects;

/** Stable modifier surface; behavior is intentionally introduced after RED coverage. */
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
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, path);
    }
}
