package io.github._4drian3d.kickredirect.listener.objects;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginManager;

public class TestPluginManager implements PluginManager {
    @Override
    public void addToClasspath(Object arg0, Path arg1) {
    }

    @Override
    public Optional<PluginContainer> fromInstance(Object arg0) {
        return Optional.empty();
    }

    @Override
    public Optional<PluginContainer> getPlugin(String arg0) {
        return Optional.empty();
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        return Collections.emptyList();
    }

    @Override
    public boolean isLoaded(String arg0) {
        return false;
    }
}
