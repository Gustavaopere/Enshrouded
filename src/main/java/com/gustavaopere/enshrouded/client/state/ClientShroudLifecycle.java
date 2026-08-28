package com.gustavaopere.enshrouded.client.state;

import com.gustavaopere.enshrouded.Enshrouded;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

/**
 * Connection-scoped lifecycle for authoritative Shroud sequence numbers.
 */
@EventBusSubscriber(modid = Enshrouded.MOD_ID, value = Dist.CLIENT)
public final class ClientShroudLifecycle {
    private ClientShroudLifecycle() {
    }

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientShroudState.INSTANCE.reset();
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientShroudState.INSTANCE.reset();
    }
}
