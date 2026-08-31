package com.gustavaopere.enshrouded.client.render;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShroudRenderStateTest {
    @Test
    void ordinaryAndDeadlyHaveDistinctProfilesBeyondColorAlone() {
        ShroudColorProfile ordinary = ShroudColorProfile.ordinary();
        ShroudColorProfile deadly = ShroudColorProfile.deadly();

        assertTrue(deadly.farPlaneFactor() < ordinary.farPlaneFactor());
        assertTrue(deadly.nearPlaneFactor() < ordinary.nearPlaneFactor());
        assertTrue(deadly.red() > ordinary.red());
        assertTrue(ordinary.blue() > deadly.blue());
    }

    @Test
    void repeatedBoundaryCrossingInterpolatesWithoutLeakingPriorZoneState() {
        ShroudRenderState state = new ShroudRenderState();

        for (int i = 0; i < 12; i++) {
            state.advance(ShroudSeverity.SHROUD, false, 1.0F);
        }
        assertEquals(1.0F, state.ordinaryWeight(), 0.0001F);
        assertEquals(0.0F, state.deadlyWeight(), 0.0001F);

        state.advance(ShroudSeverity.DEADLY, false, 1.0F);
        assertTrue(state.ordinaryWeight() > 0.0F, "edge transition should retain a bounded ordinary contribution");
        assertTrue(state.deadlyWeight() > 0.0F, "edge transition should begin Deadly contribution immediately");

        for (int i = 0; i < 12; i++) {
            state.advance(ShroudSeverity.CLEAR, false, 1.0F);
        }
        assertEquals(0.0F, state.ordinaryWeight(), 0.0001F);
        assertEquals(0.0F, state.deadlyWeight(), 0.0001F);

        for (int i = 0; i < 12; i++) {
            state.advance(ShroudSeverity.DEADLY, false, 1.0F);
        }
        assertEquals(0.0F, state.ordinaryWeight(), 0.0001F);
        assertEquals(1.0F, state.deadlyWeight(), 0.0001F);
    }

    @Test
    void sanctuarySuppressionTargetsClearEvenWhenLatentSeverityIsDeadly() {
        ShroudRenderState state = new ShroudRenderState();
        for (int i = 0; i < 12; i++) {
            state.advance(ShroudSeverity.DEADLY, false, 1.0F);
        }
        for (int i = 0; i < 12; i++) {
            state.advance(ShroudSeverity.DEADLY, true, 1.0F);
        }

        assertEquals(0.0F, state.ordinaryWeight(), 0.0001F);
        assertEquals(0.0F, state.deadlyWeight(), 0.0001F);
    }

    @Test
    void largeFrameDeltaIsBoundedAndCannotOvershoot() {
        ShroudRenderState state = new ShroudRenderState();
        state.advance(ShroudSeverity.DEADLY, false, 1000.0F);

        assertEquals(0.0F, state.ordinaryWeight(), 0.0001F);
        assertEquals(1.0F, state.deadlyWeight(), 0.0001F);
    }

    @Test
    void hotPathStateMathRemainsCheap() {
        ShroudRenderState state = new ShroudRenderState();
        long start = System.nanoTime();
        for (int i = 0; i < 250_000; i++) {
            state.advance((i & 1) == 0 ? ShroudSeverity.SHROUD : ShroudSeverity.DEADLY, false, 0.25F);
            state.colorProfile(0.85D);
        }
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000L;

        assertTrue(elapsedMillis < 1_000L, "250k render-state updates took " + elapsedMillis + " ms");
    }
}
