package com.gustavaopere.enshrouded.story.manifestation;

import com.gustavaopere.enshrouded.api.progression.ProgressionRuntimeBindings;
import com.gustavaopere.enshrouded.story.boss.LichBossRuntime;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * Runtime bridge for the first manifestation encounter.
 *
 * <p>Encounter identity and story transitions remain owned by {@link ManifestationEncounterService};
 * this class only exposes the canonical runtime service and routes real NeoForge death events to it.</p>
 */
public final class ManifestationRuntime {
    private static final ManifestationEncounterService SERVICE = new ManifestationEncounterService(
            ProgressionRuntimeBindings.ownerResolver(),
            LichBossRuntime.director()
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
        NeoForge.EVENT_BUS.addListener(ManifestationRuntime::onLivingDeath);
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        SERVICE.defeatFromActor(event.getEntity());
    }
}
