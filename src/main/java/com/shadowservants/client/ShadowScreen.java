package com.shadowservants.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ShadowScreen extends Screen {
    public ShadowScreen() {
        super(Component.literal("Shadow Servants"));
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 75;
        addRenderableWidget(Button.builder(Component.literal("Status"), b -> command("status")).bounds(x, y, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Invocar Zombie"), b -> command("summon minecraft:zombie")).bounds(x, y + 25, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Invocar Esqueleto"), b -> command("summon minecraft:skeleton")).bounds(x, y + 50, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Invocar Aranha"), b -> command("summon minecraft:spider")).bounds(x, y + 75, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Guardar todos"), b -> command("dismiss_all")).bounds(x, y + 100, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Fechar"), b -> onClose()).bounds(x, y + 125, 200, 20).build());
    }

    private void command(String cmd) {
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.connection.sendCommand("shadowservants " + cmd);
            onClose();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xD0101010);
        graphics.fill(width / 2 - 115, height / 2 - 105, width / 2 + 115, height / 2 + 165, 0xE01B1B1B);
        graphics.drawCenteredString(font, title, width / 2, height / 2 - 95, 0xFFFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
