package com.gustavaopere.enshrouded.shroud.expansion;

import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

final class ShroudFrontierBehaviorRedTest {
    @Test
    void frontierIsBoundedDeduplicatedAndFifo() {
        ShroudFrontier frontier = new ShroudFrontier(2);
        ShroudFrontierEntry first = entry(0, 0L);
        ShroudFrontierEntry duplicateCell = new ShroudFrontierEntry(first.position(), 0L, 1L);
        ShroudFrontierEntry second = entry(1, 2L);
        ShroudFrontierEntry overflow = entry(2, 3L);

        assertEquals(2, frontier.capacity());
        assertTrue(frontier.offer(first));
        assertFalse(frontier.offer(duplicateCell));
        assertTrue(frontier.offer(second));
        assertFalse(frontier.offer(overflow));
        assertEquals(2, frontier.size());

        assertEquals(Optional.of(first), frontier.poll());
        assertEquals(Optional.of(second), frontier.poll());
        assertEquals(Optional.empty(), frontier.poll());
        assertTrue(frontier.isEmpty());
    }

    @Test
    void frontierRejectsNonPositiveCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ShroudFrontier(0));
        assertThrows(IllegalArgumentException.class, () -> new ShroudFrontier(-1));
    }

    private static ShroudFrontierEntry entry(int x, long sequence) {
        return new ShroudFrontierEntry(new ShroudCellPos(x, 0, 0), 0L, sequence);
    }
}
