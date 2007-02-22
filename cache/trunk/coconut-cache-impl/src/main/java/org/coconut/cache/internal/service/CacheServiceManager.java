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

    private final Map<Class, CacheService> instanciated = new HashMap<Class, CacheService>();

    private List<Class<? extends CacheService>> types = new ArrayList<Class<? extends CacheService>>();

    public CacheServiceManager(CacheConfiguration<K, V> conf) {
        this.conf = conf;
    }

    public void addService(Class<? extends CacheService> impl) {
        types.add(impl);
    }

    public <T> T getService(Class<T> type) {
        CacheService cs = instanciated.get(type);
        if (cs != null) {
            return (T) cs;
        }
        for (CacheService c : instanciated.values()) {
            if (type.isAssignableFrom(c.getClass())) {
                instanciated.put(type, c);
            }
        }
        // for (CacheService<K, V> cs : instantiatedServices) {
        // System.out.println(type.getSimpleName() + " " +
        // cs.getClass().getSimpleName()
        // + " " + type.isAssignableFrom(cs.getClass()));
        // if (type.isAssignableFrom(cs.getClass())) {
        // return (T) cs;
        // }
        // }
        return null;
    }

    public <T> T initialize(Class<T> type) {
        if (type == null) {
            throw new NullPointerException("clazz is null");
        }
        T service = (T) getService(type);
        if (service != null) {
            return service;
        }
        Class<T> clazz = null;
        for (Class c : types) {
            if (type.isAssignableFrom(c)) {
                clazz = c;
            }
        }
        if (clazz == null) {
            throw new IllegalStateException("No defined service " + type + " " + types.toString());
        }
        Constructor<T> c = null;
        // T service = null;
        try {
            c = (Constructor<T>) clazz.getDeclaredConstructor(CacheConfiguration.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Could not create cache instance, no public contructor taking a single CacheConfiguration instance",
                    e);
        }
        try {
            service = c.newInstance(conf);
            instanciated.put(clazz, (CacheService) service);
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
        for (CacheService<K, V> cs : instanciated.values()) {
            cs.start(c, (Map) Collections.EMPTY_MAP);
        }
    }

    public void initializeApm(ManagedGroup root) {
        for (CacheService<K, V> cs : instanciated.values()) {
            if (cs instanceof AbstractCacheService) {
                ((AbstractCacheService<K, V>) cs).addTo(root);
            }
        }
    }
}
