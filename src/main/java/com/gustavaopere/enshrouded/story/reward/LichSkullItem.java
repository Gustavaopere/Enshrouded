package com.gustavaopere.enshrouded.story.reward;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

/**
 * Authentic Enshrouded trophy for the first Lich manifestation.
 *
 * <p>Authenticity is encoded in persistent item-stack component data. Display name and lore are
 * presentation only and are never trusted by progression logic.</p>
 */
public final class LichSkullItem extends Item {
    private static final String ROOT_KEY = "EnshroudedLichSkull";
    private static final String FORMAT_KEY = "Format";
    private static final String ENCOUNTER_KEY = "EncounterId";
    private static final String MANIFESTATION_KEY = "ManifestationIndex";
    private static final int FORMAT_VERSION = 1;
    public static final int LEVEL_ONE_MANIFESTATION = 1;

    public LichSkullItem(Properties properties) {
        super(properties);
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
        CompoundTag identity = new CompoundTag();
        identity.putInt(FORMAT_KEY, FORMAT_VERSION);
        identity.putUUID(ENCOUNTER_KEY, encounterId);
        identity.putInt(MANIFESTATION_KEY, manifestationIndex);
        CompoundTag customData = new CompoundTag();
        customData.put(ROOT_KEY, identity);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));
        return stack;
    }

    public static boolean isAuthenticLevelOne(ItemStack stack) {
        return stack != null
                && !stack.isEmpty()
                && stack.getCount() >= 1
                && stack.getItem() instanceof LichSkullItem
                && manifestationIndex(stack).orElse(-1) == LEVEL_ONE_MANIFESTATION
                && encounterId(stack).isPresent();
    }

    public static Optional<UUID> encounterId(ItemStack stack) {
        CompoundTag identity = identity(stack);
        if (identity == null
                || identity.getInt(FORMAT_KEY) != FORMAT_VERSION
                || !identity.contains(ENCOUNTER_KEY, Tag.TAG_INT_ARRAY)) {
            return Optional.empty();
        }
        try {
            return Optional.of(identity.getUUID(ENCOUNTER_KEY));
        } catch (RuntimeException malformed) {
            return Optional.empty();
        }
    }

    public static OptionalInt manifestationIndex(ItemStack stack) {
        CompoundTag identity = identity(stack);
        if (identity == null
                || identity.getInt(FORMAT_KEY) != FORMAT_VERSION
                || !identity.contains(MANIFESTATION_KEY, Tag.TAG_INT)) {
            return OptionalInt.empty();
        }
        int manifestationIndex = identity.getInt(MANIFESTATION_KEY);
        return manifestationIndex > 0 ? OptionalInt.of(manifestationIndex) : OptionalInt.empty();
    }

    private static CompoundTag identity(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof LichSkullItem)) {
            return null;
        }
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return null;
        }
        CompoundTag root = customData.copyTag();
        if (!root.contains(ROOT_KEY, Tag.TAG_COMPOUND)) {
            return null;
        }
        return root.getCompound(ROOT_KEY);
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
