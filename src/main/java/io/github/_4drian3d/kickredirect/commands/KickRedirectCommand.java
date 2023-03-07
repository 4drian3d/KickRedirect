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
                        final var messages = plugin.messages();
                        source.sendMessage(
                            plugin.formatter().format(
                                    messages.get().reload().reloadingMessage(),
                                source
                        ));
                        final long start = System.currentTimeMillis();
                        messages.reload()
                                .thenCombineAsync(plugin.config().reload(), (c, m) -> c && m)
                                .exceptionally(t -> {
                                    plugin.getLogger().error("An unexpected error occurred on config reloading", t);
                                    return false;
                                })
                                .thenAcceptAsync(result -> {
                                    final var updatedMessages = messages.get().reload();
                                    final var duration = System.currentTimeMillis()-start;
                                    source.sendMessage(plugin.formatter()
                                        .format(
                                                result ? updatedMessages.reloadMessage() : updatedMessages.failedReload(), source,
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
