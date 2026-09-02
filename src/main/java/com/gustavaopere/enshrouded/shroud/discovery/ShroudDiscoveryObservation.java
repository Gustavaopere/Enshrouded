package com.gustavaopere.enshrouded.shroud.discovery;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;

import java.util.List;
import java.util.Objects;

/** Pure observation step that discovers only the canonical source actually sampled for a player. */
public final class ShroudDiscoveryObservation {
    private ShroudDiscoveryObservation() {
    }

    public static Result observe(
            ShroudDiscoveryState discovery,
            ProgressionOwner owner,
            String dimensionId,
            ShroudSample sample,
            ShroudWorldState world) {
        Objects.requireNonNull(discovery, "discovery");
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(dimensionId, "dimensionId");
        Objects.requireNonNull(sample, "sample");
        Objects.requireNonNull(world, "world");

        ShroudDiscoveryState next = discovery;
        if (sample.sourceId().isPresent()) {
            ShroudCoreState core = world.cores().get(sample.sourceId().orElseThrow());
            if (core != null && core.lifecycleState() != CoreLifecycleState.DORMANT) {
                next = discovery.discover(owner, new DiscoveredCore(
                        core.id(),
                        dimensionId,
                        core.center(),
                        core.lifecycleState()));
            }
        }
        return new Result(next, next.visibleTo(owner));
    }

    public record Result(ShroudDiscoveryState state, List<DiscoveredCore> visibleCores) {
        public Result {
            Objects.requireNonNull(state, "state");
            visibleCores = List.copyOf(Objects.requireNonNull(visibleCores, "visibleCores"));
        }
    }
}
