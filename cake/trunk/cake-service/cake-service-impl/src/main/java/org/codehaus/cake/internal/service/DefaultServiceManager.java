package org.codehaus.cake.internal.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.cake.service.ServiceManager;

public class DefaultServiceManager implements ServiceManager {
    final Map<Class<?>, Object> services = new ConcurrentHashMap<Class<?>, Object>();

    /** {@inheritDoc} */
    public Map<Class<?>, Object> getAllServices() {
        return new HashMap<Class<?>, Object>(services);
    }

    /** {@inheritDoc} */
    public final <T> T getService(Class<T> serviceType) {
        if (serviceType == null) {
            throw new NullPointerException("serviceType is null");
        }
        T t = (T) getAllServices().get(serviceType);
        if (t == null) {
            throw new UnsupportedOperationException("Unknown service " + serviceType);
        }
        return t;
    }

    /** {@inheritDoc} */
    public final boolean hasService(Class<?> type) {
        return services.containsKey(type);
    }
}
