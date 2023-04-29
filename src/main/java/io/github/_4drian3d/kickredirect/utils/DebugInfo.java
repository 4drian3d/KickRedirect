package io.github._4drian3d.kickredirect.utils;

import com.velocitypowered.api.event.player.KickedFromServerEvent;

import io.github._4drian3d.kickredirect.enums.KickStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public record DebugInfo(
        String playerName,
        String serverName,
        boolean duringServerConnect,
        String originalReason,
        String finalResult,
        KickStep step
) {

    public DebugInfo(final KickedFromServerEvent event, String server, KickStep step) {
        this(
            event.getPlayer().getUsername(),
            server,
            event.kickedDuringServerConnect(),
            event.getServerKickReason()
                    .map(PlainTextComponentSerializer.plainText()::serialize)
                    .orElse("NONE"),
            event.getResult().getClass().getTypeName(),
            step
        );
    }

    public TagResolver commonResolver() {
        return TagResolver.builder()
                .resolvers(
                        Placeholder.unparsed("player_name", playerName()),
                        Placeholder.component("during_server_connect", Component.text(duringServerConnect())),
                        Placeholder.unparsed("reason", originalReason()),
                        Placeholder.unparsed("step", step.toString())
                )
                .build();
    }
}
