package com.gustavaopere.enshrouded.presentation.setpiece;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Pure composition contract for a Stage 10 Shroud Core Nest.
 *
 * <p>The layout never places blocks, loads chunks or owns Shroud state. {@link Role#CORE_ANCHOR}
 * marks the single position where an already-authoritative Shroud Core belongs; every other role
 * is presentation/environmental composition for a future structure/worldgen consumer.</p>
 */
public final class ShroudCoreNestLayout {
    public static final int MAX_HORIZONTAL_RADIUS = 4;
    private static final int MIN_VERTICAL_OFFSET = -1;
    private static final int MAX_VERTICAL_OFFSET = 3;

    private final List<Part> parts;

    private ShroudCoreNestLayout(List<Part> parts) {
        this.parts = validate(parts);
    }

    public static ShroudCoreNestLayout ordinary() {
        return new ShroudCoreNestLayout(List.of(
                part(0, 0, 0, Role.CORE_ANCHOR),
                part(3, 0, 0, Role.RIB),
                part(-3, 0, 0, Role.RIB),
                part(0, 0, 3, Role.RIB),
                part(0, 0, -3, Role.RIB),
                part(2, 0, 2, Role.ROOT_VEIN),
                part(-2, 0, 2, Role.ROOT_VEIN),
                part(2, 0, -2, Role.ROOT_VEIN),
                part(-2, 0, -2, Role.ROOT_VEIN),
                part(1, -1, 3, Role.SLUDGE_BASIN),
                part(-1, -1, -3, Role.SLUDGE_BASIN),
                part(2, 2, 0, Role.HANGING_GROWTH),
                part(-2, 2, 0, Role.HANGING_GROWTH),
                part(0, 2, 2, Role.HANGING_GROWTH),
                part(0, 2, -2, Role.HANGING_GROWTH),
                part(4, 0, 1, Role.RUIN),
                part(-4, 0, -1, Role.RUIN)
        ));
    }

    public static ShroudCoreNestLayout deadly() {
        ArrayList<Part> parts = new ArrayList<>(ordinary().parts());
        parts.add(part(3, 1, 2, Role.RIB));
        parts.add(part(-3, 1, -2, Role.RIB));
        parts.add(part(3, 1, -2, Role.ROOT_VEIN));
        parts.add(part(-3, 1, 2, Role.ROOT_VEIN));
        parts.add(part(3, -1, 1, Role.SLUDGE_BASIN));
        parts.add(part(-3, -1, -1, Role.SLUDGE_BASIN));
        parts.add(part(1, 3, 2, Role.HANGING_GROWTH));
        parts.add(part(-1, 3, -2, Role.HANGING_GROWTH));
        parts.add(part(4, 0, -2, Role.RUIN));
        parts.add(part(-4, 0, 2, Role.RUIN));
        return new ShroudCoreNestLayout(parts);
    }

    public List<Part> parts() {
        return parts;
    }

    /** Returns an immutable translated view; it performs no world access or mutation. */
    public List<Placement> placements(BlockPos coreAnchor) {
        Objects.requireNonNull(coreAnchor, "coreAnchor");
        return parts.stream()
                .map(part -> new Placement(coreAnchor.offset(part.offset()), part.role()))
                .toList();
    }

    private static Part part(int x, int y, int z, Role role) {
        return new Part(new BlockPos(x, y, z), role);
    }

    private static List<Part> validate(List<Part> candidate) {
        Objects.requireNonNull(candidate, "candidate");
        List<Part> copy = List.copyOf(candidate);
        if (copy.isEmpty()) {
            throw new IllegalArgumentException("Shroud Core Nest layout must not be empty");
        }

        Set<BlockPos> occupied = new HashSet<>();
        int coreAnchors = 0;
        for (Part part : copy) {
            BlockPos offset = part.offset();
            if (!occupied.add(offset)) {
                throw new IllegalArgumentException("Shroud Core Nest contains duplicate offset " + offset);
            }
            if (Math.abs(offset.getX()) > MAX_HORIZONTAL_RADIUS
                    || Math.abs(offset.getZ()) > MAX_HORIZONTAL_RADIUS
                    || offset.getY() < MIN_VERTICAL_OFFSET
                    || offset.getY() > MAX_VERTICAL_OFFSET) {
                throw new IllegalArgumentException("Shroud Core Nest part exceeds bounded footprint: " + offset);
            }
            if (part.role() == Role.CORE_ANCHOR) {
                coreAnchors++;
                if (!offset.equals(BlockPos.ZERO)) {
                    throw new IllegalArgumentException("authoritative core anchor must be at the layout origin");
                }
            }
        }
        if (coreAnchors != 1) {
            throw new IllegalArgumentException("Shroud Core Nest must contain exactly one authoritative core anchor");
        }
        return copy;
    }

    public enum Role {
        CORE_ANCHOR,
        RIB,
        ROOT_VEIN,
        SLUDGE_BASIN,
        HANGING_GROWTH,
        RUIN
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
