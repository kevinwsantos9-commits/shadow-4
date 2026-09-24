package com.shadowservants;

import com.shadowservants.client.ShadowClient;
import com.shadowservants.command.ShadowServantCommands;
import com.shadowservants.gameplay.ShadowServantEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ShadowServants.MOD_ID)
public final class ShadowServants {
    public static final String MOD_ID = "shadowservants";

    public ShadowServants(FMLJavaModLoadingContext context) {
        // ShadowServantEvents já é registrada automaticamente via @Mod.EventBusSubscriber,
        // então registrá-la aqui de novo fazia cada evento (morte, tick) disparar 2x.
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
                ShadowServantCommands.register(event.getDispatcher()));
    }
}
