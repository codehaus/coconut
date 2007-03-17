/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service;

import java.util.Map;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheService<K, V> implements InternalCacheService<K, V> {

    private volatile AbstractCache<K, V> cache;

    private final CacheConfiguration<K, V> conf;

    private volatile Map<String, Object> properties;

    private final InternalCacheServiceManager manager;

    public AbstractCacheService(InternalCacheServiceManager manager,
            CacheConfiguration<K, V> conf) {
        this.conf = conf;
        this.manager = manager;

    }

    protected void checkStarted() {
        manager.checkStarted();
    }

    public void addTo(ManagedGroup dg) {

    }

    public final void start(AbstractCache<K, V> cache, Map<String, Object> properties) {
        this.cache = cache;
        this.properties = properties;
        try {
            doStart(cache, properties);
        } catch (Exception e) {
            throw new CacheException("Could not start cache", e);
        }
    }

    protected void doStart(AbstractCache<K, V> cache, Map<String, Object> properties)
            throws Exception {

    }

    protected final CacheConfiguration<K, V> getConf() {
        return conf;
    }

    protected Object getMutex() {
        return cache;
    }

    /**
     * @see org.coconut.cache.internal.service.InternalCacheService#shutdown(java.lang.Runnable)
     */
    public void shutdown(Runnable shutdownCallback) throws Exception {
        shutdownCallback.run();
    }
}
