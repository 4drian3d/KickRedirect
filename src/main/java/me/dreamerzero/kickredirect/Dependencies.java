package me.dreamerzero.kickredirect;

import com.velocitypowered.api.plugin.PluginManager;
import me.dreamerzero.kickredirect.utils.Constants;
import net.byteflux.libby.Library;
import net.byteflux.libby.VelocityLibraryManager;
import net.byteflux.libby.relocation.Relocation;
import org.slf4j.Logger;

import java.nio.file.Path;

public final class Dependencies {
    private Dependencies() {}
    static void loadDependencies(KickRedirect plugin, Logger logger, PluginManager manager, Path path) {
        final VelocityLibraryManager<KickRedirect> libraryManager
                = new VelocityLibraryManager<>(logger, path, manager, plugin, "libs");
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
}
