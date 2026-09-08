package com.gustavaopere.enshrouded.presentation.setpiece;

import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Pure narrative composition contract for the Level-1 Lich manifestation landmark.
 *
 * <p>The landmark owns no encounter id, Story state, boss lifecycle or reward. Its origin is only
 * the position to pass to the existing manifestation service when gameplay decides to start an
 * encounter. Structural roles are presentation hints for a future structure/worldgen consumer.</p>
 */
public final class LichManifestationLandmarkLayout {
    private final List<Part> parts;

    private LichManifestationLandmarkLayout(List<Part> parts) {
        this.parts = validate(parts);
    }

    public static LichManifestationLandmarkLayout levelOne() {
        return new LichManifestationLandmarkLayout(List.of(
                part(0, 0, 0, Role.ENCOUNTER_ORIGIN),
                part(1, 0, 0, Role.RITUAL_DAIS),
                part(-1, 0, 0, Role.RITUAL_DAIS),
                part(0, 0, 1, Role.RITUAL_DAIS),
                part(0, 0, -1, Role.RITUAL_DAIS),
                part(-3, 2, 0, Role.BROKEN_HALO),
                part(-2, 3, 0, Role.BROKEN_HALO),
                part(2, 3, 0, Role.BROKEN_HALO),
                part(3, 2, 0, Role.BROKEN_HALO),
                part(4, 0, 2, Role.SPECTRAL_ANCHOR),
                part(4, 0, -2, Role.SPECTRAL_ANCHOR),
                part(-4, 0, 2, Role.SPECTRAL_ANCHOR),
                part(-4, 0, -2, Role.SPECTRAL_ANCHOR),
                part(-2, 1, -4, Role.PORTAL_FRAME),
                part(-2, 2, -4, Role.PORTAL_FRAME),
                part(2, 1, -4, Role.PORTAL_FRAME),
                part(2, 2, -4, Role.PORTAL_FRAME),
                part(-1, 3, -4, Role.PORTAL_FRAME),
                part(0, 3, -4, Role.PORTAL_FRAME),
                part(1, 3, -4, Role.PORTAL_FRAME),
                part(3, 0, 3, Role.BONE_MOTIF),
                part(-3, 0, 3, Role.BONE_MOTIF),
                part(2, 0, 4, Role.BONE_MOTIF),
                part(-2, 0, 4, Role.BONE_MOTIF)
        ));
    }

    public List<Part> parts() {
        return parts;
    }

    /** The physical landmark center is the canonical origin supplied to the existing encounter service. */
    public BlockPos encounterOrigin(BlockPos landmarkOrigin) {
        return Objects.requireNonNull(landmarkOrigin, "landmarkOrigin").immutable();
    }

    public int maxHorizontalRadius() {
        return 5;
    }

    /** Returns immutable translated presentation placements without accessing or mutating a world. */
    public List<Placement> placements(BlockPos landmarkOrigin) {
        BlockPos origin = encounterOrigin(landmarkOrigin);
        return parts.stream()
                .map(part -> new Placement(origin.offset(part.offset()), part.role()))
                .toList();
    }

    private static Part part(int x, int y, int z, Role role) {
        return new Part(new BlockPos(x, y, z), role);
    }

    private List<Part> validate(List<Part> candidate) {
        Objects.requireNonNull(candidate, "candidate");
        List<Part> copy = List.copyOf(candidate);
        if (copy.isEmpty()) {
            throw new IllegalArgumentException("Lich manifestation landmark must not be empty");
        }

        Set<BlockPos> occupied = new HashSet<>();
        int origins = 0;
        long radiusSquared = (long) maxHorizontalRadius() * maxHorizontalRadius();
        for (Part part : copy) {
            BlockPos offset = part.offset();
            if (!occupied.add(offset)) {
                throw new IllegalArgumentException("Lich landmark contains duplicate offset " + offset);
            }
            long horizontalDistanceSquared = (long) offset.getX() * offset.getX()
                    + (long) offset.getZ() * offset.getZ();
            if (horizontalDistanceSquared > radiusSquared || offset.getY() < 0 || offset.getY() > 3) {
                throw new IllegalArgumentException("Lich landmark part exceeds bounded footprint: " + offset);
            }
            if (part.role() == Role.ENCOUNTER_ORIGIN) {
                origins++;
                if (!offset.equals(BlockPos.ZERO)) {
                    throw new IllegalArgumentException("encounter origin must be at the landmark origin");
                }
            }
        }
        if (origins != 1) {
            throw new IllegalArgumentException("Lich landmark must contain exactly one encounter origin");
        }
        return copy;
    }

    public enum Role {
        ENCOUNTER_ORIGIN,
        RITUAL_DAIS,
        BROKEN_HALO,
        SPECTRAL_ANCHOR,
        PORTAL_FRAME,
        BONE_MOTIF
    }

    public record Part(BlockPos offset, Role role) {
        public Part {
            offset = Objects.requireNonNull(offset, "offset").immutable();
            role = Objects.requireNonNull(role, "role");
        }
    }

    public record Placement(BlockPos pos, Role role) {
        public Placement {
            pos = Objects.requireNonNull(pos, "pos").immutable();
            role = Objects.requireNonNull(role, "role");
        }
    }
}
