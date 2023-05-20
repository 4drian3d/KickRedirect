package io.github._4drian3d.kickredirect.enums;

import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.ServerKickResult;

import io.github._4drian3d.kickredirect.objects.TestRegisteredServer;
import net.kyori.adventure.text.Component;

public enum KickResultType {
    NOTIFY {
        @Override
        public ServerKickResult result(Component component) {
            return KickedFromServerEvent.Notify.create(component);
        }
    },
    DISCONNECT{
        @Override
        public ServerKickResult result(Component component) {
            return KickedFromServerEvent.DisconnectPlayer.create(component);
        }
    },
    REDIRECT{
        @Override
        public ServerKickResult result(Component component) {
            return KickedFromServerEvent.RedirectPlayer.create(new TestRegisteredServer(), component);
        }
    };

    public abstract ServerKickResult result(Component component);
}
