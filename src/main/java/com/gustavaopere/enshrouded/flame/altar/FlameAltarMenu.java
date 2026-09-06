package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.registry.ModMenus;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.Objects;

/** Menu synchronizing the altar slot and authoritative owner progression summary. */
public final class FlameAltarMenu extends AbstractContainerMenu {
    public static final int ACTIVATE_BUTTON_ID = 0;
    private static final int DATA_FLAME_LEVEL = 0;
    private static final int DATA_PASSAGE_LEVEL = 1;
    private static final int DATA_NEXT_LEVEL_READY = 2;
    private static final int DATA_COUNT = 3;
    private static final int ALTAR_SLOT_COUNT = 1;

    private final ItemStackHandler altarInventory;
    private final ContainerData data;
    private final FlameAltarBlockEntity altar;

    /** Client constructor used by the registered MenuType. */
    public FlameAltarMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(ALTAR_SLOT_COUNT), new SimpleContainerData(DATA_COUNT), null);
    }

    /** Server constructor created by the FlameAltarBlockEntity MenuProvider. */
    public FlameAltarMenu(int containerId, Inventory playerInventory, FlameAltarBlockEntity altar) {
        this(
                containerId,
                playerInventory,
                Objects.requireNonNull(altar, "altar").inventory(),
                initialData(playerInventory.player),
                altar
        );
    }

    private FlameAltarMenu(
            int containerId,
            Inventory playerInventory,
            ItemStackHandler altarInventory,
            ContainerData data,
            FlameAltarBlockEntity altar) {
        super(ModMenus.FLAME_ALTAR.get(), containerId);
        this.altarInventory = Objects.requireNonNull(altarInventory, "altarInventory");
        this.data = Objects.requireNonNull(data, "data");
        this.altar = altar;

        checkContainerDataCount(data, DATA_COUNT);
        addDataSlots(data);
        addSlot(new SlotItemHandler(altarInventory, FlameAltarService.OFFERING_SLOT, 80, 35));
        addPlayerInventory(playerInventory);
    }

    public int flameLevel() {
        return data.get(DATA_FLAME_LEVEL);
    }

    public int passageLevel() {
        return data.get(DATA_PASSAGE_LEVEL);
    }

    public boolean nextLevelReady() {
        return data.get(DATA_NEXT_LEVEL_READY) != 0;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id != ACTIVATE_BUTTON_ID || !(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        int flameLevelBefore = flameLevel();
        FlameAltarService.ActivationResult result = FlameAltarRuntime.activate(serverPlayer, altarInventory);
        refreshData(serverPlayer);
        if (altar != null && result.status() == FlameAltarService.Status.APPLIED) {
            altar.triggerAuthoritativePresentation(flameLevel() > flameLevelBefore);
        }
        serverPlayer.displayClientMessage(Component.translatable(messageKey(result.status())), false);
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        if (altar == null || altar.getLevel() == null) {
            return true;
        }
        return stillValid(
                ContainerLevelAccess.create(altar.getLevel(), altar.getBlockPos()),
                player,
                ModBlocks.FLAME_ALTAR.get()
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot source = slots.get(index);
        if (!source.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = source.getItem();
        ItemStack copy = sourceStack.copy();
        if (index < ALTAR_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, ALTAR_SLOT_COUNT, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(sourceStack, 0, ALTAR_SLOT_COUNT, false)) {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            source.set(ItemStack.EMPTY);
        } else {
            source.setChanged();
        }
        return copy;
    }

    private void refreshData(ServerPlayer player) {
        FlameAltarRuntime.ProgressionSnapshot snapshot = FlameAltarRuntime.snapshot(player);
        data.set(DATA_FLAME_LEVEL, snapshot.flameLevel());
        data.set(DATA_PASSAGE_LEVEL, snapshot.passageLevel());
        data.set(DATA_NEXT_LEVEL_READY, snapshot.nextLevelReady() ? 1 : 0);
    }

    private static ContainerData initialData(Player player) {
        SimpleContainerData data = new SimpleContainerData(DATA_COUNT);
        if (player instanceof ServerPlayer serverPlayer) {
            FlameAltarRuntime.ProgressionSnapshot snapshot = FlameAltarRuntime.snapshot(serverPlayer);
            data.set(DATA_FLAME_LEVEL, snapshot.flameLevel());
            data.set(DATA_PASSAGE_LEVEL, snapshot.passageLevel());
            data.set(DATA_NEXT_LEVEL_READY, snapshot.nextLevelReady() ? 1 : 0);
        }
        return data;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    private static String messageKey(FlameAltarService.Status status) {
        return switch (status) {
            case APPLIED -> "message.enshrouded.flame_altar.applied";
            case ALREADY_COMPLETED -> "message.enshrouded.flame_altar.already_completed";
            case NO_MATCHING_RITUAL -> "message.enshrouded.flame_altar.no_matching_ritual";
        };
    }
}
