package com.gustavaopere.enshrouded.story.manifestation;

import com.gustavaopere.enshrouded.api.progression.ProgressionRuntimeBindings;
import com.gustavaopere.enshrouded.exposure.ExposureRuntime;
import com.gustavaopere.enshrouded.story.boss.LichBossRuntime;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * Runtime bridge for the first manifestation encounter.
 *
 * <p>Encounter identity and story transitions remain owned by {@link ManifestationEncounterService};
 * this class only composes the bounded arena overlay into the canonical Exposure query and routes
 * real NeoForge death events to the encounter service.</p>
 */
public final class ManifestationRuntime {
    private static final FirstManifestationDefinition DEFINITION = FirstManifestationDefinition.levelOne();
    private static final LichArenaRule ARENA_RULE = new LichArenaRule(ExposureRuntime.shroudQuery(), DEFINITION);
    private static final ManifestationEncounterService SERVICE = new ManifestationEncounterService(
            ProgressionRuntimeBindings.ownerResolver(),
            LichBossRuntime.director(),
            ARENA_RULE
    );
    private static boolean registered;

    private ManifestationRuntime() {
    }

    public static ManifestationEncounterService service() {
        return SERVICE;
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        ExposureRuntime.decorateShroudQuery(current -> ARENA_RULE);
        NeoForge.EVENT_BUS.addListener(ManifestationRuntime::onLivingDeath);
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        SERVICE.defeatFromActor(event.getEntity());
    }
}
