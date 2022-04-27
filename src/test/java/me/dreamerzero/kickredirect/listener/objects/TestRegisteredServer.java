package me.dreamerzero.kickredirect.listener.objects;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.proxy.server.ServerPing;

public class TestRegisteredServer implements RegisteredServer {

    @Override
    public boolean sendPluginMessage(ChannelIdentifier arg0, byte[] arg1) {
        return true;
    }

    @Override
    public Collection<Player> getPlayersConnected() {
        return Collections.emptyList();
    }

    @Override
    public ServerInfo getServerInfo() {
        return new ServerInfo(null, null);
    }

    @Override
    public CompletableFuture<ServerPing> ping() {
        return CompletableFuture.completedFuture(null);
    }
}
