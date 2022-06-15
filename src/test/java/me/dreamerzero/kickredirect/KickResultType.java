package me.dreamerzero.kickredirect;

import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.ServerKickResult;

import me.dreamerzero.kickredirect.listener.objects.TestRegisteredServer;
import net.kyori.adventure.text.Component;

public enum KickResultType {
    NOTIFY {
        @Override
        ServerKickResult result(Component component) {
            return KickedFromServerEvent.Notify.create(component);
        }
    },
    DISCONNECT{
        @Override
        ServerKickResult result(Component component) {
            return KickedFromServerEvent.DisconnectPlayer.create(component);
        }
    },
    REDIRECT{
        @Override
        ServerKickResult result(Component component) {
            return KickedFromServerEvent.RedirectPlayer.create(new TestRegisteredServer(), component);
        }
    };

    ServerKickResult result(Component component) {
        return null;
    }
}
