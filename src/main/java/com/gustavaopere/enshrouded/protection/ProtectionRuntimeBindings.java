package com.gustavaopere.enshrouded.protection;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/** Stable runtime handle for optional protection providers. */
public final class ProtectionRuntimeBindings {
    private static final ProtectedAreaService FALLBACK = ProtectedAreaService.none();
    private static final AtomicReference<ProtectedAreaService> CURRENT = new AtomicReference<>(FALLBACK);
    private static final ProtectedAreaService HANDLE =
            (level, pos, kind) -> CURRENT.get().protectionAt(level, pos, kind);

    private ProtectionRuntimeBindings() {
    }

    public static ProtectedAreaService protectedAreas() {
        return HANDLE;
    }

    public static void install(List<ProtectedAreaService> providers) {
        Objects.requireNonNull(providers, "providers");
        CURRENT.set(providers.isEmpty() ? FALLBACK : new CompositeProtectedAreaService(providers));
    }

    public static void reset() {
        CURRENT.set(FALLBACK);
    }
}
