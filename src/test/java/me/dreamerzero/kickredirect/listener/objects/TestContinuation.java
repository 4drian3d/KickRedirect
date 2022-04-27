package me.dreamerzero.kickredirect.listener.objects;

import com.velocitypowered.api.event.Continuation;

public class TestContinuation implements Continuation {
    private boolean resumed;

    @Override
    public void resume() {
        this.resumed = true;

    }

    @Override
    public void resumeWithException(Throwable arg0) {

    }

    public boolean resumed() {
        return this.resumed;
    }
}
