package com.gustavaopere.enshrouded.client.render.shroud;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/** Resource selection for the Stage 10 Shroud Core hero asset. */
public final class ShroudCoreGeoModel extends GeoModel<ShroudCoreBlockEntity> {
    private static final ResourceLocation ORDINARY_MODEL =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "geo/shroud_core.geo.json");
    private static final ResourceLocation DEADLY_MODEL =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "geo/shroud_core_deadly.geo.json");
    private static final ResourceLocation ORDINARY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "textures/block/shroud_core.png");
    private static final ResourceLocation DEADLY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "textures/block/shroud_core_deadly.png");
    private static final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "animations/shroud_core.animation.json");

    @Override
    public ResourceLocation getModelResource(ShroudCoreBlockEntity animatable) {
        return animatable.presentationProfile() == ShroudCoreBlockEntity.PresentationProfile.DEADLY
                ? DEADLY_MODEL
                : ORDINARY_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ShroudCoreBlockEntity animatable) {
        return animatable.presentationProfile() == ShroudCoreBlockEntity.PresentationProfile.DEADLY
                ? DEADLY_TEXTURE
                : ORDINARY_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ShroudCoreBlockEntity animatable) {
        return ANIMATION;
    }
}
