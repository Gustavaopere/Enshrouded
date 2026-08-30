package com.gustavaopere.enshrouded.story.manifestation;

import com.gustavaopere.enshrouded.api.shroud.ShroudQuery;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Temporary, dimension-local arena Shroud overlay for active Lich encounters.
 *
 * <p>The overlay is deliberately ephemeral. It decorates an existing read-only {@link ShroudQuery}
 * and never creates a persistent core, region or cell in the canonical Shroud field.</p>
 */
public final class LichArenaRule implements ShroudQuery {
    private final ShroudQuery baseQuery;
    private final FirstManifestationDefinition definition;
    private final Map<ServerLevel, LinkedHashMap<UUID, ArenaOverlay>> overlays =
            Collections.synchronizedMap(new WeakHashMap<>());

    public LichArenaRule(ShroudQuery baseQuery, FirstManifestationDefinition definition) {
        this.baseQuery = Objects.requireNonNull(baseQuery, "baseQuery");
        this.definition = Objects.requireNonNull(definition, "definition");
    }

    public boolean activate(ServerLevel level, UUID encounterId, BlockPos center) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(encounterId, "encounterId");
        Objects.requireNonNull(center, "center");

        synchronized (overlays) {
            LinkedHashMap<UUID, ArenaOverlay> levelOverlays =
                    overlays.computeIfAbsent(level, ignored -> new LinkedHashMap<>());
            if (levelOverlays.containsKey(encounterId)) {
                return false;
            }
            levelOverlays.put(encounterId, new ArenaOverlay(encounterId, center.immutable()));
            return true;
        }
    }

    public boolean cleanup(ServerLevel level, UUID encounterId) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(encounterId, "encounterId");

        synchronized (overlays) {
            LinkedHashMap<UUID, ArenaOverlay> levelOverlays = overlays.get(level);
            if (levelOverlays == null || levelOverlays.remove(encounterId) == null) {
                return false;
            }
            if (levelOverlays.isEmpty()) {
                overlays.remove(level);
            }
            return true;
        }
    }

    @Override
    public ShroudSample sample(ServerLevel level, BlockPos pos, @Nullable Entity entity) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(pos, "pos");

        ShroudSample base = Objects.requireNonNull(baseQuery.sample(level, pos, entity), "base sample");
        if (base.sanctuarySuppressed()) {
            return base;
        }

        Optional<ArenaOverlay> active = overlayAt(level, pos);
        if (active.isEmpty()) {
            return base;
        }

        if (base.severity() == ShroudSeverity.DEADLY
                || (base.severity() == ShroudSeverity.SHROUD && base.intensity() >= definition.arenaIntensity())) {
            return base;
        }

        ArenaOverlay overlay = active.orElseThrow();
        return new ShroudSample(
                Math.max(base.intensity(), definition.arenaIntensity()),
                ShroudSeverity.SHROUD,
                Optional.of(overlay.encounterId()),
                false
        );
    }

    private Optional<ArenaOverlay> overlayAt(ServerLevel level, BlockPos pos) {
        synchronized (overlays) {
            LinkedHashMap<UUID, ArenaOverlay> levelOverlays = overlays.get(level);
            if (levelOverlays == null) {
                return Optional.empty();
            }
            long radius = definition.arenaRadius();
            long radiusSquared = radius * radius;
            return levelOverlays.values().stream()
                    .filter(overlay -> distanceSquared(overlay.center(), pos) <= radiusSquared)
                    .findFirst();
        }
    }

    private static long distanceSquared(BlockPos first, BlockPos second) {
        long dx = (long) first.getX() - second.getX();
        long dy = (long) first.getY() - second.getY();
        long dz = (long) first.getZ() - second.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    private record ArenaOverlay(UUID encounterId, BlockPos center) {
        private ArenaOverlay {
            Objects.requireNonNull(encounterId, "encounterId");
            center = Objects.requireNonNull(center, "center").immutable();
        }
    }
}
