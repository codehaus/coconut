package org.codehaus.cake.internal.service;

import java.util.Map;

import org.codehaus.cake.service.ServiceRegistrant;

public class DefaultServiceRegistrant implements ServiceRegistrant {
    private DefaultServiceManager manager;

    public DefaultServiceRegistrant(DefaultServiceManager manager) {
        this.manager = manager;
    }

    @Override
    public <T> void registerService(Class<T> key, T service) {
        manager.services.put(key, service);
    
    }

    /** {@inheritDoc} */
    public Map<Class<?>, Object> getAllServices() {
        throw new UnsupportedOperationException("Can only register services");
    }

    /** {@inheritDoc} */
    public final <T> T getService(Class<T> serviceType) {
        throw new UnsupportedOperationException("Can only register services");
    }

    /** {@inheritDoc} */
    public final boolean hasService(Class<?> type) {
        throw new UnsupportedOperationException("Can only register services");
    }
}
