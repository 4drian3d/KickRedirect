package me.dreamerzero.kickredirect.listener;

import java.util.Optional;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.dreamerzero.kickredirect.KickRedirect;
import me.dreamerzero.kickredirect.enums.CheckMode;
import me.dreamerzero.kickredirect.utils.DebugInfo;
import me.dreamerzero.kickredirect.utils.ServerUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public final class KickListener {
    private final KickRedirect plugin;

    public KickListener(final KickRedirect plugin){
        this.plugin = plugin;
    }

    private static final PlainTextComponentSerializer SERIALIZER = PlainTextComponentSerializer.plainText();

    @Subscribe(order = PostOrder.EARLY)
    public void onKickFromServer(final KickedFromServerEvent event, final Continuation continuation){
        if(!event.getResult().isAllowed()){
            continuation.resume();
            plugin.debugCache().put(event.getPlayer().getUniqueId(), new DebugInfo(event, null));
            return;
        }
        final Optional<String> optional = event.getServerKickReason().map(SERIALIZER::serialize);
        if (optional.isPresent() && containsCheck(optional.get())
            || optional.isEmpty() && plugin.config().get().redirectOnNullMessage()
        ) {
            final RegisteredServer server = ServerUtils.getConfigServer(plugin);
            if (server == null) {
                plugin.getProxy().getConsoleCommandSource().sendMessage(
                    plugin.formatter().format(
                        plugin.messages().get().noServersFoundToRedirect(),
                        event.getPlayer(),
                        Placeholder.unparsed(
                            "sendmode",
                            plugin.config().get().getSendMode().toString()
                        )
                    )
                );
                final String kickMessage = plugin.messages().get().kickMessage();
                if(!kickMessage.isBlank()) {
                    event.setResult(
                        KickedFromServerEvent.DisconnectPlayer.create(
                            plugin.formatter().format(kickMessage, event.getPlayer())
                        )
                    );
                }
                continuation.resume();
                plugin.debugCache().put(event.getPlayer().getUniqueId(), new DebugInfo(event, null));
                return;
            }
            final String redirectMessage = plugin.messages().get().redirectMessage();
            event.setResult(redirectMessage.isBlank()
                ? KickedFromServerEvent.RedirectPlayer.create(server)
                : KickedFromServerEvent.RedirectPlayer.create(
                    server, plugin.formatter().format(redirectMessage, event.getPlayer())));
            continuation.resume();
            plugin.debugCache().put(event.getPlayer().getUniqueId(), new DebugInfo(event, server.getServerInfo().getName()));
            return;
        }
        continuation.resume();
        plugin.debugCache().put(event.getPlayer().getUniqueId(), new DebugInfo(event, null));
    }

    private boolean containsCheck(final String message) {
        for (final String msg : plugin.config().get().getMessagesToCheck()) {
            if (message.contains(msg)) {
                return plugin.config().get().checkMode() == CheckMode.WHITELIST;
            }
        }
        return plugin.config().get().checkMode() != CheckMode.WHITELIST;
    }
}
