package com.gustavaopere.enshrouded.client.hud;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.client.state.ClientExposureState;
import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import java.util.Locale;

/** Client-only HUD projection of the synchronized Stage 03 exposure snapshot. */
public final class ShroudHudOverlay {
    private static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "exposure_hud");
    private static final ResourceLocation ICON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "textures/gui/shroud_hud_icons.png");

    private static final int PANEL_WIDTH = 136;
    private static final int PANEL_HEIGHT = 44;
    private static final int MARGIN = 8;
    private static final long NANOS_PER_TICK = 50_000_000L;

    private static long trackedSequence = Long.MIN_VALUE;
    private static long acceptedAtNanos;

    private ShroudHudOverlay() {
    }

    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LAYER_ID, ShroudHudOverlay::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        EnshroudedClientConfig.HudSettings settings = EnshroudedClientConfig.hudSettings();
        if (!settings.visible()) {
            return;
        }

        ClientExposureState state = ClientExposureState.INSTANCE;
        long sequence = state.lastSequence();
        long now = System.nanoTime();
        if (sequence != trackedSequence) {
            trackedSequence = sequence;
            acceptedAtNanos = now;
        }

        int elapsedTicks = sequence < 0L
                ? 0
                : (int) Math.min(Integer.MAX_VALUE, Math.max(0L, (now - acceptedAtNanos) / NANOS_PER_TICK));
        ExposureHudModel model = ExposureHudModel.fromSnapshot(state.snapshot(), elapsedTicks);
        if (!model.visible()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        double scale = settings.scale();
        int logicalWidth = (int) Math.floor(graphics.guiWidth() / scale);
        int logicalHeight = (int) Math.floor(graphics.guiHeight() / scale);
        int x = anchorX(settings.anchor(), logicalWidth);
        int y = anchorY(settings.anchor(), logicalHeight);

        graphics.pose().pushPose();
        graphics.pose().scale((float) scale, (float) scale, 1.0F);
        try {
            int background = model.zoneKind() == ExposureHudModel.ZoneKind.DEADLY
                    ? 0xD0601018
                    : 0xC0181724;
            int border = model.zoneKind() == ExposureHudModel.ZoneKind.DEADLY
                    ? 0xFFF0A060
                    : 0xFF9A91BD;

            graphics.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, background);
            graphics.hLine(x, x + PANEL_WIDTH - 1, y, border);
            graphics.hLine(x, x + PANEL_WIDTH - 1, y + PANEL_HEIGHT - 1, border);
            graphics.vLine(x, y, y + PANEL_HEIGHT - 1, border);
            graphics.vLine(x + PANEL_WIDTH - 1, y, y + PANEL_HEIGHT - 1, border);

            int textureU = model.zoneKind() == ExposureHudModel.ZoneKind.DEADLY ? 16 : 0;
            graphics.blit(ICON_TEXTURE, x + 6, y + 7, textureU, 0, 16, 16, 32, 16);

            graphics.drawString(minecraft.font,
                    Component.translatable(model.zoneTranslationKey()), x + 28, y + 6, 0xFFFFFFFF, true);
            graphics.drawString(minecraft.font,
                    Component.literal(model.countdownText()), x + 28, y + 17, 0xFFFFFFFF, true);

            String madnessKey = "hud.enshrouded.madness."
                    + model.madnessStage().name().toLowerCase(Locale.ROOT);
            graphics.drawString(minecraft.font,
                    Component.translatable("hud.enshrouded.madness", Component.translatable(madnessKey)),
                    x + 6, y + 30, 0xFFE2DFF0, true);

            if (model.passageWarning()) {
                graphics.drawString(minecraft.font,
                        Component.translatable(model.warningTranslationKey()),
                        x + 82, y + 17, 0xFFFFD27F, true);
            }
        } finally {
            graphics.pose().popPose();
        }
    }

    private static int anchorX(EnshroudedClientConfig.HudAnchor anchor, int logicalWidth) {
        return switch (anchor) {
            case TOP_LEFT, BOTTOM_LEFT -> MARGIN;
            case TOP_RIGHT, BOTTOM_RIGHT -> Math.max(MARGIN, logicalWidth - PANEL_WIDTH - MARGIN);
        };
    }

    private static int anchorY(EnshroudedClientConfig.HudAnchor anchor, int logicalHeight) {
        return switch (anchor) {
            case TOP_LEFT, TOP_RIGHT -> MARGIN;
            case BOTTOM_LEFT, BOTTOM_RIGHT -> Math.max(MARGIN, logicalHeight - PANEL_HEIGHT - MARGIN);
        };
    }
}
