package com.gustavaopere.enshrouded.client.state;

import com.gustavaopere.enshrouded.shroud.discovery.DiscoveredCore;
import com.gustavaopere.enshrouded.shroud.discovery.ShroudDiscoveryPayload;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** Client-only cache of the server-authorized complete marker snapshot. */
public final class ClientShroudDiscoveryState {
    public static final ClientShroudDiscoveryState INSTANCE = new ClientShroudDiscoveryState();

    private final CopyOnWriteArrayList<Consumer<Snapshot>> listeners = new CopyOnWriteArrayList<>();
    private long lastSequence = -1L;
    private String ownerStableKey;
    private List<DiscoveredCore> cores = List.of();

    private ClientShroudDiscoveryState() {
    }

    public boolean accept(ShroudDiscoveryPayload payload) {
        Objects.requireNonNull(payload, "payload");
        Snapshot updated;
        synchronized (this) {
            boolean sameOwner = Objects.equals(ownerStableKey, payload.ownerStableKey());
            if (sameOwner && payload.sequence() <= lastSequence) {
                return false;
            }
            lastSequence = payload.sequence();
            ownerStableKey = payload.ownerStableKey();
            cores = List.copyOf(payload.cores());
            updated = snapshotUnsafe();
        }
        notifyListeners(updated);
        return true;
    }

    public synchronized Snapshot snapshot() {
        return snapshotUnsafe();
    }

    public void addListener(Consumer<Snapshot> listener) {
        Consumer<Snapshot> checked = Objects.requireNonNull(listener, "listener");
        listeners.addIfAbsent(checked);
        checked.accept(snapshot());
    }

    public void removeListener(Consumer<Snapshot> listener) {
        listeners.remove(Objects.requireNonNull(listener, "listener"));
    }

    public void reset() {
        Snapshot cleared;
        synchronized (this) {
            lastSequence = -1L;
            ownerStableKey = null;
            cores = List.of();
            cleared = snapshotUnsafe();
        }
        notifyListeners(cleared);
    }

    private Snapshot snapshotUnsafe() {
        return new Snapshot(lastSequence, ownerStableKey, cores);
    }

    private void notifyListeners(Snapshot snapshot) {
        listeners.forEach(listener -> listener.accept(snapshot));
    }

    public record Snapshot(long sequence, String ownerStableKey, List<DiscoveredCore> cores) {
        public Snapshot {
            cores = List.copyOf(Objects.requireNonNull(cores, "cores"));
        }

        public boolean authorized() {
            return ownerStableKey != null;
        }
    }
}
