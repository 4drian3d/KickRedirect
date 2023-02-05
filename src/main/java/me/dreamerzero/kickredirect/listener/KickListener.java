package me.dreamerzero.kickredirect.listener;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.ServerKickResult;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.dreamerzero.kickredirect.KickRedirect;
import me.dreamerzero.kickredirect.enums.CheckMode;
import me.dreamerzero.kickredirect.enums.KickStep;
import me.dreamerzero.kickredirect.utils.DebugInfo;
import me.dreamerzero.kickredirect.utils.Strings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public final class KickListener {
    private final KickRedirect plugin;
    private final Map<UUID, String> sent = new HashMap<>();

    public KickListener(final KickRedirect plugin){
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onKickFromServer(final KickedFromServerEvent event, final Continuation continuation){
        final Player player = event.getPlayer();
        if (shouldKick(player, event.getServer())) {
            continuation.resume();
            cache(event, event.getServer().getServerInfo().getName(), KickStep.REPEATED_ATTEMPT);
            // This should keep the "original" DisconnectPlayer result
            return;
        }
        if (reasonCheck(event)) {
            final RegisteredServer server = plugin.config().get().getSendMode().server(plugin);
            if (server == null) {
                plugin.getProxy().getConsoleCommandSource().sendMessage(
                    plugin.formatter().format(
                        plugin.messages().get().noServersFoundToRedirect(),
                        player,
                        Placeholder.unparsed(
                            "sendmode",
                            plugin.config().get().getSendMode().toString()
                        )
                    )
                );
                applyKickResult(event);
                continuation.resume();
                cache(event, null, KickStep.NULL_SERVER);
            } else {
                event.setResult(redirectResult(server, player));
                continuation.resume();
                cache(event, server.getServerInfo().getName(), KickStep.AVAILABLE_SERVER);
                addToSended(player, server);
            }
        } else {
            continuation.resume();
            cache(event, null, KickStep.DISALLOWED_REASON);
        }
        
    }

    void cache(KickedFromServerEvent event, String serverName, KickStep step) {
        if(plugin.config().get().debug())
            plugin.debugCache()
                .put(
                    event.getPlayer().getUniqueId(),
                    new DebugInfo(event, serverName, step)
                );
    }

    boolean reasonCheck(KickedFromServerEvent event) {
        final Optional<String> optional = event.getServerKickReason()
            .map(PlainTextComponentSerializer.plainText()::serialize);

        if (optional.isPresent()) {
            final String message = optional.get();
            for (final String msg : plugin.config().get().getMessagesToCheck()) {
                if (Strings.containsIgnoreCase(message, msg)) {
                    return plugin.config().get().checkMode() == CheckMode.WHITELIST;
                }
            }
            return plugin.config().get().checkMode() != CheckMode.WHITELIST;
        } else {
            return plugin.config().get().redirectOnNullMessage();
        }
    }

    ServerKickResult redirectResult(RegisteredServer server, Player player) {
        final String redirectMessage = plugin.messages().get().redirectMessage();
        if (redirectMessage.isBlank()) {
            return KickedFromServerEvent.RedirectPlayer.create(server);
        } else {
            final Component message = plugin.formatter().format(redirectMessage, player);
            return KickedFromServerEvent.RedirectPlayer.create(server, message);
        }
    }

    void applyKickResult(KickedFromServerEvent event) {
        final String kickMessage = plugin.messages().get().kickMessage();
        if(!kickMessage.isBlank()) {
            event.setResult(
                KickedFromServerEvent.DisconnectPlayer.create(
                    plugin.formatter().format(kickMessage, event.getPlayer())
                )
            );
        }
    }

    void addToSended(Player player, RegisteredServer server) {
        sent.put(player.getUniqueId(), server.getServerInfo().getName());
        plugin.getProxy().getScheduler()
            .buildTask(plugin, () -> sent.remove(player.getUniqueId()))
            .delay(Duration.ofMillis(10))
            .schedule();
    }

    boolean shouldKick(Player player, RegisteredServer server) {
        return Objects.equals(sent.get(player.getUniqueId()), server.getServerInfo().getName());
    }
}
