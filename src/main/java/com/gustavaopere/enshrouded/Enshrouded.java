package com.gustavaopere.enshrouded;

import com.gustavaopere.enshrouded.registry.ModRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Enshrouded.MOD_ID)
public final class Enshrouded {
    public static final String MOD_ID = "enshrouded";
    public static final String MINECRAFT_LINE = "1.21.1";

    private static final Logger LOGGER = LoggerFactory.getLogger(Enshrouded.class);

    public Enshrouded(IEventBus modBus) {
        ModRegistries.register(modBus);
        LOGGER.info("Enshrouded bootstrap complete");
    }
}
