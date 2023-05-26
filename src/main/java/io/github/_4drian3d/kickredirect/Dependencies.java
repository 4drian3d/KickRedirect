package io.github._4drian3d.kickredirect;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import io.github._4drian3d.kickredirect.utils.Constants;
import net.byteflux.libby.Library;
import net.byteflux.libby.VelocityLibraryManager;
import net.byteflux.libby.relocation.Relocation;
import org.slf4j.Logger;

import java.nio.file.Path;

public final class Dependencies {
    @Inject
    private KickRedirect plugin;
    @Inject
    private Logger logger;
    @Inject
    private PluginManager pluginManager;
    @Inject
    @DataDirectory
    private Path dataDirectory;

    void loadDependencies() {
        final VelocityLibraryManager<KickRedirect> libraryManager
                = new VelocityLibraryManager<>(logger, dataDirectory, pluginManager, plugin, "libs");
        final Relocation configurateRelocation
                = new Relocation(
                        "org{}spongepowered",
                "io.github._4drian3d.kickredirect.libs{}org{}spongepowered");
        final Relocation geantyrefRelocation =
                new Relocation(
                        "io{}leangen{}geantyref",
                        "io.github._4drian3d.kickredirect.libs{}io{}leangen{}geantyref");
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

        libraryManager.addMavenCentral();
        libraryManager.loadLibraries(geantyref, hocon, confCore);
    }
}
