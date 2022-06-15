package me.dreamerzero.kickredirect;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import me.dreamerzero.kickredirect.listener.objects.TestPlayer;
import me.dreamerzero.kickredirect.listener.objects.TestRegisteredServer;
import net.kyori.adventure.text.Component;

class DebugTest {
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
        
        bundle.applyListener();

        assertNotNull(bundle.getPlugin().debugCache().getIfPresent(bundle.getPlayer().getUniqueId()));
    }
}
