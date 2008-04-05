package org.codehaus.cake.internal.service.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import jsr166y.forkjoin.ForkJoinExecutor;

import org.codehaus.cake.container.ContainerConfiguration;
import org.codehaus.cake.container.lifecycle.Startable;
import org.codehaus.cake.container.lifecycle.StartableService;
import org.codehaus.cake.service.ServiceRegistrant;
import org.codehaus.cake.service.executor.ExecutorsConfiguration;
import org.codehaus.cake.service.executor.ExecutorsManager;
import org.codehaus.cake.service.executor.ExecutorsService;
import org.codehaus.cake.util.attribute.AttributeMap;
import org.codehaus.cake.util.attribute.Attributes;

public class DefaultExecutorService implements ExecutorsService, StartableService {
    private final ExecutorsManager manager;

    public DefaultExecutorService(ExecutorsConfiguration conf) {
        ExecutorsManager manager = conf.getExecutorManager();
        if (manager == null) {
            manager = new ExecutorsManager();
        }
        this.manager = manager;
    }

    @Override
    public ExecutorService getExecutorService(Object service) {
        return getExecutorService(service, Attributes.EMPTY_ATTRIBUTE_MAP);
    }

    @Override
    public ExecutorService getExecutorService(Object service, AttributeMap attributes) {
        return manager.getExecutorService(service, attributes);
    }

    @Override
    public ForkJoinExecutor getForkJoinExecutor(Object service) {
        return getForkJoinExecutor(service, Attributes.EMPTY_ATTRIBUTE_MAP);
    }

    @Override
    public ForkJoinExecutor getForkJoinExecutor(Object service, AttributeMap attributes) {
        return manager.getForkJoinExecutor(service, attributes);

    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService(Object service) {
        return getScheduledExecutorService(service, Attributes.EMPTY_ATTRIBUTE_MAP);
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService(Object service,
            AttributeMap attributes) {
        return manager.getScheduledExecutorService(service, attributes);
    }

    @Override
    @Startable
    public void start(ContainerConfiguration<?> configuration, ServiceRegistrant serviceRegistrant)
            throws Exception {
        serviceRegistrant.registerService(ExecutorsService.class, this);
    }
}
