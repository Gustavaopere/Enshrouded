package com.gustavaopere.enshrouded.client.state;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.client.render.ShroudFogController;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

/**
 * Resets client-only exposure presentation state when a connection ends.
 *
 * <p>The authoritative server tracker deliberately starts a fresh sequence epoch after logout.
 * Keeping the previous client sequence would make legitimate sequence 0..N packets from the next
 * connection look stale, so all exposure-derived presentation caches must reset at the same
 * lifecycle boundary.</p>
 */
@EventBusSubscriber(modid = Enshrouded.MOD_ID, value = Dist.CLIENT)
public final class ClientExposureLifecycle {
    private ClientExposureLifecycle() {
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientExposureState.INSTANCE.reset();
        ShroudFogController.reset();
    }
}
