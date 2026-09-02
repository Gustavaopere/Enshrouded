package com.gustavaopere.enshrouded.shroud.discovery;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** Immutable owner-scoped discovery state. It never infers or migrates knowledge between owners. */
public final class ShroudDiscoveryState {
    private static final Comparator<DiscoveredCore> VISIBLE_ORDER = Comparator
            .comparing(DiscoveredCore::dimensionId)
            .thenComparing(core -> core.coreId().toString());

    private final Map<String, Map<UUID, DiscoveredCore>> byOwner;

    private ShroudDiscoveryState(Map<String, Map<UUID, DiscoveredCore>> byOwner) {
        LinkedHashMap<String, Map<UUID, DiscoveredCore>> copied = new LinkedHashMap<>();
        byOwner.forEach((owner, cores) -> copied.put(owner, Collections.unmodifiableMap(new LinkedHashMap<>(cores))));
        this.byOwner = Collections.unmodifiableMap(copied);
    }

    public static ShroudDiscoveryState empty() {
        return new ShroudDiscoveryState(Map.of());
    }

    public static ShroudDiscoveryState fromStableKeys(Map<String, ? extends Map<UUID, DiscoveredCore>> byOwner) {
        Objects.requireNonNull(byOwner, "byOwner");
        LinkedHashMap<String, Map<UUID, DiscoveredCore>> validated = new LinkedHashMap<>();
        byOwner.forEach((stableKey, cores) -> {
            if (ProgressionOwner.parse(stableKey).isEmpty()) {
                throw new IllegalArgumentException("invalid progression owner stable key: " + stableKey);
            }
            Objects.requireNonNull(cores, "cores");
            LinkedHashMap<UUID, DiscoveredCore> coreCopy = new LinkedHashMap<>();
            cores.forEach((coreId, core) -> {
                Objects.requireNonNull(coreId, "coreId");
                Objects.requireNonNull(core, "core");
                if (!coreId.equals(core.coreId())) {
                    throw new IllegalArgumentException("discovery core map key does not match core id: " + coreId);
                }
                coreCopy.put(coreId, core);
            });
            if (!coreCopy.isEmpty()) {
                validated.put(stableKey, coreCopy);
            }
        });
        return new ShroudDiscoveryState(validated);
    }

    public ShroudDiscoveryState discover(ProgressionOwner owner, DiscoveredCore core) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(core, "core");
        String key = owner.stableKey();
        DiscoveredCore existing = byOwner.getOrDefault(key, Map.of()).get(core.coreId());
        if (core.equals(existing)) {
            return this;
        }

        LinkedHashMap<String, Map<UUID, DiscoveredCore>> owners = mutableCopy();
        LinkedHashMap<UUID, DiscoveredCore> cores = new LinkedHashMap<>(owners.getOrDefault(key, Map.of()));
        cores.put(core.coreId(), core);
        owners.put(key, cores);
        return new ShroudDiscoveryState(owners);
    }

    public ShroudDiscoveryState forget(ProgressionOwner owner, UUID coreId) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(coreId, "coreId");
        String key = owner.stableKey();
        Map<UUID, DiscoveredCore> known = byOwner.get(key);
        if (known == null || !known.containsKey(coreId)) {
            return this;
        }

        LinkedHashMap<String, Map<UUID, DiscoveredCore>> owners = mutableCopy();
        LinkedHashMap<UUID, DiscoveredCore> cores = new LinkedHashMap<>(known);
        cores.remove(coreId);
        if (cores.isEmpty()) {
            owners.remove(key);
        } else {
            owners.put(key, cores);
        }
        return new ShroudDiscoveryState(owners);
    }

    public ShroudDiscoveryState forgetEverywhere(UUID coreId) {
        Objects.requireNonNull(coreId, "coreId");
        boolean found = byOwner.values().stream().anyMatch(cores -> cores.containsKey(coreId));
        if (!found) {
            return this;
        }
        LinkedHashMap<String, Map<UUID, DiscoveredCore>> owners = mutableCopy();
        List<String> keys = new ArrayList<>(owners.keySet());
        for (String key : keys) {
            LinkedHashMap<UUID, DiscoveredCore> cores = new LinkedHashMap<>(owners.get(key));
            cores.remove(coreId);
            if (cores.isEmpty()) {
                owners.remove(key);
            } else {
                owners.put(key, cores);
            }
        }
        return new ShroudDiscoveryState(owners);
    }

    public List<DiscoveredCore> visibleTo(ProgressionOwner owner) {
        Objects.requireNonNull(owner, "owner");
        ArrayList<DiscoveredCore> result = new ArrayList<>(byOwner.getOrDefault(owner.stableKey(), Map.of()).values());
        result.sort(VISIBLE_ORDER);
        return List.copyOf(result);
    }

    public Set<String> ownerStableKeys() {
        return byOwner.keySet();
    }

    public Map<String, Map<UUID, DiscoveredCore>> byOwnerStableKey() {
        return byOwner;
    }

    private LinkedHashMap<String, Map<UUID, DiscoveredCore>> mutableCopy() {
        LinkedHashMap<String, Map<UUID, DiscoveredCore>> owners = new LinkedHashMap<>();
        byOwner.forEach((owner, cores) -> owners.put(owner, new LinkedHashMap<>(cores)));
        return owners;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof ShroudDiscoveryState state && byOwner.equals(state.byOwner);
    }

    @Override
    public int hashCode() {
        return byOwner.hashCode();
    }

    @Override
    public String toString() {
        return "ShroudDiscoveryState" + byOwner;
    }
}
