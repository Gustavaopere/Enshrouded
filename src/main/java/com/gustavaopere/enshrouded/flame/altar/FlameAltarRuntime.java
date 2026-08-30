package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionRuntimeBindings;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitual;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualExecutor;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitualRegistry;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionSavedData;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Objects;

/**
 * Shared runtime binding between physical Flame Altars and the canonical ritual engine.
 *
 * <p>The registry contains definitions only. Owner progression remains in
 * {@link FlameProgressionSavedData}; execution remains in {@link FlameRitualExecutor}.</p>
 */
public final class FlameAltarRuntime {
    private static final FlameRitualRegistry RITUALS = new FlameRitualRegistry();
    private static final FlameAltarService ALTAR_SERVICE = new FlameAltarService(RITUALS);

    private FlameAltarRuntime() {
    }

    /** Stable provider seam used by Stage 06 to bind story rituals later. */
    public static void registerRitual(FlameRitual ritual) {
        RITUALS.register(Objects.requireNonNull(ritual, "ritual"));
    }

    public static FlameAltarService.ActivationResult activate(ServerPlayer player, IItemHandler inventory) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(inventory, "inventory");
        MinecraftServer server = Objects.requireNonNull(player.getServer(), "player server");
        return ALTAR_SERVICE.activate(
                player.getUUID(),
                inventory,
                FlameRitualExecutor.forServer(server, RITUALS)
        );
    }

    public static ProgressionSnapshot snapshot(ServerPlayer player) {
        Objects.requireNonNull(player, "player");
        MinecraftServer server = Objects.requireNonNull(player.getServer(), "player server");
        ProgressionOwner owner = Objects.requireNonNull(
                ProgressionRuntimeBindings.ownerResolver().resolve(player.getUUID()),
                "resolved owner"
        );
        FlameProgressionState.OwnerProgression progression =
                FlameProgressionSavedData.get(server).progression(owner);
        return new ProgressionSnapshot(
                progression.flameLevel(),
                progression.passageLevel(),
                progression.nextLevelReady()
        );
    }

    public record ProgressionSnapshot(int flameLevel, int passageLevel, boolean nextLevelReady) {
    }
}
