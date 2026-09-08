package com.gustavaopere.enshrouded.presentation.setpiece;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShroudCoreNestLayoutTest {
    @Test
    void ordinaryNestHasExactlyOneAuthoritativeCoreAtItsAnchor() {
        ShroudCoreNestLayout layout = ShroudCoreNestLayout.ordinary();

        List<ShroudCoreNestLayout.Part> anchors = layout.parts().stream()
                .filter(part -> part.role() == ShroudCoreNestLayout.Role.CORE_ANCHOR)
                .toList();

        assertEquals(1, anchors.size());
        assertEquals(BlockPos.ZERO, anchors.getFirst().offset());
        assertEquals(layout.parts().size(), new HashSet<>(layout.parts().stream().map(ShroudCoreNestLayout.Part::offset).toList()).size());
        assertTrue(layout.parts().stream().anyMatch(part -> part.role() == ShroudCoreNestLayout.Role.RIB));
        assertTrue(layout.parts().stream().anyMatch(part -> part.role() == ShroudCoreNestLayout.Role.ROOT_VEIN));
        assertTrue(layout.parts().stream().anyMatch(part -> part.role() == ShroudCoreNestLayout.Role.SLUDGE_BASIN));
        assertTrue(layout.parts().stream().anyMatch(part -> part.role() == ShroudCoreNestLayout.Role.HANGING_GROWTH));
        assertTrue(layout.parts().stream().anyMatch(part -> part.role() == ShroudCoreNestLayout.Role.RUIN));
    }

    @Test
    void deadlyVariantStaysBoundedButIsCompositionallyDistinct() {
        ShroudCoreNestLayout ordinary = ShroudCoreNestLayout.ordinary();
        ShroudCoreNestLayout deadly = ShroudCoreNestLayout.deadly();

        assertNotEquals(ordinary.parts(), deadly.parts());
        assertEquals(1, deadly.parts().stream().filter(part -> part.role() == ShroudCoreNestLayout.Role.CORE_ANCHOR).count());
        assertTrue(deadly.parts().size() > ordinary.parts().size());
        assertTrue(deadly.parts().stream().allMatch(part -> withinBound(part.offset(), ShroudCoreNestLayout.MAX_HORIZONTAL_RADIUS)));
    }

    @Test
    void absolutePlacementsArePureTranslationOfImmutableOffsets() {
        ShroudCoreNestLayout layout = ShroudCoreNestLayout.ordinary();
        BlockPos anchor = new BlockPos(120, 70, -48);
        List<ShroudCoreNestLayout.Placement> placements = layout.placements(anchor);

        assertEquals(layout.parts().size(), placements.size());
        for (int index = 0; index < placements.size(); index++) {
            ShroudCoreNestLayout.Part part = layout.parts().get(index);
            ShroudCoreNestLayout.Placement placement = placements.get(index);
            assertEquals(anchor.offset(part.offset()), placement.pos());
            assertEquals(part.role(), placement.role());
        }
        assertThrows(UnsupportedOperationException.class,
                () -> placements.add(new ShroudCoreNestLayout.Placement(anchor, ShroudCoreNestLayout.Role.RUIN)));
    }

    @Test
    void layoutsNeverEncodeASecondCoreRole() {
        for (ShroudCoreNestLayout layout : List.of(ShroudCoreNestLayout.ordinary(), ShroudCoreNestLayout.deadly())) {
            Set<BlockPos> corePositions = new HashSet<>();
            layout.parts().stream()
                    .filter(part -> part.role() == ShroudCoreNestLayout.Role.CORE_ANCHOR)
                    .map(ShroudCoreNestLayout.Part::offset)
                    .forEach(corePositions::add);
            assertEquals(Set.of(BlockPos.ZERO), corePositions);
        }
    }

    private static boolean withinBound(BlockPos offset, int radius) {
        return Math.abs(offset.getX()) <= radius && Math.abs(offset.getZ()) <= radius;
    }
}
