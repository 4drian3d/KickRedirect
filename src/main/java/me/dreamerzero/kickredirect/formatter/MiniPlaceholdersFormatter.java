package me.dreamerzero.kickredirect.formatter;

import org.jetbrains.annotations.NotNull;

import me.dreamerzero.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class MiniPlaceholdersFormatter implements Formatter {

    @Override
    public Component format(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(
            string,
            MiniPlaceholders.getGlobalPlaceholders()
        );
    }

    @Override
    public Component format(@NotNull String string, @NotNull TagResolver... extraResolver) {
        return MiniMessage.miniMessage().deserialize(
            string,
            TagResolver.builder()
                .resolver(MiniPlaceholders.getGlobalPlaceholders())
                .resolvers(extraResolver)
                .build()
        );
    }

    @Override
    public Component format(@NotNull String string, Audience audience) {
        return MiniMessage.miniMessage().deserialize(
            string,
            MiniPlaceholders.getAudienceGlobalPlaceholders(audience)
        );
    }

    @Override
    public Component format(@NotNull String string, Audience audience, @NotNull TagResolver... extraResolver) {
        return MiniMessage.miniMessage().deserialize(
            string,
            TagResolver.builder()
                .resolver(MiniPlaceholders.getAudienceGlobalPlaceholders(audience))
                .resolvers(extraResolver)
                .build()
        );
    }

}
