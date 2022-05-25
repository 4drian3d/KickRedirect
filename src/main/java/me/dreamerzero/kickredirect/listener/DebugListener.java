package me.dreamerzero.kickredirect.listener;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;

import me.dreamerzero.kickredirect.KickRedirect;
import me.dreamerzero.kickredirect.utils.DebugInfo;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public final class DebugListener {
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
                final TagResolver commonResolver = debug.commonResolver();
                final TagResolver redirectResolver = TagResolver
                    .resolver(commonResolver, Placeholder.unparsed("result", debug.result()));
                final TagResolver eventResolver = TagResolver
                    .resolver(commonResolver, Placeholder.unparsed("result", event.getResult().getClass().getTypeName()));

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
