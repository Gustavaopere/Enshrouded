package com.gustavaopere.enshrouded;

import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.registry.ModRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Enshrouded.MOD_ID)
public final class Enshrouded {
    public static final String MOD_ID = "enshrouded";
    public static final String MINECRAFT_LINE = "1.21.1";

    private static final Logger LOGGER = LoggerFactory.getLogger(Enshrouded.class);

    public Enshrouded(IEventBus modBus, ModContainer modContainer) {
        ModRegistries.register(modBus);
        modContainer.registerConfig(ModConfig.Type.SERVER, EnshroudedConfig.SERVER_SPEC);
        LOGGER.info("Enshrouded bootstrap complete");
    }
}
