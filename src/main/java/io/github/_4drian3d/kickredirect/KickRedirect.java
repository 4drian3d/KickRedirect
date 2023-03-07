package io.github._4drian3d.kickredirect;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import io.github._4drian3d.kickredirect.configuration.Configuration;
import io.github._4drian3d.kickredirect.configuration.ConfigurationContainer;
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
        id = Constants.ID,
        name = Constants.NAME,
        version = Constants.VERSION,
        description = Constants.DESCRIPTION,
        url = Constants.URL,
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
    private ConfigurationContainer<Configuration.Config> config;
    private ConfigurationContainer<Configuration.Messages> messages;
    private Cache<UUID, DebugInfo> cache;

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

    public @NotNull Path getPluginPath() {
        return this.pluginPath;
    }

    public @NotNull Logger getLogger() {
        return this.logger;
    }

    public ConfigurationContainer<Configuration.Config> config() {
        return this.config;
    }

    public ConfigurationContainer<Configuration.Messages> messages() {
        return this.messages;
    }

    public Formatter formatter() {
        return this.formatter;
    }

    public Cache<UUID, DebugInfo> debugCache() {
        if (this.cache == null) {
            this.cache = Caffeine.newBuilder()
                    .expireAfterAccess(2, TimeUnit.SECONDS)
                    .build();
        }
        return this.cache;
    }

    public boolean loadConfig() {
        this.config = Configuration.loadMainConfig(this);
        this.messages = Configuration.loadMessages(this);
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
        proxy.getEventManager().register(this, new KickListener(this));
        proxy.getEventManager().register(this, new DebugListener(this));

        this.proxy.getConsoleCommandSource().sendMessage(
                MiniMessage.miniMessage().deserialize(
                        "<gradient:red:#fff494>[KickRedirect]</gradient> <gradient:#78edff:#699dff>Fully started plugin in "
                                + (System.currentTimeMillis() - start)
                                + "ms")
        );
    }
}