package com.gustavaopere.enshrouded.protection;

import com.gustavaopere.enshrouded.api.shroud.MutationKind;

import java.util.Objects;

/** Pure decision policy behind the world-aware mutation authority. */
final class MutationSafetyPolicy {
    private final MutationSafetyMode mode;
    private final boolean allowIndeterminateProtection;
    private final boolean allowBlockEntityMutation;

    MutationSafetyPolicy(
            MutationSafetyMode mode,
            boolean allowIndeterminateProtection,
            boolean allowBlockEntityMutation) {
        this.mode = Objects.requireNonNull(mode, "mode");
        this.allowIndeterminateProtection = allowIndeterminateProtection;
        this.allowBlockEntityMutation = allowBlockEntityMutation;
    }

    boolean allows(
            MutationKind kind,
            ProtectionDecision protection,
            boolean warded,
            boolean blockEntity,
            boolean safeTagged,
            boolean aggressiveTagged,
            boolean replaceable) {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(protection, "protection");

        if (protection == ProtectionDecision.PROTECTED) {
            return false;
        }
        if (protection == ProtectionDecision.INDETERMINATE && !allowIndeterminateProtection) {
            return false;
        }
        if (blockEntity && !allowBlockEntityMutation) {
            return false;
        }
        if (warded && isThreatIntroducing(kind)) {
            return false;
        }

        return switch (kind) {
            case CORRUPTION -> safeTagged || (mode == MutationSafetyMode.AGGRESSIVE && aggressiveTagged);
            case CORE_PLACEMENT -> replaceable;
            case PURIFICATION, RITUAL_STRUCTURE -> true;
        };
    }

    private static boolean isThreatIntroducing(MutationKind kind) {
        return kind == MutationKind.CORRUPTION || kind == MutationKind.CORE_PLACEMENT;
    }
}
