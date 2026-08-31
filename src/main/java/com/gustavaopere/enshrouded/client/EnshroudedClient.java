package com.gustavaopere.enshrouded.client;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.client.ambient.ShroudAmbientController;
import com.gustavaopere.enshrouded.client.effects.ShroudParticleController;
import com.gustavaopere.enshrouded.client.hud.ShroudHudOverlay;
import com.gustavaopere.enshrouded.client.render.ShroudFogController;
import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

/** Physical-client-only Stage 07 bootstrap. */
@Mod(value = Enshrouded.MOD_ID, dist = Dist.CLIENT)
public final class EnshroudedClient {
    public EnshroudedClient(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, EnshroudedClientConfig.CLIENT_SPEC);
        modBus.addListener(ShroudHudOverlay::registerGuiLayers);
        modBus.addListener(ShroudParticleController::registerParticleProviders);
        ShroudFogController.register(NeoForge.EVENT_BUS);
        ShroudAmbientController.register(NeoForge.EVENT_BUS);
        ShroudParticleController.register(NeoForge.EVENT_BUS);
    }
}
