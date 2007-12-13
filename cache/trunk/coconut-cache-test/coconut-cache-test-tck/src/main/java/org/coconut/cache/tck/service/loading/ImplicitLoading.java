/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M1_TO_M5_MAP;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;
import static org.coconut.test.CollectionTestUtil.M5;
import static org.coconut.test.CollectionTestUtil.M6;
import static org.coconut.test.CollectionTestUtil.M7;

import java.util.HashMap;
import java.util.Map;

import org.coconut.cache.Cache;
import org.junit.Test;

/**
 * Tests loading through one of the {@link Cache#get(Object)} or
 * {@link Cache#getAll(java.util.Collection)} methods.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ImplicitLoading extends AbstractLoadingTestBundle {

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

    @Test
    public void keepLoadedValue() {
        assertGet(M1);
        assertEquals(1, loader.getNumberOfLoads());
        assertGet(M1);
        assertEquals(1, loader.getNumberOfLoads());
    }
}
