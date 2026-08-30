package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Enshrouded.MOD_ID);

    public static final DeferredItem<BlockItem> FLAME_ALTAR = ITEMS.registerSimpleBlockItem(ModBlocks.FLAME_ALTAR);

    private ModItems() {
    }
}
