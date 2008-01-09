package org.coconut.cache.internal.service.servicemanager;

import java.util.Collection;
import java.util.List;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.AbstractCache;
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.picocontainer.MutablePicoContainer;
import org.coconut.internal.picocontainer.defaults.DefaultPicoContainer;

public class ServiceComposer {

    /** The picocontainer used to wire servicers. */
    private final MutablePicoContainer container = new DefaultPicoContainer();

    private final AbstractCache c;

    private ServiceComposer(AbstractCache cache) {
        this.c = cache;
    }

    public <T> T getInternalService(Class<T> type) {
        T service = (T) container.getComponentInstanceOfType(type);
        return service;
    }

    AbstractCache getCache() {
        return c;
    }

    public <T> List<T> getComponentInstancesOfType(Class<T> type) {
        return container.getComponentInstancesOfType(AbstractCacheLifecycle.class);
    }

    public static ServiceComposer compose(AbstractCache cache, InternalCache internalCache,
            String name, CacheConfiguration<?, ?> conf, Collection<Class<?>> components,
            Collection<Object> instantiatedComponents) {
        ServiceComposer sc = new ServiceComposer(cache);
        sc.container.registerComponentInstance(sc);
        sc.container.registerComponentInstance(internalCache);
        sc.container.registerComponentInstance(name);
        sc.container.registerComponentInstance(conf.getClock());
        sc.container.registerComponentInstance(conf);
        for (AbstractCacheServiceConfiguration<?, ?> c : conf.getAllConfigurations()) {
            sc.container.registerComponentInstance(c);
        }
        for (Class cla : components) {
            sc.container.registerComponentImplementation(cla);
        }
        for (Object o : instantiatedComponents) {
            sc.container.registerComponentInstance(o);
        }
        return sc;
    }
}
