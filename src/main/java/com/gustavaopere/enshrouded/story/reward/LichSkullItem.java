package com.gustavaopere.enshrouded.story.reward;

import com.gustavaopere.enshrouded.client.render.story.LichSkullRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Authentic, non-placeable Enshrouded trophy for the first Lich manifestation.
 *
 * <p>Encounter identity remains persistent component data and is the only authority used by
 * progression/reward logic. GeckoLib is presentation-only and may animate the trophy without
 * creating or mutating encounter state.</p>
 */
public final class LichSkullItem extends Item implements GeoItem {
    private static final String ROOT_KEY = "EnshroudedLichSkull";
    private static final RawAnimation IDLE_ANIMATION =
            RawAnimation.begin().thenLoop("animation.lich_skull.idle");
    public static final int LEVEL_ONE_MANIFESTATION = 1;

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public LichSkullItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private LichSkullRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new LichSkullRenderer();
                }
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "lich_skull", 8,
                state -> state.setAndContinue(IDLE_ANIMATION)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.FAIL;
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
