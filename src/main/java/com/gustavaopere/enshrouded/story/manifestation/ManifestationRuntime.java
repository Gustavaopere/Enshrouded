package com.gustavaopere.enshrouded.story.manifestation;

import com.gustavaopere.enshrouded.api.progression.ProgressionRuntimeBindings;
import com.gustavaopere.enshrouded.exposure.ExposureRuntime;
import com.gustavaopere.enshrouded.registry.ModItems;
import com.gustavaopere.enshrouded.story.boss.LichBossRuntime;
import com.gustavaopere.enshrouded.story.reward.LichRewardService;
import com.gustavaopere.enshrouded.story.reward.LichSkullItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * Runtime bridge for the first manifestation encounter.
 *
 * <p>Encounter identity and story transitions remain owned by {@link ManifestationEncounterService};
 * reward authority remains owned by {@link LichRewardService}. This class composes the bounded arena
 * overlay into Exposure, routes real NeoForge death events, and delivers the Enshrouded trophy without
 * touching provider-owned loot. Reward state is committed only when the physical drop was accepted by
 * the server level.</p>
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
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, ManifestationRuntime::onLivingDeath);
    }

    static void onLivingDeath(LivingDeathEvent event) {
        if (event.isCanceled()) {
            return;
        }

        LivingEntity actor = event.getEntity();
        SERVICE.defeatFromActor(actor).ifPresent(defeat -> {
            if (!(actor.level() instanceof ServerLevel level)) {
                return;
            }
            LichRewardService.forLevel(level).issue(defeat.encounterId(), receipt -> {
                ItemStack skull = LichSkullItem.createAuthentic(
                        ModItems.LICH_SKULL_MANIFESTATION_1.get(),
                        receipt.encounterId(),
                        receipt.manifestationIndex()
                );
                ItemEntity drop = new ItemEntity(
                        level,
                        actor.getX(),
                        actor.getY() + 0.5D,
                        actor.getZ(),
                        skull
                );
                drop.setDefaultPickUpDelay();
                return level.addFreshEntity(drop);
            });
        });
    }
}
