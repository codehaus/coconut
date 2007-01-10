/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private final List<Class<? extends CacheService>> l = new ArrayList<Class<? extends CacheService>>();

    private List<CacheService> services = new ArrayList<CacheService>();

    public CacheServiceManager(CacheConfiguration<K, V> conf) {
        this.conf = conf;
    }

    public void addDefault(Class<? extends CacheService>... services) {
        l.addAll(Arrays.asList(services));
    }

    private <T extends CacheService> Class<T> getService(Class<T> type) {
        for (Class<? extends CacheService> cs : l) {
            if (type.isAssignableFrom(cs)) {
                return (Class<T>) cs;
            }
        }
        throw new IllegalArgumentException();
    }

    public <T extends CacheService<K, V>> T create(Class<? extends CacheService> type) {
        if (type == null) {
            throw new NullPointerException("clazz is null");
        }
        Class<T> clazz = getService((Class) type);
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
            services.add(service);
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
        for (CacheService<K, V> cs : services) {
            cs.start(c, (Map) Collections.EMPTY_MAP);
        }
    }

    public void initializeApm(ManagedGroup root) {
        for (CacheService<K, V> cs : services) {
            if (cs instanceof AbstractCacheService) {
                ((AbstractCacheService<K, V>) cs).addTo(root);
            }
        }
    }
}
