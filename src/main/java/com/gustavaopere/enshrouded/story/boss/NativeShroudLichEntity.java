package com.gustavaopere.enshrouded.story.boss;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

/** Standalone physical manifestation used when no optional boss provider is available. */
public final class NativeShroudLichEntity extends Skeleton {
    public static final double MAX_HEALTH = 80.0D;
    public static final double ATTACK_DAMAGE = 6.0D;
    public static final double MOVEMENT_SPEED = 0.28D;
    public static final double FOLLOW_RANGE = 40.0D;
    public static final double ARMOR = 8.0D;

    public NativeShroudLichEntity(EntityType<? extends NativeShroudLichEntity> type, Level level) {
        super(type, level);
        this.xpReward = 50;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Skeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)
                .add(Attributes.ARMOR, ARMOR);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}
