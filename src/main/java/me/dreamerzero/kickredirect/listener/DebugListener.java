package me.dreamerzero.kickredirect.listener;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;

import me.dreamerzero.kickredirect.KickRedirect;
import me.dreamerzero.kickredirect.utils.DebugInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class DebugListener {
    private final KickRedirect plugin;

    public DebugListener(final KickRedirect plugin){
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.LAST)
    public EventTask afterKickFromServer(final KickedFromServerEvent event) {
        if (plugin.config().get().debug()) {
            return EventTask.async(() -> {
                final DebugInfo debug = plugin.debugCache().getIfPresent(event.getPlayer().getUniqueId());
                if (debug == null) {
                    return;
                }
                final TagResolver commonResolver = TagResolver.builder()
                    .resolvers(
                        Placeholder.unparsed("player_name", debug.playerName()),
                        Placeholder.unparsed("server_name", debug.serverName()),
                        Placeholder.component("during_server_connect", Component.text(debug.duringServerConnect())),
                        Placeholder.unparsed("reason", debug.originalReason()))
                    .build();
                final TagResolver redirectResolver = TagResolver.builder()
                    .resolvers(commonResolver, Placeholder.unparsed("result", debug.result()))
                    .build();
                final TagResolver eventResolver = TagResolver.builder()
                    .resolvers(commonResolver, Placeholder.unparsed("result", event.getResult().getClass().getTypeName()))
                    .build();

                plugin.getProxy().getConsoleCommandSource().sendMessage(
                    plugin.formatter().format(
                        plugin.messages().get().debug().redirectResult(),
                        event.getPlayer(), redirectResolver));
                plugin.getProxy().getConsoleCommandSource().sendMessage(
                    plugin.formatter().format(
                        plugin.messages().get().debug().finalResult(),
                        event.getPlayer(), eventResolver));
            });
        }
        return null;
    }
}
