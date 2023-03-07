package io.github._4drian3d.kickredirect.configuration;

import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public final class ConfigurationContainer<C extends Configuration.ConfigSection> {
    private C config;
    private final HoconConfigurationLoader loader;
    private final Class<C> clazz;
    private final Logger logger;

    public ConfigurationContainer(
        final C config,
        final Class<C> clazz,
        final HoconConfigurationLoader loader,
        final Logger logger
    ) {
        this.config = config;
        this.loader = loader;
        this.clazz = clazz;
        this.logger = logger;
    }

    public void reload() {
        this.safeReload();
    }

    public C get() {
        return this.config;
    }

    private void safeReload() {
        C newConfig = null;
        try {
            final CommentedConfigurationNode node = loader.load();
            newConfig = node.get(clazz);
            node.set(clazz, config);
            loader.save(node);
        } catch (ConfigurateException exception) {
            logger.error("Could not load config.conf file", exception);
        } finally {
            if (newConfig != null) {
                config = newConfig;
            }
        }
    }
}
