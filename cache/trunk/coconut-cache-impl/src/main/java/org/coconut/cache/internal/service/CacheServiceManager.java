/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheServiceManager<K, V> {

    private final CacheConfiguration<K, V> conf;

    private final Map<Class<? extends CacheService>, Class<? extends CacheService>> m = new HashMap<Class<? extends CacheService>, Class<? extends CacheService>>();

    private List<CacheService> instantiatedServices = new ArrayList<CacheService>();

    public CacheServiceManager(CacheConfiguration<K, V> conf) {
        this.conf = conf;
    }

    public void setService(Class<? extends CacheService> impl) {
        m.put(impl, impl);
    }

    public void setService(Class<? extends CacheService> type,
            Class<? extends CacheService> impl) {
        m.put(type, impl);
    }

    private <T extends CacheService> Class<T> getService(
            Class<? extends CacheService> type) {

        Class<? extends CacheService> c = m.get(type);
        if (c == null) {
            throw new IllegalArgumentException("No such service registered for type = "
                    + type);
        }
        return (Class) c;
    }

    public <T extends CacheService<K, V>> T create(Class<? extends CacheService> type) {
        if (type == null) {
            throw new NullPointerException("clazz is null");
        }
        Class<T> clazz = getService(type);
        Constructor<T> c = null;
        T service = null;
        try {
            c = (Constructor<T>) clazz.getDeclaredConstructor(CacheConfiguration.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Could not create cache instance, no public contructor taking a single CacheConfiguration instance",
                    e);
        }
        try {
            service = c.newInstance(conf);
            instantiatedServices.add(service);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "Could not create cache instance, specified clazz " + clazz
                            + ") is an interface or abstract class", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not create instance of " + clazz, e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Constructor threw exception", e);
        }
        return service;
    }

    public void initializeAll(AbstractCache<K, V> c) {
        for (CacheService<K, V> cs : instantiatedServices) {
            cs.start(c, (Map) Collections.EMPTY_MAP);
        }
    }

    public void initializeApm(ManagedGroup root) {
        for (CacheService<K, V> cs : instantiatedServices) {
            if (cs instanceof AbstractCacheService) {
                ((AbstractCacheService<K, V>) cs).addTo(root);
            }
        }
    }
}
