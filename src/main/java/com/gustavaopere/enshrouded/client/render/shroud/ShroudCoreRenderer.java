package com.gustavaopere.enshrouded.client.render.shroud;

import com.gustavaopere.enshrouded.shroud.core.ShroudCoreBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/** Client-only renderer for the Ordinary/Deadly Shroud Core hero asset. */
public final class ShroudCoreRenderer extends GeoBlockRenderer<ShroudCoreBlockEntity> {
    public ShroudCoreRenderer(BlockEntityRendererProvider.Context context) {
        super(new ShroudCoreGeoModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
