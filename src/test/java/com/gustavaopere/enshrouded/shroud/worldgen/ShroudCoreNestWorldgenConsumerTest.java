package com.gustavaopere.enshrouded.shroud.worldgen;

import com.gustavaopere.enshrouded.presentation.setpiece.ShroudCoreNestLayout;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShroudCoreNestWorldgenConsumerTest {
    @Test
    void everyNonControllerRoleHasAnExplicitProductionMaterial() {
        EnumSet<ShroudCoreNestLayout.Role> roles = EnumSet.allOf(ShroudCoreNestLayout.Role.class);
        roles.remove(ShroudCoreNestLayout.Role.CORE_ANCHOR);

        for (ShroudCoreNestLayout.Role role : roles) {
            assertTrue(ShroudCoreNestWorldgenConsumer.materialFor(role) != null, role::name);
        }
        assertThrows(IllegalArgumentException.class,
                () -> ShroudCoreNestWorldgenConsumer.materialFor(ShroudCoreNestLayout.Role.CORE_ANCHOR));
    }

    @Test
    void ordinaryConsumerWorkIsBoundedByTheCanonicalLayout() {
        long decorations = ShroudCoreNestLayout.ordinary().parts().stream()
                .filter(part -> part.role() != ShroudCoreNestLayout.Role.CORE_ANCHOR)
                .count();

        assertEquals(ShroudCoreNestLayout.ordinary().parts().size() - 1L, decorations);
        assertTrue(decorations < 32, "Level 1 nest must remain a small bounded placement set");
    }

    @Test
    void surfaceProjectionNeverCarvesBelowWorldgenSurface() {
        assertEquals(0, ShroudCoreNestWorldgenConsumer.resolvedVerticalOffset(
                ShroudCoreNestLayout.Role.SLUDGE_BASIN, -1));
        assertEquals(0, ShroudCoreNestWorldgenConsumer.resolvedVerticalOffset(
                ShroudCoreNestLayout.Role.RIB, 0));
        assertEquals(2, ShroudCoreNestWorldgenConsumer.resolvedVerticalOffset(
                ShroudCoreNestLayout.Role.HANGING_GROWTH, 2));
        assertEquals(0, ShroudCoreNestWorldgenConsumer.resolvedVerticalOffset(
                ShroudCoreNestLayout.Role.RUIN, -1));
    }
}
