package io.github._4drian3d.kickredirect.listener.objects;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.crypto.IdentifiedKey;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.player.PlayerSettings;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import com.velocitypowered.api.proxy.player.TabList;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.GameProfile.Property;

import io.github._4drian3d.kickredirect.builder.RequestBuilder;

import com.velocitypowered.api.util.ModInfo;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;

public class TestPlayer implements Player {
    private final boolean op;
    private final String name;
    private final UUID uuid = UUID.randomUUID();

    public TestPlayer(String name, boolean op){
        this.op = op;
        this.name = name;
    }

    @Override
    public Tristate getPermissionValue(String permission) {
        return Tristate.fromBoolean(op);
    }

    @Override
    public @NotNull Identity identity() {
        return Identity.identity(uuid);
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return ProtocolVersion.MINECRAFT_1_18;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress(404);
    }

    @Override
    public Optional<InetSocketAddress> getVirtualHost() {
        return Optional.of(new InetSocketAddress(404));
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void clearHeaderAndFooter() {}

    @Override
    public ConnectionRequestBuilder createConnectionRequest(RegisteredServer arg0) {
        return RequestBuilder.builder()
            .result(true)
            .server(new TestRegisteredServer())
            .build();
    }

    @Override
    public void disconnect(Component component) {
    }

    @Override
    public @Nullable ResourcePackInfo getAppliedResourcePack() {
        return null;
    }

    @Override
    public @Nullable String getClientBrand() {
        return null;
    }

    @Override
    public Optional<ServerConnection> getCurrentServer() {
        return Optional.empty();
    }

    @Override
    public @Nullable Locale getEffectiveLocale() {
        return null;
    }

    @Override
    public GameProfile getGameProfile() {
        return null;
    }

    @Override
    public List<Property> getGameProfileProperties() {
        return null;
    }

    @Override
    public Optional<ModInfo> getModInfo() {
        return Optional.empty();
    }

    @Override
    public @Nullable ResourcePackInfo getPendingResourcePack() {
        return null;
    }

    @Override
    public long getPing() {
        return 100;
    }

    @Override
    public Component getPlayerListFooter() {
        return Component.text("Footer");
    }

    @Override
    public Component getPlayerListHeader() {
        return Component.text("Header");
    }

    @Override
    public PlayerSettings getPlayerSettings() {
        return null;
    }

    @Override
    public boolean hasSentPlayerSettings() {
        return true;
    }

    @Override
    public TabList getTabList() {
        return null;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isOnlineMode() {
        return false;
    }

    @Override
    public boolean sendPluginMessage(ChannelIdentifier arg0, byte[] arg1) {
        return false;
    }

    @Override
    public void sendResourcePack(String arg0) {

    }

    @Override
    public void sendResourcePack(String arg0, byte[] arg1) {

    }

    @Override
    public void sendResourcePackOffer(ResourcePackInfo arg0) {

    }

    @Override
    public void setEffectiveLocale(Locale arg0) {

    }

    @Override
    public void setGameProfileProperties(List<Property> arg0) {

    }

    @Override
    public void spoofChatInput(String arg0) {

    }

    @Override
    public IdentifiedKey getIdentifiedKey() {
        return null;
    }
}
