package com.gustavaopere.enshrouded.api.combat;

import net.minecraft.world.damagesource.DamageSource;

@FunctionalInterface
public interface MagicDamageClassifier {
    MagicDamageClassification classify(DamageSource source);
}
