package com.gustavaopere.enshrouded.story.boss;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Standalone physical manifestation used when no optional boss provider is available. */
public final class NativeShroudLichEntity extends Skeleton {
    public static final double MAX_HEALTH = 80.0D;
    public static final double ATTACK_DAMAGE = 6.0D;
    public static final double MOVEMENT_SPEED = 0.28D;
    public static final double PHASE_TWO_MOVEMENT_SPEED = 0.36D;
    public static final double FOLLOW_RANGE = 40.0D;
    public static final double ARMOR = 8.0D;
    private static final float PHASE_ONE_RANGED_DAMAGE = 5.0F;
    private static final float PHASE_TWO_RANGED_DAMAGE = 7.0F;
    private static final int SHROUD_DARKNESS_TICKS = 100;

    private int combatPhase = 1;

    public NativeShroudLichEntity(EntityType<? extends NativeShroudLichEntity> type, Level level) {
        super(type, level);
        this.xpReward = 50;
        this.setPersistenceRequired();
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        this.reassessWeaponGoal();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Skeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)
                .add(Attributes.ARMOR, ARMOR);
    }

    int combatPhase() {
        return combatPhase;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (target == null || !target.isAlive() || target.isRemoved()) {
            return;
        }

        float damage = combatPhase >= 2 ? PHASE_TWO_RANGED_DAMAGE : PHASE_ONE_RANGED_DAMAGE;
        if (target.hurt(this.damageSources().mobAttack(this), damage)) {
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, SHROUD_DARKNESS_TICKS, 0), this);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && combatPhase == 1 && this.getHealth() <= this.getMaxHealth() * 0.5F) {
            combatPhase = 2;
            AttributeInstance movement = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movement != null) {
                movement.setBaseValue(PHASE_TWO_MOVEMENT_SPEED);
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}
