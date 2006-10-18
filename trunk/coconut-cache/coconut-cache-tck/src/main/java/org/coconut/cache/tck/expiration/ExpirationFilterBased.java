/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.expiration;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_TO_M5_KEY_SET;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.junit.Assert.assertEquals;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ExpirationFilterBased extends CacheTestBundle {

    private ExpirationFilter f;

    @Before
    public void setUpCaches() {
        f = new ExpirationFilter();
        c = newCache(newConf().setClock(clock).expiration().setFilter(f).c());
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
     * Tests that time based expiration still works even though a filter is
     * defined.
     */
    public void timeExpirationStillWorks() {
        incTime(3);
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(3);

        incTime(10);
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(0);
    }

    /**
     * Tests a custom expiration filter.
     */
    @Test
    public void testFilter() {
        assertSize(5); // time still has influence
        f.isExpired = true;
        assertNullGet(M3);
        assertEquals(M3.getKey(), f.lastEntry.getKey());
        assertEquals(M3.getValue(), f.lastEntry.getValue());
        f.isExpired = false;
        assertGet(M2);
        f.isExpired = true;
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(0); // time has no influence any more
    }
}
