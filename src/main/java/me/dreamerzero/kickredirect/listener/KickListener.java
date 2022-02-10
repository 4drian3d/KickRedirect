package me.dreamerzero.kickredirect.listener;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.dreamerzero.kickredirect.KickRedirect;
import me.dreamerzero.kickredirect.configuration.Configuration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class KickListener {
    private final KickRedirect plugin;
    private final Configuration.Config config;
    private final Random rm;

    public KickListener(KickRedirect plugin){
        this.plugin = plugin;
        this.config = Configuration.getConfig();
        this.rm = new Random();
    }

    private static final PlainTextComponentSerializer SERIALIZER = PlainTextComponentSerializer.plainText();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().character('&').hexColors().build();

    @Subscribe
    public void onKickFromServer(KickedFromServerEvent event){
        event.getServerKickReason().map(SERIALIZER::serialize).ifPresent(reason -> {
            Stream<String> stream = config.getMessagesToCheck().stream();
            if(config.isWhitelist() ? stream.anyMatch(reason::contains) : stream.noneMatch(reason::contains)){
                RegisteredServer server = this.getConfigServer();
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
    }

    private RegisteredServer getConfigServer(){
        switch(config.getSendMode()){
            case TO_FIRST: return this.getFirstServer();
            case TO_EMPTIEST_SERVER: return this.getEmptiestServer();
            case RANDOM: return this.getRandomServer();
        }
        return null;
    }

    private RegisteredServer getRandomServer(){
        final List<String> servers = config.getServersToRedirect();
        for(int i = 0; i < servers.size(); i++){
            String server = servers.get(rm.nextInt(servers.size())-1);
            Optional<RegisteredServer> sv = plugin.getProxy().getServer(server);
            if(sv.isPresent()) return sv.get();
        }
        return null;
    }

    private RegisteredServer getFirstServer(){
        for(String st : config.getServersToRedirect()){
            Optional<RegisteredServer> sv = plugin.getProxy().getServer(st);
            if(sv.isPresent()) return sv.get();
        }
        return null;
    }

    private RegisteredServer getEmptiestServer(){
        RegisteredServer emptiest = null;
        for(String st : config.getServersToRedirect()){
            Optional<RegisteredServer> sv = plugin.getProxy().getServer(st);
            if(sv.isPresent()) {
                RegisteredServer actualsv = sv.get();
                if(emptiest == null)
                    emptiest = actualsv;
                else {
                    if(actualsv.getPlayersConnected().size() < emptiest.getPlayersConnected().size()){
                        emptiest = actualsv;
                    }
                }
            }
        }
        return emptiest;
    }
}
