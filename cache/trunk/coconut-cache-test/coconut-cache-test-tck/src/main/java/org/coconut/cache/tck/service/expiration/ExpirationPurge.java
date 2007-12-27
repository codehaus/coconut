/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.expiration;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;
import static org.coconut.test.CollectionTestUtil.M5;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.test.util.CacheEntryFilter;
import org.junit.Test;

/**
 * This test bundle tests the on-evict expiration strategy for a cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class ExpirationPurge extends AbstractExpirationTestBundle {

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
        init();
        put(M1, 2);
        incTime();
        purge();
        assertGet(M1);

        incTime();
        purge();
        assertNullPeek("Element M1 was not expired and removed", M1);
        assertSize(0);
    }

    /**
     * Simple tests that just tests a lot of elements each expiring at different times.
     */
    @Test
    public void evictManyElements() {
        init();
        fillItUp();
        assertSize(5);

        incTime(); // time1
        purge();
        assertSize(5);

        incTime(); // time2
        purge();
        assertSize(4);

        incTime(); // time3
        purge();
        assertSize(3);

        incTime(); // time4
        purge();
        assertSize(1);

        incTime(); // time5
        purge();
        assertSize(1);

        incTime(); // time6
        purge();
        assertSize(0);
    }

    /**
     * Tests a custom expiration filter.
     */
    @Test
    public void customExpirationFilter() {
        CacheEntryFilter f = new CacheEntryFilter();
        init(conf.expiration().setExpirationFilter(f));
        fillItUp();

        incTime(3);
        purge();
        assertSize(3); // time still has influence

        f.setAccept(true);
        purge();
        assertSize(0);
    }
    
    /**
     * {@link CacheExpirationService#purgeExpired()} lazy starts the cache.
     */
    @Test
    public void purgeLazyStart() {
        init();
        assertFalse(c.isStarted());
        purge();
        checkLazystart();
    }

    /**
     * {@link CacheExpirationService#purgeExpired()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test
    public void purgeShutdown() throws InterruptedException {
        init(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should not fail, but result is undefined until terminated
        purge();

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));

        purge();
    }

}
