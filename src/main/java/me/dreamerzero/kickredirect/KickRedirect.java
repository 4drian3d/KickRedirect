package me.dreamerzero.kickredirect;

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

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import me.dreamerzero.kickredirect.commands.KickRedirectCommand;
import me.dreamerzero.kickredirect.configuration.Configuration;
import me.dreamerzero.kickredirect.configuration.ConfigurationContainer;
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
    private Formatter formatter;
    private ConfigurationContainer<Configuration.Config> config;
    private ConfigurationContainer<Configuration.Messages> messages;
    private Cache<UUID, DebugInfo> cache;

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
    public void onProxyInitialization(final ProxyInitializeEvent event){
        this.initialize(false);
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
                .expireAfterAccess(3, TimeUnit.SECONDS)
                .build();
        }
        return this.cache;
    }

    private void loadDependencies() {
        final VelocityLibraryManager<KickRedirect> libraryManager
            = new VelocityLibraryManager<>(this.logger, this.pluginPath, pluginManager, this, "libs");
        final Relocation configurateRelocation
            = new Relocation("org{}spongepowered", "me.dreamerzero.kickredirect.libs.sponge");
        final Relocation geantyrefRelocation =
            new Relocation("io{}leangen{}geantyref", "me.dreamerzero.kickredirect.libs.geantyref");
        final Library hocon = Library.builder()
            .groupId("org{}spongepowered")
            .artifactId("configurate-hocon")
            .version(Constants.CONFIGURATE)
            .id("configurate-hocon")
            .relocate(configurateRelocation)
            .relocate(geantyrefRelocation)
            .build();
        final Library confCore = Library.builder()
            .groupId("org{}spongepowered")
            .artifactId("configurate-core")
            .version(Constants.CONFIGURATE)
            .id("configurate-core")
            .relocate(configurateRelocation)
            .relocate(geantyrefRelocation)
            .build();
        final Library geantyref = Library.builder()
            .groupId("io{}leangen{}geantyref")
            .artifactId("geantyref")
            .version(Constants.GEANTYREF)
            .id("geantyref")
            .relocate(geantyrefRelocation)
            .build();
        final Library caffeine = Library.builder()
            .groupId("com{}github{}ben-manes{}caffeine")
            .artifactId("caffeine")
            .version(Constants.CAFFEINE)
            .id("caffeine")
            .relocate("com{}github{}ben-manes{}caffeine", "me.dreamerzero.kickredirect.libs.caffeine")
            .build();

        libraryManager.addMavenCentral();
        libraryManager.loadLibrary(geantyref);
        libraryManager.loadLibrary(hocon);
        libraryManager.loadLibrary(confCore);
        libraryManager.loadLibrary(caffeine);
    }

    public boolean loadConfig() {
        this.config = Configuration.loadMainConfig(this);
        this.messages = Configuration.loadMessages(this);
        return this.config != null && this.messages != null;
    }

    void initialize(final boolean test) {
        final long start = System.currentTimeMillis();
        this.proxy.getConsoleCommandSource().sendMessage(
            MiniMessage.miniMessage().deserialize("<gradient:red:#fff494>[KickRedirect]</gradient> <gradient:#78edff:#699dff>Starting plugin...")
        );
        if (!test) {
            this.loadDependencies();
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
                "<gradient:red:#fff494>[KickRedirect]</gradient> <gradient:#78edff:#699dff> Fully started plugin in "
                + (System.currentTimeMillis() - start)
                + "ms")
        );
    }
}