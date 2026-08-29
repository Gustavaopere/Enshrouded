package com.gustavaopere.enshrouded.ecology.state;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

/** Fail-closed eligibility for entity corruption. */
public final class CorruptionEligibility {
    public static final TagKey<EntityType<?>> CORRUPTIBLE = tag("corruptible");
    public static final TagKey<EntityType<?>> IMMUNE = tag("immune");
    public static final TagKey<EntityType<?>> BOSS_EXCLUDED = tag("boss_excluded");

    private CorruptionEligibility() {
    }

    public static boolean isEligible(LivingEntity entity) {
        Objects.requireNonNull(entity, "entity");
        EntityType<?> type = entity.getType();
        return isEligible(
                entity instanceof Player,
                type.is(CORRUPTIBLE),
                type.is(IMMUNE),
                type.is(BOSS_EXCLUDED)
        );
    }

    static boolean isEligible(boolean isPlayer, boolean isAllowlisted, boolean isImmune, boolean isExcludedBoss) {
        return !isPlayer && isAllowlisted && !isImmune && !isExcludedBoss;
    }

    private static TagKey<EntityType<?>> tag(String path) {
        return TagKey.create(
                Registries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, path)
        );
    }
}
