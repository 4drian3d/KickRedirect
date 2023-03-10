package io.github._4drian3d.kickredirect.listener.objects;

import com.velocitypowered.api.event.Continuation;

import static java.util.Objects.requireNonNullElse;

public class TestContinuation implements Continuation {
    private boolean resumed = false;
    private final Runnable onResume;

    public TestContinuation(Runnable onResume) {
        this.onResume = requireNonNullElse(onResume, () -> {});
    }

    @Override
    public void resume() {
        this.resumed = true;
        onResume.run();
    }

    @Override
    public void resumeWithException(Throwable arg0) {
        this.resumed = true;
        onResume.run();
    }

    public boolean resumed() {
        return this.resumed;
    }
}
