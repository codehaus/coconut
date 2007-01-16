/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi.service;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheService<K, V> extends AbstractService implements
        CacheService<K, V> {

    /**
     * An AbstractCacheService can be in one of the following states:
     * <ul>
     * <li>{@link #NEW}<br>
     * A service that has not yet been started is in this state. </li>
     * <li>{@link #RUNNING}<br>
     * A service actively processing events is in this state. </li>
     * <li>{@link #SHUTDOWN}<br>
     * A thread that is blocked waiting for a monitor lock is in this state.
     * </li>
     * <li>{@link #STOP}<br>
     * A thread that is blocked waiting for a monitor lock is in this state.
     * </li>
     * <li>{@link #TERMINATED}<br>
     * A thread that is waiting indefinitely for another thread to perform a
     * particular action is in this state. </li>
     * </ul>
     * <p>
     * A manager can be in only one state at a given point in time.
     * 
     * @see AbstractStageManager#getState
     */
    public static enum RunState {
        NOT_STARTED, RUNNING, SHUTDOWN, STOP, TERMINATED;
    }

    private volatile RunState runState;

    // private volatile

    private volatile AbstractCache<K, V> cache;

    private final CacheConfiguration<K, V> conf;

    private volatile Map<String, Object> properties;

    public AbstractCacheService(CacheConfiguration<K, V> conf) {
        this.conf = conf;
    }

    public void addTo(ManagedGroup dg) {

    }

    boolean isStarted() {
        return runState != RunState.NOT_STARTED;
    }

    public final void start(AbstractCache<K, V> cache, Map<String, Object> properties) {
        final ReentrantLock mainLock = getMainLock();
        mainLock.lock();
        try {
            if (tryStart()) {
                this.cache = cache;
                this.properties = properties;
                try {
                    doStart(cache, properties);
                } catch (Exception e) {
                    throw new RuntimeException("Could not start cache");
                }
            }
        } finally {
            mainLock.unlock();
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
}
