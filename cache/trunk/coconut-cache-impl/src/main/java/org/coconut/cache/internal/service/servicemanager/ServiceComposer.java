package org.coconut.cache.internal.service.servicemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.InternalCache;
import org.coconut.cache.internal.service.event.InternalCacheEventService;
import org.coconut.cache.internal.service.loading.InternalCacheLoadingService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.picocontainer.MutablePicoContainer;
import org.coconut.internal.picocontainer.defaults.DefaultPicoContainer;
import org.coconut.operations.CollectionPredicates;
import org.coconut.operations.Ops.Predicate;

public class ServiceComposer {

    /** The picocontainer used to wire servicers. */
    private final MutablePicoContainer container = new DefaultPicoContainer();

    private ServiceComposer(Cache<?, ?> cache, InternalCache internalCache, String name,
            CacheConfiguration<?, ?> conf, Collection<Class<?>> classes) {
        container.registerComponentInstance(this);
        container.registerComponentInstance(internalCache);
        container.registerComponentInstance(name);
        container.registerComponentInstance(conf.getClock());
        container.registerComponentInstance(cache);
        container.registerComponentInstance(conf);
        for (AbstractCacheServiceConfiguration<?, ?> c : conf.getAllConfigurations()) {
            container.registerComponentInstance(c);
        }
        for (Class cla : removeUnusedServices(conf, classes)) {
            container.registerComponentImplementation(cla);
        }
    }

    public void registerInstance(AbstractCacheServiceManager o) {
        container.registerComponentInstance(AbstractCacheServiceManager.class, o);
    }

    protected Collection<Class<?>> removeUnusedServices(CacheConfiguration<?, ?> conf,
            Collection<Class<?>> classes) {
        ArrayList<Class<?>> c = new ArrayList<Class<?>>(classes);
        if (!conf.management().isEnabled()) {
            CollectionPredicates.retain(c, new Predicate<Class>() {
                public boolean evaluate(Class t) {
                    return !CacheManagementService.class.isAssignableFrom(t);
                }
            });
        }
        if (!conf.event().isEnabled()) {
            CollectionPredicates.retain(c, new Predicate<Class>() {
                public boolean evaluate(Class t) {
                    return !InternalCacheEventService.class.isAssignableFrom(t);
                }
            });
        }
        if (conf.loading().getLoader() == null) {
            CollectionPredicates.retain(c, new Predicate<Class>() {
                public boolean evaluate(Class t) {
                    return !InternalCacheLoadingService.class.isAssignableFrom(t);
                }
            });
        }
        return c;
    }

    public <T> T getInternalService(Class<T> type) {
        T service = (T) container.getComponentInstanceOfType(type);
        return service;
    }

    public <T> List<T> getComponentInstancesOfType(Class<T> type) {
        return container.getComponentInstancesOfType(AbstractCacheLifecycle.class);
    }

    public static ServiceComposer compose(Cache<?, ?> cache, InternalCache internalCache,
            String name, CacheConfiguration<?, ?> conf, Collection<Class<?>> classes) {
        return new ServiceComposer(cache, internalCache, name, conf, classes);
    }
}
