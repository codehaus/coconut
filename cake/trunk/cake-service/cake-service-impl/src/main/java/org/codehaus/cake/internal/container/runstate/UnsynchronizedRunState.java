package org.codehaus.cake.internal.container.runstate;

import java.util.concurrent.TimeUnit;

import org.codehaus.cake.internal.container.RunState;
import org.codehaus.cake.internal.container.phases.StartPhase;
import org.codehaus.cake.internal.container.phases.StartedPhase;
import org.codehaus.cake.internal.container.phases.StopPhase;
import org.codehaus.cake.internal.service.spi.ContainerInfo;

public class UnsynchronizedRunState extends RunState {
    // private final SynchronizedRunState runState;

    private StartPhase startPhase;
    private StartedPhase startedPhase;
    private StopPhase shutdownPhase;
    int state;
    private Throwable startupException;

    public UnsynchronizedRunState(ContainerInfo info, StartedPhase started, StartPhase startPhase,
            StopPhase stopPhase) {
        super(info);
        this.startPhase = startPhase;
        this.shutdownPhase = stopPhase;
        this.startedPhase = started;
    }

    public boolean tryStart() {
        if (isStarting()) {
            throw new IllegalStateException(
                    "Cannot invoke this method from CacheLifecycle.start(Map services), should be invoked from CacheLifecycle.started(Cache c)");
        }
        return transitionToStarting();
    }

    public boolean transitionTo(int state) {
        int s = get();
        if (s >= state)
            return false;
        this.state = state;
        if (state == STARTING) {
            startPhase.start(this);
        } else if (state == RUNNING) {
            //startedPhase.run(this);
        }
        return true;
    }

    public void shutdown(boolean shutdownNow) {
        if (!isAtLeastShutdown()) {
            shutdownPhase.runPhaseSilent(this);
            transitionToShutdown();
            transitionToTerminated();
        }
    }

    @Override
    public void trySetStartupException(Throwable cause) {
        if (startupException == null) {
            startupException = cause;
        }
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return isTerminated();
    }

    @Override
    public void checkExceptions() {
        Throwable re = startupException;
        if (re != null) {
            throw new IllegalStateException("Cache failed to start previously", re);
        }
    }

    @Override
    protected int get() {
        return state;
    }
}
