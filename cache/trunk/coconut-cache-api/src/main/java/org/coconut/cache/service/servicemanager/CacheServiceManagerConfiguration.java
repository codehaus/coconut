/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.coconut.cache.Cache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheServiceManagerConfiguration {

    private final Set<CacheService> services = new HashSet<CacheService>();

    private final Map<Class<?>, Object> attached = new HashMap<Class<?>, Object>();

// private final Set<EventProcessor<? super Cache<?, ?>>> startedNotifier = new
// HashSet<EventProcessor<? super Cache<?, ?>>>();

    // private final Set<EventProcessor<? super Cache<?, ?>>> terminatedNotifier = new
    // HashSet<EventProcessor<? super Cache<?, ?>>>();

    /**
     * Attaches the specified instance to the service map of the cache. This object can
     * then later be retrived by calling {@link Cache#getService(Class)}.
     * 
     * <pre>
     * CacheServiceManagerConfiguration csmc;
     * csmc.attach(String.class, &quot;fooboo&quot;);
     * 
     * ...later..
     * Cache&lt;?,?&gt; c;
     * assert &quot;fooboo&quot; = c.getService(String.class);
     * </pre>
     * 
     * @param key
     *            the key to attach the specified instance to
     * @param instance
     *            the instance to map the specified key to
     * @return
     * @throws IllegalArgumentException
     *             If an instance has already been registered with the specified key
     * @throws NullPointerException
     *             if the specified key or instance is null
     */
    public CacheServiceManagerConfiguration attach(Class<?> key, Object instance) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (instance == null) {
            throw new NullPointerException("instance is null");
        } else if (attached.containsKey(key)) {
            throw new IllegalArgumentException(
                    "An instance for the specified key is already registered, key=" + key);
        }
        attached.put(key, instance);
        return this;
    }

    public CacheServiceManagerConfiguration addService(CacheService service) {
        if (service == null) {
            throw new NullPointerException("service is null");
        }
        services.add(service);
        return this;
    }

// public CacheServiceManagerConfiguration addStartNotifier(
// EventProcessor<? super Cache<?, ?>> hook) {
// startedNotifier.add(hook);
// return this;
// }
//
// public CacheServiceManagerConfiguration addTerminationNotifier(
// EventProcessor<? super Cache<?, ?>> hook) {
// terminatedNotifier.add(hook);
// return this;
// }
}
