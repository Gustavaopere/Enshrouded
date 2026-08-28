package com.gustavaopere.enshrouded.shroud.query;

import com.gustavaopere.enshrouded.api.shroud.FlameWardQuery;
import com.gustavaopere.enshrouded.api.shroud.ShroudQuery;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * Canonical server-authoritative Shroud lookup backed by dimension-local SavedData.
 *
 * <p>The expensive index build is tied to immutable {@link ShroudWorldState} snapshots. Repeated
 * lookups against an unchanged state are coarse-cell hash lookups and never iterate all cores or
 * force-load Minecraft chunks.</p>
 */
public final class DefaultShroudQuery implements ShroudQuery {
    private final ShroudGridGeometry geometry;
    private final FlameWardQuery wardQuery;
    private final Supplier<ShroudSeverityThresholds> thresholds;
    private final Map<ServerLevel, CacheEntry> cache = Collections.synchronizedMap(new WeakHashMap<>());

    public DefaultShroudQuery(
            ShroudGridGeometry geometry,
            FlameWardQuery wardQuery,
            ShroudSeverityThresholds thresholds) {
        this(geometry, wardQuery, () -> Objects.requireNonNull(thresholds, "thresholds"));
    }

    private DefaultShroudQuery(
            ShroudGridGeometry geometry,
            FlameWardQuery wardQuery,
            Supplier<ShroudSeverityThresholds> thresholds) {
        this.geometry = Objects.requireNonNull(geometry, "geometry");
        this.wardQuery = Objects.requireNonNull(wardQuery, "wardQuery");
        this.thresholds = Objects.requireNonNull(thresholds, "thresholds");
    }

    public static DefaultShroudQuery levelOne(ShroudGridGeometry geometry) {
        return new DefaultShroudQuery(
                geometry,
                FlameWardQuery.none(),
                () -> new ShroudSeverityThresholds(EnshroudedConfig.shroudDeadlyIntensityThreshold()));
    }

    @Override
    public ShroudSample sample(ServerLevel level, BlockPos pos, @Nullable Entity entity) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(pos, "pos");

        ShroudWorldState state = ShroudSavedData.get(level).state();
        ShroudSpatialIndex index = indexFor(level, state);
        boolean warded = wardQuery.suppresses(level, pos);
        return index.sample(geometry.cellAt(pos), Objects.requireNonNull(thresholds.get(), "thresholds result"), warded);
    }

    private ShroudSpatialIndex indexFor(ServerLevel level, ShroudWorldState state) {
        synchronized (cache) {
            CacheEntry current = cache.get(level);
            if (current != null && current.state() == state) {
                return current.index();
            }
            ShroudSpatialIndex rebuilt = ShroudSpatialIndex.from(state);
            cache.put(level, new CacheEntry(state, rebuilt));
            return rebuilt;
        }
    }

    private record CacheEntry(ShroudWorldState state, ShroudSpatialIndex index) {
        private CacheEntry {
            Objects.requireNonNull(state, "state");
            Objects.requireNonNull(index, "index");
        }
    }
}
