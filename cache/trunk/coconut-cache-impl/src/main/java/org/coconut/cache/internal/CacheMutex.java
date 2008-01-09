package org.coconut.cache.internal;

public class CacheMutex {

    private final Object o;

    public Object getMutex() {
        return o;
    }
    
    private CacheMutex(Object o) {
        this.o = o;
    }

    static CacheMutex from(Object o) {
        return new CacheMutex(o);
    }
}
