package io.github._4drian3d.kickredirect.configuration;

import org.jetbrains.annotations.VisibleForTesting;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import io.github._4drian3d.kickredirect.enums.CheckMode;
import io.github._4drian3d.kickredirect.enums.SendMode;

import java.util.List;

@ConfigSerializable
@SuppressWarnings({"CanBeFinal", "FieldMayBeFinal"})
public final class Configuration implements Section {
    @Comment("Sets the list of available servers to forward to the player\nDepending on the configuration of sendMode it will be sent to one server or another")
    private List<String> serversToRedirect = List.of("lobby1", "lobby2");

    @Comment("Redirect the player if the expulsion message is null or empty")
    private boolean redirectOnNullMessage = true;

    @Comment("Sets whether to perform whitelist or blacklist detection\nAvailable options:\nWHITELIST: It will check if the expulsion string contains any of this strings\nBLACKLIST: It will check if the expulsion string not contains any of this strings")
    private CheckMode checkMode = CheckMode.WHITELIST;

    @Comment("Set the messages to be checked by blacklist or whitelist in case they are present in the expulsion message")
    private List<String> messagesToCheck = List.of("kicked from server", "shutdown");

    @Comment("Sets the sending mode\nAvailable options:\nTO_FIRST | It will send the player to the first available server configured in serversToRedirect\nTO_EMPTIEST_SERVER | Send the player to the emptiest server that is available according to the serversToRedirect configuration\nRANDOM | Send to a random server from the configured servers")
    private SendMode sendMode = SendMode.TO_FIRST;

    @Comment("Sets the limit of times the random server will be calculated to send in case the sending mode is RANDOM")
    private int randomAttempts = 5;

    @Comment("Enables debug mode")
    private boolean debug = false;

    public List<String> getServersToRedirect(){
        return this.serversToRedirect;
    }

    public CheckMode checkMode() {
        return this.checkMode;
    }

    public boolean redirectOnNullMessage() {
        return this.redirectOnNullMessage;
    }

    public List<String> getMessagesToCheck(){
        return this.messagesToCheck;
    }

    public SendMode getSendMode(){
        return this.sendMode;
    }

    public int getRandomAttempts(){
        return this.randomAttempts;
    }

    public boolean debug() {
        return this.debug;
    }

    @VisibleForTesting
    public void debug(boolean bool) {
        this.debug = bool;
    }

}
