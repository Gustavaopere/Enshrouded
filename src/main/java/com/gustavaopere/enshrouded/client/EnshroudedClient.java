package com.gustavaopere.enshrouded.client;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.client.hud.ShroudHudOverlay;
import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/** Physical-client-only Stage 07 bootstrap. */
@Mod(value = Enshrouded.MOD_ID, dist = Dist.CLIENT)
public final class EnshroudedClient {
    public EnshroudedClient(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, EnshroudedClientConfig.CLIENT_SPEC);
        modBus.addListener(ShroudHudOverlay::registerGuiLayers);
    }
}
