package com.gustavaopere.enshrouded.presentation.setpiece;

import com.gustavaopere.enshrouded.story.manifestation.FirstManifestationDefinition;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LichManifestationLandmarkLayoutTest {
    @Test
    void landmarkHasOneEncounterOriginAndAllNarrativeCompositionRoles() {
        LichManifestationLandmarkLayout layout = LichManifestationLandmarkLayout.levelOne();

        assertEquals(1, layout.parts().stream()
                .filter(part -> part.role() == LichManifestationLandmarkLayout.Role.ENCOUNTER_ORIGIN)
                .count());
        assertEquals(BlockPos.ZERO, layout.parts().stream()
                .filter(part -> part.role() == LichManifestationLandmarkLayout.Role.ENCOUNTER_ORIGIN)
                .findFirst()
                .orElseThrow()
                .offset());
        assertTrue(layout.parts().stream().anyMatch(part -> part.role() == LichManifestationLandmarkLayout.Role.RITUAL_DAIS));
        assertTrue(layout.parts().stream().anyMatch(part -> part.role() == LichManifestationLandmarkLayout.Role.BROKEN_HALO));
        assertTrue(layout.parts().stream().anyMatch(part -> part.role() == LichManifestationLandmarkLayout.Role.SPECTRAL_ANCHOR));
        assertTrue(layout.parts().stream().anyMatch(part -> part.role() == LichManifestationLandmarkLayout.Role.PORTAL_FRAME));
        assertTrue(layout.parts().stream().anyMatch(part -> part.role() == LichManifestationLandmarkLayout.Role.BONE_MOTIF));
    }

    @Test
    void landmarkFitsInsideCanonicalLevelOneArenaWithoutOwningItsRadius() {
        LichManifestationLandmarkLayout layout = LichManifestationLandmarkLayout.levelOne();
        int arenaRadius = FirstManifestationDefinition.levelOne().arenaRadius();

        assertTrue(layout.maxHorizontalRadius() < arenaRadius);
        assertTrue(layout.parts().stream().allMatch(part -> horizontalDistanceSquared(part.offset())
                <= (long) layout.maxHorizontalRadius() * layout.maxHorizontalRadius()));
    }

    @Test
    void translatedLandmarkExposesCanonicalEncounterOriginWithoutLifecycleState() {
        LichManifestationLandmarkLayout layout = LichManifestationLandmarkLayout.levelOne();
        BlockPos origin = new BlockPos(-64, 82, 144);
        List<LichManifestationLandmarkLayout.Placement> placements = layout.placements(origin);

        assertEquals(origin, layout.encounterOrigin(origin));
        assertEquals(layout.parts().size(), placements.size());
        assertEquals(layout.parts().size(), new HashSet<>(placements.stream().map(LichManifestationLandmarkLayout.Placement::pos).toList()).size());
        assertEquals(Set.of(origin), placements.stream()
                .filter(placement -> placement.role() == LichManifestationLandmarkLayout.Role.ENCOUNTER_ORIGIN)
                .map(LichManifestationLandmarkLayout.Placement::pos)
                .collect(java.util.stream.Collectors.toSet()));
        assertThrows(UnsupportedOperationException.class,
                () -> placements.add(new LichManifestationLandmarkLayout.Placement(origin, LichManifestationLandmarkLayout.Role.BONE_MOTIF)));
    }

    @Test
    void layoutContainsNoEncounterOrStoryIdentity() {
        LichManifestationLandmarkLayout layout = LichManifestationLandmarkLayout.levelOne();

        assertTrue(layout.getClass().getDeclaredFields().length <= 2,
                "landmark composition must not grow persistent encounter/story identity fields");
        assertTrue(layout.parts().stream().noneMatch(part -> part.role().name().contains("STATE")));
        assertTrue(layout.parts().stream().noneMatch(part -> part.role().name().contains("REWARD")));
    }

    private static long horizontalDistanceSquared(BlockPos offset) {
        return (long) offset.getX() * offset.getX() + (long) offset.getZ() * offset.getZ();
    }
}
