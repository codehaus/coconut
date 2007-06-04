/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.coconut.cache.Cache;
import org.coconut.core.EventProcessor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheServiceManagerConfiguration {

    private final Set<CacheService> services = new HashSet<CacheService>();

    private final Map<Class, Object> attached = new HashMap<Class, Object>();

    private final Set<EventProcessor<? super Cache<?, ?>>> startedNotifier = new HashSet<EventProcessor<? super Cache<?, ?>>>();

    private final Set<EventProcessor<? super Cache<?, ?>>> terminatedNotifier = new HashSet<EventProcessor<? super Cache<?, ?>>>();

    public CacheServiceManagerConfiguration attach(Class<?> key, Object instance) {
        if (attached.containsKey(key)) {
            throw new IllegalArgumentException(
                    "An instance for the specified key is already registered, key=" + key);
        }
        attached.put(key, instance);
        return this;
    }

    public CacheServiceManagerConfiguration addService(CacheService lifecycle) {
        services.add(lifecycle);
        return this;
    }

//    public CacheServiceManagerConfiguration addStartNotifier(
//            EventProcessor<? super Cache<?, ?>> hook) {
//        startedNotifier.add(hook);
//        return this;
//    }
//
//    public CacheServiceManagerConfiguration addTerminationNotifier(
//            EventProcessor<? super Cache<?, ?>> hook) {
//        terminatedNotifier.add(hook);
//        return this;
//    }
}
