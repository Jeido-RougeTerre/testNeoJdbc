package com.jeido.test.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class CommandUtils {

    @SuppressWarnings("unused")
    public static int nyi(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendFailure(Component.translatable("command.test.nyi"));
        return 0;
    }
}
