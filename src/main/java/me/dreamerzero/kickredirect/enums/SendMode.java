package me.dreamerzero.kickredirect.enums;

import java.util.Optional;
import java.util.Random;

import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.dreamerzero.kickredirect.KickRedirect;

public enum SendMode {
    TO_FIRST {
        @Override
        public RegisteredServer server(KickRedirect plugin) {
            for (final String st : plugin.config().get().getServersToRedirect()) {
                final Optional<RegisteredServer> sv = plugin.getProxy().getServer(st);
                if (sv.isPresent()) return sv.get();
            }
            return null;
        }
    },
    TO_EMPTIEST_SERVER {
        @Override
        public RegisteredServer server(KickRedirect plugin) {
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
                        if (actualsv.getPlayersConnected().size() < emptiest.getPlayersConnected().size()) {
                            emptiest = actualsv;
                        }
                    }
                }
            }
            return emptiest;
        }
    },
    RANDOM {
        @Override
        public RegisteredServer server(KickRedirect plugin) {
            final String[] servers = plugin.config().get().getServersToRedirect();
            Optional<RegisteredServer> server;
            for (int i = 0; i < plugin.config().get().getRandomAttempts(); i++) {
                if (servers.length == 1) {
                    server = plugin.getProxy().getServer(servers[0]);
                } else {
                    int value = rm.nextInt(servers.length);
                    server = plugin.getProxy().getServer(servers[value]);
                }
                if (server.isPresent()) return server.get();
            }
            return null;
        }
    };
    private static final Random rm = new Random();

    public abstract RegisteredServer server(KickRedirect plugin);
}
