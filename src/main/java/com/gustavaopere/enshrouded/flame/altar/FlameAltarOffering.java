package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.flame.ritual.FlameRitual;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Objects;

/**
 * Server-owned snapshot of one Flame Altar inventory slot.
 *
 * <p>The snapshot is used for ritual validation. Consumption re-checks the live handler before
 * extracting anything, so an inventory change between validation and commit fails closed rather
 * than consuming a different item.</p>
 */
public final class FlameAltarOffering implements FlameRitual.Offering {
    private final IItemHandler inventory;
    private final int slot;
    private final ItemStack snapshot;

    private FlameAltarOffering(IItemHandler inventory, int slot, ItemStack snapshot) {
        this.inventory = Objects.requireNonNull(inventory, "inventory");
        this.slot = slot;
        this.snapshot = Objects.requireNonNull(snapshot, "snapshot").copy();
    }

    public static FlameAltarOffering capture(IItemHandler inventory, int slot) {
        Objects.requireNonNull(inventory, "inventory");
        if (slot < 0 || slot >= inventory.getSlots()) {
            throw new IllegalArgumentException("altar offering slot out of bounds: " + slot);
        }
        return new FlameAltarOffering(inventory, slot, inventory.getStackInSlot(slot));
    }

    /** Returns a defensive copy of the server-observed offering. */
    public ItemStack stack() {
        return snapshot.copy();
    }

    /**
     * Consumes exactly one item only if the live slot still represents the captured offering.
     * Returns false without mutation when the slot changed or became empty.
     */
    public boolean consumeOne() {
        if (snapshot.isEmpty()) {
            return false;
        }

        ItemStack live = inventory.getStackInSlot(slot);
        if (live.isEmpty() || !ItemStack.isSameItemSameComponents(snapshot, live)) {
            return false;
        }

        ItemStack simulated = inventory.extractItem(slot, 1, true);
        if (simulated.getCount() != 1 || !ItemStack.isSameItemSameComponents(snapshot, simulated)) {
            return false;
        }

        ItemStack extracted = inventory.extractItem(slot, 1, false);
        return extracted.getCount() == 1 && ItemStack.isSameItemSameComponents(snapshot, extracted);
    }
}
