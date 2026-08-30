package com.gustavaopere.enshrouded.exposure;

import com.gustavaopere.enshrouded.api.progression.FlamePassageQuery;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.api.progression.ProgressionRuntimeBindings;
import com.gustavaopere.enshrouded.api.shroud.ShroudQuery;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.exposure.deadly.FlameGatedDeadlyExposurePolicy;
import com.gustavaopere.enshrouded.exposure.deadly.PassageRequirement;
import com.gustavaopere.enshrouded.exposure.madness.MadnessRuntime;
import com.gustavaopere.enshrouded.exposure.redsludge.RedSludgeExposureHandler;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.query.DefaultShroudQuery;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;
import java.util.Optional;

/**
 * Authoritative Level-1 player exposure runtime. Persistent state lives only in the player
 * attachment; cadence and sync tracking are ephemeral and are discarded on logout.
 */
public final class ExposureRuntime {
    public static final int SAMPLE_INTERVAL_TICKS = 20;
    public static final int MAX_ELAPSED_TICKS = SAMPLE_INTERVAL_TICKS * 5;

    private static final ShroudQuery QUERY = DefaultShroudQuery.levelOne(ShroudGridGeometry.levelOne());
    private static final ExposureSamplingCadence CADENCE = new ExposureSamplingCadence(SAMPLE_INTERVAL_TICKS);
    private static final ExposurePlayerSyncTracker SYNC_TRACKER = new ExposurePlayerSyncTracker();
    private static final ProgressionOwnerResolver PROGRESSION_OWNER_RESOLVER = ProgressionRuntimeBindings.ownerResolver();
    private static final FlamePassageQuery FLAME_PASSAGE_QUERY = ProgressionRuntimeBindings.passageQuery();
    private static final ShroudSample FORCED_DEADLY_CONTACT = new ShroudSample(
            1.0F,
            ShroudSeverity.DEADLY,
            Optional.empty(),
            false
    );

    private ExposureRuntime() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ExposureRuntime::onPlayerTickPost);
        NeoForge.EVENT_BUS.addListener(ExposureRuntime::onPlayerLoggedOut);
    }

    static void onPlayerTickPost(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        var attachmentType = ShroudExposureAttachment.PLAYER_EXPOSURE.get();
        int maxReserveTicks = EnshroudedConfig.exposureMaxReserveTicks();
        if (player.hasData(attachmentType)) {
            ShroudExposureAttachment persisted = player.getData(attachmentType);
            MadnessRuntime.enforcePenalty(
                    player,
                    Math.min(persisted.remainingTicks(), maxReserveTicks),
                    maxReserveTicks
            );
        }

        long serverTick = player.serverLevel().getServer().getTickCount();
        CADENCE.elapsedTicks(player.getUUID(), serverTick)
                .ifPresent(elapsedTicks -> process(player, Math.min(elapsedTicks, MAX_ELAPSED_TICKS)));
    }

    static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            forget(player);
        }
    }

    static ExposureSnapshot process(ServerPlayer player, int elapsedTicks) {
        Objects.requireNonNull(player, "player");
        ShroudSample sample = QUERY.sample(player.serverLevel(), player.blockPosition(), player);
        return processSample(player, sample, elapsedTicks);
    }

    /**
     * Applies one immediate local Deadly hazard tick while preserving the ordinary attachment,
     * progression gate, sync and Madness pipeline. The ordinary cadence is aligned to this server
     * tick so local contact cannot double-charge the same elapsed interval.
     */
    public static ExposureSnapshot processForcedDeadlyContact(ServerPlayer player) {
        Objects.requireNonNull(player, "player");
        long serverTick = player.serverLevel().getServer().getTickCount();
        CADENCE.align(player.getUUID(), serverTick);
        return processSample(player, FORCED_DEADLY_CONTACT, 1);
    }

    private static ExposureSnapshot processSample(ServerPlayer player, ShroudSample sample, int elapsedTicks) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(sample, "sample");
        if (elapsedTicks < 0) {
            throw new IllegalArgumentException("elapsedTicks must be >= 0");
        }

        var attachmentType = ShroudExposureAttachment.PLAYER_EXPOSURE.get();
        int maxReserveTicks = EnshroudedConfig.exposureMaxReserveTicks();
        ShroudExposureAttachment state;
        if (player.hasData(attachmentType)) {
            ShroudExposureAttachment persisted = player.getData(attachmentType);
            state = new ShroudExposureAttachment(
                    ExposureSchema.CURRENT_VERSION,
                    Math.min(persisted.remainingTicks(), maxReserveTicks)
            );
        } else {
            state = ShroudExposureAttachment.full(maxReserveTicks);
            player.setData(attachmentType, state);
        }

        ExposureService service = new ExposureService(
                maxReserveTicks,
                1,
                1,
                MAX_ELAPSED_TICKS,
                new FlameGatedDeadlyExposurePolicy(
                        PROGRESSION_OWNER_RESOLVER,
                        FLAME_PASSAGE_QUERY,
                        new PassageRequirement(EnshroudedConfig.deadlyRequiredPassageLevel()),
                        EnshroudedConfig.exposureEmergencyWindowTicks(),
                        DeadlyExposurePolicy.DEFAULT_RAPID_DRAIN_TICKS_PER_TICK
                )
        );
        ExposureSnapshot snapshot = service.tick(player.getUUID(), state, sample, elapsedTicks);
        ShroudExposureAttachment nextState = snapshot.attachmentState();
        if (!nextState.equals(state) || !player.hasData(attachmentType)) {
            player.setData(attachmentType, nextState);
        }

        SYNC_TRACKER.update(player.getUUID(), snapshot)
                .ifPresent(payload -> PacketDistributor.sendToPlayer(player, payload));
        MadnessRuntime.apply(player, snapshot);
        return snapshot;
    }

    static void forget(ServerPlayer player) {
        Objects.requireNonNull(player, "player");
        CADENCE.forget(player.getUUID());
        SYNC_TRACKER.forget(player.getUUID());
        RedSludgeExposureHandler.forget(player.getUUID());
    }
}
