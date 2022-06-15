package me.dreamerzero.kickredirect;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.ServerKickResult;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;

public final class EventBuilder implements AbstractBuilder<KickedFromServerEvent> {
    private Player player;
    private RegisteredServer server;
    private Component reason;
    private boolean duringServerConnect = false;
    private ServerKickResult result;

    private EventBuilder(){}

    public EventBuilder result(@NotNull ServerKickResult result) {
        this.result = result;
        return this;
    }

    public EventBuilder player(@NotNull Player player) {
        this.player = player;
        return this;
    }

    public EventBuilder reason(Component reason) {
        this.reason = reason;
        return this;
    }

    public EventBuilder duringServerConnect(boolean serverConnect) {
        this.duringServerConnect = serverConnect;
        return this;
    }

    public EventBuilder server(@NotNull RegisteredServer server) {
        this.server = server;
        return this;
    }

    public static EventBuilder builder() {
        return new EventBuilder();
    }
    

    @Override
    public @NotNull KickedFromServerEvent build() {
        return new KickedFromServerEvent(
            player,
            server,
            reason,
            duringServerConnect,
            result
        );
    }
    
}
