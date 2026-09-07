package com.gustavaopere.enshrouded.client.render.story;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.story.reward.LichSkullItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/** Resource contract for the Stage 10.04 Lich Skull trophy. */
public final class LichSkullGeoModel extends GeoModel<LichSkullItem> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            Enshrouded.MOD_ID, "geo/lich_skull_manifestation_1.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            Enshrouded.MOD_ID, "textures/item/lich_skull_manifestation_1.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            Enshrouded.MOD_ID, "animations/lich_skull_manifestation_1.animation.json");

    @Override
    public ResourceLocation getModelResource(LichSkullItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(LichSkullItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(LichSkullItem animatable) {
        return ANIMATION;
    }
}
