package com.gustavaopere.enshrouded.story.boss;

import com.gustavaopere.enshrouded.api.story.LichManifestationProvider;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Deterministic registry for Lich manifestation providers.
 *
 * <p>The native provider is installed at construction time as the mandatory
 * lowest-priority fallback. Optional providers may register with higher
 * priorities; unavailable providers are omitted from each selection snapshot.</p>
 */
public final class LichManifestationProviderRegistry {
    public static final int NATIVE_FALLBACK_PRIORITY = Integer.MIN_VALUE;

    private final Map<String, Entry> providers = new LinkedHashMap<>();

    public LichManifestationProviderRegistry(LichManifestationProvider nativeFallback) {
        LichManifestationProvider fallback = Objects.requireNonNull(nativeFallback, "nativeFallback");
        String id = requireId(fallback);
        providers.put(id, new Entry(fallback, NATIVE_FALLBACK_PRIORITY));
    }

    public synchronized void register(LichManifestationProvider provider, int priority) {
        LichManifestationProvider candidate = Objects.requireNonNull(provider, "provider");
        String id = requireId(candidate);
        if (providers.containsKey(id)) {
            throw new IllegalArgumentException("duplicate Lich manifestation provider id: " + id);
        }
        providers.put(id, new Entry(candidate, priority));
    }

    public synchronized List<LichManifestationProvider> availableProviders() {
        return providers.values().stream()
                .filter(entry -> entry.provider().isAvailable())
                .sorted(Comparator.comparingInt(Entry::priority)
                        .reversed()
                        .thenComparing(entry -> entry.provider().id()))
                .map(Entry::provider)
                .toList();
    }

    private static String requireId(LichManifestationProvider provider) {
        String id = Objects.requireNonNull(provider.id(), "provider.id()").trim();
        if (id.isEmpty()) {
            throw new IllegalArgumentException("Lich manifestation provider id must not be blank");
        }
        return id;
    }

    private record Entry(LichManifestationProvider provider, int priority) {
    }
}
