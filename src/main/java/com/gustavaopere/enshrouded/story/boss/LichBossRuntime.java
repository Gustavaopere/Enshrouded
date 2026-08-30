package com.gustavaopere.enshrouded.story.boss;

import com.gustavaopere.enshrouded.api.story.LichManifestationProvider;

/** Single Stage 06 provider registry/director used by standalone and optional integrations. */
public final class LichBossRuntime {
    private static final NativeLichManifestationProvider NATIVE_PROVIDER = new NativeLichManifestationProvider();
    private static final LichManifestationProviderRegistry REGISTRY =
            new LichManifestationProviderRegistry(NATIVE_PROVIDER);
    private static final ManifestationDirector DIRECTOR = new ManifestationDirector(REGISTRY);

    private LichBossRuntime() {
    }

    public static ManifestationDirector director() {
        return DIRECTOR;
    }

    public static void registerProvider(LichManifestationProvider provider, int priority) {
        REGISTRY.register(provider, priority);
    }
}
