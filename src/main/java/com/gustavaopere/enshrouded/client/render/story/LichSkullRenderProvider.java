package com.gustavaopere.enshrouded.client.render.story;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

/** Physical-client-only provider injected into the common GeoItem during renderer registration. */
public final class LichSkullRenderProvider implements GeoRenderProvider {
    private LichSkullRenderer renderer;

    @Override
    public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
        if (renderer == null) {
            renderer = new LichSkullRenderer();
        }
        return renderer;
    }
}
