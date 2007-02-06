/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.spi.AbstractCache;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DummyCache extends AbstractCache {
    public volatile boolean isStarted;

    /**
     * @param configuration
     */
    public DummyCache(CacheConfiguration configuration) {
        super(configuration);
    }


    /**
     * @see org.coconut.cache.spi.AbstractCache#trimToSize(int)
     */
    @Override
    public void trimToSize(int newSize) {
    }

    /**
     * @see java.util.AbstractMap#entrySet()
     */
    @Override
    public Set entrySet() {
        return null;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#initialize()
     */
    @Override
    public void start() {
        isStarted = true;
    }

    /**
     * @see org.coconut.cache.Cache#getEntry(java.lang.Object)
     */
    public CacheEntry getEntry(Object key) {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * @see org.coconut.cache.Cache#peek(java.lang.Object)
     */
    public Object peek(Object key) {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * @see org.coconut.cache.Cache#peekEntry(java.lang.Object)
     */
    public CacheEntry peekEntry(Object key) {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * @see org.coconut.cache.Cache#put(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit)
     */
    public Object put(Object key, Object value, long timeout, TimeUnit unit) {
        // TODO Auto-generated method stub
        return null;
    }
}
