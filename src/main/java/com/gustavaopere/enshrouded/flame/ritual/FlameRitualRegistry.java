package com.gustavaopere.enshrouded.flame.ritual;

import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Stable-ID registry for Flame ritual definitions. */
public final class FlameRitualRegistry {
    private final Map<ResourceLocation, FlameRitual> rituals = new LinkedHashMap<>();

    public synchronized void register(FlameRitual ritual) {
        Objects.requireNonNull(ritual, "ritual");
        ResourceLocation id = Objects.requireNonNull(ritual.id(), "ritual.id()");
        if (rituals.putIfAbsent(id, ritual) != null) {
            throw new IllegalArgumentException("duplicate Flame ritual id: " + id);
        }
    }

    public synchronized Optional<FlameRitual> find(ResourceLocation id) {
        return Optional.ofNullable(rituals.get(Objects.requireNonNull(id, "id")));
    }

    public synchronized int size() {
        return rituals.size();
    }
}
