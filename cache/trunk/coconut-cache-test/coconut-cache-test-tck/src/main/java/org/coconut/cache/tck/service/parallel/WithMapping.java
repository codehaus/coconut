package org.coconut.cache.tck.service.parallel;

import static org.coconut.test.CollectionTestUtil.M1_TO_M5_KEY_SET;

import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

import org.coconut.cache.service.parallel.ParallelCache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.operations.Mappers;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Procedure;
import org.junit.Test;

public class WithMapping extends AbstractCacheTCKTest {

    public ParallelCache.WithMapping<Integer> p() {
        return parallel().get().withMapping((Mapper) Mappers.keyFromMapEntry());
    }

    @Test(expected = NullPointerException.class)
    public void applyNPE() {
        p().apply(null);
    }
    @Test(expected = NullPointerException.class)
    public void withFilterNPE() {
        parallel().get().withMapping(null);
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
        assertEquals(0, copy.list.size());
        init(5);
        p().apply(copy);
        assertEquals(5, copy.list.size());
        assertEquals(new HashSet(M1_TO_M5_KEY_SET), new HashSet(copy.list));
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
        assertEquals(5, p().size());
    }

    @Test
    public void filterOnFilter() {
        init(5);
        ParallelCache.WithFilter wf = parallel().get().withFilter(
                Predicates.mapAndEvaluate((Mapper) Mappers.keyFromMapEntry(), Predicates
                        .greaterThen(1)));
        assertEquals(4, wf.size());
        wf = wf.withFilter(Predicates.mapAndEvaluate((Mapper) Mappers.keyFromMapEntry(), Predicates
                .lessThen(5)));
        assertEquals(3, wf.size());
    }

    static class Copy implements Procedure<Integer> {
        final CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<Integer>();

        public void apply(Integer t) {
            list.add(t);
        }
    }
}
