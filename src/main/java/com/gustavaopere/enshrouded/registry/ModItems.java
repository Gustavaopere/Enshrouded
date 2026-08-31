package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.story.reward.LichSkullItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Enshrouded.MOD_ID);

    public static final DeferredItem<BlockItem> FLAME_ALTAR = ITEMS.registerSimpleBlockItem(ModBlocks.FLAME_ALTAR);
    public static final DeferredItem<LichSkullItem> LICH_SKULL_MANIFESTATION_1 = ITEMS.registerItem(
            "lich_skull_manifestation_1",
            LichSkullItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    private ModItems() {
    }
}
