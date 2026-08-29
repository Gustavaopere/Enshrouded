package com.gustavaopere.enshrouded.exposure.redsludge;

import com.gustavaopere.enshrouded.exposure.ExposureRuntime;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** Server-authoritative contact seam for the Red Sludge hazard. */
public final class RedSludgeExposureHandler {
    private static final float SECONDARY_CONTACT_DAMAGE = 1.0F;
    private static final Map<UUID, Long> LAST_CONTACT_TICK = new HashMap<>();

    private RedSludgeExposureHandler() {
    }

    public static void onContact(Entity entity) {
        Objects.requireNonNull(entity, "entity");
        if (entity instanceof ServerPlayer player) {
            onContact(
                    player,
                    ExposureRuntime::processForcedDeadlyContact,
                    RedSludgeExposureHandler::applyVanillaSecondaryDamage
            );
        }
    }

    static void onContact(
            ServerPlayer player,
            Consumer<ServerPlayer> exposureProcessor,
            BiConsumer<ServerPlayer, Float> damageSink) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(exposureProcessor, "exposureProcessor");
        Objects.requireNonNull(damageSink, "damageSink");

        long serverTick = player.serverLevel().getServer().getTickCount();
        Long previous = LAST_CONTACT_TICK.put(player.getUUID(), serverTick);
        if (previous != null && previous == serverTick) {
            return;
        }

        exposureProcessor.accept(player);
        if (player.isAlive()) {
            damageSink.accept(player, SECONDARY_CONTACT_DAMAGE);
        }
    }

    private static void applyVanillaSecondaryDamage(ServerPlayer player, float amount) {
        player.hurt(player.damageSources().generic(), amount);
    }

    public static void forget(UUID playerId) {
        LAST_CONTACT_TICK.remove(Objects.requireNonNull(playerId, "playerId"));
    }
}
