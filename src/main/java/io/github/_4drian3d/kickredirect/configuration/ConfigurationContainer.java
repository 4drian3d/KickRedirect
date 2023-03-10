package io.github._4drian3d.kickredirect.configuration;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public final class ConfigurationContainer<C extends Section> {
    private final AtomicReference<C> config;
    private final HoconConfigurationLoader loader;
    private final Class<C> clazz;
    private final Logger logger;

    private ConfigurationContainer(
        final C config,
        final Class<C> clazz,
        final HoconConfigurationLoader loader,
        final Logger logger
    ) {
        this.config = new AtomicReference<>(config);
        this.loader = loader;
        this.clazz = clazz;
        this.logger = logger;
    }

    public CompletableFuture<Boolean> reload() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final CommentedConfigurationNode node = loader.load();
                C newConfig = node.get(clazz);
                node.set(clazz, newConfig);
                loader.save(node);
                config.set(newConfig);
                return true;
            } catch (ConfigurateException exception) {
                logger.error("Could not reload {} configuration file", clazz.getSimpleName(), exception);
                return false;
            }
        });
    }

    public @NotNull C get() {
        return this.config.get();
    }

    public static <C extends Section> ConfigurationContainer<C> load(
            final Logger logger,
            final Path path,
            final Class<C> clazz,
            final String file
    ) {
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .defaultOptions(opts -> opts
                        .shouldCopyDefaults(true)
                        .header("KickRedirect | by 4drian3d\n")
                )
                .path(path.resolve(file+".conf"))
                .build();


        try {
            final CommentedConfigurationNode node = loader.load();
            final C config = node.get(clazz);
            node.set(clazz, config);
            loader.save(node);
            return new ConfigurationContainer<>(config, clazz, loader, logger);
        } catch (ConfigurateException exception){
            logger.error("Could not load {} configuration file", clazz.getSimpleName(), exception);
            return null;
        }
    }
}
