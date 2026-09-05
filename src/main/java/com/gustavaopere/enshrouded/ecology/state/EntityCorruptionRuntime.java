package com.gustavaopere.enshrouded.ecology.state;

import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.ecology.combat.CorruptedCombatRuntime;
import com.gustavaopere.enshrouded.ecology.purification.EntityPurificationService;
import com.gustavaopere.enshrouded.performance.PerformanceCounters;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.query.DefaultShroudQuery;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Objects;

/**
 * Server-only, per-entity corruption driver. No world scan or replacement conversion is performed.
 */
public final class EntityCorruptionRuntime {
    static final int SAMPLE_INTERVAL_TICKS = 20;
    private static final float ACCUMULATION_PER_TICK = 1.0F / 1200.0F;
    private static final float REGRESSION_PER_TICK = 1.0F / 600.0F;
    private static final int MAX_ELAPSED_TICKS = 100;

    private static final DefaultShroudQuery SHROUD_QUERY =
            DefaultShroudQuery.levelOne(ShroudGridGeometry.levelOne());
    private static final EntityCorruptionService SERVICE =
            new EntityCorruptionService(ACCUMULATION_PER_TICK, REGRESSION_PER_TICK, MAX_ELAPSED_TICKS);

    private static boolean registered;

    private EntityCorruptionRuntime() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        NeoForge.EVENT_BUS.addListener(EntityCorruptionRuntime::onEntityTickPost);
    }

    private static void onEntityTickPost(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity living)
                || !(living.level() instanceof ServerLevel)
                || living.tickCount % SAMPLE_INTERVAL_TICKS != 0) {
            return;
        }
        advance(living, SAMPLE_INTERVAL_TICKS, null);
    }

    static void advanceNow(LivingEntity entity) {
        advance(entity, SAMPLE_INTERVAL_TICKS, null);
    }

    static void advanceNow(LivingEntity entity, Iterable<? extends Player> candidates) {
        advance(entity, SAMPLE_INTERVAL_TICKS, Objects.requireNonNull(candidates, "candidates"));
    }

    private static void advance(
            LivingEntity entity,
            int elapsedTicks,
            Iterable<? extends Player> candidateOverride) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }

        EntityCorruptionAttachment existing = entity.getExistingDataOrNull(EntityCorruptionAttachment.ENTITY_CORRUPTION);
        if (!CorruptionEligibility.isEligible(entity)) {
            boolean stateUpdated = existing != null && existing.intensity() > 0.0F;
            EntityPurificationService.purify(entity);
            recordEntitySample(stateUpdated);
            return;
        }

        ShroudSample sample = SHROUD_QUERY.sample(level, entity.blockPosition(), entity);
        boolean effectiveUnsafe = !sample.sanctuarySuppressed()
                && sample.severity() != ShroudSeverity.CLEAR
                && sample.intensity() > 0.0F;
        if (existing == null && !effectiveUnsafe) {
            CorruptedCombatRuntime.clearIfActive(entity);
            recordEntitySample(false);
            return;
        }

        EntityCorruptionAttachment current = existing == null
                ? EntityCorruptionAttachment.clean()
                : existing;
        EntityCorruptionAttachment next = SERVICE.tick(current, sample, elapsedTicks);
        if (next.intensity() <= 0.0F) {
            boolean stateUpdated = existing != null && existing.intensity() > 0.0F;
            EntityPurificationService.purify(entity);
            recordEntitySample(stateUpdated);
            return;
        }

        boolean stateUpdated = !next.equals(existing);
        if (stateUpdated) {
            entity.setData(EntityCorruptionAttachment.ENTITY_CORRUPTION, next);
        }
        if (candidateOverride == null) {
            CorruptedCombatRuntime.synchronize(entity, next.intensity());
        } else {
            CorruptedCombatRuntime.synchronize(entity, next.intensity(), candidateOverride);
        }
        recordEntitySample(stateUpdated);
    }

    private static void recordEntitySample(boolean stateUpdated) {
        PerformanceCounters.global().recordEntityUpdate(1L, stateUpdated ? 1L : 0L);
    }
}
