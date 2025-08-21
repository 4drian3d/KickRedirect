package io.github._4drian3d.kickredirect.formatter;

import org.jetbrains.annotations.NotNull;

import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public final class MiniPlaceholdersFormatter extends Formatter {

    @Override
    public Component format(final @NotNull String string) {
        return miniMessage().deserialize(
                string,
                MiniPlaceholders.globalPlaceholders()
        );
    }

    @Override
    public Component format(
            final @NotNull String string,
            final @NotNull TagResolver@NotNull... extraResolver
    ) {
        return miniMessage().deserialize(
                string,
                TagResolver.builder()
                        .resolver(MiniPlaceholders.globalPlaceholders())
                        .resolvers(extraResolver)
                        .build()
        );
    }

    @Override
    public Component format(
            final @NotNull String string,
            final @NotNull Audience audience
    ) {
        return miniMessage().deserialize(
                string,
                audience,
                MiniPlaceholders.audienceGlobalPlaceholders()
        );
    }

    @Override
    public Component format(
            final @NotNull String string,
            final @NotNull Audience audience,
            final @NotNull TagResolver@NotNull... extraResolver) {
        return miniMessage().deserialize(
                string,
                audience,
                TagResolver.builder()
                        .resolver(MiniPlaceholders.audienceGlobalPlaceholders())
                        .resolvers(extraResolver)
                        .build()
        );
    }

}
