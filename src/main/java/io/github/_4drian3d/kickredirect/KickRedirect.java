package io.github._4drian3d.kickredirect;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github._4drian3d.kickredirect.commands.KickRedirectCommand;
import io.github._4drian3d.kickredirect.listener.DebugListener;
import io.github._4drian3d.kickredirect.listener.KickListener;
import io.github._4drian3d.kickredirect.modules.PluginModule;
import io.github._4drian3d.kickredirect.utils.Constants;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.velocity.Metrics;

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
    private ProxyServer proxy;
    @Inject
    private Metrics.Factory metrics;
    @Inject
    private Injector injector;

    @Subscribe
    public void onProxyInitialization(final ProxyInitializeEvent event) {
        final long start = System.currentTimeMillis();
        final int pluginId = 16944;
        metrics.make(this, pluginId);

        this.proxy.getConsoleCommandSource().sendMessage(
                MiniMessage.miniMessage()
                        .deserialize("<gradient:red:#fff494>[KickRedirect]</gradient> <gradient:#78edff:#699dff>Starting plugin...")
        );

        injector.getInstance(Dependencies.class).loadDependencies();
        injector = injector.createChildInjector(new PluginModule());

        injector.getInstance(KickRedirectCommand.class).command();
        injector.getInstance(KickListener.class).register();
        injector.getInstance(DebugListener.class).register();

        this.proxy.getConsoleCommandSource().sendMessage(
                MiniMessage.miniMessage().deserialize(
                        "<gradient:red:#fff494>[KickRedirect]</gradient> <gradient:#78edff:#699dff>Fully started plugin in "
                                + (System.currentTimeMillis() - start)
                                + "ms")
        );
    }
}