package me.dreamerzero.kickredirect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.velocitypowered.api.event.player.KickedFromServerEvent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import me.dreamerzero.kickredirect.listener.objects.TestPlayer;
import me.dreamerzero.kickredirect.listener.objects.TestRegisteredServer;
import net.kyori.adventure.text.Component;

class ListenerTest {

    @Test
    @DisplayName("Correct Redirect")
    void testCorrectRedirect() {
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .event(EventBuilder.builder()
                .result(KickResultType.DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer())
            )
            .build();

        bundle.applyListener();

        assertInstanceOf(KickedFromServerEvent.RedirectPlayer.class, bundle.getEvent().getResult());
    }

    @Test
    @DisplayName("Exact Server")
    void testexactServer() {
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .event(EventBuilder.builder()
                .result(KickResultType.DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer())
            )
            .build();

        bundle.applyListener();

        assertEquals(
            bundle.getProxyServer().getServer("lobby1").orElse(null),
            ((KickedFromServerEvent.RedirectPlayer)bundle.getEvent().getResult()).getServer());
    }

    @Test
    void testContinuation() {
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .event(EventBuilder.builder()
                .result(KickResultType.DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer())
            )
            .build();

        bundle.applyListener();

        assertTrue(bundle.getContinuation().resumed());
    }
}
