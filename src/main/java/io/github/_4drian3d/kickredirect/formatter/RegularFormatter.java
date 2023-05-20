package io.github._4drian3d.kickredirect.formatter;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public final class RegularFormatter implements Formatter {

    @Override
    public Component format(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }

    @Override
    public Component format(@NotNull String string, @NotNull TagResolver... extraResolver) {
        return MiniMessage.miniMessage().deserialize(string, extraResolver);
    }

    @Override
    public Component format(@NotNull String string, Audience audience) {
        return format(string);
    }

    @Override
    public Component format(@NotNull String string, Audience audience, @NotNull TagResolver... extraResolver) {
        return format(string, extraResolver);
    }
}
