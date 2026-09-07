package com.gustavaopere.enshrouded.client.render.story;

import com.gustavaopere.enshrouded.story.reward.LichSkullItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/** Client-only renderer for the animated Lich Skull trophy. */
public final class LichSkullRenderer extends GeoItemRenderer<LichSkullItem> {
    public LichSkullRenderer() {
        super(new LichSkullGeoModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
