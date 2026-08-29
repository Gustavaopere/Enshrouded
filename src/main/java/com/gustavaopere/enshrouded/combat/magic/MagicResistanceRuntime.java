package com.gustavaopere.enshrouded.combat.magic;

import com.gustavaopere.enshrouded.api.combat.MagicDamageClassification;
import com.gustavaopere.enshrouded.api.combat.MagicDamageClassifier;
import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.ecology.state.CorruptionEligibility;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionAttachment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/** Applies the single Enshrouded corruption-magic reducer at NeoForge's mutable pre-damage phase. */
public final class MagicResistanceRuntime {
    private static final MagicDamageClassifier CLASSIFIER = new DefaultMagicDamageClassifier();
    private static boolean registered;

    private MagicResistanceRuntime() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        NeoForge.EVENT_BUS.addListener(MagicResistanceRuntime::onLivingDamagePre);
    }

    private static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel) || !CorruptionEligibility.isEligible(entity)) {
            return;
        }

        EntityCorruptionAttachment corruption =
                entity.getExistingDataOrNull(EntityCorruptionAttachment.ENTITY_CORRUPTION);
        if (corruption == null || corruption.intensity() <= 0.0F) {
            return;
        }

        float incoming = event.getNewDamage();
        if (!Float.isFinite(incoming) || incoming <= 0.0F) {
            return;
        }

        // Classification is resolved exactly once. Optional adapters may later enrich this classifier
        // boundary, but they must never install a second resistance reducer.
        MagicDamageClassification classification = CLASSIFIER.classify(event.getSource());
        if (!classification.magical()) {
            return;
        }

        MagicResistanceService service =
                new MagicResistanceService(EnshroudedConfig.corruptionMagicResistanceCap());
        float reduced = service.reduceDamage(incoming, corruption.intensity(), classification);
        if (Float.isFinite(reduced) && reduced >= 0.0F && reduced < incoming) {
            event.setNewDamage(reduced);
        }
    }
}
