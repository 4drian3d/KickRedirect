package io.github._4drian3d.kickredirect.listener;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.Inject;
import com.velocitypowered.api.event.AwaitingEventExecutor;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.ServerKickResult;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.kickredirect.KickRedirect;
import io.github._4drian3d.kickredirect.configuration.Configuration;
import io.github._4drian3d.kickredirect.configuration.ConfigurationContainer;
import io.github._4drian3d.kickredirect.configuration.Messages;
import io.github._4drian3d.kickredirect.enums.CheckMode;
import io.github._4drian3d.kickredirect.enums.KickStep;
import io.github._4drian3d.kickredirect.formatter.Formatter;
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
    @Inject
    private KickRedirect plugin;
    @Inject
    private ProxyServer proxyServer;
    @Inject
    private EventManager eventManager;
    @Inject
    private Formatter formatter;
    @Inject
    private ConfigurationContainer<Configuration> configurationContainer;
    @Inject
    private ConfigurationContainer<Messages> messagesContainer;
    @Inject
    private Cache<UUID, DebugInfo> debugCache;
    private final Cache<UUID, String> sent = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMillis(10))
            .build();

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
                final Configuration configuration = configurationContainer.get();
                final RegisteredServer server = configuration
                        .getSendMode()
                        .server(
                                proxyServer,
                                configuration.getServersToRedirect(),
                                configuration.getRandomAttempts()
                        );
                if (server == null) {
                    proxyServer.getConsoleCommandSource().sendMessage(
                            formatter.format(
                                    messagesContainer.get().noServersFoundToRedirect(),
                                    player,
                                    Placeholder.unparsed(
                                            "sendmode",
                                            configurationContainer.get().getSendMode().toString()
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
        if (configurationContainer.get().debug()) {
            debugCache.put(event.getPlayer().getUniqueId(), new DebugInfo(event, serverName, step));
        }
    }

    boolean reasonCheck(KickedFromServerEvent event) {
        final Optional<String> optional = event.getServerKickReason()
                .map(PlainTextComponentSerializer.plainText()::serialize);

        final Configuration configuration = configurationContainer.get();

        if (optional.isPresent()) {
            final String message = optional.get();
            for (final String msg : configuration.getMessagesToCheck()) {
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
        final String redirectMessage = messagesContainer.get().redirectMessage();
        if (redirectMessage.isBlank()) {
            return KickedFromServerEvent.RedirectPlayer.create(server);
        } else {
            final Component message = formatter.format(redirectMessage, player);
            return KickedFromServerEvent.RedirectPlayer.create(server, message);
        }
    }

    void applyKickResult(KickedFromServerEvent event) {
        final String kickMessage = messagesContainer.get().kickMessage();
        if (!kickMessage.isBlank()) {
            event.setResult(
                KickedFromServerEvent.DisconnectPlayer.create(
                    formatter.format(kickMessage, event.getPlayer())
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

    public void register() {
        eventManager.register(
            plugin,
            KickedFromServerEvent.class,
            configurationContainer.get().getListenerPriority(),
            this
        );
    }
}
