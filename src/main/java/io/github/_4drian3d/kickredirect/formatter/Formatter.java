package io.github._4drian3d.kickredirect.formatter;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public sealed class Formatter permits MiniPlaceholdersFormatter {
    public Component format(final @NotNull String string) {
        return miniMessage().deserialize(string);
    }

    public Component format(
            final @NotNull String string,
            final @NotNull TagResolver@NotNull... extraResolver
    ) {
        return miniMessage().deserialize(string, extraResolver);
    }

    public Component format(
            final @NotNull String string,
            final @NotNull Audience audience) {
        return format(string);
    }

    public Component format(
            final @NotNull String string,
            final @NotNull Audience audience,
            final @NotNull TagResolver@NotNull... extraResolver
    ) {
        return format(string, extraResolver);
    }
}
