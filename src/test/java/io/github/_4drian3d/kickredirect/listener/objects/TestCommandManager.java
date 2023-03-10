package io.github._4drian3d.kickredirect.listener.objects;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.tree.CommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandMeta.Builder;
import com.velocitypowered.api.command.CommandSource;

import org.checkerframework.checker.nullness.qual.Nullable;

public enum TestCommandManager implements CommandManager {
    INSTANCE;

    @Override
    public CompletableFuture<Boolean> executeAsync(CommandSource arg0, String arg1) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> executeImmediatelyAsync(CommandSource arg0, String arg1) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Collection<String> getAliases() {
        return null;
    }

    @Override
    public @Nullable CommandMeta getCommandMeta(String arg0) {
        return null;
    }

    @Override
    public boolean hasCommand(String arg0) {
        return false;
    }

    @Override
    public Builder metaBuilder(String arg0) {
        return new Builder() {

            @Override
            public Builder aliases(String... arg0) {
                return this;
            }

            @Override
            public CommandMeta build() {
                return new CommandMeta() {

                    @Override
                    public Collection<String> getAliases() {
                        return null;
                    }

                    @Override
                    public Collection<CommandNode<CommandSource>> getHints() {
                        return null;
                    }

                    @Override
                    public @Nullable Object getPlugin() {
                        return null;
                    }

                };
            }

            @Override
            public Builder hint(CommandNode<CommandSource> arg0) {
                return this;
            }

            @Override
            public Builder plugin(Object arg0) {
                return this;
            }
            
        };
    }

    @Override
    public Builder metaBuilder(BrigadierCommand arg0) {
        return metaBuilder(arg0.getNode().getLiteral());
    }

    @Override
    public void register(BrigadierCommand arg0) {

    }

    @Override
    public void register(CommandMeta arg0, Command arg1) {

    }

    @Override
    public void unregister(String arg0) {

    }

    @Override
    public void unregister(CommandMeta arg0) {

    }

}
