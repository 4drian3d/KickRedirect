package me.dreamerzero.kickredirect.listener;

import java.util.Optional;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.dreamerzero.kickredirect.KickRedirect;
import me.dreamerzero.kickredirect.configuration.Configuration;
import me.dreamerzero.kickredirect.enums.SendMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class KickListener {
    private final KickRedirect plugin;
    private final Configuration.Config config;
    public KickListener(KickRedirect plugin){
        this.plugin = plugin;
        this.config = Configuration.getConfig();
    }
    @Subscribe
    public void onKickFromServer(KickedFromServerEvent event){
        Optional<Component> kickReason = event.getServerKickReason();
        if(kickReason.isPresent() && PlainTextComponentSerializer.plainText().serialize(kickReason.get()).contains(config.getKickMessage())){
            RegisteredServer server = config.getSendMode() == SendMode.TO_FIRST ? getFirstServer() : getEmptiestServer();
            if(server == null) {
                plugin.getLogger().error("No servers were found to redirect the player to");
                return;
            }
            event.setResult(KickedFromServerEvent.RedirectPlayer.create(server));
        }
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
