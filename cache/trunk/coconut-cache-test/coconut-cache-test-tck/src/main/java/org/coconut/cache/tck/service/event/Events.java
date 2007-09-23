package org.coconut.cache.tck.service.event;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEvent;

public class Events {

    public CacheEvent.CacheCleared<Integer, String> cleared(Cache<Integer, String> cache,
            int previousSize) {
        return null;
    }

}
