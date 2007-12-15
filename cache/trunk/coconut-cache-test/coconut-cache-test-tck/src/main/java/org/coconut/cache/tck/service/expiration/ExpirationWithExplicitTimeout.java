/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.expiration;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M1_TO_M5_KEY_SET;
import static org.coconut.test.CollectionTestUtil.M1_TO_M5_SET;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;
import static org.coconut.test.CollectionTestUtil.M5;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ExpirationWithExplicitTimeout extends AbstractExpirationTestBundle {
    @Before
    public void setUpCaches() {
       setCache();
    }

    /**
     * Simple tests that just tests a lot of elements each expiring at different times.
     * size() is normally a constant time operation so we need to 'touch' all elements by
     * calling getAll(all elements) before they expired.
     */
    @Test
    public void manyElements() {
        put(M1, 2);
        put(M2, 3);
        put(M3, 4);
        put(M4, 4);
        put(M5, 6);

        incTime(); // time1
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(5);

        incTime(); // time2
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(4);

        incTime(); // time3
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(3);

        incTime(); // time4
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(1);

        incTime(); // time5
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(1);

        incTime(); // time6
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(0);
    }

    /**
     * Tests that when inserting (put) an element that already exist in the cache the
     * expiration time of that element is overridden.
     */
    @Test
    public void overrideEviction() {
        put(M3, 100);
        // M3,M4 expires as time=4
        incTime(4);
        purge();
        assertGet(M3);
        assertNullGet(M4);
        incTime(96);
        purge();
        assertNullGet(M3);
    }

    /**
     * Tests that the expiration of putAll (with default timeout) are done according to
     * the relative time at insertation and not from 0.
     */
    //@Test()
    //TODO enable
    public void relativePutAllDefaultTimeOut() {
        c = newCache(0);
        putAll(M1, M2);

        incTime(5);
        putAll(M3, M4);

        incTime(5);
        purge();
        assertNullGet(M1, M2);
        assertGet(M3, M4);

        incTime(5);
        purge();

        incTime(1);
        assertNullGet(M3, M4);
    }

    @Test
    public void mapOperationsDoNotTimeout() {
        put(M1, 2);
        put(M2, 3);
        put(M3, 4);
        put(M4, 4);
        put(M5, 6);
        incTime(100);
        assertSize(5);

        assertTrue(containsKey(M1));
        assertTrue(containsValue(M1));
        assertEquals(5, c.entrySet().size());
        assertEquals(M1_TO_M5_SET, c.entrySet());
        assertEquals(5, c.keySet().size());
        assertTrue(c.keySet().containsAll(M1_TO_M5_KEY_SET)
                && M1_TO_M5_KEY_SET.containsAll(c.keySet()));
        assertEquals(M1.getValue(), c.peek(M1.getKey()));

        assertEquals(M1.getValue(), c.put(M1.getKey(), "AB"));

        // TODO
        // putAll
        // putIfAbsent
        // remove
        // remove2
        // replace1
        // replace2
    }

    // map operations
    // containsValue
    // entrySet
    // getAll
    // keySet
    // peek
    // put -- returns null if element has been evicted??
    // putAll
    // putIfAbsent
    // remove, remove2
    // replace1, replace2
    // size
    //

    // Reload
    // equals
    // hashCode
}
