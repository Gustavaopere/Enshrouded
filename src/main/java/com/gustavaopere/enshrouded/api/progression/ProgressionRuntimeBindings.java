package com.gustavaopere.enshrouded.api.progression;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Stable Foundation-owned runtime handles for progression providers.
 *
 * <p>Consumers keep these delegating handles for their whole lifetime. Stage providers may replace
 * the backing pair at server lifecycle boundaries without forcing consumers to import provider
 * implementations or recreate static fields.</p>
 */
public final class ProgressionRuntimeBindings {
    private static final Bindings FALLBACK = new Bindings(
            ProgressionOwnerResolver.standalone(),
            FlamePassageQuery.levelOneFallback()
    );
    private static final AtomicReference<Bindings> CURRENT = new AtomicReference<>(FALLBACK);

    private static final ProgressionOwnerResolver OWNER_HANDLE =
            playerId -> CURRENT.get().ownerResolver().resolve(playerId);
    private static final FlamePassageQuery PASSAGE_HANDLE =
            owner -> CURRENT.get().passageQuery().passageLevel(owner);

    private ProgressionRuntimeBindings() {
    }

    public static ProgressionOwnerResolver ownerResolver() {
        return OWNER_HANDLE;
    }

    public static FlamePassageQuery passageQuery() {
        return PASSAGE_HANDLE;
    }

    /** Atomically replaces both provider sides so consumers never observe a mixed binding pair. */
    public static void install(ProgressionOwnerResolver ownerResolver, FlamePassageQuery passageQuery) {
        CURRENT.set(new Bindings(
                Objects.requireNonNull(ownerResolver, "ownerResolver"),
                Objects.requireNonNull(passageQuery, "passageQuery")
        ));
    }

    /** Restores standalone Level-1 fail-safe providers, used at server shutdown and by tests. */
    public static void reset() {
        CURRENT.set(FALLBACK);
    }

    private record Bindings(
            ProgressionOwnerResolver ownerResolver,
            FlamePassageQuery passageQuery) {
    }
}
