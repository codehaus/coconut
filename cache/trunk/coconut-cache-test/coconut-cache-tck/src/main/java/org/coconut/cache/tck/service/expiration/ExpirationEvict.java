/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.expiration;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;

import org.coconut.cache.tck.testutil.CacheEntryFilter;
import org.junit.Test;

/**
 * This test bundle tests the on-evict expiration strategy for a cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class ExpirationEvict extends AbstractExpirationTestBundle {

    private void fillItUp() {
        put(M1, 2);
        put(M2, 3);
        put(M3, 4);
        put(M4, 4);
        put(M5, 6);
    }

    /**
     * Test that the expiration status of an element is checked when we call evict.
     */
    @Test
    public void evictSingleElement() {
        c = newCache(newConf().setClock(clock));
        put(M1, 2);
        incTime();
        evict();
        assertGet(M1);

        incTime();
        evict();
        assertNullGet("Element M1 was not expired and removed", M1);
        assertSize(0);
    }

    /**
     * Simple tests that just tests a lot of elements each expiring at different times.
     */
    @Test
    public void evictManyElements() {
        c = newCache(newConf().setClock(clock));
        fillItUp();
        assertSize(5);

        incTime(); // time1
        evict();
        assertSize(5);

        incTime(); // time2
        evict();
        assertSize(4);

        incTime(); // time3
        evict();
        assertSize(3);

        incTime(); // time4
        evict();
        assertSize(1);

        incTime(); // time5
        evict();
        assertSize(1);

        incTime(); // time6
        evict();
        assertSize(0);
    }

    /**
     * Tests a custom expiration filter.
     */
    @Test
    public void customExpirationFilter() {
        CacheEntryFilter f = new CacheEntryFilter();
        c = newCache(newConf().setClock(clock).expiration().setExpirationFilter(f).c());
        fillItUp();

        incTime(3);
        evict();
        assertSize(3); // time still has influence

        f.setAccept(true);
        evict();
        assertSize(0);
    }
}
