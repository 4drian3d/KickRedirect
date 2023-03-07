package io.github._4drian3d.kickredirect.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings({"CanBeFinal", "FieldMayBeFinal"})
public final class Messages implements Section {
    @Comment("Main Command Message displayed on '/kickredirect' execution")
    private String mainCommandMessage = "<gradient:red:#fff494>[KickRedirect]</gradient> <gray>|</gray> <white>by <gradient:#0BAB64:#3BB78F>4drian3d";
    @Comment("Sets the message to send if no server is found to which to send the player")
    private String kickMessage = "<gradient:#FF0000:dark_red>You could not be sent to a backup server";

    @Comment("Message to send in player correctly redirect")
    private String redirectMessage = "";

    @Comment("Error message to be sent in case no server is available to send to player")
    private String noServersFoundToRedirect = "<gradient:red:#fff494>[KickRedirect]</gradient> <gradient:#b82e00:#ff4000>No servers were found to redirect the player to. <gray>SendMode: <sendmode>";

    @Comment("\nReload Messages")
    private Reload reloadMessages = new Reload();

    @Comment("\nDebug Messages")
    private Debug debugMessages = new Debug();

    public String mainCommandMessage() {
        return this.mainCommandMessage;
    }

    public String kickMessage(){
        return this.kickMessage;
    }

    public String redirectMessage() {
        return this.redirectMessage;
    }

    public String noServersFoundToRedirect() {
        return this.noServersFoundToRedirect;
    }

    public Reload reload() {
        return this.reloadMessages;
    }

    public Debug debug() {
        return this.debugMessages;
    }

    @SuppressWarnings("FieldMayBeFinal")
    @ConfigSerializable
    public static class Reload {
        @Comment("Message to send in plugin reload start")
        private String reloadingMessage = "<gradient:red:#fff494>[KickRedirect]</gradient> <gradient:#78edff:#699dff>Reloading Configuration...";

        @Comment("Message to send in plugin reload")
        private String reloadMessage = "<gradient:red:#fff494>[KickRedirect]</gradient> <gradient:#78edff:#699dff>Correctly Reloaded Configuration in <time> ms";

        @Comment("Message to send in failed plugin reload")
        private String failedReload = "<gradient:red:#fff494>[KickRedirect]</gradient> <gradient:#78edff:#699dff>An error ocurred in configuration reload, check your console logs";

        public String reloadingMessage() {
            return this.reloadingMessage;
        }

        public String reloadMessage() {
            return this.reloadMessage;
        }

        public String failedReload() {
            return this.failedReload;
        }
    }

    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
    @ConfigSerializable
    public static class Debug {
        private String redirectResult = "----- INITIAL DEBUG ----- "+
                "<newline> Player: <player_name>"+
                "<newline> Server: <server_name>"+
                "<newline> Kick Reason: <reason>"+
                "<newline> Kicked in Server Connect: <during_server_connect>"+
                "<newline> Calculated result: <result>"+
                "<newline> Step: <step>";

        private String finalResult = "----- FINAL DEBUG -----"+
                "<newline> Player: <player_name>"+
                "<newline> Server: <server_name>"+
                "<newline> Kick Reason: <reason>"+
                "<newline> Kicked in Server Connect: <during_server_connect>"+
                "<newline> Final Event Result: <result>"+
                "<newline> Step: <step>";

        public String redirectResult() {
            return this.redirectResult;
        }

        public String finalResult() {
            return this.finalResult;
        }
    }
}
