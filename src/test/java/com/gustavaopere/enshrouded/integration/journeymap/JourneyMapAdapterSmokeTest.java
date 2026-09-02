package com.gustavaopere.enshrouded.integration.journeymap;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.client.state.ClientShroudDiscoveryState;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.discovery.DiscoveredCore;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.common.waypoint.Waypoint;
import journeymap.api.v2.common.waypoint.WaypointFactory;
import journeymap.api.v2.common.waypoint.WaypointGroup;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class JourneyMapAdapterSmokeTest {
    @BeforeEach
    void installWaypointFactory() {
        new WaypointFactory(new WaypointFactory.WaypointStore() {
            @Override
            public Waypoint createWaypoint(String modId, BlockPos pos, String name, String primaryDimension, boolean persistent) {
                return waypoint(modId, pos, name, primaryDimension, persistent);
            }

            @Override
            @SuppressWarnings("deprecation")
            public Waypoint createClientWaypoint(String modId, BlockPos pos, String name, String primaryDimension, boolean persistent) {
                return waypoint(modId, pos, name, primaryDimension, persistent);
            }

            @Override
            public Waypoint fromWaypointJsonString(String waypoint) {
                throw new UnsupportedOperationException();
            }

            @Override
            public WaypointGroup fromGroupJsonString(String waypoint) {
                throw new UnsupportedOperationException();
            }

            @Override
            public WaypointGroup createWaypointGroup(String modId, String name) {
                throw new UnsupportedOperationException();
            }
        });
    }

    @Test
    void discoveredCoreIsAddedAndThenRemovedWhenAuthorizationSnapshotNoLongerContainsIt() {
        RecordingApi recording = new RecordingApi();
        JourneyMapAdapter adapter = new JourneyMapAdapter(recording.api());
        ProgressionOwner owner = ProgressionOwner.player(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        UUID coreId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        DiscoveredCore core = new DiscoveredCore(
                coreId,
                "minecraft:overworld",
                new BlockPos(80, 64, -24),
                CoreLifecycleState.ACTIVE);

        adapter.accept(new ClientShroudDiscoveryState.Snapshot(0L, owner.stableKey(), List.of(core)));
        assertTrue(recording.active.isEmpty(), "adapter must not mutate JourneyMap before mapping starts");

        adapter.mappingStarted();
        assertEquals(1, recording.active.size());
        assertEquals(1, recording.addCalls);
        Waypoint marker = recording.active.getFirst();
        assertEquals(coreId.toString(), marker.getCustomData("enshrouded_core_id"));
        assertFalse(marker.isPersistent(), "Enshrouded marker must never become JourneyMap persistence authority");

        adapter.accept(new ClientShroudDiscoveryState.Snapshot(1L, owner.stableKey(), List.of()));
        assertTrue(recording.active.isEmpty());
        assertEquals(1, recording.removeCalls,
                "removing authorization from the complete snapshot must remove the corresponding marker");
    }

    @Test
    void purifiedLifecycleUpdatesTheAlreadyAuthorizedMarkerWithoutDiscoveringAnotherCore() {
        RecordingApi recording = new RecordingApi();
        JourneyMapAdapter adapter = new JourneyMapAdapter(recording.api());
        ProgressionOwner owner = ProgressionOwner.team("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        UUID coreId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        BlockPos pos = new BlockPos(-32, 70, 48);

        adapter.accept(new ClientShroudDiscoveryState.Snapshot(0L, owner.stableKey(), List.of(
                new DiscoveredCore(coreId, "minecraft:overworld", pos, CoreLifecycleState.ACTIVE))));
        adapter.mappingStarted();
        Waypoint original = recording.active.getFirst();

        adapter.accept(new ClientShroudDiscoveryState.Snapshot(1L, owner.stableKey(), List.of(
                new DiscoveredCore(coreId, "minecraft:overworld", pos, CoreLifecycleState.PURIFIED))));

        assertEquals(1, recording.active.size());
        assertTrue(recording.active.getFirst() == original, "lifecycle update must preserve the in-session marker identity");
        assertEquals("Purified Shroud Core", original.getName());
        assertEquals(2, recording.addCalls, "JourneyMap receives one update for the known marker, not a second discovery");
    }

    private static Waypoint waypoint(String modId, BlockPos pos, String name, String dimension, boolean persistent) {
        Map<String, Object> values = new HashMap<>();
        Map<String, String> customData = new HashMap<>();
        values.put("ModId", modId);
        values.put("BlockPos", pos);
        values.put("Name", name);
        values.put("PrimaryDimension", dimension);
        values.put("Persistent", persistent);

        return (Waypoint) Proxy.newProxyInstance(
                Waypoint.class.getClassLoader(),
                new Class<?>[]{Waypoint.class},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    if (methodName.equals("setCustomData") && args != null && args.length == 2) {
                        customData.put((String) args[0], (String) args[1]);
                        return null;
                    }
                    if (methodName.equals("getCustomData") && args != null && args.length == 1) {
                        return customData.get((String) args[0]);
                    }
                    if (methodName.startsWith("set") && args != null && args.length == 1) {
                        values.put(methodName.substring(3), args[0]);
                        return null;
                    }
                    if (methodName.startsWith("get") && (args == null || args.length == 0)) {
                        Object value = values.get(methodName.substring(3));
                        return value != null ? value : defaultValue(method.getReturnType());
                    }
                    if (methodName.startsWith("is") && (args == null || args.length == 0)) {
                        Object value = values.get(methodName.substring(2));
                        return value != null ? value : defaultValue(method.getReturnType());
                    }
                    if (methodName.equals("hashCode")) {
                        return System.identityHashCode(proxy);
                    }
                    if (methodName.equals("equals")) {
                        return proxy == args[0];
                    }
                    if (methodName.equals("toString")) {
                        return "FakeWaypoint" + values;
                    }
                    return defaultValue(method.getReturnType());
                });
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == byte.class) {
            return (byte) 0;
        }
        if (type == short.class) {
            return (short) 0;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == float.class) {
            return 0F;
        }
        if (type == double.class) {
            return 0D;
        }
        if (type == char.class) {
            return '\0';
        }
        return null;
    }

    private static final class RecordingApi {
        private final List<Waypoint> active = new ArrayList<>();
        private int addCalls;
        private int removeCalls;

        IClientAPI api() {
            return (IClientAPI) Proxy.newProxyInstance(
                    IClientAPI.class.getClassLoader(),
                    new Class<?>[]{IClientAPI.class},
                    (proxy, method, args) -> {
                        return switch (method.getName()) {
                            case "addWaypoint" -> {
                                Waypoint waypoint = (Waypoint) args[1];
                                if (active.stream().noneMatch(existing -> existing == waypoint)) {
                                    active.add(waypoint);
                                }
                                addCalls++;
                                yield null;
                            }
                            case "removeWaypoint" -> {
                                Waypoint waypoint = (Waypoint) args[1];
                                active.removeIf(existing -> existing == waypoint);
                                removeCalls++;
                                yield null;
                            }
                            case "removeAllWaypoints" -> {
                                active.clear();
                                yield null;
                            }
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "equals" -> proxy == args[0];
                            case "toString" -> "RecordingJourneyMapApi";
                            default -> defaultValue(method.getReturnType());
                        };
                    });
        }
    }
}
