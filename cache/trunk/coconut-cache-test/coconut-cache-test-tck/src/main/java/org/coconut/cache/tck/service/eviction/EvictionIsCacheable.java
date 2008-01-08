/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.eviction;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;
import static org.coconut.test.CollectionTestUtil.M5;
import static org.coconut.test.CollectionTestUtil.newEntry;

import java.util.Arrays;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.core.Logger.Level;
import org.coconut.operations.Mappers;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;
import org.coconut.test.throwables.RuntimeException1;
import org.coconut.test.throwables.RuntimeException2;
import org.junit.Test;

public class EvictionIsCacheable extends AbstractCacheTCKTest {

    static Predicate ic = Predicates.mapAndEvaluate((Mapper) Mappers.mapEntryToKey(), Predicates
            .anyEquals(1, 2));

    static Predicate ivalue = Predicates.mapAndEvaluate((Mapper) Mappers.mapEntryToValue(),
            Predicates.anyEquals("A", "B"));

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
        assertSize(2); //?? replace
    }

    @Test
    public void replace() {
        c = newCache(newConf().eviction().setIsCacheableFilter(ivalue));
        put(M1);
        put(M2);
        assertSize(2);
        replace(M1.getKey(), "C");
        assertSize(2); //?? replace
        replace(M2.getKey(), "D");
        assertSize(2);//?? replace
    }

    @Test
    public void putUpdate() {
        c = newCache(newConf().eviction().setIsCacheableFilter(ivalue));
        put(M1);
        put(M2);
        assertSize(2);
        c.put(M1.getKey(), "C");
        assertSize(2); //?? replace
        c.put(M2.getKey(), "D");
        assertSize(2);//?? replace
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
        assertSize(2);//?? replace
    }

    @Test
    public void predicateFail() {
        init(conf.eviction().setIsCacheableFilter(new Predicate() {
            public boolean evaluate(Object t) {
                throw RuntimeException1.INSTANCE;
            }
        }).c().exceptionHandling().setExceptionHandler(exceptionHandler));
        put(M1);
        assertSize(0);
        exceptionHandler.eat(RuntimeException1.INSTANCE, Level.Fatal);
    }

    @Test
    public void get() {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        c = newCache(newConf().eviction().setIsCacheableFilter(ic).c().loading().setLoader(loader));
        assertGet(M1, M2, M3, M4, M5);
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

    @Test
    public void loadRemoveOld() {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        c = newCache(newConf().eviction().setIsCacheableFilter(ic).c().loading().setLoader(loader));
        loading().load(M1.getKey());
    }
}
