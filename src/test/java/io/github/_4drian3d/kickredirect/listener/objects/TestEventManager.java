package io.github._4drian3d.kickredirect.listener.objects;

import java.util.concurrent.CompletableFuture;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.PostOrder;

public class TestEventManager implements EventManager {

    @Override
    public <E> CompletableFuture<E> fire(E arg0) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void register(Object arg0, Object arg1) {

    }

    @Override
    public <E> void register(Object arg0, Class<E> arg1, PostOrder arg2, EventHandler<E> arg3) {

    }

    @Override
    public <E> void unregister(Object arg0, EventHandler<E> arg1) {

    }

    @Override
    public void unregisterListener(Object arg0, Object arg1) {

    }

    @Override
    public void unregisterListeners(Object arg0) {

    }

}
