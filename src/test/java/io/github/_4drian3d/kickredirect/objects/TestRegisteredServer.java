package io.github._4drian3d.kickredirect.objects;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.PluginMessageEncoder;
import com.velocitypowered.api.proxy.server.PingOptions;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.proxy.server.ServerPing;
import org.jetbrains.annotations.NotNull;

import static org.mockito.Mockito.mock;

public class TestRegisteredServer implements RegisteredServer {
    private String name;
    public String name() {
        return name == null ? "TEST-SERVER-"+hashCode() : name;
    }
    private final List<Player> PLAYERS = new ArrayList<>();

    public TestRegisteredServer() {
    }

    public TestRegisteredServer(int playerCount) {
        for (int i = 0; i< playerCount; i++) {
            PLAYERS.add(null);
        }
    }

    public RegisteredServer name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "RegisteredServer["+name()+"]";
    }

    @Override
    public boolean sendPluginMessage(ChannelIdentifier arg0, byte[] arg1) {
        return true;
    }

    @Override
    public boolean sendPluginMessage(@NotNull ChannelIdentifier channelIdentifier, @NotNull PluginMessageEncoder pluginMessageEncoder) {
        return false;
    }

    @Override
    public Collection<Player> getPlayersConnected() {
        return PLAYERS;
    }

    @Override
    public ServerInfo getServerInfo() {
        return new ServerInfo(name(), new InetSocketAddress(404));
    }

    @Override
    public CompletableFuture<ServerPing> ping() {
        return CompletableFuture.completedFuture(mock());
    }

    @Override
    public CompletableFuture<ServerPing> ping(PingOptions pingOptions) {
        return CompletableFuture.failedFuture(new Throwable());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof TestRegisteredServer o))
            return false;
        return Objects.equals(this.name(), o.name());
    }
}
