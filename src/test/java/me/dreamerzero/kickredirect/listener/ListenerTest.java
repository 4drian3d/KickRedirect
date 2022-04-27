package me.dreamerzero.kickredirect.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.ProxyServer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import me.dreamerzero.kickredirect.KickRedirect;
import me.dreamerzero.kickredirect.listener.objects.TestContinuation;
import me.dreamerzero.kickredirect.listener.objects.TestPlayer;
import me.dreamerzero.kickredirect.listener.objects.TestProxyServer;
import me.dreamerzero.kickredirect.listener.objects.TestRegisteredServer;
import me.dreamerzero.kickredirect.utils.ServerUtils;
import net.kyori.adventure.text.Component;

public class ListenerTest {
    public static KickRedirect plugin;
    public static ProxyServer proxy;

    @BeforeAll
    static void loadConfig() {
        proxy = new TestProxyServer();
        plugin = new KickRedirect(
            proxy,
            Path.of("build", "reports", "tests", "test"),
            LoggerFactory.getLogger(ListenerTest.class),
            proxy.getPluginManager()
        );
        plugin.loadConfig();
    }

    @Test
    @DisplayName("Redirect Test")
    void testRedirect() {
        TestBundle bundle = new TestBundle(Component.text("shutdown from server"));
        assertNotNull(plugin.config());
        assertNotNull(plugin.messages());

        assertEquals(proxy.getServer("lobby1").orElse(null), ServerUtils.getConfigServer(plugin));

        new KickListener(plugin)
            .onKickFromServer(bundle.event, bundle.continuation);
        assertNotNull(plugin.debugCache().getIfPresent(bundle.player.getUniqueId()));
        assertTrue(bundle.continuation.resumed());

        var result = assertInstanceOf(KickedFromServerEvent.RedirectPlayer.class, bundle.event.getResult());
        assertEquals(proxy.getServer("lobby1").orElse(null), result.getServer());
    }

    public static class TestBundle {
        public final KickedFromServerEvent event;
        public final TestContinuation continuation;
        public final TestPlayer player;

        public TestBundle(Component reason) {
            this.player = new TestPlayer("4drian3d", true);
            this.event = new KickedFromServerEvent(
                player,
                new TestRegisteredServer(),
                reason,
                false,
                KickedFromServerEvent.DisconnectPlayer.create(Component.text("test"))
            );
            this.continuation = new TestContinuation();
        }
    }
}
