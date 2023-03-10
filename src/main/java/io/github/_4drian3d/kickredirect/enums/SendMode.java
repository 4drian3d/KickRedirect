package io.github._4drian3d.kickredirect.enums;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public enum SendMode {
    TO_FIRST {
        @Override
        public RegisteredServer server(ProxyServer proxyServer, List<String> servers, int randomAttempts) {
            for (final String st : servers) {
                final Optional<RegisteredServer> sv = proxyServer.getServer(st);
                if (sv.isPresent()) return sv.get();
            }
            return null;
        }
    },
    TO_EMPTIEST_SERVER {
        @Override
        public RegisteredServer server(ProxyServer proxyServer, List<String> servers, int randomAttempts) {
            RegisteredServer emptiest = null;
            for (final String st : servers) {
                final Optional<RegisteredServer> sv = proxyServer.getServer(st);
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
        public RegisteredServer server(ProxyServer proxyServer, List<String> servers, int randomAttempts) {
            Optional<RegisteredServer> server = servers.size() == 1
                    ? proxyServer.getServer(servers.get(0))
                    : Optional.empty();
            for (int i = 0; i < randomAttempts; i++) {
                if (server.isPresent()) return server.get();
                int value = rm.nextInt(servers.size());
                server = proxyServer.getServer(servers.get(value));
            }
            return null;
        }
    };
    private static final Random rm = new Random();

    public abstract RegisteredServer server(ProxyServer proxyServer, List<String> servers, int randomAttempts);
}
