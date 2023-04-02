package io.github._4drian3d.kickredirect.listener;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.kickredirect.EventBundle;
import io.github._4drian3d.kickredirect.KickedEventBuilder;
import io.github._4drian3d.kickredirect.enums.KickStep;
import io.github._4drian3d.kickredirect.listener.objects.TestContinuation;
import io.github._4drian3d.kickredirect.listener.objects.TestPlayer;
import io.github._4drian3d.kickredirect.listener.objects.TestRegisteredServer;
import io.github._4drian3d.kickredirect.utils.DebugInfo;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static io.github._4drian3d.kickredirect.enums.KickResultType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ListenerTest {
    @TempDir Path path;
    @Test
    void testAllowed() {
        KickedEventBuilder builder = KickedEventBuilder.builder()
            .player(new TestPlayer("aea", false))
            .server(new TestRegisteredServer())
            .reason(Component.text("test"));

        KickedFromServerEvent event = builder
            .result(REDIRECT.result(Component.empty()))
            .build();

        assertFalse(event.getResult().isAllowed());

        event = builder
            .result(NOTIFY.result(Component.empty()))
            .build();

        assertFalse(event.getResult().isAllowed());

        event = builder
            .result(DISCONNECT.result(Component.empty()))
            .build();

        assertTrue(event.getResult().isAllowed());
    }

    @Test
    void reasonTest() {
        EventBundle bundle = EventBundle.builder()
            .debug(true)
            .path(path)
            .build();
        KickedEventBuilder builder = KickedEventBuilder.builder()
            .player(new TestPlayer("aea", false))
            .server(new TestRegisteredServer())
            .result(DISCONNECT.result(Component.empty()));
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
            .path(path)
            .event(KickedEventBuilder.builder()
                .result(DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer())
            )
            .build();

        KickListener listener = new KickListener(bundle.getPlugin());

        assertThat(listener.redirectResult(new TestRegisteredServer(), bundle.getPlayer()))
            .isInstanceOf(KickedFromServerEvent.RedirectPlayer.class);
    }

    @Test
    @DisplayName("Exact Server")
    void testExactServer() {
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .event(KickedEventBuilder.builder()
                .result(DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer())
            )
            .path(path)
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
            .path(path)
            .event(KickedEventBuilder.builder()
                .result(DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer())
            )
            .build();

        bundle.applyListener();

        Assertions.assertTrue(bundle.getContinuation().resumed());
    }

    // https://github.com/4drian3d/KickRedirect/issues/5
    @Test
    void issue5() {
        RegisteredServer server = new TestRegisteredServer().name("lobby1");
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .path(path)
            .event(KickedEventBuilder.builder()
                .result(DISCONNECT.result(Component.empty()))
                .server(server)
            )
            .debug(true)
            .build();

        KickListener listener = new KickListener(bundle.getPlugin());
        Continuation continuation = new TestContinuation(null);

        EventTask task = listener.executeAsync(bundle.getEvent());

        task.execute(continuation);
        task.execute(continuation);
        
        assertThat(bundle.getPlugin().debugCache().asMap())
            .isNotNull()
            .containsKey(bundle.getPlayer().getUniqueId())
            .extracting(a -> a.get(bundle.getPlayer().getUniqueId()))
            .isNotNull()
            .extracting(DebugInfo::step)
            .isEqualTo(KickStep.REPEATED_ATTEMPT);
    }
}
