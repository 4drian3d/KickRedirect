package io.github._4drian3d.kickredirect.listener;

import com.velocitypowered.api.event.AwaitingEventExecutor;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.ServerKickResult;

import io.github._4drian3d.kickredirect.utils.DebugInfo;
import io.github._4drian3d.kickredirect.KickRedirect;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DebugListener implements AwaitingEventExecutor<KickedFromServerEvent> {
    private final KickRedirect plugin;

    public DebugListener(final KickRedirect plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable EventTask executeAsync(KickedFromServerEvent event) {
        if (!plugin.config().get().debug()) {
            return null;
        }
        return EventTask.async(() -> {
            final DebugInfo debug = plugin.debugCache().getIfPresent(event.getPlayer().getUniqueId());
            if (debug == null) {
                return;
            }
            final TagResolver commonResolver = debug.commonResolver();
            final TagResolver redirectResolver = TagResolver
                    .resolver(
                            commonResolver,
                            Placeholder.unparsed("result", debug.result()),
                            Placeholder.unparsed("server_name", debug.serverName() == null
                                    ? "NONE" : debug.serverName())
                    );
            final TagResolver eventResolver = TagResolver
                    .resolver(
                            commonResolver,
                            Placeholder.unparsed("result", event.getResult().getClass().getTypeName()),
                            Placeholder.unparsed("server_name", serverName(event.getResult()))
                    );
            var config = plugin.messages().get().debug();
            var console = plugin.getProxy().getConsoleCommandSource();
            console.sendMessage(
                    plugin.formatter().format(
                            config.redirectResult(), event.getPlayer(), redirectResolver));
            console.sendMessage(
                    plugin.formatter().format(
                            config.finalResult(), event.getPlayer(), eventResolver));
        });
    }

    private String serverName(ServerKickResult result) {
        if (result instanceof KickedFromServerEvent.RedirectPlayer) {
            return ((KickedFromServerEvent.RedirectPlayer) result).getServer().getServerInfo().getName();
        }
        return "NONE";
    }
}
