package com.gustavaopere.enshrouded.story.reward;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

/**
 * Authentic Enshrouded trophy for the first Lich manifestation.
 *
 * <p>Authenticity is encoded in persistent item-stack component data. Display name and lore are
 * presentation only and are never trusted by progression logic. The vanilla Wither Skeleton skull
 * blocks are used only to select Minecraft's skull item renderer; this trophy deliberately cannot
 * place or remap those vanilla blocks.</p>
 */
public final class LichSkullItem extends StandingAndWallBlockItem {
    private static final String ROOT_KEY = "EnshroudedLichSkull";
    public static final int LEVEL_ONE_MANIFESTATION = 1;

    public LichSkullItem(Properties properties) {
        super(
                Blocks.WITHER_SKELETON_SKULL,
                Blocks.WITHER_SKELETON_WALL_SKULL,
                properties,
                Direction.DOWN
        );
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.FAIL;
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
        // Render through the vanilla skull block type without replacing the vanilla block->item mapping.
    }

    /** Creates exactly one trophy stack with durable encounter/manifestation identity. */
    public static ItemStack createAuthentic(Item item, UUID encounterId, int manifestationIndex) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(encounterId, "encounterId");
        if (!(item instanceof LichSkullItem)) {
            throw new IllegalArgumentException("authentic Lich skull requires LichSkullItem backing item");
        }
        if (manifestationIndex != LEVEL_ONE_MANIFESTATION) {
            throw new IllegalArgumentException("Level-1 Lich skull requires manifestation index 1");
        }

        ItemStack stack = new ItemStack(item, 1);
        CompoundTag customData = new CompoundTag();
        customData.put(ROOT_KEY, new LichSkullIdentity(encounterId, manifestationIndex).encode());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));
        return stack;
    }

    public static boolean isAuthenticLevelOne(ItemStack stack) {
        return identity(stack)
                .filter(identity -> identity.manifestationIndex() == LEVEL_ONE_MANIFESTATION)
                .isPresent();
    }

    public static Optional<UUID> encounterId(ItemStack stack) {
        return identity(stack).map(LichSkullIdentity::encounterId);
    }

    public static OptionalInt manifestationIndex(ItemStack stack) {
        Optional<LichSkullIdentity> identity = identity(stack);
        return identity.isPresent()
                ? OptionalInt.of(identity.orElseThrow().manifestationIndex())
                : OptionalInt.empty();
    }

    public static Optional<LichSkullIdentity> identity(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof LichSkullItem)) {
            return Optional.empty();
        }
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return Optional.empty();
        }
        CompoundTag root = customData.copyTag();
        if (!root.contains(ROOT_KEY, Tag.TAG_COMPOUND)) {
            return Optional.empty();
        }
        return LichSkullIdentity.decode(root.getCompound(ROOT_KEY));
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.enshrouded.lich_skull_manifestation_1.tooltip"));
        if (isAuthenticLevelOne(stack)) {
            tooltipComponents.add(Component.translatable("item.enshrouded.lich_skull_manifestation_1.authentic"));
        }
    }
}
