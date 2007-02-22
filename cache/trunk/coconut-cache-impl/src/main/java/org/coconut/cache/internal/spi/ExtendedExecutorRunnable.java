/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.spi;

import java.util.Collection;

import org.coconut.cache.spi.CacheExecutorRunnable;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ExtendedExecutorRunnable {

    interface LoadKey<K> extends CacheExecutorRunnable {
        K getKey();
    }

    interface LoadKeys<K> extends CacheExecutorRunnable {
        Collection<? extends K> getKeys();
    }

    interface ServiceStatistics extends CacheExecutorRunnable {

    }

    interface ServiceManagement extends CacheExecutorRunnable {

    }

    interface ServiceEvent extends CacheExecutorRunnable {

    }
}
