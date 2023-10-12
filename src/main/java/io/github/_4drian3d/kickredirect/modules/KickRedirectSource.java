package io.github._4drian3d.kickredirect.modules;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

@Singleton
public final class KickRedirectSource implements CommandSource {
    @Inject
    private ComponentLogger logger;

    @Override
    public Tristate getPermissionValue(final String permission) {
        return Tristate.TRUE;
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        logger.info(message);
    }
}
