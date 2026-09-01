package com.gustavaopere.enshrouded.integration;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.integration.epicfight.EpicFightAdapter;
import com.gustavaopere.enshrouded.integration.ftbchunks.FtbChunksCompatibilityProbe;
import com.gustavaopere.enshrouded.integration.ftbchunks.FtbChunksProtectionAdapter;
import com.gustavaopere.enshrouded.integration.ftbteams.FtbTeamsCompatibilityProbe;
import com.gustavaopere.enshrouded.integration.ftbteams.FtbTeamsOwnerResolver;
import com.gustavaopere.enshrouded.integration.minecolonies.MineColoniesCompatibilityProbe;
import com.gustavaopere.enshrouded.integration.minecolonies.MineColoniesProtectionAdapter;
import com.gustavaopere.enshrouded.protection.ProtectedAreaService;
import com.gustavaopere.enshrouded.protection.ProtectionRuntimeBindings;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Optional Stage-08 integration bootstrap. Provider API shapes are detected once and cached;
 * canonical Enshrouded authorities remain the only mutation/progression owners.
 */
public final class CombatClaimsTeamsIntegrationRuntime {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombatClaimsTeamsIntegrationRuntime.class);
    private static final AtomicBoolean REGISTERED = new AtomicBoolean();

    private static volatile FtbTeamsCompatibilityProbe teamsProbe = FtbTeamsCompatibilityProbe.detect(false);
    private static volatile FtbChunksCompatibilityProbe chunksProbe = FtbChunksCompatibilityProbe.detect(false);
    private static volatile MineColoniesCompatibilityProbe coloniesProbe = MineColoniesCompatibilityProbe.detect(false);
    private static volatile EpicFightAdapter epicFight = new EpicFightAdapter(false);

    private static final ProgressionOwnerResolver OWNER_RESOLVER = playerId -> {
        Objects.requireNonNull(playerId, "playerId");
        if (!EnshroudedConfig.ftbTeamsSharedProgression()) {
            return ProgressionOwner.player(playerId);
        }
        return new FtbTeamsOwnerResolver(teamsProbe::teamId).resolve(playerId);
    };

    private CombatClaimsTeamsIntegrationRuntime() {
    }

    public static void register(IEventBus modBus) {
        Objects.requireNonNull(modBus, "modBus");
        if (!REGISTERED.compareAndSet(false, true)) {
            return;
        }

        ModList modList = ModList.get();
        teamsProbe = FtbTeamsCompatibilityProbe.detect(modList.isLoaded("ftbteams"));
        chunksProbe = FtbChunksCompatibilityProbe.detect(modList.isLoaded("ftbchunks"));
        coloniesProbe = MineColoniesCompatibilityProbe.detect(modList.isLoaded("minecolonies"));
        epicFight = new EpicFightAdapter(ModList.get().isLoaded("epicfight"));

        logProbeState("FTB Teams", teamsProbe.status());
        logProbeState("FTB Chunks", chunksProbe.status());
        logProbeState("MineColonies", coloniesProbe.status());
        if (epicFight.loaded()) {
            LOGGER.info("Epic Fight compatibility detected; Enshrouded keeps its existing damage pipeline unchanged");
        }

        NeoForge.EVENT_BUS.addListener(CombatClaimsTeamsIntegrationRuntime::onServerStarted);
        NeoForge.EVENT_BUS.addListener(CombatClaimsTeamsIntegrationRuntime::onServerStopped);
    }

    public static ProgressionOwnerResolver ownerResolver() {
        return OWNER_RESOLVER;
    }

    static void onServerStarted(ServerStartedEvent event) {
        List<ProtectedAreaService> providers = new ArrayList<>(2);
        if (chunksProbe.status() != FtbChunksCompatibilityProbe.Status.MOD_ABSENT) {
            providers.add(new FtbChunksProtectionAdapter(chunksProbe::protectionAt));
        }
        if (coloniesProbe.status() != MineColoniesCompatibilityProbe.Status.MOD_ABSENT) {
            providers.add(new MineColoniesProtectionAdapter(coloniesProbe::protectionAt));
        }
        ProtectionRuntimeBindings.install(providers);
    }

    static void onServerStopped(ServerStoppedEvent event) {
        ProtectionRuntimeBindings.reset();
    }

    private static void logProbeState(String provider, Enum<?> status) {
        if ("INCOMPATIBLE".equals(status.name())) {
            LOGGER.warn("{} is loaded but its expected 1.21.1 API contract is unavailable; queries will fail closed", provider);
        } else if ("AVAILABLE".equals(status.name())) {
            LOGGER.info("{} optional integration enabled", provider);
        }
    }
}
