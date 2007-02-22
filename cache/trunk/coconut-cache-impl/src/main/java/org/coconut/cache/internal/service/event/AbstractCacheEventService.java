/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.AbstractCacheService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AbstractCacheEventService<K, V> extends AbstractCacheService<K, V> {

    public AbstractCacheEventService(CacheConfiguration<K, V> conf) {
        super(conf);
    }
}
