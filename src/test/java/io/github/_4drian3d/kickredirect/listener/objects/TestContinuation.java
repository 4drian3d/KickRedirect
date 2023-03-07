package io.github._4drian3d.kickredirect.listener.objects;

import com.velocitypowered.api.event.Continuation;

public class TestContinuation implements Continuation {
    private boolean resumed = false;

    @Override
    public void resume() {
        this.resumed = true;
    }

    @Override
    public void resumeWithException(Throwable arg0) {
        this.resumed = true;
    }

    public boolean resumed() {
        return this.resumed;
    }
}
