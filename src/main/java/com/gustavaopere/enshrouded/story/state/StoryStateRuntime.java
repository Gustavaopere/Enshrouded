package com.gustavaopere.enshrouded.story.state;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.UUID;

/** Reconciles persisted Lich encounters once the active server has finished starting. */
public final class StoryStateRuntime {
    private StoryStateRuntime() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(StoryStateRuntime::onServerStarted);
    }

    static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        StorySavedData.get(server).reconcileActiveEncounters(entityId -> isLivingActorPresent(server, entityId));
    }

    private static boolean isLivingActorPresent(MinecraftServer server, UUID entityId) {
        for (var level : server.getAllLevels()) {
            var entity = level.getEntity(entityId);
            if (entity instanceof LivingEntity living && living.isAlive() && !living.isRemoved()) {
                return true;
            }
        }
        return false;
    }
}
