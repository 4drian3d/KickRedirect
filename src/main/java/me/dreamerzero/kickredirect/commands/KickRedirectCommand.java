package me.dreamerzero.kickredirect.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;

import me.dreamerzero.kickredirect.KickRedirect;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class KickRedirectCommand {
    private KickRedirectCommand(){}

    public static void command(final KickRedirect plugin) {
        final LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder.<CommandSource>literal("kickredirect")
            .requires(src -> src.hasPermission("kickredirect.command"))
            .then(LiteralArgumentBuilder.<CommandSource>literal("reload")
                .executes(cmd -> {
                    cmd.getSource().sendMessage(MiniMessage.miniMessage().deserialize(plugin.messages().reloadMessage()));
                    return plugin.loadConfig() ? Command.SINGLE_SUCCESS : BrigadierCommand.FORWARD;
                })
            ).build();

        var manager = plugin.getProxy().getCommandManager();
        var meta = manager.metaBuilder("kickredirect").plugin(plugin).build();
        manager.register(meta, new BrigadierCommand(command));
    }
}
