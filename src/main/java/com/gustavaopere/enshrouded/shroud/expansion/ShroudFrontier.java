package com.gustavaopere.enshrouded.shroud.expansion;

import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Runtime-owned bounded queue of pending logical Shroud expansion work.
 *
 * <p>The frontier is deterministic FIFO and deduplicates cells while they are queued. It is a
 * purely logical structure and has no world/chunk dependency.</p>
 */
public final class ShroudFrontier {
    private final int capacity;
    private final ArrayDeque<ShroudFrontierEntry> entries = new ArrayDeque<>();
    private final Set<ShroudCellPos> queuedPositions = new HashSet<>();

    public ShroudFrontier(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        this.capacity = capacity;
    }

    public int capacity() {
        return capacity;
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public boolean offer(ShroudFrontierEntry entry) {
        Objects.requireNonNull(entry, "entry");
        if (queuedPositions.contains(entry.position()) || entries.size() >= capacity) {
            return false;
        }
        entries.addLast(entry);
        queuedPositions.add(entry.position());
        return true;
    }

    public Optional<ShroudFrontierEntry> poll() {
        ShroudFrontierEntry entry = entries.pollFirst();
        if (entry == null) {
            return Optional.empty();
        }
        queuedPositions.remove(entry.position());
        return Optional.of(entry);
    }
}
