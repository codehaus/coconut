/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.expiration;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@SuppressWarnings("unchecked")
public class ExpirationWithDefaultTimeout extends AbstractCacheTCKTestBundle {
    @Before
    public void setUpCaches() {
        c = newCache(newConf().setClock(clock).expiration().setDefaultTimeToLive(10,
                TimeUnit.MILLISECONDS).c());
    }

    /**
     * Tests that default expiration works.
     */
    @Test
    public void simpleExpirationPut() {
        put(M1);
        assertGet(M1);
        incTime(5);
        assertGet(M1);
        incTime(5);
        assertNullGet(M1);
    }

    /**
     * Tests that default expiration works when getEntry() is called.
     */
    @Test
    public void simpleExpirationPutGetEntry() {
        put(M1);
        incTime(5);
        assertGetEntry(M1);
        incTime(5);
        assertNull(c.getEntry(M1.getKey()));
    }

    /**
     * Tests that default expiration works.
     */
    @Test
    public void simpleExpirationPutAll() {
        putAll(M1, M2);
        incTime(5);
        assertGetAll(M1, M2);
        incTime(5);
        assertNullGet(M1, M2);
    }

    @Test
    /**
     * Tests that default expiration works with relative time.
     */
    public void defaultExpirationRelative() {
        put(M1);

        incTime(5);
        put(M2);

        incTime(5);
        assertNullGet(M1);
        assertGet(M2);

        incTime(5);
        assertNullGet(M1);
        assertNullGet(M2);
    }

    /**
     * Tests that when inserting (putAll) an element that already exist in the
     * cache (inserted with the default expiration timeout) the expiration time
     * of that element is overridden.
     */
    @Test
    public void overrideDefaultExpiration() {
        put(M1);

        incTime(2);
        put(M1); // should expire at time 12 (10+2) now

        incTime(9); // time = 11
        assertGet(M1);

        incTime(1); // time = 12
        assertNullGet(M1);
    }

    @Test
    /**
     * Tests that default expiration works with relative time and put all.
     */
    public void defaultExpirationRelativePutAll() {
        putAll(M1, M2);

        incTime(5);
        putAll(M2, M3);

        incTime(5);
        assertNullGet(M1);
        assertGet(M2);
        assertGet(M3);

        incTime(5);
        assertNullGet(M1);
        assertNullGet(M2);
        assertNullGet(M3);
    }

    /**
     * Tests that peek has no side effecs, peek should not check for expiration
     * of elements.
     */
    @Test
    public void peekNoSideEffects() {
        put(M1);
        incTime(10);
        assertPeek(M1); // now you see it, no check of expiration
        assertPeekEntry(M1);
        assertNullGet("Element M1 was not expired and removed", M1);
        assertNullPeek(M1); // now you don't (get should have removed the item)
        assertNull(c.peekEntry(M1.getKey()));
    }

}
