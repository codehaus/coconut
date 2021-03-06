/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.spi;

import org.coconut.cache.Cache;

/**
 * All asynchronously tasks that are parsed on to the a user specified executor should
 * implement this interface.
 * <p>
 * TODO: Refactor
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheExecutorRunnable extends Runnable {
    interface CacheClear extends CacheExecutorRunnable {
        Cache getCache();
    }

    interface CacheEvict extends CacheExecutorRunnable {
        Cache getCache();
    }
}
