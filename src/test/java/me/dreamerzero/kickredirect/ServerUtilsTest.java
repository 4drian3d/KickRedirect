package me.dreamerzero.kickredirect;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.dreamerzero.kickredirect.enums.SendMode;
import me.dreamerzero.kickredirect.listener.objects.TestProxyServer;
import me.dreamerzero.kickredirect.listener.objects.TestRegisteredServer;

class ServerUtilsTest {
    @Test
    @DisplayName("ServerUtils Test")
    void configSendMode() {
        EventBundle bundle = EventBundle.builder().build();

        var server = bundle.getPlugin().config().get().getSendMode().server(bundle.getPlugin());
        assertThat(bundle.getProxyServer().getServer("lobby1"))
            .isPresent()
            .contains(server);
    }

    @Test
    void randomSendMode() {
        EventBundle bundle = EventBundle.builder().build();

        assertThat(SendMode.RANDOM.server(bundle.getPlugin()))
            .isNotNull()
            .isIn(bundle.getProxyServer().getAllServers());
    }

    @Test
    void firstServer() {
        EventBundle bundle = EventBundle.builder().build();

        var server = SendMode.TO_FIRST.server(bundle.getPlugin());

        assertThat(server).isNotNull();

        assertThat(bundle.getProxyServer().getServer("lobby1"))
            .isPresent()
            .contains(server);
    }

    @Test
    void emptiestServer() {
        EventBundle bundle = EventBundle.builder().build();

        RegisteredServer server = SendMode.TO_EMPTIEST_SERVER.server(bundle.getPlugin());
        assertNotNull(server);
        assertThat(bundle.getProxyServer().getServer("lobby2"))
            .isPresent()
            .contains(server);
    }

    // https://github.com/4drian3d/KickRedirect/issues/4
    @Test
    void issue4() {
        RegisteredServer server = new TestRegisteredServer(5);
        EventBundle bundle = EventBundle.builder()
            .server(new TestProxyServer() {
                @Override
                public Collection<RegisteredServer> getAllServers() {
                    return Collections.singleton(server);
                }
                @Override
                public Optional<RegisteredServer> getServer(String arg) {
                    return Optional.of(server);
                }
            })
            .build();
        SendMode sendMode = SendMode.RANDOM;

        assertDoesNotThrow(() -> sendMode.server(bundle.getPlugin()));
    }
}
