package me.dreamerzero.kickredirect;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.dreamerzero.kickredirect.utils.ServerUtils;

class ServerUtilsTest {
    @Test
    @DisplayName("ServerUtils Test")
    void configSendMode() {
        EventBundle bundle = EventBundle.builder().build();

        var server = ServerUtils.getConfigServer(bundle.getPlugin());
        assertThat(bundle.getProxyServer().getServer("lobby1"))
            .isPresent()
            .contains(server);
    }

    @Test
    void randomSendMode() {
        EventBundle bundle = EventBundle.builder().build();

        assertThat(ServerUtils.getRandomServer(bundle.getPlugin()))
            .isNotNull()
            .isIn(bundle.getProxyServer().getAllServers());
    }

    @Test
    void firstServer() {
        EventBundle bundle = EventBundle.builder().build();

        var server = ServerUtils.getFirstServer(bundle.getPlugin());

        assertThat(server).isNotNull();

        assertThat(bundle.getProxyServer().getServer("lobby1"))
            .isPresent()
            .contains(server);
    }

    @Test
    void emptiestServer() {
        EventBundle bundle = EventBundle.builder().build();

        RegisteredServer server = ServerUtils.getEmptiestServer(bundle.getPlugin());
        assertNotNull(server);
        assertThat(bundle.getProxyServer().getServer("lobby2"))
            .isPresent()
            .contains(server);
    }
}
