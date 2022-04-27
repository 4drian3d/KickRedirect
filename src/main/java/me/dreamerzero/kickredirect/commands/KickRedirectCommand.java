package me.dreamerzero.kickredirect.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;

import me.dreamerzero.kickredirect.KickRedirect;

public final class KickRedirectCommand {
    private KickRedirectCommand(){}

    public static void command(final KickRedirect plugin) {
        final LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder.<CommandSource>literal("kickredirect")
            .requires(src -> src.hasPermission("kickredirect.command"))
            .then(LiteralArgumentBuilder.<CommandSource>literal("reload")
                .executes(cmd -> {
                    final var source = cmd.getSource();
                    source.sendMessage(
                        plugin.formatter().format(
                            plugin.messages().reload().reloadingMessage(),
                            source
                    ));
                    source.sendMessage(
                        plugin.formatter().format(
                            plugin.loadConfig()
                                ? plugin.messages().reload().reloadMessage()
                                : plugin.messages().reload().failedReload(),
                            source
                    ));
                    return Command.SINGLE_SUCCESS;
                })
            ).build();

        var manager = plugin.getProxy().getCommandManager();
        var meta = manager.metaBuilder("kickredirect").plugin(plugin).build();
        manager.register(meta, new BrigadierCommand(command));
    }
}
