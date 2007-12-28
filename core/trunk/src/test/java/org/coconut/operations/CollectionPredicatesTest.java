/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;
import org.coconut.test.TestUtil;
import org.junit.Test;

public class CollectionPredicatesTest {

    @Test
    public void allTrue() {
        Predicate<Number> p = (Predicate) Predicates.anyEquals(2, 3);
        Predicate<Number> p2 = (Predicate) Predicates.anyEquals(1, 2, 3, 4, 5);
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
        assertFalse(CollectionPredicates.isAllTrue(list, p));
        assertTrue(CollectionPredicates.isAllTrue(list, p2));
    }

    @Test(expected = NullPointerException.class)
    public void allTrueNPE1() {
        CollectionPredicates.isAllTrue(null, TestUtil.dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void allTrueNPE2() {
        CollectionPredicates.isAllTrue(new ArrayList(), null);
    }

    @Test
    public void anyTrue() {
        Predicate<Number> p = (Predicate) Predicates.anyEquals(2, 3);
        Predicate<Number> p2 = (Predicate) Predicates.anyEquals(5, 6, 7, 8);
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
        assertTrue(CollectionPredicates.isAnyTrue(list, p));
        assertFalse(CollectionPredicates.isAnyTrue(list, p2));
    }

    @Test(expected = NullPointerException.class)
    public void anyTrueNPE1() {
        CollectionPredicates.isAnyTrue(null, TestUtil.dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void anyTrueNPE2() {
        CollectionPredicates.isAnyTrue(new ArrayList(), null);
    }

    @Test
    public void filter() {
        Predicate<Number> p = (Predicate) Predicates.anyEquals(2, 3);
        Collection<Integer> c = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
        List<Integer> list = CollectionPredicates.filter(c, p);
        assertEquals(2, list.size());
        assertEquals(2, list.get(0));
        assertEquals(3, list.get(1));
    }

    @Test
    public void filterMap() {
        Predicate<Map.Entry<Integer, String>> p = new Predicate<Map.Entry<Integer, String>>() {
            public boolean evaluate(Map.Entry<Integer, String> element) {
                return element.getKey().equals(2) || element.getValue().equals("3");
            }
        };
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(1, "1");
        m.put(2, "2");
        m.put(3, "3");
        m.put(4, "4");
        Map<Integer, String> map = CollectionPredicates.filterMap(m, p);
        assertEquals(2, map.size());
        assertEquals("2", map.get(2));
        assertEquals("3", map.get(3));
    }

    @Test
    public void filterMapKey() {
        Predicate<Number> p = new Predicate<Number>() {
            public boolean evaluate(Number element) {
                return element.equals(2) || element.equals(3);
            }
        };
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(1, "1");
        m.put(2, "2");
        m.put(3, "3");
        m.put(4, "4");
        Map<Integer, String> map = CollectionPredicates.filterMapKeys(m, p);
        assertEquals(2, map.size());
        assertEquals("2", map.get(2));
        assertEquals("3", map.get(3));
    }

    @Test(expected = NullPointerException.class)
    public void filterMapKeyNPE1() {
        CollectionPredicates.filterMapKeys(null, TestUtil.dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void filterMapKeyNPE2() {
        CollectionPredicates.filterMapKeys(new HashMap(), null);
    }

    @Test(expected = NullPointerException.class)
    public void filterMapNPE1() {
        CollectionPredicates.filterMap(null, TestUtil.dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void filterMapNPE2() {
        CollectionPredicates.filterMap(new HashMap(), null);
    }

    @Test
    public void filterMapValue() {
        Predicate<String> p = new Predicate<String>() {
            public boolean evaluate(String element) {
                return element.equals("2") || element.equals("3");
            }
        };
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(1, "1");
        m.put(2, "2");
        m.put(3, "3");
        m.put(4, "4");
        Map<Integer, String> map = CollectionPredicates.filterMapValues(m, p);
        assertEquals(2, map.size());
        assertEquals("2", map.get(2));
        assertEquals("3", map.get(3));
    }

    @Test(expected = NullPointerException.class)
    public void filterMapValueNPE1() {
        CollectionPredicates.filterMapValues(null, TestUtil.dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void filterMapValueNPE2() {
        CollectionPredicates.filterMapValues(new HashMap(), null);
    }

    @Test(expected = NullPointerException.class)
    public void filterNPE1() {
        CollectionPredicates.filter(null, TestUtil.dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void filterNPE2() {
        CollectionPredicates.filter(new ArrayList(), null);
    }

    @Test
    public void mapperPredicate() {
        Predicate<Number> p = (Predicate) Predicates.anyEquals(4, 16);
        Mapper<Integer, Integer> m = new Mapper<Integer, Integer>() {
            public Integer map(Integer from) {
                return from.intValue() * from.intValue();
            }
        };
        Predicate mapped = Predicates.mapAndEvaluate(m, p);
        assertTrue(mapped.evaluate(2));
        assertFalse(mapped.evaluate(3));
        assertTrue(mapped.evaluate(4));

        assertSame(p, ((Predicates.MapAndEvaluatePredicate) mapped).getPredicate());
        assertSame(m, ((Predicates.MapAndEvaluatePredicate) mapped).getMapper());
        mapped.toString();
    }

    @Test(expected = NullPointerException.class)
    public void mapperPredicateNPE1() {
        Predicates.mapAndEvaluate(null, TestUtil.dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void mapperPredicateNPE2() {
        Predicates.mapAndEvaluate(TestUtil.dummy(Mapper.class), null);
    }

    @Test
    public void removeFrom() {
        Predicate<Number> p = (Predicate) Predicates.anyEquals(2, 3);
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
        CollectionPredicates.retain(list, p);
        assertEquals(2, list.size());
        assertEquals(2, list.get(0));
        assertEquals(3, list.get(1));
    }

    @Test(expected = NullPointerException.class)
    public void removeFromNPE1() {
        CollectionPredicates.retain(null, TestUtil.dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void removeFromNPE2() {
        CollectionPredicates.retain(new ArrayList(), null);
    }
}
