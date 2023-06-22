package io.github._4drian3d.kickredirect.modules;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import io.github._4drian3d.velocityhexlogger.HexLogger;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@Singleton
public final class KickRedirectSource implements CommandSource {
    @Inject
    private HexLogger hexLogger;

    @Override
    public Tristate getPermissionValue(final String permission) {
        return Tristate.TRUE;
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        hexLogger.info(message);
    }
}
