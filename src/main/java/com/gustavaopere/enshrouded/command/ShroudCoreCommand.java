package com.gustavaopere.enshrouded.command;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreBlockEntity;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Operator-only Level-1 discovery/testing commands. They deliberately use the same physical core
 * and lifecycle paths as normal gameplay rather than mutating Shroud SavedData directly.
 */
public final class ShroudCoreCommand {
    private static final AtomicBoolean RUNTIME_REGISTERED = new AtomicBoolean();

    private ShroudCoreCommand() {
    }

    public static void registerRuntime() {
        if (RUNTIME_REGISTERED.compareAndSet(false, true)) {
            NeoForge.EVENT_BUS.addListener(ShroudCoreCommand::onRegisterCommands);
        }
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> core = Commands.literal("core")
                .then(Commands.literal("create")
                        .executes(context -> create(
                                context.getSource(),
                                BlockPos.containing(context.getSource().getPosition())))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(context -> create(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos")))))
                .then(Commands.literal("inspect")
                        .executes(context -> inspect(
                                context.getSource(),
                                BlockPos.containing(context.getSource().getPosition())))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(context -> inspect(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos")))))
                .then(Commands.literal("destroy")
                        .executes(context -> destroy(
                                context.getSource(),
                                BlockPos.containing(context.getSource().getPosition())))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(context -> destroy(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos")))));

        dispatcher.register(Commands.literal(Enshrouded.MOD_ID)
                .requires(source -> source.hasPermission(2))
                .then(core));
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static int create(CommandSourceStack source, BlockPos pos) {
        ServerLevel level = source.getLevel();
        if (!level.hasChunkAt(pos)) {
            source.sendFailure(Component.literal("Target chunk is not loaded; refusing to force-load it."));
            return 0;
        }
        if (!level.getBlockState(pos).canBeReplaced()) {
            source.sendFailure(Component.literal("Target position is not replaceable: " + pos.toShortString()));
            return 0;
        }
        if (!Block.canSupportRigidBlock(level, pos.below())) {
            source.sendFailure(Component.literal("Shroud core requires rigid support below: " + pos.toShortString()));
            return 0;
        }
        if (!level.setBlock(pos, ModBlocks.SHROUD_CORE.get().defaultBlockState(), Block.UPDATE_CLIENTS)) {
            source.sendFailure(Component.literal("Failed to place Shroud core at " + pos.toShortString()));
            return 0;
        }
        if (level.getBlockEntity(pos) instanceof ShroudCoreBlockEntity coreBlockEntity) {
            coreBlockEntity.requestAutomaticActivation();
        }
        source.sendSuccess(() -> Component.literal("Created Level 1 Shroud core seed at " + pos.toShortString()), true);
        return 1;
    }

    private static int inspect(CommandSourceStack source, BlockPos pos) {
        ServerLevel level = source.getLevel();
        Optional<ShroudCoreState> core = ShroudSavedData.get(level).state().cores().values().stream()
                .filter(candidate -> candidate.center().equals(pos))
                .min(Comparator.comparing(ShroudCoreState::id));
        if (core.isEmpty()) {
            source.sendFailure(Component.literal("No registered Shroud core at " + pos.toShortString()));
            return 0;
        }

        ShroudCoreState state = core.orElseThrow();
        source.sendSuccess(() -> Component.literal(
                "Shroud core " + state.id()
                        + " state=" + state.lifecycleState().id()
                        + " tier=" + state.tier()
                        + " radius=" + state.maxInfluenceRadius()), false);
        return 1;
    }

    private static int destroy(CommandSourceStack source, BlockPos pos) {
        ServerLevel level = source.getLevel();
        if (!level.hasChunkAt(pos)) {
            source.sendFailure(Component.literal("Target chunk is not loaded; refusing to force-load it."));
            return 0;
        }
        if (!level.getBlockState(pos).is(ModBlocks.SHROUD_CORE.get())) {
            source.sendFailure(Component.literal("No physical Shroud core at " + pos.toShortString()));
            return 0;
        }
        if (!level.destroyBlock(pos, false)) {
            source.sendFailure(Component.literal("Failed to destroy Shroud core at " + pos.toShortString()));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Destroyed Shroud core at " + pos.toShortString()), true);
        return 1;
    }
}
