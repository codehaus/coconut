package org.coconut.cache.tck.service.eviction;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.newEntry;

import java.util.Arrays;

import org.coconut.cache.policy.IsCacheable;
import org.coconut.cache.policy.IsCacheables;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.operations.Predicates;
import org.junit.Test;

public class EvictionIsCacheable extends AbstractCacheTCKTest {

    static IsCacheable ic = IsCacheables.fromKeyPredicate(Predicates.anyEquals(1, 2));

    static IsCacheable ivalue = IsCacheables.fromValuePredicate(Predicates.anyEquals("A", "B"));

    @Test
    public void put() {
        c = newCache(newConf().eviction().setIsCacheableFilter(ivalue));
        put(M1);
        assertSize(1);
        put(M2);
        assertSize(2);
        put(M3);
        put(M4);
        assertSize(2);
        put(M1.getKey(), "C");
        assertSize(1);
    }

    @Test
    public void replace() {
        c = newCache(newConf().eviction().setIsCacheableFilter(ivalue));
        put(M1);
        put(M2);
        assertSize(2);
        replace(M1.getKey(), "C");
        assertSize(1);
        replace(M2.getKey(), "D");
        assertSize(0);
    }

    @Test
    public void putUpdate() {
        c = newCache(newConf().eviction().setIsCacheableFilter(ivalue));
        put(M1);
        put(M2);
        assertSize(2);
        c.put(M1.getKey(), "C");
        assertSize(1);
        c.put(M2.getKey(), "D");
        assertSize(0);
        put(M1);
        put(M2);
        assertSize(2);
    }

    @Test
    public void putAll() {
        c = newCache(newConf().eviction().setIsCacheableFilter(ivalue));
        putAll(M1, M3, M4);
        assertSize(1);
        putAll(M1, M2, M3, M4);
        assertSize(2);
        putAll(newEntry(M1.getKey(), "foo"), M2, M3, M4);
        assertSize(1);
    }

    @Test
    public void get() {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        c = newCache(newConf().eviction().setIsCacheableFilter(ic).c().loading().setLoader(loader));
        assertGet(M1, M2);
        assertNullGet(M3, M4, M5);
        assertSize(2);
        assertEquals(5, loader.getNumberOfLoads());
    }

    @Test
    public void getAll() {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        c = newCache(newConf().eviction().setIsCacheableFilter(ic).c().loading().setLoader(loader));
        getAll(M1, M2, M3, M4, M5);
        assertSize(2);
        assertEquals(5, loader.getNumberOfLoads());
    }

    @Test
    public void load() {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        c = newCache(newConf().eviction().setIsCacheableFilter(ic).c().loading().setLoader(loader));
        loading().load(M1.getKey());
        loading().forceLoad(M2.getKey());
        loading().load(M3.getKey());
        loading().forceLoad(M4.getKey());
        loading().load(M5.getKey());
        awaitAllLoads();
        assertSize(2);
        assertEquals(5, loader.getNumberOfLoads());
    }

    @Test
    public void loadAll() {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        c = newCache(newConf().eviction().setIsCacheableFilter(ic).c().loading().setLoader(loader));
        loading().loadAll(Arrays.asList(1, 2, 3, 4, 5));
        awaitAllLoads();
        assertSize(2);
        assertEquals(5, loader.getNumberOfLoads());
    }
}
