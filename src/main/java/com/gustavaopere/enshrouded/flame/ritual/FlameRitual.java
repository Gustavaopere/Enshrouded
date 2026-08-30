package com.gustavaopere.enshrouded.flame.ritual;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionState;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.UUID;

/**
 * Provider- and UI-agnostic description of one Flame ritual.
 *
 * <p>Physical altar inventories and story reward/item classes adapt into the opaque offering
 * boundary later; core ritual execution never imports those implementation types.</p>
 */
public interface FlameRitual {
    ResourceLocation id();

    /** Stable invocation intent expected by this ritual. */
    ResourceLocation intentId();

    /** Eligibility is evaluated against one immutable owner/progression snapshot. */
    boolean isEligible(Context context);

    /** Offering validation/consumption contract supplied by the ritual definition. */
    OfferingContract offering();

    /** Progression result to commit when this invocation succeeds. */
    RitualOutcome outcome(Context context);

    /** Immutable per-invocation context; the owner is resolved exactly once by the executor. */
    record Context(
            UUID invokerId,
            ProgressionOwner owner,
            FlameProgressionState.OwnerProgression progression,
            ResourceLocation intentId) {
        public Context {
            Objects.requireNonNull(invokerId, "invokerId");
            Objects.requireNonNull(owner, "owner");
            Objects.requireNonNull(progression, "progression");
            Objects.requireNonNull(intentId, "intentId");
        }
    }

    /** Marker boundary for caller-owned offering snapshots. */
    interface Offering {
    }

    /**
     * Validates then consumes a caller-owned offering.
     *
     * <p>{@link #consume(Context, Offering)} is invoked only inside the progression transaction and
     * only after duplicate/eligibility/intent checks have passed.</p>
     */
    interface OfferingContract {
        boolean accepts(Context context, Offering offering);

        void consume(Context context, Offering offering);
    }
}
