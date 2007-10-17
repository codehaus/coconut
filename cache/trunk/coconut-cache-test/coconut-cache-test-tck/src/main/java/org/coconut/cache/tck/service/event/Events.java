/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEvent;

public class Events {

    public CacheEvent.CacheCleared<Integer, String> cleared(Cache<Integer, String> cache,
            int previousSize) {
        return null;
    }

}
