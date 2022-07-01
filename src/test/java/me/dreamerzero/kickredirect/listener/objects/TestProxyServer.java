package me.dreamerzero.kickredirect.listener.objects;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.config.ProxyConfig;
import com.velocitypowered.api.proxy.messages.ChannelRegistrar;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import com.velocitypowered.api.util.ProxyVersion;

import net.kyori.adventure.text.Component;

public class TestProxyServer implements ProxyServer {
    private PluginManager pManager;
    private EventManager eManager;
    private CommandManager cManager;
    private final Map<String, RegisteredServer> servers = Map.of(
        "lobby1", new TestRegisteredServer(5).name("lobby1"),
        "lobby2", new TestRegisteredServer(0).name("lobby2"));

    public TestProxyServer(){
        this.pManager = new TestPluginManager();
        this.eManager = new TestEventManager();
        this.cManager = new TestCommandManager();
    }

    @Override
    public RegisteredServer createRawRegisteredServer(ServerInfo arg0) {
        return null;
    }

    @Override
    public ResourcePackInfo.Builder createResourcePackBuilder(String arg0) {
        return null;
    }

    @Override
    public Collection<Player> getAllPlayers() {
        return Set.<Player>of();
    }

    @Override
    public Collection<RegisteredServer> getAllServers() {
        return servers.values();
    }

    @Override
    public InetSocketAddress getBoundAddress() {
        return null;
    }

    @Override
    public ChannelRegistrar getChannelRegistrar() {
        return null;
    }

    @Override
    public CommandManager getCommandManager() {
        return this.cManager;
    }

    @Override
    public ProxyConfig getConfiguration() {
        return null;
    }

    @Override
    public ConsoleCommandSource getConsoleCommandSource() {
        return new ConsoleCommandSource(){
            @Override
            public Tristate getPermissionValue(String arg0) {
                return Tristate.TRUE;
            }
        };
    }

    @Override
    public EventManager getEventManager() {
        return this.eManager;
    }

    @Override
    public Optional<Player> getPlayer(String name) {
        return Optional.of(new TestPlayer(name, false));
    }

    @Override
    public Optional<Player> getPlayer(UUID arg0) {
        return Optional.of(new TestPlayer("name", false));
    }

    @Override
    public int getPlayerCount() {
        return 69;
    }

    @Override
    public PluginManager getPluginManager() {
        return this.pManager;
    }

    @Override
    public Scheduler getScheduler() {
        return new Scheduler(){
            @Override
            public TaskBuilder buildTask(Object plugin, Runnable consumer) {
                return new TaskBuilder() {

                    @Override
                    public TaskBuilder delay(@IntRange(from = 0) long time, @NotNull TimeUnit unit) {
                        return this;
                    }

                    @Override
                    public TaskBuilder repeat(@IntRange(from = 0) long time, @NotNull TimeUnit unit) {
                        return this;
                    }

                    @Override
                    public TaskBuilder clearDelay() {
                        return this;
                    }

                    @Override
                    public TaskBuilder clearRepeat() {
                        return this;
                    }

                    @Override
                    public ScheduledTask schedule() {
                        return null;
                    }
                    
                };
            }

            @Override
            public TaskBuilder buildTask(Object plugin, Consumer<ScheduledTask> consumer) {
                return null;
            }

            @Override
            public Collection<ScheduledTask> tasksByPlugin(Object plugin) {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public Optional<RegisteredServer> getServer(String arg0) {
        return Optional.ofNullable(servers.get(arg0));
    }

    @Override
    public ProxyVersion getVersion() {
        return new ProxyVersion("asd", "Peruviankkit", "3.1.2");
    }

    @Override
    public Collection<Player> matchPlayer(String arg0) {
        return Set.<Player>of();
    }

    @Override
    public Collection<RegisteredServer> matchServer(String arg0) {
        return Set.<RegisteredServer>of();
    }

    @Override
    public RegisteredServer registerServer(ServerInfo arg0) {
        return null;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void shutdown(Component arg0) {
    }

    @Override
    public void unregisterServer(ServerInfo arg0) {
    }
}
