package io.github._4drian3d.kickredirect.listener;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.inject.Inject;
import com.velocitypowered.api.event.AwaitingEventExecutor;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.ServerKickResult;
import io.github._4drian3d.kickredirect.KickRedirect;
import io.github._4drian3d.kickredirect.configuration.Configuration;
import io.github._4drian3d.kickredirect.configuration.ConfigurationContainer;
import io.github._4drian3d.kickredirect.configuration.Messages;
import io.github._4drian3d.kickredirect.formatter.Formatter;
import io.github._4drian3d.kickredirect.modules.KickRedirectSource;
import io.github._4drian3d.kickredirect.utils.DebugInfo;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public final class DebugListener implements AwaitingEventExecutor<KickedFromServerEvent> {
    @Inject
    private KickRedirect plugin;
    @Inject
    private EventManager eventManager;
    @Inject
    private Cache<UUID, DebugInfo> debugCache;
    @Inject
    private Formatter formatter;
    @Inject
    private ConfigurationContainer<Configuration> configurationContainer;
    @Inject
    private ConfigurationContainer<Messages> messagesContainer;
    @Inject
    private KickRedirectSource source;

    @Override
    public @Nullable EventTask executeAsync(KickedFromServerEvent event) {
        if (!configurationContainer.get().debug()) {
            return null;
        }
        return EventTask.async(() -> {
            final DebugInfo debug = debugCache.getIfPresent(event.getPlayer().getUniqueId());
            if (debug == null) {
                return;
            }
            final TagResolver commonResolver = debug.commonResolver();
            final TagResolver redirectResolver = TagResolver
                    .resolver(
                            commonResolver,
                            Placeholder.unparsed("result", debug.finalResult()),
                            Placeholder.unparsed("server_name", debug.serverName() == null
                                    ? "NONE" : debug.serverName())
                    );
            final TagResolver eventResolver = TagResolver
                    .resolver(
                            commonResolver,
                            Placeholder.unparsed("result", event.getResult().getClass().getTypeName()),
                            Placeholder.unparsed("server_name", serverName(event.getResult()))
                    );
            final Messages.Debug messages = messagesContainer.get().debug();
            source.sendMessage(
                    formatter.format(
                            messages.redirectResult(), event.getPlayer(), redirectResolver));
            source.sendMessage(
                    formatter.format(
                            messages.finalResult(), event.getPlayer(), eventResolver));
        });
    }

    private String serverName(ServerKickResult result) {
        if (result instanceof KickedFromServerEvent.RedirectPlayer redirectPlayer) {
            return redirectPlayer.getServer().getServerInfo().getName();
        }
        return "NONE";
    }

    public void register() {
        eventManager.register(
            plugin,
            KickedFromServerEvent.class,
            PostOrder.LAST,
            this
        );
    }
}
