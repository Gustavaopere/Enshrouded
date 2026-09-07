package com.gustavaopere.enshrouded.client.state;

import com.gustavaopere.enshrouded.network.AdvancedVfxPayload;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Client presentation inbox for ephemeral authoritative Stage 10.08 event cues. */
public final class ClientAdvancedVfxState {
    public static final ClientAdvancedVfxState INSTANCE = new ClientAdvancedVfxState();
    public static final int MAX_PENDING_CUES = 16;

    private final ArrayDeque<AdvancedVfxPayload> pending = new ArrayDeque<>(MAX_PENDING_CUES);

    private ClientAdvancedVfxState() {}

    public synchronized void accept(AdvancedVfxPayload payload) {
        Objects.requireNonNull(payload, "payload");
        if (pending.size() >= MAX_PENDING_CUES) {
            pending.removeFirst();
        }
        pending.addLast(payload);
    }

    public synchronized List<AdvancedVfxPayload> drain() {
        if (pending.isEmpty()) {
            return List.of();
        }
        List<AdvancedVfxPayload> drained = List.copyOf(new ArrayList<>(pending));
        pending.clear();
        return drained;
    }

    public synchronized void reset() {
        pending.clear();
    }
}
