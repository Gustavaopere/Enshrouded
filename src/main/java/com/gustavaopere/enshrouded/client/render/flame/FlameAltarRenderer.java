package com.gustavaopere.enshrouded.client.render.flame;

import com.gustavaopere.enshrouded.flame.altar.FlameAltarBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/** Client-only renderer for the animated Flame Altar hero asset. */
public final class FlameAltarRenderer extends GeoBlockRenderer<FlameAltarBlockEntity> {
    public FlameAltarRenderer(BlockEntityRendererProvider.Context context) {
        super(new FlameAltarGeoModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
