package org.codehaus.cake.internal.container;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.codehaus.cake.container.Container;
import org.codehaus.cake.internal.service.spi.ContainerInfo;
import org.codehaus.cake.service.ServiceManager;

public abstract class AbstractInternalContainer implements Container {

    private final String name;;

    private final RunState runState;

    private final ServiceManager sm;

    public AbstractInternalContainer(Composer composer) {
        if (!composer.hasService(Container.class)) {
            composer.registerInstance(Container.class, this);
        }
        name = composer.get(ContainerInfo.class).getContainerName();
        sm = composer.get(ServiceManager.class);
        runState = composer.get(RunState.class);
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return runState.awaitTermination(timeout, unit);
    }

    /** {@inheritDoc} */
    public Map<Class<?>, Object> getAllServices() {
        lazyStart();
        return sm.getAllServices();
    }

    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public final <T> T getService(Class<T> serviceType) {
        lazyStart();
        return sm.getService(serviceType);
    }

    /** {@inheritDoc} */
    public final boolean hasService(Class<?> type) {
        lazyStart();
        return sm.hasService(type);
    }

    @Override
    public boolean isShutdown() {
        return runState.isAtLeastShutdown();
    }

    @Override
    public boolean isStarted() {
        return runState.isAtLeastRunning();
    }

    @Override
    public boolean isTerminated() {
        return runState.isTerminated();
    }

    protected void lazyStart() {
        runState.isRunningLazyStart(false);
    }

    protected void lazyStartFailIfShutdown() {
        runState.isRunningLazyStart(true);
    }

    @Override
    public void shutdown() {
        runState.shutdown(false);
    }

    @Override
    public void shutdownNow() {
        runState.shutdown(true);
    }
}
