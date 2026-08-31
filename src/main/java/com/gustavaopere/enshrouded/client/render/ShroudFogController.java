package com.gustavaopere.enshrouded.client.render;

import com.gustavaopere.enshrouded.client.state.ClientExposureState;
import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import net.minecraft.world.level.material.FogType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

/** Physical-client-only Stage 07 fog projection over synchronized server-authored exposure state. */
public final class ShroudFogController {
    private static final ShroudRenderState STATE = new ShroudRenderState();
    private static final float MIN_FOG_SPAN = 0.25F;

    private ShroudFogController() {
    }

    public static void register(IEventBus gameBus) {
        gameBus.addListener(ShroudFogController::onRenderFramePre);
        gameBus.addListener(ShroudFogController::onRenderFog);
        gameBus.addListener(ShroudFogController::onComputeFogColor);
    }

    /** Clears presentation-only interpolation state at client connection boundaries. */
    public static void reset() {
        STATE.reset();
    }

    private static void onRenderFramePre(RenderFrameEvent.Pre event) {
        EnshroudedClientConfig.FogSettings settings = EnshroudedClientConfig.fogSettings();
        if (!settings.enabled()) {
            STATE.reset();
            return;
        }

        ClientExposureState exposureState = ClientExposureState.INSTANCE;
        if (exposureState.lastSequence() < 0L) {
            STATE.reset();
            return;
        }

        ExposureSnapshot snapshot = exposureState.snapshot();
        float deltaTicks = event.getPartialTick().getGameTimeDeltaTicks();
        STATE.advance(snapshot.severity(), snapshot.sanctuarySuppressed(), deltaTicks);
    }

    private static void onRenderFog(ViewportEvent.RenderFog event) {
        EnshroudedClientConfig.FogSettings settings = EnshroudedClientConfig.fogSettings();
        if (!settings.enabled() || settings.intensity() <= 0.0D || event.getType() != FogType.NONE || !STATE.active()) {
            return;
        }

        ShroudColorProfile profile = STATE.colorProfile(settings.intensity());
        float originalNear = event.getNearPlaneDistance();
        float originalFar = event.getFarPlaneDistance();
        float near = Math.max(0.0F, originalNear * profile.nearPlaneFactor());
        float far = Math.max(near + MIN_FOG_SPAN, originalFar * profile.farPlaneFactor());

        event.setNearPlaneDistance(near);
        event.setFarPlaneDistance(far);
        event.setCanceled(true);
    }

    private static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        EnshroudedClientConfig.FogSettings settings = EnshroudedClientConfig.fogSettings();
        if (!settings.enabled() || settings.intensity() <= 0.0D || !STATE.active()
                || event.getCamera().getFluidInCamera() != FogType.NONE) {
            return;
        }

        ShroudColorProfile profile = STATE.colorProfile(settings.intensity());
        event.setRed(clamp01(event.getRed() * profile.red()));
        event.setGreen(clamp01(event.getGreen() * profile.green()));
        event.setBlue(clamp01(event.getBlue() * profile.blue()));
    }

    private static float clamp01(float value) {
        if (!Float.isFinite(value)) {
            return 0.0F;
        }
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
