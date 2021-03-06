package me.dreamerzero.kickredirect.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import me.dreamerzero.kickredirect.KickedEventBuilder;
import me.dreamerzero.kickredirect.EventBundle;
import me.dreamerzero.kickredirect.enums.KickResultType;
import me.dreamerzero.kickredirect.enums.KickStep;
import me.dreamerzero.kickredirect.listener.objects.TestContinuation;
import me.dreamerzero.kickredirect.listener.objects.TestPlayer;
import me.dreamerzero.kickredirect.listener.objects.TestRegisteredServer;
import net.kyori.adventure.text.Component;

class DebugTest {
    @TempDir Path path;

    @Test
    void testKickListenerDebug() {
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .event(KickedEventBuilder.builder()
                .duringServerConnect(false)
                .result(KickResultType.DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer().name("TEstServer"))
            )
            .debug(true)
            .path(path)
            .build();

        KickListener listener = new KickListener(bundle.getPlugin());
        listener.cache(bundle.getEvent(), "TEstServer", KickStep.DISALLOWED_REASON);

        var cache = bundle.getPlugin().debugCache();
        assertNotNull(cache);

        assertThat(cache.asMap())
            .containsKey(bundle.getPlayer().getUniqueId());
    }


    @Test
    void testDebugListener() {
        EventBundle bundle = EventBundle.builder()
            .player(new TestPlayer("4drian3d", true))
            .event(KickedEventBuilder.builder()
                .duringServerConnect(false)
                .result(KickResultType.DISCONNECT.result(Component.text("")))
                .server(new TestRegisteredServer().name("TEstServer"))
            )
            .debug(true)
            .path(path)
            .build();
        
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
        var cache = bundle.getPlugin().debugCache();
        assertNotNull(cache.getIfPresent(bundle.getPlayer().getUniqueId()));
    }
}
