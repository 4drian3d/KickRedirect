package me.dreamerzero.kickredirect.utils;

import com.velocitypowered.api.event.player.KickedFromServerEvent;

import me.dreamerzero.kickredirect.enums.KickStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public final class DebugInfo {
    private final String playerName;
    private final String serverName;
    private final boolean duringServerConnect;
    private final String originalReason;
    private final String finalResult;
    private final KickStep step;

    public DebugInfo(final KickedFromServerEvent event, String server, KickStep step) {
        this.playerName = event.getPlayer().getUsername();
        this.serverName = server;
        this.duringServerConnect = event.kickedDuringServerConnect();
        this.originalReason = event.getServerKickReason()
                .map(PlainTextComponentSerializer.plainText()::serialize)
                .orElse("NONE");
        this.finalResult = event.getResult().getClass().getTypeName();
        this.step = step;
    }

    public String playerName() {
        return this.playerName;
    }

    public String serverName() {
        return this.serverName;
    }

    public boolean duringServerConnect() {
        return this.duringServerConnect;
    }

    public String originalReason() {
        return this.originalReason;
    }

    public String result() {
        return this.finalResult;
    }

    public KickStep step() {
        return this.step;
    }

    public TagResolver commonResolver() {
        return TagResolver.builder()
                .resolvers(
                        Placeholder.unparsed("player_name", playerName()),
                        Placeholder.component("during_server_connect", Component.text(duringServerConnect())),
                        Placeholder.unparsed("reason", originalReason()),
                        Placeholder.unparsed("step", step.toString())
                )
                .build();
    }
}
