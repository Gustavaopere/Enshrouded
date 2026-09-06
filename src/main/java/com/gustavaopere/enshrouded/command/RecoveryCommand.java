package com.gustavaopere.enshrouded.command;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.datafix.PersistentSubsystem;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.core.CoreMutationResult;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreBlockEntity;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreDestroyedEvent;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreService;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.gustavaopere.enshrouded.story.state.EncounterOutcome;
import com.gustavaopere.enshrouded.story.state.EncounterRecord;
import com.gustavaopere.enshrouded.story.state.StorySavedData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Explicit operator recovery for narrowly diagnosable persistence mismatches.
 *
 * <p>Commands never scan chunks/entities globally, never force-load chunks and never expose a
 * destructive reset-all path. Repair either replays an existing canonical owner or performs the
 * narrow lifecycle transition that normal runtime would have performed.</p>
 */
public final class RecoveryCommand {
    private static final AtomicBoolean RUNTIME_REGISTERED = new AtomicBoolean();

    private RecoveryCommand() {
    }

    public static void registerRuntime() {
        if (RUNTIME_REGISTERED.compareAndSet(false, true)) {
            NeoForge.EVENT_BUS.addListener(RecoveryCommand::onRegisterCommands);
        }
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> recovery = Commands.literal("recovery")
                .then(Commands.literal("diagnose")
                        .executes(context -> diagnose(context.getSource())))
                .then(Commands.literal("core_requeue")
                        .executes(context -> coreRequeue(
                                context.getSource(),
                                BlockPos.containing(context.getSource().getPosition())))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(context -> coreRequeue(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos")))))
                .then(Commands.literal("core_retire_missing")
                        .executes(context -> coreRetireMissing(
                                context.getSource(),
                                BlockPos.containing(context.getSource().getPosition())))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(context -> coreRetireMissing(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos")))))
                .then(Commands.literal("story_reconcile")
                        .executes(context -> storyReconcile(context.getSource())));

        dispatcher.register(Commands.literal(Enshrouded.MOD_ID)
                .requires(source -> source.hasPermission(2))
                .then(recovery));
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static int diagnose(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        int loadedLogicalCores = 0;
        int loadedCoreMismatches = 0;

        for (ServerLevel level : server.getAllLevels()) {
            for (ShroudCoreState core : ShroudSavedData.get(level).state().cores().values()) {
                BlockPos center = core.center();
                if (!level.hasChunkAt(center)) {
                    continue;
                }
                loadedLogicalCores++;
                if (!(level.getBlockEntity(center) instanceof ShroudCoreBlockEntity blockEntity)) {
                    if (core.lifecycleState() == CoreLifecycleState.DORMANT
                            || core.lifecycleState() == CoreLifecycleState.ACTIVE) {
                        loadedCoreMismatches++;
                    }
                    continue;
                }
                ShroudCoreBlockEntity.RecoveryIdentity identity = blockEntity.recoveryIdentity();
                if (!identity.coreId().equals(core.id()) || !identity.regionId().equals(core.regionId())) {
                    loadedCoreMismatches++;
                }
            }
        }

        StorySavedData story = StorySavedData.get(server);
        int activeEncounters = 0;
        int orphanedEncounters = 0;
        for (EncounterRecord encounter : story.state().encounters().values()) {
            if (encounter.outcome() != EncounterOutcome.ACTIVE) {
                continue;
            }
            activeEncounters++;
            UUID entityId = encounter.entityId().orElseThrow();
            if (!isLivingActorPresent(server, entityId)) {
                orphanedEncounters++;
            }
        }

        int finalLoadedLogicalCores = loadedLogicalCores;
        int finalLoadedCoreMismatches = loadedCoreMismatches;
        int finalActiveEncounters = activeEncounters;
        int finalOrphanedEncounters = orphanedEncounters;
        source.sendSuccess(() -> Component.literal(
                "Enshrouded persistence schemas: " + schemaSummary()
                        + "; loaded logical cores=" + finalLoadedLogicalCores
                        + ", loaded core mismatches=" + finalLoadedCoreMismatches
                        + "; active encounters=" + finalActiveEncounters
                        + ", orphaned encounters=" + finalOrphanedEncounters
        ), false);
        return finalLoadedCoreMismatches + finalOrphanedEncounters;
    }

    private static int coreRequeue(CommandSourceStack source, BlockPos pos) {
        ServerLevel level = source.getLevel();
        if (!level.hasChunkAt(pos)) {
            source.sendFailure(Component.literal("Target chunk is not loaded; refusing to force-load it."));
            return 0;
        }
        if (!(level.getBlockEntity(pos) instanceof ShroudCoreBlockEntity blockEntity)) {
            source.sendFailure(Component.literal("No physical Shroud core block entity at " + pos.toShortString()));
            return 0;
        }

        ShroudCoreBlockEntity.RecoveryIdentity identity = blockEntity.recoveryIdentity();
        ShroudSavedData data = ShroudSavedData.get(level);
        Optional<ShroudCoreState> logicalAtPosition = logicalCoreAt(data, pos);
        if (logicalAtPosition.isPresent()) {
            ShroudCoreState logical = logicalAtPosition.orElseThrow();
            if (!logical.id().equals(identity.coreId()) || !logical.regionId().equals(identity.regionId())) {
                source.sendFailure(Component.literal(
                        "Physical/logical Shroud core identity conflict at " + pos.toShortString()
                                + "; refusing to overwrite canonical state."
                ));
                return 0;
            }
            source.sendSuccess(() -> Component.literal(
                    "Shroud core is already logically registered with matching identity at " + pos.toShortString()
            ), false);
            return 1;
        }

        ShroudCoreState sameId = data.state().cores().get(identity.coreId());
        if (sameId != null && !sameId.center().equals(pos)) {
            source.sendFailure(Component.literal(
                    "Physical core ID already belongs to logical core at " + sameId.center().toShortString()
                            + "; refusing recovery collision."
            ));
            return 0;
        }
        boolean regionCollision = data.state().regions().containsKey(identity.regionId());
        if (regionCollision) {
            source.sendFailure(Component.literal(
                    "Physical region ID is already owned by another logical record; refusing recovery collision."
            ));
            return 0;
        }

        blockEntity.enqueueRecoveryRegistration(level);
        source.sendSuccess(() -> Component.literal(
                "Queued canonical Shroud core registration recovery at " + pos.toShortString()
        ), true);
        return 1;
    }

    private static int coreRetireMissing(CommandSourceStack source, BlockPos pos) {
        ServerLevel level = source.getLevel();
        if (!level.hasChunkAt(pos)) {
            source.sendFailure(Component.literal("Target chunk is not loaded; refusing to force-load it."));
            return 0;
        }

        ShroudSavedData data = ShroudSavedData.get(level);
        Optional<ShroudCoreState> logicalAtPosition = logicalCoreAt(data, pos);
        if (logicalAtPosition.isEmpty()) {
            source.sendFailure(Component.literal("No logical Shroud core registered at " + pos.toShortString()));
            return 0;
        }
        if (level.getBlockState(pos).is(ModBlocks.SHROUD_CORE.get())
                || level.getBlockEntity(pos) instanceof ShroudCoreBlockEntity) {
            source.sendFailure(Component.literal(
                    "A physical Shroud core still exists at " + pos.toShortString()
                            + "; refusing missing-core retirement."
            ));
            return 0;
        }

        ShroudCoreState core = logicalAtPosition.orElseThrow();
        CoreMutationResult result;
        if (core.lifecycleState() == CoreLifecycleState.DORMANT) {
            result = ShroudCoreService.discardDormant(data.state(), core.id());
        } else if (core.lifecycleState() == CoreLifecycleState.ACTIVE) {
            result = ShroudCoreService.destroy(data.state(), core.id());
        } else {
            source.sendSuccess(() -> Component.literal(
                    "Logical Shroud core at " + pos.toShortString()
                            + " is already " + core.lifecycleState().id() + "; no recovery mutation required."
            ), false);
            return 1;
        }

        if (!result.changed()) {
            source.sendFailure(Component.literal("Recovery produced no canonical state transition for " + core.id()));
            return 0;
        }
        data.replace(result.state());
        if (core.lifecycleState() == CoreLifecycleState.ACTIVE) {
            NeoForge.EVENT_BUS.post(new ShroudCoreDestroyedEvent(level, core.id()));
        }
        source.sendSuccess(() -> Component.literal(
                "Retired missing physical Shroud core " + core.id()
                        + " using canonical lifecycle transition."
        ), true);
        return 1;
    }

    private static int storyReconcile(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        boolean changed = StorySavedData.get(server).reconcileActiveEncounters(
                entityId -> isLivingActorPresent(server, entityId)
        );
        if (!changed) {
            source.sendSuccess(() -> Component.literal("No orphaned ACTIVE story encounter required reconciliation."), false);
            return 1;
        }
        source.sendSuccess(() -> Component.literal(
                "Reconciled orphaned ACTIVE story encounter(s) by canonical ABORTED transition; no reward was issued."
        ), true);
        return 1;
    }

    private static Optional<ShroudCoreState> logicalCoreAt(ShroudSavedData data, BlockPos pos) {
        return data.state().cores().values().stream()
                .filter(candidate -> candidate.center().equals(pos))
                .min(Comparator.comparing(ShroudCoreState::id));
    }

    private static boolean isLivingActorPresent(MinecraftServer server, UUID entityId) {
        for (ServerLevel level : server.getAllLevels()) {
            var entity = level.getEntity(entityId);
            if (entity instanceof LivingEntity living && living.isAlive() && !living.isRemoved()) {
                return true;
            }
        }
        return false;
    }

    private static String schemaSummary() {
        StringBuilder result = new StringBuilder();
        for (PersistentSubsystem subsystem : PersistentSubsystem.values()) {
            if (!result.isEmpty()) {
                result.append(", ");
            }
            result.append(subsystem.id()).append('=').append(subsystem.currentVersion());
        }
        return result.toString();
    }
}
