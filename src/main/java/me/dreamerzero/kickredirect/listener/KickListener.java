package me.dreamerzero.kickredirect.listener;

import java.util.Collection;
import java.util.Optional;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.dreamerzero.kickredirect.KickRedirect;
import me.dreamerzero.kickredirect.enums.CheckMode;
import me.dreamerzero.kickredirect.utils.ServerUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public final class KickListener {
    private final KickRedirect plugin;

    public KickListener(KickRedirect plugin){
        this.plugin = plugin;
    }

    private static final PlainTextComponentSerializer SERIALIZER = PlainTextComponentSerializer.plainText();

    @Subscribe(order = PostOrder.LAST)
    public void onKickFromServer(final KickedFromServerEvent event, final Continuation continuation){
        if(!event.getResult().isAllowed()){
            continuation.resume();
            return;
        }
        final Collection<String> messagesToCheck = plugin.config().getMessagesToCheck();
        final Optional<String> optional = event.getServerKickReason().map(SERIALIZER::serialize);
        if (
            (optional.isPresent() && (plugin.config().checkMode() == CheckMode.WHITELIST
                ? messagesToCheck.stream().anyMatch(optional.get()::contains)
                : messagesToCheck.stream().noneMatch(optional.get()::contains))
            ) || optional.isEmpty() && plugin.config().redirectOnNullMessage()
        ) {
            final RegisteredServer server = ServerUtils.getConfigServer(plugin);
            if (server == null) {
                plugin.getProxy().getConsoleCommandSource().sendMessage(
                    plugin.formatter().format(
                        plugin.messages().noServersFoundToRedirect(),
                        event.getPlayer(),
                        Placeholder.unparsed(
                            "sendmode",
                            plugin.config().getSendMode().toString()
                        )
                    )
                );
                final String kickMessage = plugin.messages().kickMessage();
                if(!kickMessage.isBlank()) {
                    event.setResult(
                        KickedFromServerEvent.DisconnectPlayer.create(
                            plugin.formatter().format(kickMessage, event.getPlayer())
                        )
                    );
                }
                continuation.resume();
                return;
            }
            event.setResult(KickedFromServerEvent.RedirectPlayer.create(server));
        }
        continuation.resume();
    }
}
