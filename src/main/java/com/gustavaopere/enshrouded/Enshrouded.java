package com.gustavaopere.enshrouded;

import com.gustavaopere.enshrouded.combat.magic.MagicResistanceRuntime;
import com.gustavaopere.enshrouded.command.ShroudCoreCommand;
import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionRuntime;
import com.gustavaopere.enshrouded.exposure.ExposureRuntime;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarRuntime;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionRuntime;
import com.gustavaopere.enshrouded.flame.ward.FlameWardRuntime;
import com.gustavaopere.enshrouded.integration.arszero.ArsZeroIntegrationRuntime;
import com.gustavaopere.enshrouded.network.ModNetworking;
import com.gustavaopere.enshrouded.network.ShroudSyncRuntime;
import com.gustavaopere.enshrouded.registry.ModRegistries;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreRegistrationQueue;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionRuleReloadRuntime;
import com.gustavaopere.enshrouded.story.manifestation.ManifestationRuntime;
import com.gustavaopere.enshrouded.story.ritual.LevelOneLichSkullRitual;
import com.gustavaopere.enshrouded.story.state.StoryStateRuntime;
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
        ArsZeroIntegrationRuntime.register(modBus);
        FlameAltarRuntime.registerRitual(new LevelOneLichSkullRitual());
        modBus.addListener(ModNetworking::register);
        modContainer.registerConfig(ModConfig.Type.SERVER, EnshroudedConfig.SERVER_SPEC);
        ShroudCoreRegistrationQueue.registerRuntime();
        ShroudCoreCommand.registerRuntime();
        ShroudSyncRuntime.register();
        FlameProgressionRuntime.register();
        StoryStateRuntime.register();
        ManifestationRuntime.register();
        FlameWardRuntime.register();
        ExposureRuntime.register();
        EntityCorruptionRuntime.register();
        MagicResistanceRuntime.register();
        CorruptionRuleReloadRuntime.register();
        LOGGER.info("Enshrouded bootstrap complete");
    }
}
