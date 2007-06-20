package org.coconut.cache.service.exceptionhandling;

import org.coconut.cache.Cache;
import org.coconut.core.Logger;

public abstract class CacheExceptionContext<K, V> {
    public abstract Cache<K, V> getCache();

    public abstract Logger defaultLogger();
}
