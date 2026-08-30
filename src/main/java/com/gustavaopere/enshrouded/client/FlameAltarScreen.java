package com.gustavaopere.enshrouded.client;

import com.gustavaopere.enshrouded.flame.altar.FlameAltarMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Lightweight client view; all ritual authority remains on the server menu/runtime. */
public final class FlameAltarScreen extends AbstractContainerScreen<FlameAltarMenu> {
    public FlameAltarScreen(FlameAltarMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(
                Component.translatable("screen.enshrouded.flame_altar.activate"),
                button -> {
                    if (minecraft != null && minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, FlameAltarMenu.ACTIVATE_BUTTON_ID);
                    }
                }
        ).bounds(leftPos + 58, topPos + 56, 60, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF17100C);
        graphics.fill(leftPos + 6, topPos + 18, leftPos + imageWidth - 6, topPos + 78, 0xFF2A1B14);
        graphics.fill(leftPos + 78, topPos + 33, leftPos + 96, topPos + 51, 0xFF4A2A18);
        graphics.fill(leftPos + 7, topPos + 82, leftPos + imageWidth - 7, topPos + imageHeight - 7, 0xFF211A17);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0xFFE7C9A4, false);
        graphics.drawString(
                font,
                Component.translatable("screen.enshrouded.flame_altar.flame_level", menu.flameLevel()),
                8,
                22,
                0xFFFFB45C,
                false
        );
        graphics.drawString(
                font,
                Component.translatable("screen.enshrouded.flame_altar.passage_level", menu.passageLevel()),
                8,
                34,
                0xFFE7C9A4,
                false
        );
        graphics.drawString(
                font,
                Component.translatable(menu.nextLevelReady()
                        ? "screen.enshrouded.flame_altar.ready"
                        : "screen.enshrouded.flame_altar.not_ready"),
                8,
                46,
                menu.nextLevelReady() ? 0xFF8FE388 : 0xFFC9A58A,
                false
        );
        graphics.drawString(font, playerInventoryTitle, 8, 72, 0xFFD4C2B3, false);
    }
}
