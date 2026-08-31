package com.gustavaopere.enshrouded.client.state;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.client.ambient.ShroudAmbientController;
import com.gustavaopere.enshrouded.client.effects.ShroudParticleController;
import com.gustavaopere.enshrouded.client.render.ShroudFogController;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

/** Resets all exposure-derived client presentation state when a connection ends. */
@EventBusSubscriber(modid = Enshrouded.MOD_ID, value = Dist.CLIENT)
public final class ClientExposureLifecycle {
    private ClientExposureLifecycle() {}

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientExposureState.INSTANCE.reset();
        ShroudFogController.reset();
        ShroudAmbientController.reset();
        ShroudParticleController.reset();
    }
}
