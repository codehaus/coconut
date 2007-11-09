/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ThreadUtilsTest {

    @Test
    public void testSameThreadExecutor() {
        Executor e = ThreadUtils.SAME_THREAD_EXECUTOR;
        final Thread t = Thread.currentThread();
        final AtomicBoolean b = new AtomicBoolean();
        e.execute(new Runnable() {
            public void run() {
                assertEquals(t, Thread.currentThread());
                b.set(true);
            }
        });
        assertTrue(b.get());
    }

    @Test
    public void testNullCallable() throws Exception {
        assertNull(ThreadUtils.nullCallable().call());
    }

}
