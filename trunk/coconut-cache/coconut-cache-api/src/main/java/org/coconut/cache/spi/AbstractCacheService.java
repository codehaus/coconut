/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.Map;

import org.coconut.core.Log;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheService<K, V> {

//    private volatile Log log;
//
//    protected Log getLog() {
//        return log;
//    }
//
//    void initializeCC(AbstractCache<K, V> cache, Map<String, Object> properties) {
//        log = cache.getConfiguration().getErrorHandler().getLogger();
//    }

    protected void initialize(AbstractCache<K, V> cache, Map<String, Object> properties) {
        // ignore
    }
}
