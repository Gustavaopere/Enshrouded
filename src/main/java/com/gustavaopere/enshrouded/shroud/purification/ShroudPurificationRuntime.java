package com.gustavaopere.enshrouded.shroud.purification;

import com.gustavaopere.enshrouded.api.shroud.FlameWardRuntimeBindings;
import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.protection.DefaultMutationAuthority;
import com.gustavaopere.enshrouded.protection.ProtectedAreaService;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.core.ShroudCorePurifiedEvent;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionRuleReloadListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Server-thread coordinator for logical regression and best-effort loaded-world healing. */
public final class ShroudPurificationRuntime {
    private static final int RESTORATION_QUEUE_CAPACITY = 8192;
    private static final ShroudGridGeometry GEOMETRY = ShroudGridGeometry.levelOne();
    private static final ShroudRegressionScheduler REGRESSION =
            new ShroudRegressionScheduler(GEOMETRY, PurificationPolicy.levelOne());
    private static final ConcurrentMap<ResourceKey<Level>, TerrainRestorationService> RESTORATION =
            new ConcurrentHashMap<>();

    private ShroudPurificationRuntime() {
    }

    /**
     * Advances one bounded purification tick. Logical regression is authoritative and persisted;
     * visual restoration is independent best-effort work and never blocks the PURIFIED terminal
     * state.
     */
    public static boolean advance(ServerLevel level) {
        Objects.requireNonNull(level, "level");
        ShroudSavedData savedData = ShroudSavedData.get(level);
        ShroudWorldState current = savedData.state();
        ShroudWorldState next = current;
        ShroudRegressionScheduler.TickResult regressionResult = null;

        boolean hasDestroyedCore = current.cores().values().stream()
                .anyMatch(core -> core.lifecycleState() == CoreLifecycleState.DESTROYED);
        if (hasDestroyedCore) {
            int regressionBudget = EnshroudedConfig.coreRegressionWorkPerTick();
            regressionResult = REGRESSION.tick(current, regressionBudget, regressionBudget);
            next = regressionResult.state();
            if (!next.equals(current)) {
                savedData.replace(next);
                publishPurifiedTransitions(level, current, next);
            }
        }

        TerrainRestorationService restoration = restoration(level);
        if (regressionResult != null) {
            regressionResult.regressedCells().stream()
                    .filter(ShroudRegressionScheduler.RegressedCell::cleared)
                    .forEach(cell -> restoration.scheduleClearedCell(level, cell.position()));
        }

        int cleanupBudget = EnshroudedConfig.purificationCleanupWorkPerTick();
        int visualMutations = restoration.tick(level, cleanupBudget, cleanupBudget);
        return !next.equals(current) || visualMutations > 0;
    }

    public static void clear() {
        RESTORATION.clear();
    }

    static int pendingCleanupWork(ResourceKey<Level> dimension) {
        TerrainRestorationService service = RESTORATION.get(Objects.requireNonNull(dimension, "dimension"));
        return service == null ? 0 : service.pendingWork();
    }

    private static void publishPurifiedTransitions(ServerLevel level, ShroudWorldState before, ShroudWorldState after) {
        before.cores().values().stream()
                .filter(core -> core.lifecycleState() == CoreLifecycleState.DESTROYED)
                .filter(core -> {
                    var updated = after.cores().get(core.id());
                    return updated != null && updated.lifecycleState() == CoreLifecycleState.PURIFIED;
                })
                .forEach(core -> {
                    ShroudPurificationPresentation.onPurified(level, core);
                    NeoForge.EVENT_BUS.post(new ShroudCorePurifiedEvent(level, core.id()));
                });
    }

    private static TerrainRestorationService restoration(ServerLevel level) {
        return RESTORATION.computeIfAbsent(
                level.dimension(),
                ignored -> new TerrainRestorationService(
                        CorruptionRuleReloadListener::currentRegistry,
                        DefaultMutationAuthority.fromConfig(
                                FlameWardRuntimeBindings.query(),
                                ProtectedAreaService.none()
                        ),
                        GEOMETRY,
                        RESTORATION_QUEUE_CAPACITY
                )
        );
    }
}
