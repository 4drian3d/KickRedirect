package me.dreamerzero.kickredirect.listener;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.velocitypowered.api.event.player.KickedFromServerEvent;

import me.dreamerzero.kickredirect.EventBuilder;
import me.dreamerzero.kickredirect.EventBundle;
import me.dreamerzero.kickredirect.KickResultType;
import me.dreamerzero.kickredirect.listener.objects.TestPlayer;
import me.dreamerzero.kickredirect.listener.objects.TestRegisteredServer;
import net.kyori.adventure.text.Component;

class ListenerTest {
    @Test
    void testAllowed() {
        EventBuilder builder = EventBuilder.builder()
            .player(new TestPlayer("aea", false))
            .server(new TestRegisteredServer())
            .reason(Component.text("test"));

        KickedFromServerEvent event = builder
            .result(KickResultType.REDIRECT.result(Component.empty()))
            .build();

        assertFalse(event.getResult().isAllowed());

        event = builder
            .result(KickResultType.NOTIFY.result(Component.empty()))
            .build();

        assertFalse(event.getResult().isAllowed());

        event = builder
            .result(KickResultType.DISCONNECT.result(Component.empty()))
            .build();

        assertTrue(event.getResult().isAllowed());
    }

    @Test
    void reasonTest() {
        EventBundle bundle = EventBundle.builder()
            .debug(true)
            .build();
        EventBuilder builder = EventBuilder.builder()
            .player(new TestPlayer("aea", false))
            .server(new TestRegisteredServer())
            .result(KickResultType.DISCONNECT.result(Component.empty()));
        KickedFromServerEvent event = builder
            .reason(Component.text("kicked from server"))
            .build();

        KickListener listener = new KickListener(bundle.getPlugin());

        assertTrue(listener.reasonCheck(event));

        event = builder.reason(Component.empty()).build();

        assertFalse(listener.reasonCheck(event));

        event = builder.reason(null).build();

        assertTrue(listener.reasonCheck(event));
    }

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

        KickListener listener = new KickListener(bundle.getPlugin());

        assertThat(listener.redirectResult(new TestRegisteredServer(), bundle.getPlayer()))
            .isInstanceOf(KickedFromServerEvent.RedirectPlayer.class);
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

        var result = assertInstanceOf(
            KickedFromServerEvent.RedirectPlayer.class,
            bundle.getEvent().getResult());

        assertThat(bundle.getProxyServer().getServer("lobby1"))
            .isPresent()
            .contains(result.getServer());
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
