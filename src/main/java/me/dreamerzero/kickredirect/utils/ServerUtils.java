package me.dreamerzero.kickredirect.utils;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.velocitypowered.api.proxy.server.RegisteredServer;

import org.jetbrains.annotations.Nullable;

import me.dreamerzero.kickredirect.KickRedirect;
import me.dreamerzero.kickredirect.configuration.Configuration;
import me.dreamerzero.kickredirect.configuration.Configuration.Config;

public final class ServerUtils {
    private ServerUtils(){}
    private static final Random rm = new Random();

    public static @Nullable RegisteredServer getConfigServer(KickRedirect plugin){
        final Config config = Configuration.getConfig();
        switch(config.getSendMode()){
            case TO_FIRST: return ServerUtils.getFirstServer(plugin, config);
            case TO_EMPTIEST_SERVER: return ServerUtils.getEmptiestServer(plugin, config);
            case RANDOM: return ServerUtils.getRandomServer(plugin, config);
        }
        return null;
    }

    private static @Nullable RegisteredServer getRandomServer(KickRedirect plugin, Config config){
        final List<String> servers = config.getServersToRedirect();
        for(int i = 0; i < config.getRandomAttempts(); i++){
            String server = servers.get(rm.nextInt(servers.size())-1);
            Optional<RegisteredServer> sv = plugin.getProxy().getServer(server);
            if(sv.isPresent()) return sv.get();
        }
        return null;
    }

    private static @Nullable RegisteredServer getFirstServer(KickRedirect plugin, Config config){
        for(String st : config.getServersToRedirect()){
            Optional<RegisteredServer> sv = plugin.getProxy().getServer(st);
            if(sv.isPresent()) return sv.get();
        }
        return null;
    }

    private static @Nullable RegisteredServer getEmptiestServer(KickRedirect plugin, Config config){
        RegisteredServer emptiest = null;
        for(String st : config.getServersToRedirect()){
            Optional<RegisteredServer> sv = plugin.getProxy().getServer(st);
            if(sv.isPresent()) {
                RegisteredServer actualsv = sv.get();
                if(actualsv.getPlayersConnected().isEmpty())
                    return actualsv;
                if(emptiest == null) {
                    emptiest = actualsv;
                } else {
                    if(actualsv.getPlayersConnected().size() < emptiest.getPlayersConnected().size()){
                        emptiest = actualsv;
                    }
                }
            }
        }
        return emptiest;
    }
}
