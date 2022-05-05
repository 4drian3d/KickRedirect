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
                    final CommandSource source = cmd.getSource();
                    final var config = plugin.messages();
                    source.sendMessage(
                        plugin.formatter().format(
                            config.get().reload().reloadingMessage(),
                            source
                    ));
                    config.reload();
                    source.sendMessage(
                        plugin.formatter().format(
                            plugin.loadConfig()
                                ? plugin.messages().get().reload().reloadMessage()
                                : plugin.messages().get().reload().failedReload(),
                            source
                    ));
                    return Command.SINGLE_SUCCESS;
                })
            ).build();

        var manager = plugin.getProxy().getCommandManager();
        manager.register(manager.metaBuilder("kickredirect")
            .plugin(plugin).build(), new BrigadierCommand(command));
    }
}
