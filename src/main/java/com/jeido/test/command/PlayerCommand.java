package com.jeido.test.command;

import com.jeido.test.entity.Player;
import com.jeido.test.repository.PlayerRepository;
import com.jeido.test.util.CommandUtils;
import com.jeido.test.util.CrudActions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.server.command.EnumArgument;

import java.util.Objects;
import java.util.UUID;

public class PlayerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> c = Commands.literal("player")
                .requires((CommandSourceStack e) -> e.hasPermission(4) )
                .then(Commands.argument("action", EnumArgument.enumArgument(CrudActions.class))
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("id", StringArgumentType.string()))));
        dispatcher.register(c);
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) {
        if (!checkPlayer(ctx)) {
            return 0;
        }

        CommandSourceStack source = ctx.getSource();

        switch (ctx.getArgument("action", CrudActions.class)) {
            case CREATE -> createPlayer(ctx);
            case READ_ALL -> CommandUtils.nyi(ctx);
            case READ_BY_ID -> CommandUtils.nyi(ctx);
            case UPDATE -> CommandUtils.nyi(ctx);
            case DELETE -> CommandUtils.nyi(ctx);
            default -> source.sendFailure(Component.translatable("command.test.player.invalid_action"));
        }
        return 0;
    }

    private static int createPlayer(CommandContext<CommandSourceStack> ctx) {
        if (!checkPlayer(ctx)) {
            return 0;
        }
        CommandSourceStack source = ctx.getSource();


        UUID uuid = Objects.requireNonNull(source.getPlayer()).getUUID();
        String name = source.getPlayer().getName().getString();

        PlayerRepository playerRepository = PlayerRepository.getInstance();
        playerRepository.save(new Player(uuid, name));
        source.sendSuccess(() -> Component.translatable("command.test.player.success.created", name, uuid.toString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static boolean checkPlayer(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();

        if (!source.hasPermission(4)) {
            source.sendFailure(Component.translatable("command.test.player.no_permission"));
            return false;
        }
        if (!source.isPlayer()) {
            source.sendFailure(Component.translatable("command.test.player.not_player"));
            return false;
        }
        return true;
    }

}
