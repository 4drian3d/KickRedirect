package io.github._4drian3d.kickredirect.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import io.github._4drian3d.kickredirect.configuration.Configuration;
import io.github._4drian3d.kickredirect.configuration.ConfigurationContainer;
import io.github._4drian3d.kickredirect.configuration.Messages;
import io.github._4drian3d.kickredirect.formatter.Formatter;
import io.github._4drian3d.kickredirect.formatter.MiniPlaceholdersFormatter;
import io.github._4drian3d.kickredirect.formatter.RegularFormatter;
import io.github._4drian3d.kickredirect.utils.DebugInfo;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class PluginModule extends AbstractModule {
    @Provides
    @Singleton
    private Formatter formatter(final PluginManager pluginManager) {
        return pluginManager.isLoaded("miniplaceholders")
                ? new MiniPlaceholdersFormatter()
                : new RegularFormatter();
    }

    @Provides
    @Singleton
    private ConfigurationContainer<Messages> messagesContainer(
            final Logger logger,
            final @DataDirectory Path path
    ) {
        return ConfigurationContainer.load(logger, path, Messages.class, "messages.conf");
    }

    @Provides
    @Singleton
    private ConfigurationContainer<Configuration> configurationContainer(
            final Logger logger,
            final @DataDirectory Path path
    ) {
        return ConfigurationContainer.load(logger, path, Configuration.class, "config.conf");
    }

    @Provides
    @Singleton
    private Cache<UUID, DebugInfo> cache() {
        return Caffeine.newBuilder()
                .expireAfterAccess(2, TimeUnit.SECONDS)
                .build();
    }
}
