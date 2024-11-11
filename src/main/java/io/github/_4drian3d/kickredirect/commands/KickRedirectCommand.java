package io.github._4drian3d.kickredirect.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import io.github._4drian3d.kickredirect.KickRedirect;
import io.github._4drian3d.kickredirect.configuration.Configuration;
import io.github._4drian3d.kickredirect.configuration.ConfigurationContainer;
import io.github._4drian3d.kickredirect.configuration.Messages;
import io.github._4drian3d.kickredirect.formatter.Formatter;
import io.github._4drian3d.kickredirect.utils.Registrable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.slf4j.Logger;

public final class KickRedirectCommand implements Registrable {
    @Inject
    private KickRedirect plugin;
    @Inject
    private CommandManager commandManager;
    @Inject
    private Formatter formatter;
    @Inject
    private Logger logger;
    @Inject
    private ConfigurationContainer<Configuration> configurationContainer;
    @Inject
    private ConfigurationContainer<Messages> messagesContainer;

    @Override
    public void register() {
        final LiteralCommandNode<CommandSource> node = BrigadierCommand
                .literalArgumentBuilder("kickredirect")
                .executes(context -> {
                    context.getSource().sendMessage(
                            formatter.format(
                                    messagesContainer.get().mainCommandMessage()));
                    return Command.SINGLE_SUCCESS;
                })
                .requires(src -> src.hasPermission("kickredirect.command"))
                .then(BrigadierCommand.literalArgumentBuilder("reload")
                        .executes(cmd -> {
                            final CommandSource source = cmd.getSource();
                            source.sendMessage(
                                formatter.format(
                                        messagesContainer.get().reload().reloadingMessage(),
                                    source
                                ));
                            final long start = System.currentTimeMillis();
                            messagesContainer.reload()
                                    .thenCombineAsync(configurationContainer.reload(), (c, m) -> c && m)
                                    .exceptionally(t -> {
                                        logger.error("An unexpected error occurred on config reloading", t);
                                        return false;
                                    })
                                    .thenAccept(result -> {
                                        final var updatedMessages = messagesContainer.get().reload();
                                        final var duration = System.currentTimeMillis()-start;
                                        source.sendMessage(formatter
                                            .format(
                                                result ? updatedMessages.reloadMessage() : updatedMessages.failedReload(), source,
                                                Placeholder.component("time", Component.text(duration))
                                            ));
                                    });
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
        final BrigadierCommand command = new BrigadierCommand(node);

        final CommandMeta meta = commandManager.metaBuilder(command)
                .plugin(plugin)
                .aliases("kr")
                .build();
        commandManager.register(meta, command);
    }
}
