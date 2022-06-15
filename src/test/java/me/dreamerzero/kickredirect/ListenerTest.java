package me.dreamerzero.kickredirect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.ServerKickResult;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import me.dreamerzero.kickredirect.listener.KickListener;
import me.dreamerzero.kickredirect.listener.objects.TestPlayer;
import me.dreamerzero.kickredirect.listener.objects.TestRegisteredServer;
import me.dreamerzero.kickredirect.utils.ServerUtils;
import net.kyori.adventure.text.Component;

public class ListenerTest {

    @Test
    void testServerUtils() {
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .event(EventBuilder.builder()
                .duringServerConnect(false)
                .result(KickResultType.DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer())
            )
            .build();

        assertEquals(
            bundle.getProxyServer().getServer("lobby1").orElse(null),
            ServerUtils.getConfigServer(bundle.getPlugin()));
    }

    @Test
    void testCorrectRedirect() {
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .event(EventBuilder.builder()
                .duringServerConnect(false)
                .result(KickResultType.DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer())
            )
            .build();

        applyListener(bundle);

        assertInstanceOf(KickedFromServerEvent.RedirectPlayer.class, bundle.getEvent().getResult());
    }

    @Test
    void testexactServer() {
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .event(EventBuilder.builder()
                .duringServerConnect(false)
                .result(KickResultType.DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer())
            )
            .build();

        applyListener(bundle);

        assertEquals(
            bundle.getProxyServer().getServer("lobby1").orElse(null),
            ((KickedFromServerEvent.RedirectPlayer)bundle.getEvent().getResult()).getServer());
    }

    @Test
    void testContinuation() {
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .event(EventBuilder.builder()
                .duringServerConnect(false)
                .result(KickResultType.DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer())
            )
            .build();

        applyListener(bundle);

        assertTrue(bundle.getContinuation().resumed());
    }

    @Test
    void testDebug() {
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .event(EventBuilder.builder()
                .duringServerConnect(false)
                .result(KickResultType.DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer())
            )
            .build();
        
        applyListener(bundle);

        assertNotNull(bundle.getPlugin().debugCache().getIfPresent(bundle.getPlayer().getUniqueId()));
    }

    enum KickResultType {
        NOTIFY {
            @Override
            ServerKickResult result(Component component) {
                return KickedFromServerEvent.Notify.create(component);
            }
        },
        DISCONNECT{
            @Override
            ServerKickResult result(Component component) {
                return KickedFromServerEvent.DisconnectPlayer.create(component);
            }
        },
        REDIRECT{
            @Override
            ServerKickResult result(Component component) {
                return KickedFromServerEvent.RedirectPlayer.create(new TestRegisteredServer(), component);
            }
        };

        ServerKickResult result(Component component) {
            return null;
        }
    }

    private void applyListener(EventBundle bundle) {
        final KickListener listener = new KickListener(bundle.getPlugin());
        listener.onKickFromServer(bundle.getEvent(), bundle.getContinuation());
    }
}
