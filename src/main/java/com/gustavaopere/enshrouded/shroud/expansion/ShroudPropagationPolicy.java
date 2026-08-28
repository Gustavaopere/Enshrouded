package com.gustavaopere.enshrouded.shroud.expansion;

import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Objects;

/**
 * Pure logical propagation seam. Implementations must not load chunks or inspect material blocks.
 */
public interface ShroudPropagationPolicy {
    /**
     * Returns a deterministic candidate intensity in {@code [0, 1]}. A value of zero rejects the
     * candidate. Implementations must use only immutable logical/core inputs.
     */
    double intensity(ShroudCoreState core, ShroudGridGeometry geometry, ShroudCellPos candidate);

    /**
     * Returns candidate neighbors in deterministic traversal order.
     */
    List<ShroudCellPos> neighbors(ShroudCellPos source);

    /**
     * Level-1 terrain-neutral propagation: radial attenuation plus stable coordinate noise.
     */
    static ShroudPropagationPolicy terrainNeutral() {
        return TerrainNeutral.INSTANCE;
    }

    final class TerrainNeutral implements ShroudPropagationPolicy {
        private static final TerrainNeutral INSTANCE = new TerrainNeutral();

        private TerrainNeutral() {
        }

        @Override
        public double intensity(ShroudCoreState core, ShroudGridGeometry geometry, ShroudCellPos candidate) {
            Objects.requireNonNull(core, "core");
            Objects.requireNonNull(geometry, "geometry");
            Objects.requireNonNull(candidate, "candidate");

            BlockPos candidateCenter = geometry.cellCenter(candidate);
            long dx = (long) candidateCenter.getX() - core.center().getX();
            long dy = (long) candidateCenter.getY() - core.center().getY();
            long dz = (long) candidateCenter.getZ() - core.center().getZ();
            double distance = Math.sqrt((double) dx * dx + (double) dy * dy + (double) dz * dz);
            if (distance > core.maxInfluenceRadius()) {
                return 0.0D;
            }

            double radial = 1.0D - distance / core.maxInfluenceRadius();
            if (radial <= 0.0D) {
                return 0.0D;
            }

            long mixed = mix64(core.expansionSeed()
                    ^ mix64(candidate.x() * 0x9E3779B97F4A7C15L)
                    ^ mix64(candidate.y() * 0xC2B2AE3D27D4EB4FL)
                    ^ mix64(candidate.z() * 0x165667B19E3779F9L));
            double unitNoise = (mixed >>> 11) * 0x1.0p-53;
            double noiseFactor = 0.90D + unitNoise * 0.10D;
            return Math.clamp(radial * noiseFactor, 0.0D, 1.0D);
        }

        @Override
        public List<ShroudCellPos> neighbors(ShroudCellPos source) {
            Objects.requireNonNull(source, "source");
            return List.of(
                    new ShroudCellPos(Math.addExact(source.x(), 1), source.y(), source.z()),
                    new ShroudCellPos(Math.addExact(source.x(), -1), source.y(), source.z()),
                    new ShroudCellPos(source.x(), source.y(), Math.addExact(source.z(), 1)),
                    new ShroudCellPos(source.x(), source.y(), Math.addExact(source.z(), -1)),
                    new ShroudCellPos(source.x(), Math.addExact(source.y(), 1), source.z()),
                    new ShroudCellPos(source.x(), Math.addExact(source.y(), -1), source.z())
            );
        }

        private static long mix64(long value) {
            value ^= value >>> 33;
            value *= 0xff51afd7ed558ccdL;
            value ^= value >>> 33;
            value *= 0xc4ceb9fe1a85ec53L;
            value ^= value >>> 33;
            return value;
        }
    }
}
