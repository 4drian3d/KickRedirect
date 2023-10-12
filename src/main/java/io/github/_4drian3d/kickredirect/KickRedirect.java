package io.github._4drian3d.kickredirect;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import io.github._4drian3d.kickredirect.commands.KickRedirectCommand;
import io.github._4drian3d.kickredirect.listener.DebugListener;
import io.github._4drian3d.kickredirect.listener.KickListener;
import io.github._4drian3d.kickredirect.modules.PluginModule;
import io.github._4drian3d.kickredirect.utils.Constants;
import io.github._4drian3d.kickredirect.utils.Registrable;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bstats.velocity.Metrics;

import java.util.stream.Stream;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

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
    @Inject
    private Metrics.Factory metrics;
    @Inject
    private Injector injector;
    @Inject
    private ComponentLogger logger;

    @Subscribe
    public void onProxyInitialization(final ProxyInitializeEvent event) {
        final long start = System.currentTimeMillis();
        final int pluginId = 16944;
        metrics.make(this, pluginId);

        logger.info(miniMessage().deserialize("<gradient:#78edff:#699dff>Starting plugin..."));

        injector.getInstance(Dependencies.class).loadDependencies();
        injector = injector.createChildInjector(new PluginModule());

        Stream.of(KickRedirectCommand.class, KickListener.class, DebugListener.class)
                .map(injector::getInstance)
                .forEach(Registrable::register);

        final long end = System.currentTimeMillis() - start;
        logger.info(miniMessage().deserialize("<gradient:#78edff:#699dff>Fully started plugin in "+end+" ms"));
    }
}