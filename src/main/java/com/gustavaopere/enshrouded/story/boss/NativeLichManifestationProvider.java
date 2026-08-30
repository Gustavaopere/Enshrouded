package com.gustavaopere.enshrouded.story.boss;

import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.api.story.LichManifestationProvider;
import com.gustavaopere.enshrouded.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;
import java.util.UUID;

/** Built-in fallback that keeps Stage 06 functional with no optional boss mod installed. */
public final class NativeLichManifestationProvider implements LichManifestationProvider {
    public static final String ID = "enshrouded:native";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public Optional<LivingEntity> spawn(ServerLevel level, EncounterContext context) {
        NativeShroudLichEntity lich = ModEntities.SHROUD_LICH.get().create(level);
        if (lich == null) {
            return Optional.empty();
        }
        lich.moveTo(
                context.origin().getX() + 0.5D,
                context.origin().getY(),
                context.origin().getZ() + 0.5D,
                0.0F,
                0.0F
        );
        if (!level.addFreshEntity(lich)) {
            return Optional.empty();
        }
        return Optional.of(lich);
    }

    @Override
    public boolean matches(Entity entity, UUID encounterId) {
        return entity.getType() == ModEntities.SHROUD_LICH.get()
                && ManifestationDirector.encounterId(entity).filter(encounterId::equals).isPresent();
    }
}
