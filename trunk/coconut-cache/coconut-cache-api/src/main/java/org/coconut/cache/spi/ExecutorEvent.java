/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.Collection;

import org.coconut.cache.Cache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ExecutorEvent extends Runnable {

    interface LoadKey<K> extends ExecutorEvent {
        K getKey();
    }

    interface LoadKeys<K> extends ExecutorEvent {
        Collection<? extends K> getKeys();
    }

    interface Clear extends ExecutorEvent {
        Cache getCache();
    }

    interface Evict extends ExecutorEvent {
        Cache getCache();
    }
    
    interface Statistics extends ExecutorEvent {
        
    }
}
