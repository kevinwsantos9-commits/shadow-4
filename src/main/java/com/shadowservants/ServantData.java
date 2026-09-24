package com.shadowservants;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ServantData {
    private static final String ROOT = "ShadowServants";
    private static final String DEFEATS = "Defeats";
    private static final String UNLOCKED = "Unlocked";
    private static final String ACTIVE = "Active";

    private ServantData() {}

    private static CompoundTag root(ServerPlayer player) {
        CompoundTag persistent = player.getPersistentData();
        if (!persistent.contains(ROOT)) persistent.put(ROOT, new CompoundTag());
        return persistent.getCompound(ROOT);
    }

    public static int defeats(ServerPlayer p) { return root(p).getInt(DEFEATS); }
    public static int unlocked(ServerPlayer p) { return root(p).getInt(UNLOCKED); }

    public static int registerDefeat(ServerPlayer p) {
        CompoundTag r = root(p);
        int defeats = r.getInt(DEFEATS) + 1;
        int unlocked = r.getInt(UNLOCKED);
        while (defeats >= 10) {
            defeats -= 10;
            unlocked++;
        }
        r.putInt(DEFEATS, defeats);
        r.putInt(UNLOCKED, unlocked);
        p.getPersistentData().put(ROOT, r);
        return unlocked;
    }

    public static List<UUID> active(ServerPlayer p) {
        CompoundTag r = root(p);
        List<UUID> list = new ArrayList<>();
        if (!r.contains(ACTIVE)) return list;
        for (String s : r.getStringList(ACTIVE)) {
            try { list.add(UUID.fromString(s)); } catch (IllegalArgumentException ignored) {}
        }
        return list;
    }

    public static boolean canSummon(ServerPlayer p) {
        return active(p).size() < unlocked(p);
    }

    public static void addActive(ServerPlayer p, UUID uuid) {
        CompoundTag r = root(p);
        List<String> ids = new ArrayList<>(r.getStringList(ACTIVE));
        String s = uuid.toString();
        if (!ids.contains(s)) ids.add(s);
        var list = new net.minecraft.nbt.ListTag();
        for (String id : ids) list.add(net.minecraft.nbt.StringTag.valueOf(id));
        r.put(ACTIVE, list);
        p.getPersistentData().put(ROOT, r);
    }

    public static void removeActive(ServerPlayer p, UUID uuid) {
        CompoundTag r = root(p);
        var list = new net.minecraft.nbt.ListTag();
        for (String id : r.getStringList(ACTIVE)) {
            if (!id.equals(uuid.toString())) list.add(net.minecraft.nbt.StringTag.valueOf(id));
        }
        r.put(ACTIVE, list);
        p.getPersistentData().put(ROOT, r);
    }

    public static void consumeOnDeath(ServerPlayer p, UUID uuid) {
        removeActive(p, uuid);
        CompoundTag r = root(p);
        int n = Math.max(0, r.getInt(UNLOCKED) - 1);
        r.putInt(UNLOCKED, n);
        p.getPersistentData().put(ROOT, r);
    }
}
