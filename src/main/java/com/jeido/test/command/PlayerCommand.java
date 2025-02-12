package com.jeido.test.command;

import com.jeido.test.entity.Player;
import com.jeido.test.repository.PlayerRepository;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> c = Commands.literal("player")
                .requires((CommandSourceStack e) -> e.hasPermission(4))

                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("id", StringArgumentType.string())
                                        .executes(PlayerCommand::createPlayer))
                                .executes(PlayerCommand::createPlayer))
                        .executes(PlayerCommand::createPlayer)
                )

                .then(Commands.literal("read")
                        .then(Commands.literal("all")
                                .executes(PlayerCommand::readAllPlayer))
                        .then(Commands.literal("id")
                                .then(Commands.argument("id", StringArgumentType.string())
                                        .executes(PlayerCommand::readPlayer)
                                )
                        )
                )

                .then(Commands.literal("update")
                        .then(Commands.argument("id", StringArgumentType.string())
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(PlayerCommand::updatePlayer)
                                )
                        )
                )
                .then(Commands.literal("delete")
                        .then(Commands.argument("id", StringArgumentType.string())
                                .executes(PlayerCommand::deletePlayer)
                        )
                );

        dispatcher.register(c);
    }

    private static int deletePlayer(CommandContext<CommandSourceStack> ctx) {
        if (invalidPlayer(ctx)) return 0;
        CommandSourceStack source = ctx.getSource();

        UUID id = UUID.fromString(Objects.requireNonNull(StringArgumentType.getString(ctx, "id")));

        PlayerRepository repo = PlayerRepository.getInstance();

        if (!repo.exists(id)) {
            source.sendFailure(Component.translatable("command.player.not_found", id.toString()));
            return 0;
        }

        repo.delete(id);
        source.sendSuccess(() -> Component.translatable("command.player.deleted", id.toString()), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int updatePlayer(CommandContext<CommandSourceStack> ctx) {
        if (invalidPlayer(ctx)) return 0;

        CommandSourceStack source = ctx.getSource();

        UUID id = UUID.fromString(StringArgumentType.getString(ctx, "id"));
        String name = StringArgumentType.getString(ctx, "name");

        PlayerRepository playerRepository = PlayerRepository.getInstance();

        if (!playerRepository.exists(id)) {
            source.sendFailure(Component.translatable("command.test.player.not_found", id.toString()));
            return 0;
        }


        Player player = playerRepository.update(id, new Player(id, name));

        source.sendSuccess(() -> Component.translatable("command.test.player.updated", player.toString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int createPlayer(CommandContext<CommandSourceStack> ctx) {
        if (invalidPlayer(ctx)) {
            return 0;
        }
        CommandSourceStack source = ctx.getSource();
        String name;
        String id;
        try {
            name = StringArgumentType.getString(ctx, "name");
        } catch (Exception e) {
            name = Objects.requireNonNull(source.getPlayer()).getName().getString();
        }

        UUID uuid;

        try {
            id = StringArgumentType.getString(ctx, "id");
            uuid = UUID.fromString(id);
        } catch (Exception e) {
            uuid = Objects.requireNonNull(source.getPlayer()).getUUID();
        }

        PlayerRepository playerRepository = PlayerRepository.getInstance();
        String finalName = name;
        UUID finalUuid = uuid;
        if (playerRepository.exists(uuid)) {
            source.sendFailure(Component.translatable("command.test.player.already_exist", finalUuid.toString()));
            return 0;
        }
        playerRepository.save(new Player(uuid, name));
        source.sendSuccess(() -> Component.translatable("command.test.player.success.created", finalName, finalUuid.toString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int readAllPlayer(CommandContext<CommandSourceStack> ctx) {
        if (invalidPlayer(ctx)) {
            return 0;
        }

        CommandSourceStack source = ctx.getSource();
        PlayerRepository playerRepository = PlayerRepository.getInstance();
        List<Player> players = playerRepository.findAll();
        if (players.isEmpty()) {
            source.sendFailure(Component.translatable("command.test.player.no_players"));
            return 0;
        }
        StringBuilder sb = new StringBuilder();
        for (Player player : players) {
            sb.append('\n').append(player);
        }
        source.sendSuccess(() -> Component.translatable("command.test.player.success.read_all", players.size(), sb.toString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int readPlayer(CommandContext<CommandSourceStack> ctx) {
        if (invalidPlayer(ctx)) {
            return 0;
        }
        CommandSourceStack source = ctx.getSource();
        PlayerRepository playerRepository = PlayerRepository.getInstance();
        UUID uuid = UUID.fromString(StringArgumentType.getString(ctx, "id"));

        if (!playerRepository.exists(uuid)) {
            source.sendFailure(Component.translatable("command.test.player.not_found", uuid.toString()));
            return 0;
        }

        Player player = playerRepository.findById(uuid);

        source.sendSuccess(() -> Component.literal(player.toString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static boolean invalidPlayer(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();

        if (!source.hasPermission(4)) {
            source.sendFailure(Component.translatable("command.test.player.no_permission"));
            return true;
        }
        if (!source.isPlayer()) {
            source.sendFailure(Component.translatable("command.test.player.not_player"));
            return true;
        }
        return false;
    }

}
