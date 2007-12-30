package org.coconut.cache.test.service.exceptionhandling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.core.Logger.Level;

public class TestExceptionHandler<K, V> extends CacheExceptionHandler<K, V> {
    private final Queue<CacheExceptionContext> q = new ConcurrentLinkedQueue<CacheExceptionContext>();

    @Override
    public synchronized void apply(CacheExceptionContext<K, V> context) {
        q.add(context);
    }


    public void eat(Throwable cause, Level level) {
        CacheExceptionContext context = q.poll();
        assertSame(cause, context.getCause());
        assertSame(level, context.getLevel());
    }

    public void assertCleared() {
        assertEquals(0, q.size());
    }

}
