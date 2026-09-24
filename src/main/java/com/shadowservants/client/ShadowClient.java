package com.shadowservants.client;

import com.shadowservants.ShadowServants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ShadowServants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ShadowClient {
    private static final KeyMapping OPEN_MENU = new KeyMapping(
            "key.shadowservants.menu",
            com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.shadowservants"
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MENU);
    }

    @Mod.EventBusSubscriber(modid = ShadowServants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.GAME)
    public static class Tick {
        @SubscribeEvent
        public static void clientTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            if (OPEN_MENU.consumeClick() && mc.screen == null) {
                mc.setScreen(new ShadowScreen());
            }
        }
    }
}
