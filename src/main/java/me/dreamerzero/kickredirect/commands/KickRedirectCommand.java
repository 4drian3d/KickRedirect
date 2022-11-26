package me.dreamerzero.kickredirect.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;

import me.dreamerzero.kickredirect.KickRedirect;

public final class KickRedirectCommand {
    private KickRedirectCommand() {}

    public static void command(final KickRedirect plugin) {
        final var command = new BrigadierCommand(LiteralArgumentBuilder.<CommandSource>literal("kickredirect")
                .executes(context -> {
                    context.getSource().sendMessage(
                            plugin.formatter().format(
                                    plugin.messages().get().mainCommandMessage()));
                    return Command.SINGLE_SUCCESS;
                })
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
                                    ? config.get().reload().reloadMessage()
                                    : config.get().reload().failedReload(),
                                source
                        ));
                        return Command.SINGLE_SUCCESS;
                    })
                ).build());

        var manager = plugin.getProxy().getCommandManager();
        manager.register(manager.metaBuilder(command)
                        .plugin(plugin)
                        .aliases("kr")
                        .build(), command);
    }
}
