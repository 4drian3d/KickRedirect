package me.dreamerzero.kickredirect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.dreamerzero.kickredirect.utils.ServerUtils;

class ServerUtilsTest {
    @Test
    @DisplayName("ServerUtils Test")
    void configSendMode() {
        EventBundle bundle = EventBundle.builder().build();

        assertEquals(
            bundle.getProxyServer().getServer("lobby1").orElse(null),
            ServerUtils.getConfigServer(bundle.getPlugin()));
    }

    @Test
    void randomSendMode() {
        EventBundle bundle = EventBundle.builder().build();

        assertNotNull(ServerUtils.getRandomServer(bundle.getPlugin()));
    }

    @Test
    void firstServer() {
        EventBundle bundle = EventBundle.builder().build();

        assertNotNull(ServerUtils.getFirstServer(bundle.getPlugin()));
        assertEquals(
            bundle.getProxyServer().getServer("lobby1").orElse(null),
            ServerUtils.getConfigServer(bundle.getPlugin()));
    }

    @Test
    void emptiestServer() {
        EventBundle bundle = EventBundle.builder().build();

        RegisteredServer server = ServerUtils.getEmptiestServer(bundle.getPlugin());
        assertNotNull(server);
        assertEquals(server, bundle.getProxyServer().getServer("lobby2").orElse(null));
    }
}
