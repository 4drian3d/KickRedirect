package io.github._4drian3d.kickredirect.enums;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.velocitypowered.api.proxy.server.RegisteredServer;

import io.github._4drian3d.kickredirect.KickRedirect;

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
            final List<String> servers = plugin.config().get().getServersToRedirect();
            Optional<RegisteredServer> server = servers.size() == 1
                    ? plugin.getProxy().getServer(servers.get(0))
                    : Optional.empty();
            for (int i = 0; i < plugin.config().get().getRandomAttempts(); i++) {
                if (server.isPresent()) return server.get();
                int value = rm.nextInt(servers.size());
                server = plugin.getProxy().getServer(servers.get(value));
            }
            return null;
        }
    };
    private static final Random rm = new Random();

    public abstract RegisteredServer server(KickRedirect plugin);
}
