package org.codehaus.cake.internal.container;

import java.util.concurrent.TimeUnit;

import org.codehaus.cake.internal.service.spi.ContainerInfo;

public abstract class RunState {
    protected static final int READY = 0;
    protected static final int STARTING = 1;
    protected static final int RUNNING = 2;
    protected static final int SHUTDOWN = 4;
    protected static final int STOPPING = 8;
    protected static final int TERMINATED = 16;
    private final String containerType;
    private final String containerName;

    public RunState(ContainerInfo info) {
        this.containerType = info.getContainerTypeName();
        this.containerName = info.getContainerName();
    }

    public final boolean isReady() {
        return get() == READY;
    }

    public final boolean isRunning() {
        return get() == RUNNING;
    }

    public final boolean isStarting() {
        return get() == STARTING;
    }

    public final boolean isShutdown() {
        return get() == SHUTDOWN;
    }

    public final boolean isStopping() {
        return get() == STOPPING;
    }

    public final boolean isTerminated() {
        return get() == TERMINATED;
    }

    public final boolean isAtLeastRunning() {
        return get() >= RUNNING;
    }

    public final boolean isAtLeastShutdown() {
        return get() >= SHUTDOWN;
    }

    public final boolean isAtLeastStopping() {
        return get() >= STOPPING;
    }

    public final boolean transitionToStarting() {
        return transitionTo(STARTING);
    }

    public final boolean transitionToRunning() {
        return transitionTo(RUNNING);
    }

    public final boolean transitionToShutdown() {
        return transitionTo(SHUTDOWN);
    }

    public final boolean transitionToStopping() {
        return transitionTo(STOPPING);
    }

    public final boolean transitionToTerminated() {
        return transitionTo(TERMINATED);
    }

    /** {@inheritDoc} */
    public boolean isRunningLazyStart(boolean failIfShutdown) {
        while (!isRunning()) {
            if (isAtLeastShutdown()) {
                checkExceptions();
                if (failIfShutdown) {
                    throw new IllegalStateException(containerType + " [name=" + containerName + "] has been shutdown, cannot invoke method");
                } else {
                    return false;
                }
            }
            tryStart();
        }
        return true;
    }

    protected abstract int get();

    protected abstract boolean transitionTo(int state);

    public abstract boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException;

    public abstract void checkExceptions();

    public abstract void shutdown(boolean shutdownNow);

    public abstract void trySetStartupException(Throwable cause);

    public abstract boolean tryStart();
}
