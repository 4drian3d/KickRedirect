package me.dreamerzero.kickredirect.listener;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.ServerKickResult;

import me.dreamerzero.kickredirect.KickRedirect;
import me.dreamerzero.kickredirect.utils.DebugInfo;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public final class DebugListener {
    private final KickRedirect plugin;

    public DebugListener(final KickRedirect plugin) {
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.LAST)
    public EventTask afterKickFromServer(final KickedFromServerEvent event) {
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
