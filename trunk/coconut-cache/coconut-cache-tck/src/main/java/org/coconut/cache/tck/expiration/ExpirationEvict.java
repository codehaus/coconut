/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.expiration;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Before;
import org.junit.Test;

/**
 * This test bundle tests the on-evict expiration strategy for a cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ExpirationEvict extends CacheTestBundle {

    @Before
    public void setUpCache() {
        c = newCache(newConf().setClock(clock));
        fillItUp();
    }

    private void fillItUp() {
        put(M1, 2);
        put(M2, 3);
        put(M3, 4);
        put(M4, 4);
        put(M5, 6);
    }

    /**
     * Test that at time 2 M1 is expired.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void singleElementExpiration() {
        incTime();
        evict();
        assertGet(M1);
        assertSize(5);

        incTime();
        evict();
        assertNullGet("Element M1 was not expired and removed", M1);
        assertSize(4);
    }

    /**
     * Simple tests that just tests a lot of elements each expiring at different
     * times.
     */
    @Test
    public void manyElements() {
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
        ExpirationFilter f = new ExpirationFilter();
        c = newCache(newConf().setClock(clock).expiration().setFilter(f).c());
        fillItUp();

        incTime(3);
        evict();
        assertSize(3); // time still has influence

        f.isExpired = true;
        evict();
        assertSize(0);

        // TODO f.lastEntry.equals(c.getEntry(M1.getKey));
    }
}
