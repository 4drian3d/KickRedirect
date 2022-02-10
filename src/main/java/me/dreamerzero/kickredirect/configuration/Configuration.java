package me.dreamerzero.kickredirect.configuration;

import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import me.dreamerzero.kickredirect.enums.SendMode;

public class Configuration {
    private Configuration(){}
    private static Config config;
    public static void loadMainConfig(Path path, Logger logger){
        Path configPath = path.resolve("config.conf");
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
            .defaultOptions(opts -> opts
                .shouldCopyDefaults(true)
                .header("KickResult | by 4drian3d\n")
            )
            .path(configPath)
            .build();

        try {
            final CommentedConfigurationNode node = loader.load();
            config = node.get(Config.class);
            node.set(Config.class, config);
            loader.save(node);
        } catch (ConfigurateException exception){
            logger.error("Could not load config.conf file, error: {}", exception.getMessage());
        }
    }

    @ConfigSerializable
    public static class Config {
        @Comment("Sets the list of available servers to forward to the player\nDepending on the configuration of sendMode it will be sent to one server or another")
        private List<String> serversToRedirect = List.of("lobby1", "lobby2");

        @Comment("Sets the kick message to be checked to verify that the kick corresponds to a shutdown or shutdown of the server from which the player is being kicked")
        private String kickMessage = "Kicked from Server";

        @Comment("Sets the sending mode\nAvailable options:\nTO_FIRST | It will send the player to the first available server configured in serversToRedirect\nTO_EMPTIEST_SERVER | Send the player to the emptiest server that is available according to the serversToRedirect configuration\nRANDOM | Send to a random server from the configured servers")
        private SendMode sendMode = SendMode.TO_FIRST;

        public List<String> getServersToRedirect(){
            return this.serversToRedirect;
        }

        public String getKickMessage(){
            return this.kickMessage;
        }

        public SendMode getSendMode(){
            return this.sendMode;
        }
    }

    public static Config getConfig(){
        return config;
    }
}
