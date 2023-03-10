package io.github._4drian3d.kickredirect.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

import io.github._4drian3d.kickredirect.EventBundle;
import io.github._4drian3d.kickredirect.listener.objects.TestContinuation;
import io.github._4drian3d.kickredirect.listener.objects.TestRegisteredServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github._4drian3d.kickredirect.KickedEventBuilder;
import io.github._4drian3d.kickredirect.enums.KickResultType;
import io.github._4drian3d.kickredirect.enums.KickStep;
import io.github._4drian3d.kickredirect.listener.objects.TestPlayer;
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
        bundle.applyDebug().execute(new TestContinuation(latch::countDown));

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
