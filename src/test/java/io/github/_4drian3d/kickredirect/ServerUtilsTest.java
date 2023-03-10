package io.github._4drian3d.kickredirect;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.kickredirect.configuration.Configuration;
import io.github._4drian3d.kickredirect.configuration.ConfigurationContainer;
import io.github._4drian3d.kickredirect.enums.SendMode;
import io.github._4drian3d.kickredirect.listener.objects.TestProxyServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServerUtilsTest {
    @TempDir Path path;

    @Test
    @DisplayName("ServerUtils Test")
    void configSendMode() {
        var proxyServer = new TestProxyServer();

        var configuration = ConfigurationContainer.load(mock(), path, Configuration.class, "config");
        assertThat(configuration).isNotNull();

        var server = configuration.get()
                .getSendMode()
                .server(
                        proxyServer,
                    configuration.get().getServersToRedirect(),
                    configuration.get().getRandomAttempts()
                );

        assertThat(proxyServer.getServer("lobby1"))
            .isPresent()
            .contains(server);
    }

    @Test
    void randomSendMode() {
        var proxyServer = new TestProxyServer();

        var configuration = ConfigurationContainer.load(mock(), path, Configuration.class, "config");
        assertThat(configuration).isNotNull();
        assertThat(SendMode.RANDOM.server(
                proxyServer,
                configuration.get().getServersToRedirect(),
                configuration.get().getRandomAttempts()
        ))
            .isNotNull()
            .isIn(proxyServer.getAllServers());
    }

    @Test
    void firstServer() {
        var proxyServer = new TestProxyServer();
        var configuration = ConfigurationContainer.load(mock(), path, Configuration.class, "config");

        assertThat(configuration).isNotNull();

        var server = SendMode.TO_FIRST.server(
                proxyServer,
                configuration.get().getServersToRedirect(),
                configuration.get().getRandomAttempts()
        );

        assertThat(proxyServer.getServer("lobby1"))
            .isPresent()
            .contains(server);
    }

    @Test
    void emptiestServer() {
        ProxyServer proxyServer = new TestProxyServer();

        var container = ConfigurationContainer.load(
                mock(), path, Configuration.class, "config"
        );

        assertThat(container).isNotNull();

        RegisteredServer server = SendMode.TO_EMPTIEST_SERVER.server(
                proxyServer,
                container.get().getServersToRedirect(),
                container.get().getRandomAttempts()
        );

        assertThat(proxyServer.getServer("lobby2"))
            .isPresent()
            .contains(server);
    }

    // https://github.com/4drian3d/KickRedirect/issues/4
    @Test
    void issue4() {
        var configuration = ConfigurationContainer.load(mock(), path, Configuration.class, "config");

        assertThat(configuration).isNotNull();

        RegisteredServer server = mock();

        ProxyServer proxyServer = mock();
        when(proxyServer.getServer(any())).thenReturn(Optional.of(server));
        when(proxyServer.getAllServers()).thenReturn(Collections.singleton(server));

        assertDoesNotThrow(() -> SendMode.RANDOM.server(
                proxyServer,
                configuration.get().getServersToRedirect(),
                configuration.get().getRandomAttempts()
        ));
    }
}
