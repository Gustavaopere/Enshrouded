package com.gustavaopere.enshrouded.shroud.worldgen;

import net.minecraft.world.level.Level;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShroudCoreCandidateFieldTest {
    private static final int CELL_SIZE = 512;
    private static final int MINIMUM_SPACING = 128;

    @Test
    void candidateIsDeterministicAndSeedSensitive() {
        ShroudCoreCandidateField field = new ShroudCoreCandidateField(CELL_SIZE, MINIMUM_SPACING);

        ShroudCoreCandidateField.Candidate first = field.candidate(0x5EEDL, 7, -3);
        ShroudCoreCandidateField.Candidate repeated = field.candidate(0x5EEDL, 7, -3);
        ShroudCoreCandidateField.Candidate otherSeed = field.candidate(0x5EEEL, 7, -3);

        assertEquals(first, repeated);
        assertNotEquals(first, otherSeed);
    }

    @Test
    void candidateStaysInsideItsCellWithSpacingMargin() {
        ShroudCoreCandidateField field = new ShroudCoreCandidateField(CELL_SIZE, MINIMUM_SPACING);
        int margin = MINIMUM_SPACING / 2;

        for (int cellX = -4; cellX <= 4; cellX++) {
            for (int cellZ = -4; cellZ <= 4; cellZ++) {
                ShroudCoreCandidateField.Candidate candidate = field.candidate(123456789L, cellX, cellZ);
                int minX = cellX * CELL_SIZE + margin;
                int maxX = (cellX + 1) * CELL_SIZE - margin - 1;
                int minZ = cellZ * CELL_SIZE + margin;
                int maxZ = (cellZ + 1) * CELL_SIZE - margin - 1;

                assertTrue(candidate.blockX() >= minX && candidate.blockX() <= maxX);
                assertTrue(candidate.blockZ() >= minZ && candidate.blockZ() <= maxZ);
            }
        }
    }

    @Test
    void neighboringCellsCannotViolateMinimumSpacing() {
        ShroudCoreCandidateField field = new ShroudCoreCandidateField(CELL_SIZE, MINIMUM_SPACING);
        long seed = 987654321L;
        long minimumSquared = (long) MINIMUM_SPACING * MINIMUM_SPACING;

        for (int cellX = -5; cellX <= 5; cellX++) {
            for (int cellZ = -5; cellZ <= 5; cellZ++) {
                ShroudCoreCandidateField.Candidate origin = field.candidate(seed, cellX, cellZ);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dz == 0) {
                            continue;
                        }
                        ShroudCoreCandidateField.Candidate neighbor = field.candidate(seed, cellX + dx, cellZ + dz);
                        long deltaX = (long) origin.blockX() - neighbor.blockX();
                        long deltaZ = (long) origin.blockZ() - neighbor.blockZ();
                        String message = "candidate spacing violated between cells " + cellX + "," + cellZ
                                + " and " + (cellX + dx) + "," + (cellZ + dz);
                        assertTrue(deltaX * deltaX + deltaZ * deltaZ >= minimumSquared, message);
                    }
                }
            }
        }
    }

    @Test
    void automaticSeedingIsOverworldOnly() {
        ShroudCoreCandidateField field = new ShroudCoreCandidateField(CELL_SIZE, MINIMUM_SPACING);

        assertTrue(field.supportsDimension(Level.OVERWORLD));
        assertFalse(field.supportsDimension(Level.NETHER));
        assertFalse(field.supportsDimension(Level.END));
    }

    @Test
    void invalidGeometryFailsFast() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ShroudCoreCandidateField(128, 128));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ShroudCoreCandidateField(512, 0));
    }
}
