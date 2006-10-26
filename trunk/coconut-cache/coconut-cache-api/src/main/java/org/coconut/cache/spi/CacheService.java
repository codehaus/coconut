/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.management.ApmGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheService<K, V> {
    void start(AbstractCache<K, V> cache, Map<String, Object> properties);

    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

    void addTo(ApmGroup dg);
}