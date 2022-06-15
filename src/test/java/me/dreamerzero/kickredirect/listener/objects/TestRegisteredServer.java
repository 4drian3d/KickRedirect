package me.dreamerzero.kickredirect.listener.objects;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.proxy.server.ServerPing;

public class TestRegisteredServer implements RegisteredServer {
    private String name;
    public String name() {
        return this.name == null ? super.toString()+"-UNNAMED" : this.name;
    }
    private final List<Player> PLAYERS = new ArrayList<>();

    public TestRegisteredServer() {
    }

    public TestRegisteredServer(int playerCount) {
        for(int i = 0; i< playerCount; i++) {
            PLAYERS.add(null);
        }
    }

    public TestRegisteredServer(Player... players) {
        for(Player player : players) {
            PLAYERS.add(player);
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
    public Collection<Player> getPlayersConnected() {
        return PLAYERS;
    }

    @Override
    public ServerInfo getServerInfo() {
        return new ServerInfo("server", new InetSocketAddress(404));
    }

    @Override
    public CompletableFuture<ServerPing> ping() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((PLAYERS == null) ? 0 : PLAYERS.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TestRegisteredServer other = (TestRegisteredServer) obj;
        if (PLAYERS == null) {
            if (other.PLAYERS != null)
                return false;
        } else if (!PLAYERS.equals(other.PLAYERS))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
