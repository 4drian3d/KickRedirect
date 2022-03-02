package me.dreamerzero.kickredirect.listener;

import java.util.stream.Stream;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.dreamerzero.kickredirect.KickRedirect;
import me.dreamerzero.kickredirect.configuration.Configuration;
import me.dreamerzero.kickredirect.utils.ServerUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public final class KickListener {
    private final KickRedirect plugin;
    private final Configuration.Config config;

    public KickListener(KickRedirect plugin){
        this.plugin = plugin;
        this.config = Configuration.getConfig();
    }

    private static final PlainTextComponentSerializer SERIALIZER = PlainTextComponentSerializer.plainText();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().character('&').hexColors().build();

    @Subscribe(order = PostOrder.LAST)
    public void onKickFromServer(KickedFromServerEvent event, Continuation continuation){
        if(!event.getResult().isAllowed()){
            continuation.resume();
            return;
        }
        event.getServerKickReason().map(SERIALIZER::serialize).ifPresent(reason -> {
            Stream<String> stream = config.getMessagesToCheck().stream();
            if(config.isWhitelist() ? stream.anyMatch(reason::contains) : stream.noneMatch(reason::contains)){
                RegisteredServer server = ServerUtils.getConfigServer(plugin);
                if(server == null) {
                    plugin.getLogger().error("No servers were found to redirect the player to");
                    String kickMessage = config.getKickMessage();
                    if(!kickMessage.isBlank())
                        event.setResult(KickedFromServerEvent.DisconnectPlayer.create(LEGACY_SERIALIZER.deserialize(kickMessage)));
                    return;
                }
                event.setResult(KickedFromServerEvent.RedirectPlayer.create(server));
            }
        });
        continuation.resume();
    }
}
