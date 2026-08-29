package com.gustavaopere.enshrouded.protection;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/** Block-tag boundaries consumed by the centralized terrain mutation authority. */
public final class TerrainSafetyTags {
    public static final TagKey<Block> CORRUPTIBLE_SAFE = blockTag("corruptible_safe");
    public static final TagKey<Block> CORRUPTIBLE_AGGRESSIVE = blockTag("corruptible_aggressive");

    private TerrainSafetyTags() {
    }

    private static TagKey<Block> blockTag(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, path));
    }
}
