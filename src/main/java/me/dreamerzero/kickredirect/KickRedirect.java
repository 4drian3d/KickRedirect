package me.dreamerzero.kickredirect;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import me.dreamerzero.kickredirect.commands.KickRedirectCommand;
import me.dreamerzero.kickredirect.configuration.Configuration;
import me.dreamerzero.kickredirect.formatter.Formatter;
import me.dreamerzero.kickredirect.formatter.MiniPlaceholdersFormatter;
import me.dreamerzero.kickredirect.formatter.RegularFormatter;
import me.dreamerzero.kickredirect.listener.DebugListener;
import me.dreamerzero.kickredirect.listener.KickListener;
import me.dreamerzero.kickredirect.utils.Constants;
import me.dreamerzero.kickredirect.utils.DebugInfo;
import net.byteflux.libby.Library;
import net.byteflux.libby.VelocityLibraryManager;
import net.byteflux.libby.relocation.Relocation;
import net.kyori.adventure.text.minimessage.MiniMessage;

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
    private final PluginManager pluginManager;
    private Formatter formatter;
    private Configuration.Config config;
    private Configuration.Messages messages;
    private final Cache<UUID, DebugInfo> cache = Caffeine.newBuilder()
        .expireAfterAccess(4, TimeUnit.SECONDS)
        .build();

    @Inject
    public KickRedirect(
        final ProxyServer proxy,
        final @DataDirectory Path pluginPath,
        final Logger logger,
        final PluginManager pluginManager
    ) {
        this.pluginPath = pluginPath;
        this.proxy = proxy;
        this.logger = logger;
        this.pluginManager = pluginManager;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event){
        long start = System.currentTimeMillis();
        this.proxy.getConsoleCommandSource().sendMessage(
            MiniMessage.miniMessage().deserialize("<gradient:red:#fff494>[KickRedirect]</gradient> <gradient:#78edff:#699dff>Starting plugin...")
        );
        this.loadDependencies();
        if (!this.loadConfig()) {
            return;
        }
        this.formatter = proxy.getPluginManager().isLoaded("miniplaceholders")
            ? new MiniPlaceholdersFormatter()
            : new RegularFormatter();
        KickRedirectCommand.command(this);
        proxy.getEventManager().register(this, new KickListener(this));
        proxy.getEventManager().register(this, new DebugListener(this));

        long duration = System.currentTimeMillis() - start;

        this.proxy.getConsoleCommandSource().sendMessage(
            MiniMessage.miniMessage().deserialize("<gradient:red:#fff494>[KickRedirect]</gradient> <gradient:#78edff:#699dff> Fully started plugin in " + duration + "ms")
        );
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

    public Configuration.Messages messages() {
        return this.messages;
    }

    public Formatter formatter() {
        return this.formatter;
    }

    public Cache<UUID, DebugInfo> debugCache() {
        return this.cache;
    }

    private void loadDependencies() {
        final VelocityLibraryManager<KickRedirect> libraryManager = new VelocityLibraryManager<>(this.logger, this.pluginPath, pluginManager, this, "libs");
        final Relocation configurateRelocation = new Relocation("org{}spongepowered", "me.dreamerzero.kickredirect.libs.sponge");
        final Library hocon = Library.builder()
            .groupId("org{}spongepowered")
            .artifactId("configurate-hocon")
            .version("4.1.2")
            .id("configurate-hocon")
            .relocate(configurateRelocation)
            .build();
        final Library confCore = Library.builder()
            .groupId("org{}spongepowered")
            .artifactId("configurate-core")
            .version("4.1.2")
            .id("configurate-core")
            .relocate(configurateRelocation)
            .build();
        final Library geantyref = Library.builder()
            .groupId("io{}leangen{}geantyref")
            .artifactId("geantyref")
            .version("1.3.13")
            .id("geantyref")
            .relocate("io{}leangen{}geantyref", "me.dreamerzero.kickredirect.libs.geantyref")
            .build();
        final Library caffeine = Library.builder()
            .groupId("com{}github{}ben-manes{}caffeine")
            .artifactId("caffeine")
            .version("3.0.6")
            .id("caffeine")
            .relocate("com{}github{}ben-manes{}caffeine", "me.dreamerzero.kickredirect.libs.caffeine")
            .build();

        libraryManager.addMavenCentral();
        libraryManager.loadLibrary(hocon);
        libraryManager.loadLibrary(confCore);
        libraryManager.loadLibrary(geantyref);
        libraryManager.loadLibrary(caffeine);
    }

    public boolean loadConfig() {
        this.config = Configuration.loadMainConfig(pluginPath, logger);
        this.messages = Configuration.loadMessages(pluginPath, logger);
        return this.config != null && this.messages != null;
    }
}