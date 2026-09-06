package com.gustavaopere.enshrouded.client.render.flame;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/** Resource contract for the Stage 10 Flame Altar hero asset. */
public final class FlameAltarGeoModel extends GeoModel<FlameAltarBlockEntity> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "geo/flame_altar.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "textures/block/flame_altar.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "animations/flame_altar.animation.json");

    @Override
    public ResourceLocation getModelResource(FlameAltarBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FlameAltarBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(FlameAltarBlockEntity animatable) {
        return ANIMATION;
    }
}
