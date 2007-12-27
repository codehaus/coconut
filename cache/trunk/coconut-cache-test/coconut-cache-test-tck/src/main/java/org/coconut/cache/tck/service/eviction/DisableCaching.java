/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.eviction;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;
import static org.coconut.test.CollectionTestUtil.M5;

import java.util.Arrays;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.junit.Test;

public class DisableCaching extends AbstractCacheTCKTest {

    @Test
    public void put() {
        init(newConf().eviction().setDisabled(true));
        put(M1);
        assertSize(0);
        putIfAbsent(M2);
        assertSize(0);
        put(M1.getKey(), "C");
        assertSize(0);
        putAll(M2, M3);
        assertSize(0);
    }

    @Test
    public void putDisable() {
        init();
        assertFalse(eviction().isDisabled());
        put(M1);
        assertSize(1);
        putIfAbsent(M2);
        assertSize(2);
        eviction().setDisabled(true);
        assertTrue(eviction().isDisabled());
        assertSize(2);
        put(M1.getKey(), "C");
        assertGet(M1);
        assertSize(2);
        putAll(M2, M3);
        assertSize(2);

    }

    @Test
    public void get() {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        init(newConf().eviction().setDisabled(true).c().loading().setLoader(loader));
        assertGet(M1);
        assertSize(0);
        assertGet(M1, M2);
        assertSize(0);
        getAll(M2, M3, M4);
        assertSize(0);
        assertEquals(M1.getValue(), getEntry(M1).getValue());
        assertSize(0);
        assertEquals(7, loader.getNumberOfLoads());
    }

    @Test
    public void getDisable() {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        init(newConf().loading().setLoader(loader));
        assertGet(M1);
        assertSize(1);
        assertGet(M1, M2);
        assertSize(2);
        eviction().setDisabled(true);
        assertTrue(eviction().isDisabled());
        assertEquals(2, loader.getNumberOfLoads());
        getAll(M2, M3, M4);
        assertSize(2);
        assertEquals(4, loader.getNumberOfLoads());
        getAll(M4, M5);
        assertSize(2);
        assertEquals(6, loader.getNumberOfLoads());
    }

    @Test
    public void loadDisable() {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        init(newConf().loading().setLoader(loader));
        loading().load(1);
        awaitAllLoads();
        assertSize(1);
        loading().forceLoad(2);
        awaitAllLoads();
        assertSize(2);
        eviction().setDisabled(true);
        assertTrue(eviction().isDisabled());
        loading().loadAll(Arrays.asList(1, 2, 3, 4));
        loading().forceLoadAll(Arrays.asList(1, 2, 3, 4));
        awaitAllLoads();
        assertSize(2);
    }

    @Test
    public void load() {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        init(newConf().eviction().setDisabled(true).c().loading().setLoader(loader));
        loading().load(1);
        awaitAllLoads();
        assertSize(0);
        loading().forceLoad(2);
        awaitAllLoads();
        assertSize(0);
        loading().loadAll(Arrays.asList(3, 4));
        awaitAllLoads();
        assertSize(0);
    }
}
