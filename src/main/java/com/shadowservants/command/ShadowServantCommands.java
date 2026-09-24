package com.shadowservants.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.shadowservants.ServantData;
import com.shadowservants.ServantRules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;

public final class ShadowServantCommands {
    private ShadowServantCommands() {}

    private static final SuggestionProvider<CommandSourceStack> ENTITIES = (ctx, builder) -> {
        ForgeRegistries.ENTITY_TYPES.getKeys().stream()
                .map(ResourceLocation::toString)
                .filter(s -> s.startsWith(builder.getRemaining().toLowerCase()))
                .limit(80)
                .forEach(builder::suggest);
        return builder.buildFuture();
    };

    public static void register(com.mojang.brigadier.CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("shadowservants")
                .then(Commands.literal("status").executes(ctx -> status(ctx.getSource())))
                .then(Commands.literal("summon")
                        .then(Commands.argument("entity", StringArgumentType.word())
                                .suggests(ENTITIES)
                                .executes(ctx -> summon(ctx.getSource(), StringArgumentType.getString(ctx, "entity")))))
                .then(Commands.literal("dismiss")
                        .then(Commands.argument("entity", StringArgumentType.word())
                                .suggests(ENTITIES)
                                .executes(ctx -> dismiss(ctx.getSource(), StringArgumentType.getString(ctx, "entity")))))
                .then(Commands.literal("dismiss_all").executes(ctx -> dismissAll(ctx.getSource()))));
    }

    private static int status(CommandSourceStack source) {
        ServerPlayer p = source.getPlayerOrException();
        source.sendSuccess(() -> Component.literal("§5§lShadow Servants"), false);
        source.sendSuccess(() -> Component.literal("§7Progresso: §f" + ServantData.defeats(p) + "/10"), false);
        source.sendSuccess(() -> Component.literal("§7Servos disponíveis: §f" + ServantData.unlocked(p)), false);
        source.sendSuccess(() -> Component.literal("§7Servos ativos: §f" + ServantData.active(p).size()), false);
        return 1;
    }

    private static int summon(CommandSourceStack source, String raw) {
        ServerPlayer p = source.getPlayerOrException();
        if (!ServantData.canSummon(p)) {
            source.sendFailure(Component.literal("§cVocê não possui servos disponíveis."));
            return 0;
        }

        ResourceLocation id;
        try {
            id = ResourceLocation.parse(raw.contains(":") ? raw : "minecraft:" + raw);
        } catch (Exception e) {
            source.sendFailure(Component.literal("§cID de entidade inválido."));
            return 0;
        }

        var type = ForgeRegistries.ENTITY_TYPES.getValue(id);
        if (type == null) {
            source.sendFailure(Component.literal("§cEntidade não encontrada: " + id));
            return 0;
        }

        Entity entity = type.create(p.serverLevel());
        if (!(entity instanceof Mob mob) || !ServantRules.canBecomeServant(id, entity)) {
            source.sendFailure(Component.literal("§cEssa entidade não pode ser um servo."));
            return 0;
        }

        mob.moveTo(p.getX() + 2, p.getY(), p.getZ() + 2, p.getYRot(), 0);
        mob.getPersistentData().putBoolean("ShadowServant", true);
        mob.getPersistentData().putUUID("ShadowOwner", p.getUUID());
        mob.setPersistenceRequired();
        mob.setCustomName(Component.literal("Servo das Sombras"));
        mob.setCustomNameVisible(false);
        mob.setGlowingTag(true);

        if (!p.serverLevel().addFreshEntity(mob)) {
            source.sendFailure(Component.literal("§cNão foi possível invocar o servo."));
            return 0;
        }

        ServantData.addActive(p, mob.getUUID());
        source.sendSuccess(() -> Component.literal("§5Servo invocado: §f" + id), false);
        return 1;
    }

    private static int dismiss(CommandSourceStack source, String raw) {
        ServerPlayer p = source.getPlayerOrException();
        ResourceLocation id;
        try { id = ResourceLocation.parse(raw.contains(":") ? raw : "minecraft:" + raw); }
        catch (Exception e) { source.sendFailure(Component.literal("§cID inválido.")); return 0; }

        int removed = 0;
        for (var uuid : ServantData.active(p)) {
            Entity e = p.serverLevel().getEntity(uuid);
            if (e != null && ForgeRegistries.ENTITY_TYPES.getKey(e.getType()).equals(id)) {
                e.discard();
                ServantData.removeActive(p, uuid);
                removed++;
            }
        }
        source.sendSuccess(() -> Component.literal("§7Servos guardados: §f" + removed), false);
        return removed;
    }

    private static int dismissAll(CommandSourceStack source) {
        ServerPlayer p = source.getPlayerOrException();
        int removed = 0;
        for (var uuid : ServantData.active(p)) {
            Entity e = p.serverLevel().getEntity(uuid);
            if (e != null) e.discard();
            ServantData.removeActive(p, uuid);
            removed++;
        }
        final int count = removed;
        source.sendSuccess(() -> Component.literal("§7Todos os servos foram guardados: §f" + count), false);
        return removed;
    }
}
