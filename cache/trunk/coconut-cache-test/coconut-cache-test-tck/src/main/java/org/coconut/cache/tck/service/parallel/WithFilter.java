package org.coconut.cache.tck.service.parallel;

import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M4;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.ParallelCache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.operations.Mappers;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;
import org.junit.Test;

public class WithFilter extends AbstractCacheTCKTest {
    static final Predicate p = new Predicate<CacheEntry<Integer, String>>() {
        public boolean evaluate(CacheEntry<Integer, String> t) {
            return t.getKey() % 2 == 0;
        }
    };

    public ParallelCache.WithFilter<Integer, String> p() {
        return parallel().get().withFilter(p);
    }

    @Test(expected = NullPointerException.class)
    public void withFilterNPE() {
        parallel().get().withFilter(null);
    }

    @Test(expected = NullPointerException.class)
    public void withFilterNPE1() {
        parallel().get().withFilter(p).withFilter(null);
    }

    @Test(expected = NullPointerException.class)
    public void applyNPE() {
        p().apply(null);
    }

    @Test(expected = NullPointerException.class)
    public void applyNPE1() {
        init(5);
        p().apply(null);
    }

    @Test
    public void apply() {
        Copy copy = new Copy();
        p().apply(copy);
        assertEquals(0, copy.chm.size());
        init(5);
        p().apply(copy);
        assertEquals(2, copy.chm.size());
        assertEquals(new HashSet(Arrays.asList(M2, M4)), new HashSet(copy.chm.values()));
    }

    @Test
    public void shutdownNoISE() {
        init(5);
        c.shutdown();
        p().apply(new Copy());
    }

    @Test
    public void size() {
        assertEquals(0, p().size());
        init(5);
        assertEquals(2, p().size());
    }

    @Test
    public void filterOnFilter() {
        init(5);
        ParallelCache.WithFilter wf = parallel().get().withFilter(
                Predicates.mapAndEvaluate((Mapper) Mappers.mapEntryToKey(), Predicates
                        .greaterThen(1)));
        assertEquals(4, wf.size());
        wf = wf.withFilter(Predicates.mapAndEvaluate((Mapper) Mappers.mapEntryToKey(), Predicates
                .lessThen(5)));
        assertEquals(3, wf.size());
    }

    static class Copy implements Procedure<CacheEntry<Integer, String>> {
        final ConcurrentHashMap<Integer, CacheEntry<Integer, String>> chm = new ConcurrentHashMap<Integer, CacheEntry<Integer, String>>();

        public void apply(CacheEntry<Integer, String> t) {
            assertNull(chm.put(t.getKey(), t));
        }
    }
}
