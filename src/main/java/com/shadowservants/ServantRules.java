package com.shadowservants;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public final class ServantRules {
    private ServantRules() {}

    public static boolean canBecomeServant(ResourceLocation id, Entity entity) {
        if (!(entity instanceof Mob)) return false;
        if (entity instanceof Player) return false;
        String path = id.getPath();
        return !path.contains("projectile")
                && !path.contains("item")
                && !path.equals("armor_stand");
    }
}
