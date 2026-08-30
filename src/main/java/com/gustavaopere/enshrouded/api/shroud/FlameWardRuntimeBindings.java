package com.gustavaopere.enshrouded.api.shroud;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/** Stable Foundation-owned runtime handle for the active Sanctuary provider. */
public final class FlameWardRuntimeBindings {
    private static final FlameWardQuery FALLBACK = FlameWardQuery.none();
    private static final AtomicReference<FlameWardQuery> CURRENT = new AtomicReference<>(FALLBACK);
    private static final FlameWardQuery HANDLE = (level, pos) -> CURRENT.get().suppresses(level, pos);

    private FlameWardRuntimeBindings() {
    }

    public static FlameWardQuery query() {
        return HANDLE;
    }

    public static void install(FlameWardQuery query) {
        CURRENT.set(Objects.requireNonNull(query, "query"));
    }

    public static void reset() {
        CURRENT.set(FALLBACK);
    }
}
