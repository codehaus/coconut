package org.codehaus.cake.internal.container.runstate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.codehaus.cake.internal.container.RunState;
import org.codehaus.cake.internal.service.spi.ContainerInfo;
public final class SynchronizedRunState extends RunState {
    // Order among values matters
    private final AtomicReference<Throwable> startupException = new AtomicReference<Throwable>();
    /** CountDownLatch used for signalling termination. */
    private final CountDownLatch terminationLatch = new CountDownLatch(1);
    final AtomicInteger state = new AtomicInteger();
    public SynchronizedRunState(ContainerInfo info) {
        super(info);
    }
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return terminationLatch.await(timeout, unit);
    }

    public void trySetStartupException(Throwable cause) {
        startupException.compareAndSet(null, cause);
    }

    public void checkExceptions() {
        Throwable re = startupException.get();
        if (re != null) {
            throw new IllegalStateException("Cache failed to start previously", re);
        }
    }

    public boolean transitionTo(int state) {
        for (;;) {
            int s = get();
            if (s >= state)
                return false;
            if (this.state.compareAndSet(s, state)) {
                if (state == TERMINATED) {
                    terminationLatch.countDown();
                }
                return true;
            }
        }
    }

    public boolean tryStart() {
        Object mutex = this;// cache
        synchronized (mutex) {
            if (isStarting()) {
                throw new IllegalStateException(
                        "Cannot invoke this method from CacheLifecycle.start(Map services), should be invoked from CacheLifecycle.started(Cache c)");
            }
            return transitionToStarting();
        }
    }

    public void shutdown(boolean shutdownNow) {
        Object mutex = this;// cache
        synchronized (mutex) {
            if (!isAtLeastShutdown()) {
                System.out.println("shutdown");
                // throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    protected int get() {
        return state.get();
    }
}
