package me.dreamerzero.kickredirect;

import java.nio.file.Path;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import me.dreamerzero.kickredirect.configuration.Configuration;
import me.dreamerzero.kickredirect.listener.KickListener;
import me.dreamerzero.kickredirect.utils.Constants;

@Plugin(
    id = Constants.ID,
    name = Constants.NAME,
    version = Constants.VERSION,
    description = Constants.DESCRIPTION,
    url = Constants.URL,
    authors = {
        "4drian3d"
    }
)
public final class KickRedirect {
    private final ProxyServer proxy;
    private final Path pluginPath;
    private final Logger logger;
    private Configuration.Config config;

    @Inject
    public KickRedirect(
        final ProxyServer proxy,
        final @DataDirectory Path pluginPath,
        final Logger logger
    ) {
        this.pluginPath = pluginPath;
        this.proxy = proxy;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event){
        this.config = Configuration.loadMainConfig(pluginPath, logger);
        if(this.config == null) {
            return;
        }
        proxy.getEventManager().register(this, new KickListener(this));
    }

    public @NotNull ProxyServer getProxy(){
        return this.proxy;
    }

    public @NotNull Path getPluginPath(){
        return this.pluginPath;
    }

    public @NotNull Logger getLogger(){
        return this.logger;
    }

    public Configuration.Config config() {
        return this.config;
    }
}