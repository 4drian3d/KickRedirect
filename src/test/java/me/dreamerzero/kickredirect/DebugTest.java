package me.dreamerzero.kickredirect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;

import com.velocitypowered.api.proxy.Player;

import me.dreamerzero.kickredirect.listener.objects.TestContinuation;
import me.dreamerzero.kickredirect.listener.objects.TestPlayer;
import me.dreamerzero.kickredirect.listener.objects.TestRegisteredServer;
import me.dreamerzero.kickredirect.utils.DebugInfo;
import net.kyori.adventure.text.Component;

class DebugTest {
    @Test
    void testDebug() {
        Player player = new TestPlayer("4drian3d", true);
        EventBundle bundle = EventBundle.builder()
            .player(player)
            .event(EventBuilder.builder()
                .duringServerConnect(false)
                .result(KickResultType.DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer())
            )
            .build();
        
        bundle.applyListener();
        assertNotNull(bundle.getPlugin().debugCache().getIfPresent(player.getUniqueId()));
    }

    @Test
    void testEquality() {
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .event(EventBuilder.builder()
                .reason(null)
                .result(KickResultType.DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer())
            )
            .build();

        bundle.applyListener();
        DebugInfo debuginfo = bundle.getPlugin()
            .debugCache().getIfPresent(bundle.getPlayer().getUniqueId());

        assertEquals("4drian3d", debuginfo.playerName());
    }

    @Test
    void testDebugListener() {
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .event(EventBuilder.builder()
                .duringServerConnect(false)
                .result(KickResultType.DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer().name("TEstServer"))
            )
            .build();

        bundle.getPlugin().config().get().debug(true);
        
        bundle.applyListener();
        CountDownLatch latch = new CountDownLatch(1);
        bundle.applyDebug().execute(new TestContinuation() {
            @Override
            public void resume() {
                super.resume();
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (Exception e) {
            fail("Exception on CountDown latch");
        }
        

        assertEquals(0, latch.getCount());
    }
}
