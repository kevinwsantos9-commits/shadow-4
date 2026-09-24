package com.shadowservants.gameplay;

import com.shadowservants.ServantData;
import com.shadowservants.ServantRules;
import com.shadowservants.ShadowServants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ShadowServants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ShadowServantEvents {
    private static int tickCounter;

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity dead = event.getEntity();

        if (dead.getPersistentData().getBoolean("ShadowServant")) {
            UUID ownerId = dead.getPersistentData().getUUID("ShadowOwner");
            if (dead.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                ServerPlayer owner = serverLevel.getServer().getPlayerList().getPlayer(ownerId);
                if (owner != null) {
                    ServantData.consumeOnDeath(owner, dead.getUUID());
                    owner.sendSystemMessage(Component.literal("§8Seu servo das sombras foi destruído. §7Um vínculo foi perdido."));
                }
            }
            return;
        }

        if (!(dead instanceof Mob)) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(dead.getType());
        if (id != null && ServantRules.canBecomeServant(id, dead)) {
            int unlocked = ServantData.registerDefeat(player);
            if (unlocked > 0 && ServantData.defeats(player) == 0) {
                player.sendSystemMessage(Component.literal("§5§lNOVO SERVO DESBLOQUEADO! §7Você derrotou 10 criaturas."));
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent.Post event) {
        if (++tickCounter < 10) return;
        tickCounter = 0;

        for (ServerPlayer owner : event.getServer().getPlayerList().getPlayers()) {
            for (UUID id : ServantData.active(owner)) {
                Entity entity = owner.serverLevel().getEntity(id);
                if (!(entity instanceof Mob mob) || !mob.isAlive()) {
                    continue;
                }

                mob.getPersistentData().putUUID("ShadowOwner", owner.getUUID());
                mob.setCustomName(Component.literal("Servo das Sombras"));
                mob.setCustomNameVisible(false);
                mob.setGlowingTag(true);

                double distance = mob.distanceTo(owner);
                if (distance > 8.0D) {
                    mob.getNavigation().moveTo(owner, 1.15D);
                }

                LivingEntity target = owner.getLastHurtMob();
                if (target != null && target.isAlive() && target != owner && target != mob) {
                    mob.setTarget(target);
                }
            }
        }
    }
}
