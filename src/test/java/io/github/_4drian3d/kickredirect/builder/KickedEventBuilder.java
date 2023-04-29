package io.github._4drian3d.kickredirect.builder;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.ServerKickResult;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;

public final class KickedEventBuilder implements AbstractBuilder<KickedFromServerEvent> {
    private Player player;
    private RegisteredServer server;
    private Component reason;
    private ServerKickResult result;

    private KickedEventBuilder(){}

    public KickedEventBuilder result(@NotNull ServerKickResult result) {
        this.result = result;
        return this;
    }

    public KickedEventBuilder player(@NotNull Player player) {
        this.player = player;
        return this;
    }

    public KickedEventBuilder reason(Component reason) {
        this.reason = reason;
        return this;
    }

    public KickedEventBuilder server(@NotNull RegisteredServer server) {
        this.server = server;
        return this;
    }

    public static KickedEventBuilder builder() {
        return new KickedEventBuilder();
    }
    

    @Override
    public @NotNull KickedFromServerEvent build() {
        return new KickedFromServerEvent(
            player,
            server,
            reason,
            false,
            result
        );
    }
    
}
