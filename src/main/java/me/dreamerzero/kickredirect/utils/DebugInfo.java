package me.dreamerzero.kickredirect.utils;

import com.velocitypowered.api.event.player.KickedFromServerEvent;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class DebugInfo {
    private final String playerName;
    private final String serverName;
    private final boolean duringServerConnect;
    private final String originalReason;
    private final String finalResult;

    public DebugInfo(final KickedFromServerEvent event) {
        this.playerName = event.getPlayer().getUsername();
        this.serverName = event.getServer().getServerInfo().getName();
        this.duringServerConnect = event.kickedDuringServerConnect();
        this.originalReason = event.getServerKickReason().map(PlainTextComponentSerializer.plainText()::serialize).orElse("");
        this.finalResult = event.getResult().getClass().getTypeName();
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
}
