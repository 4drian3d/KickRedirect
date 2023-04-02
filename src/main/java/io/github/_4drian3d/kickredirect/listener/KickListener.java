package io.github._4drian3d.kickredirect.listener;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.velocitypowered.api.event.AwaitingEventExecutor;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.ServerKickResult;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.kickredirect.KickRedirect;
import io.github._4drian3d.kickredirect.enums.CheckMode;
import io.github._4drian3d.kickredirect.enums.KickStep;
import io.github._4drian3d.kickredirect.utils.DebugInfo;
import io.github._4drian3d.kickredirect.utils.Strings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class KickListener implements AwaitingEventExecutor<KickedFromServerEvent> {
    private final KickRedirect plugin;
    private final Cache<UUID, String> sent = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMillis(10))
            .build();

    public KickListener(final KickRedirect plugin) {
        this.plugin = plugin;
    }

    @Override
    public EventTask executeAsync(KickedFromServerEvent event) {
        return EventTask.withContinuation(continuation -> {
            final Player player = event.getPlayer();
            if (shouldKick(player, event.getServer())) {
                continuation.resume();
                cache(event, event.getServer().getServerInfo().getName(), KickStep.REPEATED_ATTEMPT);
                // This should keep the "original" DisconnectPlayer result
                return;
            }
            if (reasonCheck(event)) {
                final var configuration = plugin.config().get();
                final RegisteredServer server = configuration
                        .getSendMode()
                        .server(
                                plugin.getProxy(),
                                configuration.getServersToRedirect(),
                                configuration.getRandomAttempts()
                        );
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
                    addToSent(player, server);
                }
            } else {
                continuation.resume();
                cache(event, null, KickStep.DISALLOWED_REASON);
            }
        });
    }

    void cache(KickedFromServerEvent event, String serverName, KickStep step) {
        if (plugin.config().get().debug())
            plugin.debugCache()
                    .put(
                            event.getPlayer().getUniqueId(),
                            new DebugInfo(event, serverName, step)
                    );
    }

    boolean reasonCheck(KickedFromServerEvent event) {
        final Optional<String> optional = event.getServerKickReason()
                .map(PlainTextComponentSerializer.plainText()::serialize);

        final var configuration = plugin.config().get();

        if (optional.isPresent()) {
            final String message = optional.get();
            for (final String msg : plugin.config().get().getMessagesToCheck()) {
                if (Strings.containsIgnoreCase(message, msg)) {
                    return configuration.checkMode() == CheckMode.WHITELIST;
                }
            }
            return configuration.checkMode() != CheckMode.WHITELIST;
        } else {
            return configuration.redirectOnNullMessage();
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
        if (!kickMessage.isBlank()) {
            event.setResult(
                    KickedFromServerEvent.DisconnectPlayer.create(
                            plugin.formatter().format(kickMessage, event.getPlayer())
                    )
            );
        }
    }

    void addToSent(Player player, RegisteredServer server) {
        sent.put(player.getUniqueId(), server.getServerInfo().getName());
    }

    boolean shouldKick(Player player, RegisteredServer server) {
        return Objects.equals(sent.getIfPresent(player.getUniqueId()), server.getServerInfo().getName());
    }
}
