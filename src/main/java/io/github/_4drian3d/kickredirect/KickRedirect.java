package io.github._4drian3d.kickredirect;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import io.github._4drian3d.kickredirect.configuration.Configuration;
import io.github._4drian3d.kickredirect.configuration.ConfigurationContainer;
import io.github._4drian3d.kickredirect.configuration.Messages;
import io.github._4drian3d.kickredirect.formatter.MiniPlaceholdersFormatter;
import io.github._4drian3d.kickredirect.formatter.RegularFormatter;
import io.github._4drian3d.kickredirect.listener.DebugListener;
import io.github._4drian3d.kickredirect.listener.KickListener;
import io.github._4drian3d.kickredirect.utils.Constants;
import io.github._4drian3d.kickredirect.utils.DebugInfo;
import org.bstats.velocity.Metrics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import io.github._4drian3d.kickredirect.commands.KickRedirectCommand;
import io.github._4drian3d.kickredirect.formatter.Formatter;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Plugin(
        id = "kickredirect",
        name = "KickRedirect",
        version = Constants.VERSION,
        description = "Set the redirect result of your servers shutdown",
        url = "https://modrinth.com/plugin/kickredirect",
        authors = {
                "4drian3d"
        },
        dependencies = {
                @Dependency(
                        id = "miniplaceholders",
                        optional = true
                )
        }
)
public final class KickRedirect {
    private final ProxyServer proxy;
    private final Path pluginPath;
    private final Logger logger;
    private final PluginManager pluginManager;
    private final Metrics.Factory metrics;
    private Formatter formatter;
    private ConfigurationContainer<Configuration> config;
    private ConfigurationContainer<Messages> messages;
    private final Cache<UUID, DebugInfo> cache = Caffeine.newBuilder()
            .expireAfterAccess(2, TimeUnit.SECONDS)
            .build();

    @Inject
    public KickRedirect(
            final ProxyServer proxy,
            final @DataDirectory Path pluginPath,
            final Logger logger,
            final PluginManager pluginManager,
            final Metrics.Factory metrics
    ) {
        this.pluginPath = pluginPath;
        this.proxy = proxy;
        this.logger = logger;
        this.pluginManager = pluginManager;
        this.metrics = metrics;
    }

    @Subscribe
    public void onProxyInitialization(final ProxyInitializeEvent event) {
        final int pluginId = 16944;
        metrics.make(this, pluginId);
        this.initialize(false);
    }

    public @NotNull ProxyServer getProxy() {
        return this.proxy;
    }

    public @NotNull Logger getLogger() {
        return this.logger;
    }

    public ConfigurationContainer<Configuration> config() {
        return this.config;
    }

    public ConfigurationContainer<Messages> messages() {
        return this.messages;
    }

    public Formatter formatter() {
        return this.formatter;
    }

    public Cache<UUID, DebugInfo> debugCache() {
        return this.cache;
    }

    private boolean loadConfig() {
        this.config = ConfigurationContainer.load(logger, pluginPath, Configuration.class, "config");
        this.messages = ConfigurationContainer.load(logger, pluginPath, Messages.class, "messages");
        return this.config != null && this.messages != null;
    }

    void initialize(final boolean test) {
        final long start = System.currentTimeMillis();
        this.proxy.getConsoleCommandSource().sendMessage(
                MiniMessage.miniMessage()
                        .deserialize("<gradient:red:#fff494>[KickRedirect]</gradient> <gradient:#78edff:#699dff>Starting plugin...")
        );
        if (!test) {
            Dependencies.loadDependencies(this, this.logger, pluginManager, pluginPath);
        }
        if (!this.loadConfig()) {
            return;
        }
        this.formatter = proxy.getPluginManager().isLoaded("miniplaceholders")
                ? new MiniPlaceholdersFormatter()
                : new RegularFormatter();
        KickRedirectCommand.command(this);
        proxy.getEventManager().register(
                this,
                KickedFromServerEvent.class,
                config.get().getListenerPriority(),
                new KickListener(this)
        );
        proxy.getEventManager().register(
                this,
                KickedFromServerEvent.class,
                PostOrder.LAST,
                new DebugListener(this)
        );

        this.proxy.getConsoleCommandSource().sendMessage(
                MiniMessage.miniMessage().deserialize(
                        "<gradient:red:#fff494>[KickRedirect]</gradient> <gradient:#78edff:#699dff>Fully started plugin in "
                                + (System.currentTimeMillis() - start)
                                + "ms")
        );
    }
}