package io.github._4drian3d.kickredirect.builder;

import java.util.concurrent.CompletableFuture;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.builder.AbstractBuilder;
import org.jetbrains.annotations.NotNull;

public class RequestBuilder implements AbstractBuilder<ConnectionRequestBuilder> {
    private RegisteredServer server;
    private boolean indicationResult;

    public static RequestBuilder builder() {
        return new RequestBuilder();
    }

    public RequestBuilder server(RegisteredServer server) {
        this.server = server;
        return this;
    }

    public RequestBuilder result(boolean result) {
        this.indicationResult = result;
        return this;
    }

    @Override
    public @NotNull ConnectionRequestBuilder build() {
        return new ConnectionRequestBuilder() {

            @Override
            public RegisteredServer getServer() {
                return server;
            }

            @Override
            public CompletableFuture<Result> connect() {
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Boolean> connectWithIndication() {
                return CompletableFuture.completedFuture(indicationResult);
            }

            @Override
            public void fireAndForget() {
            }
            
        };
    }
}
