package io.github._4drian3d.kickredirect.listener;

import com.velocitypowered.api.event.player.KickedFromServerEvent;
import io.github._4drian3d.kickredirect.builder.KickedEventBuilder;
import io.github._4drian3d.kickredirect.objects.TestPlayer;
import io.github._4drian3d.kickredirect.objects.TestRegisteredServer;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import static io.github._4drian3d.kickredirect.enums.KickResultType.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListenerTest {
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
}
