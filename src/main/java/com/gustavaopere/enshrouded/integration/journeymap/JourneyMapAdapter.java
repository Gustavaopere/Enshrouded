package com.gustavaopere.enshrouded.integration.journeymap;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.client.state.ClientShroudDiscoveryState;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.discovery.DiscoveredCore;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.common.waypoint.Waypoint;
import journeymap.api.v2.common.waypoint.WaypointFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Client-only projection from server-authorized discovery snapshots into transient JourneyMap
 * waypoints. This adapter never reads Enshrouded SavedData and never discovers cores itself.
 */
public final class JourneyMapAdapter {
    private final IClientAPI api;
    private final Map<UUID, Waypoint> rendered = new LinkedHashMap<>();

    private ClientShroudDiscoveryState.Snapshot desired =
            new ClientShroudDiscoveryState.Snapshot(-1L, null, List.of());
    private boolean mappingActive;

    public JourneyMapAdapter(IClientAPI api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    public synchronized void accept(ClientShroudDiscoveryState.Snapshot snapshot) {
        desired = Objects.requireNonNull(snapshot, "snapshot");
        if (mappingActive) {
            reconcile();
        }
    }

    public synchronized void mappingStarted() {
        mappingActive = true;
        // All Enshrouded markers are transient (persistent=false); rebuild only from the latest
        // server-authorized snapshot whenever JourneyMap starts mapping a world/server.
        api.removeAllWaypoints(Enshrouded.MOD_ID);
        rendered.clear();
        reconcile();
    }

    public synchronized void mappingStopped() {
        for (Waypoint waypoint : rendered.values()) {
            api.removeWaypoint(Enshrouded.MOD_ID, waypoint);
        }
        rendered.clear();
        mappingActive = false;
    }

    private void reconcile() {
        LinkedHashMap<UUID, DiscoveredCore> wanted = new LinkedHashMap<>();
        if (desired.authorized()) {
            for (DiscoveredCore core : desired.cores()) {
                wanted.put(core.coreId(), core);
            }
        }

        rendered.entrySet().removeIf(entry -> {
            if (wanted.containsKey(entry.getKey())) {
                return false;
            }
            api.removeWaypoint(Enshrouded.MOD_ID, entry.getValue());
            return true;
        });

        for (DiscoveredCore core : wanted.values()) {
            Waypoint waypoint = rendered.get(core.coreId());
            if (waypoint == null) {
                waypoint = WaypointFactory.createWaypoint(
                        Enshrouded.MOD_ID,
                        core.pos(),
                        markerName(core),
                        core.dimensionId(),
                        false);
                waypoint.setShowBeacon(false);
                waypoint.setShowInWorld(false);
                waypoint.setShowOnMap(true);
                waypoint.setShowLabel(true);
                waypoint.setCustomData("enshrouded_core_id", core.coreId().toString());
                rendered.put(core.coreId(), waypoint);
            } else {
                waypoint.setBlockPos(core.pos());
                waypoint.setPrimaryDimension(core.dimensionId());
                waypoint.setDimensions(List.of(core.dimensionId()));
                waypoint.setName(markerName(core));
                waypoint.setPersistent(false);
            }
            api.addWaypoint(Enshrouded.MOD_ID, waypoint);
        }
    }

    private static String markerName(DiscoveredCore core) {
        return core.lifecycle() == CoreLifecycleState.PURIFIED
                ? "Purified Shroud Core"
                : "Shroud Core";
    }
}
