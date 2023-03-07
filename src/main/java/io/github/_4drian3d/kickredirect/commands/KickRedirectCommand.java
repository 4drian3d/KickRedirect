package io.github._4drian3d.kickredirect.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;

import io.github._4drian3d.kickredirect.KickRedirect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

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
                        final long start = System.currentTimeMillis();
                        config.reload()
                                .thenCombineAsync(plugin.config().reload(), (c, m) -> c && m)
                                .thenAcceptAsync(result -> {
                                    final var newConfig = config.get().reload();
                                    final var duration = System.currentTimeMillis()-start;
                                    source.sendMessage(plugin.formatter()
                                        .format(
                                                result ? newConfig.reloadMessage() : newConfig.failedReload(), source,
                                                Placeholder.component("time", Component.text(duration))
                                        ));
                                });
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
