package com.gustavaopere.enshrouded.client.hud;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.client.accessibility.AccessibilityProfile;
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
    private static final ResourceLocation HUD_FRAME_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "textures/gui/shroud_hud_frame.png");
    private static final ResourceLocation ICON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "textures/gui/shroud_hud_icons.png");

    private static final int PANEL_WIDTH = 160;
    private static final int FULL_PANEL_HEIGHT = 60;
    private static final int MINIMAL_PANEL_HEIGHT = 50;
    private static final int MARGIN = 8;
    private static final int SYMBOL_SIZE = 16;
    private static final int ICON_ORDINARY_U = 0;
    private static final int ICON_DEADLY_U = 16;
    private static final int ICON_PASSAGE_U = 32;
    private static final int ICON_MADNESS_U = 48;

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
        // Exposure snapshots are produced from server-tick cadence and the HUD renders whole seconds.
        // Do not invent additional drain from client wall-clock time: pause, low server TPS and stalls
        // must leave the last authoritative reserve unchanged until a newer server snapshot arrives.
        ExposureHudModel model = ExposureHudModel.fromSnapshot(state.snapshot(), 0);
        if (!model.visible()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        double scale = settings.scale();
        int logicalWidth = (int) Math.floor(graphics.guiWidth() / scale);
        int logicalHeight = (int) Math.floor(graphics.guiHeight() / scale);
        boolean minimal = EnshroudedClientConfig.accessibilityProfile() == AccessibilityProfile.MINIMAL;
        int panelHeight = minimal ? MINIMAL_PANEL_HEIGHT : FULL_PANEL_HEIGHT;
        int x = anchorX(settings.anchor(), logicalWidth);
        int y = anchorY(settings.anchor(), logicalHeight, panelHeight);

        graphics.pose().pushPose();
        graphics.pose().scale((float) scale, (float) scale, 1.0F);
        try {
            if (minimal) {
                renderMinimalHud(graphics, minecraft, model, x, y);
            } else {
                renderFullHud(graphics, minecraft, model, x, y);
            }
        } finally {
            graphics.pose().popPose();
        }
    }

    private static void renderFullHud(
            GuiGraphics graphics,
            Minecraft minecraft,
            ExposureHudModel model,
            int x,
            int y) {
        int frameU = model.zoneKind() == ExposureHudModel.ZoneKind.DEADLY ? PANEL_WIDTH : 0;
        graphics.blit(
                HUD_FRAME_TEXTURE,
                x,
                y,
                frameU,
                0,
                PANEL_WIDTH,
                FULL_PANEL_HEIGHT,
                PANEL_WIDTH * 2,
                64
        );

        int zoneIconU = model.zoneKind() == ExposureHudModel.ZoneKind.DEADLY
                ? ICON_DEADLY_U
                : ICON_ORDINARY_U;
        blitSymbol(graphics, x + 6, y + 7, zoneIconU);

        graphics.drawString(
                minecraft.font,
                Component.translatable(model.zoneTranslationKey()),
                x + 28,
                y + 6,
                0xFFFFFFFF,
                true
        );
        graphics.drawString(
                minecraft.font,
                Component.literal(model.countdownText()),
                x + 28,
                y + 17,
                0xFFFFFFFF,
                true
        );

        blitSymbol(graphics, x + 6, y + 28, ICON_MADNESS_U);
        drawMadnessLabel(graphics, minecraft, model, x + 25, y + 30);
        renderMadnessBar(graphics, model, x + 93, y + 31, 58, 7, false);

        if (model.passageWarning()) {
            blitSymbol(graphics, x + 6, y + 42, ICON_PASSAGE_U);
            graphics.drawWordWrap(
                    minecraft.font,
                    Component.translatable(model.warningTranslationKey()),
                    x + 26,
                    y + 45,
                    PANEL_WIDTH - 32,
                    0xFFFFD27F
            );
        }
    }

    private static void renderMinimalHud(
            GuiGraphics graphics,
            Minecraft minecraft,
            ExposureHudModel model,
            int x,
            int y) {
        int background = 0xD0101014;
        int border = model.zoneKind() == ExposureHudModel.ZoneKind.DEADLY
                ? 0xFFE49A70
                : 0xFFD2CCE8;
        graphics.fill(x, y, x + PANEL_WIDTH, y + MINIMAL_PANEL_HEIGHT, background);
        graphics.hLine(x, x + PANEL_WIDTH - 1, y, border);
        graphics.hLine(x, x + PANEL_WIDTH - 1, y + MINIMAL_PANEL_HEIGHT - 1, border);
        graphics.vLine(x, y, y + MINIMAL_PANEL_HEIGHT - 1, border);
        graphics.vLine(x + PANEL_WIDTH - 1, y, y + MINIMAL_PANEL_HEIGHT - 1, border);

        // The zone symbol remains shape-distinct in MINIMAL, so Deadly is never color-only.
        int zoneIconU = model.zoneKind() == ExposureHudModel.ZoneKind.DEADLY
                ? ICON_DEADLY_U
                : ICON_ORDINARY_U;
        blitSymbol(graphics, x + 5, y + 5, zoneIconU);
        graphics.drawString(
                minecraft.font,
                Component.translatable(model.zoneTranslationKey()),
                x + 25,
                y + 4,
                0xFFFFFFFF,
                true
        );
        graphics.drawString(
                minecraft.font,
                Component.literal(model.countdownText()),
                x + 25,
                y + 15,
                0xFFFFFFFF,
                true
        );

        drawMadnessLabel(graphics, minecraft, model, x + 5, y + 28);
        renderMadnessBar(graphics, model, x + 93, y + 29, 58, 6, true);

        if (model.passageWarning()) {
            graphics.drawWordWrap(
                    minecraft.font,
                    Component.translatable(model.warningTranslationKey()),
                    x + 6,
                    y + 40,
                    PANEL_WIDTH - 12,
                    0xFFFFD27F
            );
        }
    }

    private static void drawMadnessLabel(
            GuiGraphics graphics,
            Minecraft minecraft,
            ExposureHudModel model,
            int x,
            int y) {
        String madnessKey = "hud.enshrouded.madness."
                + model.madnessStage().name().toLowerCase(Locale.ROOT);
        graphics.drawString(
                minecraft.font,
                Component.translatable("hud.enshrouded.madness", Component.translatable(madnessKey)),
                x,
                y,
                0xFFE2DFF0,
                true
        );
    }

    private static void renderMadnessBar(
            GuiGraphics graphics,
            ExposureHudModel model,
            int x,
            int y,
            int width,
            int height,
            boolean minimal) {
        int activeSegments = model.madnessSegments();
        int gap = 1;
        int segmentWidth = Math.max(1, (width - 4 * gap) / 5);
        int inactive = minimal ? 0xFF3A3942 : 0xFF413B50;
        int active = model.madnessSegments() >= 4 ? 0xFFE19A72 : 0xFFC7B6E2;

        for (int segment = 0; segment < 5; segment++) {
            int left = x + segment * (segmentWidth + gap);
            int right = segment == 4 ? x + width : left + segmentWidth;
            graphics.fill(left, y, right, y + height, segment < activeSegments ? active : inactive);
        }
    }

    private static void blitSymbol(GuiGraphics graphics, int x, int y, int textureU) {
        graphics.blit(ICON_TEXTURE, x, y, textureU, 0, SYMBOL_SIZE, SYMBOL_SIZE, 64, 16);
    }

    private static int anchorX(EnshroudedClientConfig.HudAnchor anchor, int logicalWidth) {
        return switch (anchor) {
            case TOP_LEFT, BOTTOM_LEFT -> MARGIN;
            case TOP_RIGHT, BOTTOM_RIGHT -> Math.max(MARGIN, logicalWidth - PANEL_WIDTH - MARGIN);
        };
    }

    private static int anchorY(
            EnshroudedClientConfig.HudAnchor anchor,
            int logicalHeight,
            int panelHeight) {
        return switch (anchor) {
            case TOP_LEFT, TOP_RIGHT -> MARGIN;
            case BOTTOM_LEFT, BOTTOM_RIGHT -> Math.max(MARGIN, logicalHeight - panelHeight - MARGIN);
        };
    }
}
