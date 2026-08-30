package com.gustavaopere.enshrouded.flame.ward;

import com.gustavaopere.enshrouded.api.shroud.FlameWardQuery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;
import java.util.function.IntSupplier;

/** Server-authoritative Sanctuary provider backed by the loaded-altar ward index. */
public final class FlameWardService implements FlameWardQuery {
    public static final int DEFAULT_RADIUS = 16;
    public static final int MIN_RADIUS = 1;
    public static final int MAX_RADIUS = 128;

    private final FlameWardIndex index;
    private final IntSupplier radiusSupplier;

    public FlameWardService(FlameWardIndex index, IntSupplier radiusSupplier) {
        this.index = Objects.requireNonNull(index, "index");
        this.radiusSupplier = Objects.requireNonNull(radiusSupplier, "radiusSupplier");
    }

    public void activate(ServerLevel level, BlockPos center) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(center, "center");
        index.activate(level.dimension(), new FlameWardState(center, configuredRadius()));
    }

    public boolean deactivate(ServerLevel level, BlockPos center) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(center, "center");
        return index.deactivate(level.dimension(), center);
    }

    @Override
    public boolean suppresses(ServerLevel level, BlockPos pos) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(pos, "pos");
        return index.suppresses(level.dimension(), pos);
    }

    public int activeWardCount(ServerLevel level) {
        return index.activeWardCount(Objects.requireNonNull(level, "level").dimension());
    }

    public void clear() {
        index.clear();
    }

    private int configuredRadius() {
        return Math.max(MIN_RADIUS, Math.min(MAX_RADIUS, radiusSupplier.getAsInt()));
    }
}
