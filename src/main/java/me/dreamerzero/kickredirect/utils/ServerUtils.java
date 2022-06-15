package me.dreamerzero.kickredirect.utils;

import java.util.Optional;
import java.util.Random;

import com.velocitypowered.api.proxy.server.RegisteredServer;

import org.jetbrains.annotations.Nullable;

import me.dreamerzero.kickredirect.KickRedirect;

public final class ServerUtils {
    private ServerUtils(){}
    private static final Random rm = new Random();

    public static @Nullable RegisteredServer getConfigServer(KickRedirect plugin){
        switch (plugin.config().get().getSendMode()) {
            case TO_FIRST: return ServerUtils.getFirstServer(plugin);
            case TO_EMPTIEST_SERVER: return ServerUtils.getEmptiestServer(plugin);
            case RANDOM: return ServerUtils.getRandomServer(plugin);
        }
        return null;
    }

    public static @Nullable RegisteredServer getRandomServer(KickRedirect plugin){
        final String[] servers = plugin.config().get().getServersToRedirect();
        for(int i = 0; i < plugin.config().get().getRandomAttempts(); i++){
            Optional<RegisteredServer> sv = plugin.getProxy().getServer(servers[rm.nextInt(servers.length-1)]);
            if(sv.isPresent()) return sv.get();
        }
        return null;
    }

    public static @Nullable RegisteredServer getFirstServer(KickRedirect plugin){
        for (final String st : plugin.config().get().getServersToRedirect()) {
            final Optional<RegisteredServer> sv = plugin.getProxy().getServer(st);
            if(sv.isPresent()) return sv.get();
        }
        return null;
    }

    public static @Nullable RegisteredServer getEmptiestServer(KickRedirect plugin){
        RegisteredServer emptiest = null;
        for (final String st : plugin.config().get().getServersToRedirect()) {
            final Optional<RegisteredServer> sv = plugin.getProxy().getServer(st);
            if (sv.isPresent()) {
                RegisteredServer actualsv = sv.get();
                if (actualsv.getPlayersConnected().isEmpty())
                    return actualsv;
                if (emptiest == null) {
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
