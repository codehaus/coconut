/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.expiration;

import org.coconut.cache.CacheEntry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheExpirationService2<K, V> extends CacheExpirationService<K, V> {

    boolean expireKey(Object key);

    boolean expire(CacheEntry<?, ?> entry);

    //just as clear, but also send events.
    void removeAll();
}
