package org.codehaus.cake.internal.service.spi;

public class GlobalServiceMutex {

    private final Object o;

    public Object getMutex() {
        return o;
    }
    
    private GlobalServiceMutex(Object o) {
        this.o = o;
    }

    public static GlobalServiceMutex from(Object o) {
        return new GlobalServiceMutex(o);
    }
}
