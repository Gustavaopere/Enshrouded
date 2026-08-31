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
    void enabledAndDisabledHotPathsRemainCheap() {
        ShroudRenderState state = new ShroudRenderState();

        long enabledStart = System.nanoTime();
        for (int i = 0; i < 250_000; i++) {
            state.advance((i & 1) == 0 ? ShroudSeverity.SHROUD : ShroudSeverity.DEADLY, false, 0.25F);
            state.colorProfile(0.85D);
        }
        long enabledMillis = (System.nanoTime() - enabledStart) / 1_000_000L;

        long disabledStart = System.nanoTime();
        for (int i = 0; i < 250_000; i++) {
            state.reset();
        }
        long disabledMillis = (System.nanoTime() - disabledStart) / 1_000_000L;

        String measurements = "enabled=" + enabledMillis + "ms, disabled=" + disabledMillis + "ms for 250k iterations";
        assertTrue(enabledMillis < 1_000L, measurements);
        assertTrue(disabledMillis < 1_000L, measurements);
    }
}
