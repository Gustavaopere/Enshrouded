package com.gustavaopere.enshrouded.integration.arszero;

import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.api.story.LichManifestationProvider;
import com.gustavaopere.enshrouded.story.boss.ManifestationDirector;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.event.EventHooks;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Optional registry-only adapter for Ars Zero's native Necropolis Lich. */
public final class ArsZeroLichProvider implements LichManifestationProvider {
    public static final String ID = "ars_zero:lich";

    private final ArsZeroCompatibilityProbe probe;

    public ArsZeroLichProvider(ArsZeroCompatibilityProbe probe) {
        this.probe = Objects.requireNonNull(probe, "probe");
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public boolean isAvailable() {
        return probe.available();
    }

    @Override
    public Optional<LivingEntity> spawn(ServerLevel level, EncounterContext context) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(context, "context");

        EntityType<?> type = probe.lichType().orElse(null);
        if (type == null) {
            return Optional.empty();
        }

        Entity created = type.create(level);
        if (!(created instanceof LivingEntity living)) {
            return Optional.empty();
        }

        living.moveTo(
                context.origin().getX() + 0.5D,
                context.origin().getY(),
                context.origin().getZ() + 0.5D,
                0.0F,
                0.0F
        );
        if (living instanceof Mob mob) {
            EventHooks.finalizeMobSpawn(
                    mob,
                    level,
                    level.getCurrentDifficultyAt(context.origin()),
                    EntitySpawnReason.EVENT,
                    null
            );
        }
        if (!level.addFreshEntity(living)) {
            return Optional.empty();
        }
        return Optional.of(living);
    }

    @Override
    public boolean matches(Entity entity, UUID encounterId) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(encounterId, "encounterId");
        return probe.lichType().filter(type -> entity.getType() == type).isPresent()
                && ManifestationDirector.encounterId(entity).filter(encounterId::equals).isPresent();
    }
}
