package org.coconut.cache.internal.service.exceptionhandling;

import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;

public interface CacheExceptionService<K, V> {
    CacheExceptionHandler<K, V> getExceptionHandler();

    CacheExceptionContext<K, V> createContext();
}
