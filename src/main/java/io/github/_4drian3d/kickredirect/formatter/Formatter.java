package io.github._4drian3d.kickredirect.formatter;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public interface Formatter {
    Component format(
        final @NotNull String string
    );

    Component format(
        final @NotNull String string,
        final @NotNull TagResolver... extraResolver
    );

    Component format(
        final @NotNull String string,
        final Audience audience
    );

    Component format(
        final @NotNull String string,
        final Audience audience,
        final @NotNull TagResolver... extraResolver
    );
}
