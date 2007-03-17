/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.loading;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_TO_M5_MAP;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.M6;
import static org.coconut.test.CollectionUtils.M7;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class Loading extends LoadingTestBundle {

    @Test
    public void testSimpleLoading() {

        assertNullPeek(M1);
        assertFalse(containsKey(M1));
        assertFalse(containsValue(M1));
        assertSize(0);

        assertGet(M1);
        assertSize(1); // M1 loaded
        assertPeek(M1);
        assertTrue(containsKey(M1));
        assertTrue(containsValue(M1));

        assertGet(M2);
        assertSize(2); // M2 loaded
        assertPeek(M2);
        assertTrue(containsKey(M2));
        assertTrue(containsValue(M2));
    }

    @Test
    public void testAggregateLoading() {
        assertEquals(M1_TO_M5_MAP, getAll(M1, M2, M3, M4, M5));
        assertEquals(5, c.size());
        assertPeek(M1, M2, M3, M4, M5);
    }

    @Test
    public void testNullLoading() {
        assertNullGet(M6);
        assertSize(0);
        assertFalse(containsKey(M6));

        assertGet(M5);
        assertEquals(1, c.size());
    }

    @Test
    public void testAggregateNullLoading() {
        Map<Integer, String> s = new HashMap<Integer, String>();
        s.put(M1.getKey(), M1.getValue());
        s.put(M2.getKey(), M2.getValue());
        s.put(6, null);
        s.put(7, null);

        assertEquals(s, getAll(M1, M2, M6, M7));

        assertSize(2);

        assertPeek(M1, M2);
    }

    /**
     * isDone is true when a task completes
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void testLoad() throws InterruptedException, ExecutionException {
        Future<?> task = service.load(1);
        assertNull(task.get());
        assertTrue(task.isDone());
        assertFalse(task.isCancelled());

        assertSize(1);
        assertPeek(M1);
        assertTrue(containsKey(M1));
        assertTrue(containsValue(M1));
    }

    @Test(expected = NullPointerException.class)
    public void testLoadNullPointer() {
        service.load(null);
    }

    @Test
    public void testLoadAll() throws InterruptedException, ExecutionException {
        Future<?> task = service.loadAll(Arrays.asList(1, 2, 3, 4));
        assertNull(task.get());
        assertTrue(task.isDone());
        assertFalse(task.isCancelled());

        assertEquals(4, c.size());

        assertPeek(M1, M2, M3, M4);
    }

    @Test(expected = NullPointerException.class)
    public void testLoadAllNullPointer() {
        service.loadAll(null);
    }

    /**
     * isDone is true when a task completes
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void testLoadNull() throws InterruptedException, ExecutionException {
        Future<?> task = service.load(M6.getKey());
        assertNull(task.get());
        assertTrue(task.isDone());
        assertFalse(task.isCancelled());

        assertSize(0);
        assertFalse(containsKey(M6));

        assertNull(service.load(4).get());
        assertSize(1);
    }

    @Test
    public void testLoadManyNull() throws InterruptedException, ExecutionException {
        Future<?> task = service.loadAll(Arrays.asList(1, 2, 6, 7));
        assertNull(task.get());
        assertTrue(task.isDone());
        assertFalse(task.isCancelled());

        assertSize(2);

        assertPeek(M1, M2);
    }
}
