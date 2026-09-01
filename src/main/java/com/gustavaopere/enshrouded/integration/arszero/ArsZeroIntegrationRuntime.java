package com.gustavaopere.enshrouded.integration.arszero;

import com.gustavaopere.enshrouded.story.boss.LichBossRuntime;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/** Registers the optional Ars Zero provider only after external registries have completed. */
public final class ArsZeroIntegrationRuntime {
    public static final int PROVIDER_PRIORITY = 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(ArsZeroIntegrationRuntime.class);
    private static boolean registered;
    private static boolean bootstrapped;

    private ArsZeroIntegrationRuntime() {
    }

    public static void register(IEventBus modBus) {
        Objects.requireNonNull(modBus, "modBus");
        if (registered) {
            return;
        }
        registered = true;
        modBus.addListener(ArsZeroIntegrationRuntime::onCommonSetup);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ArsZeroIntegrationRuntime::bootstrap);
    }

    private static synchronized void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;

        ArsZeroCompatibilityProbe probe = ArsZeroCompatibilityProbe.detect();
        if (probe.status() == ArsZeroCompatibilityProbe.Status.MOD_ABSENT) {
            return;
        }
        if (!probe.available()) {
            LOGGER.warn(
                    "Ars Zero is loaded but {} is not the expected monster entity; using native Lich manifestation provider",
                    ArsZeroCompatibilityProbe.LICH_ID
            );
            return;
        }

        LichBossRuntime.registerProvider(new ArsZeroLichProvider(probe), PROVIDER_PRIORITY);
        LOGGER.info("Ars Zero Lich provider enabled for {}", ArsZeroCompatibilityProbe.LICH_ID);
    }
}
