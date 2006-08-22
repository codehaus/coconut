/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.expiration;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.junit.Assert.assertNull;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Before;
import org.junit.Test;

/**
 * This bundle tests various tweaks that could exist with an expiration implementation.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class ExpirationCommon extends CacheTestBundle {

    @Before
    public void setUpCaches() {
        c = newCache(newConf().setClock(clock));
        c0 = newCache(newConf().setClock(clock).expiration().setDefaultTimeout(10,
                TimeUnit.NANOSECONDS).c());
        put(M1, 2);
        put(M2, 3);
        put(M3, 4);
        put(M4, 4);
        put(M5, 6);
    }

    /**
     * Tests that the expiration of puts (with timeout) are done according to
     * the relative time at insertation and not from 0. That is an element with
     * a 10 s expiration inserted at time 5 should expire at time 15 and not
     * time 10.
     */
    @Test
    public void relativePut() {
        c = c0;
        put(M1, 10);

        incTime(5);
        put(M2, 10);

        incTime(5);
        evict();
        assertNullGet(M1);
        assertGet(M2);

        incTime(5);
        evict();

        incTime(1);
        assertNullGet(M2);
    }

    /**
     * Tests that the expiration of putAlls (with timeout) are done according to
     * the relative time at insertation and not from 0.
     */
    @Test
    public void relativePutAll() {
        c = c0;
        putAll(10, M1, M2);

        incTime(5);
        putAll(10, M3, M4);

        incTime(5);
        evict();
        assertNullGet(M1, M2);
        assertGet(M3, M4);

        incTime(5);
        evict();

        incTime(1);
        assertNullGet(M3, M4);
    }

    /**
     * Tests that the expiration of puts (with default timeout) are done
     * according to the relative time at insertation and not from 0.
     */
    @Test
    public void relativePutDefaultTimeOut() {
        c = c0;
        put(M1);

        incTime(5);
        put(M2);

        incTime(5);
        evict();
        assertNullGet("Cache did not expire M1 element", M1);
        assertGet(M2);

        incTime(5);
        evict();

        incTime(1);
        assertNullGet(M2);
    }

    /**
     * Tests that the expiration of putAll (with default timeout) are done
     * according to the relative time at insertation and not from 0.
     */
    @Test
    public void relativePutAllDefaultTimeOut() {
        c = c0;
        putAll(M1, M2);

        incTime(5);
        putAll(M3, M4);

        incTime(5);
        evict();
        assertNullGet(M1, M2);
        assertGet(M3, M4);

        incTime(5);
        evict();

        incTime(1);
        assertNullGet(M3, M4);
    }

    /**
     * Tests that when inserting (put) an element that already exist in the
     * cache the expiration time of that element is overridden.
     */
    @Test
    public void overrideEviction() {
        put(M3, 100);
        // M3,M4 expires as time=4
        incTime(4);
        evict();
        assertGet(M3);
        assertNullGet(M4);
        incTime(96);
        evict();
        assertNullGet(M3);
    }

    /**
     * Tests that when inserting (putAll) an element that already exist in the
     * cache the expiration time of that element is overridden.
     */
    @Test
    public void overrideEvictionPutAll() {
        put(M3, 100, TimeUnit.NANOSECONDS);
        // M3,M4 expires as time=4
        incTime(4);
        evict();
        assertGet(M3);
        assertNullGet(M4);
        incTime(96);
        evict();
        assertNull(get(M3));
    }

    /**
     * Tests that when inserting (putAll) an element that already exist in the
     * cache (inserted with the default expiration timeout) the expiration time
     * of that element is overridden.
     */
    @Test
    public void overrideEvictionDefaultExpiration() {
        c = c0;
        put(M1);
        put(M2);
        incTime(5);
        put(M1);

        incTime(5);
        evict();
        assertGet(M1);
        assertNullGet(M2);

        incTime(5);
        evict();
        assertNullGet(M1);
    }
}
