package org.coconut.cache.test.service.exceptionhandling;

import org.coconut.cache.Cache;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.core.Logger.Level;

public class TestExceptionHandler<K, V> extends CacheExceptionHandler<K, V> {

    private final int count;

    private int i = 0;

    private Cache c;

    private Level level;

    private Throwable cause;

    private String msg;

    public TestExceptionHandler() {
        this(1);
    }

    public TestExceptionHandler(int count) {
        this.count = count;
    }

    @Override
    public synchronized void apply(CacheExceptionContext<K, V> context) {
        i++;
        if (i > count) {
            throw new AssertionError(i);
        }
        this.c = context.getCache();
        msg = context.getMessage();
        cause = context.getCause();
    }

    public Cache getC() {
        return c;
    }

    public Throwable getCause() {
        return cause;
    }

    public String getMsg() {
        return msg;
    }
}
