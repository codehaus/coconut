/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.jsr166yops;

import static org.codehaus.cake.jsr166yops.CollectionOps.MAP_ENTRY_TO_KEY_OP;
import static org.codehaus.cake.jsr166yops.CollectionOps.MAP_ENTRY_TO_VALUE_OP;
import static org.codehaus.cake.test.util.CollectionTestUtil.M1;
import static org.codehaus.cake.test.util.CollectionTestUtil.M1_NULL;
import static org.codehaus.cake.test.util.CollectionTestUtil.NULL_A;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.codehaus.cake.test.util.TestUtil.dummy;
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
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import jsr166y.forkjoin.Ops.Predicate;
import jsr166y.forkjoin.Ops.Procedure;

import org.codehaus.cake.test.util.TestUtil;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class CollectionOpsTest {
    Mockery context = new JUnit4Mockery();

    @Test
    public void addToCollection() {
        final Collection col = context.mock(Collection.class);
        context.checking(new Expectations() {
            {
                one(col).add(-1);
            }
        });
        CollectionOps.addToCollection(col).op(-1);
        assertIsSerializable(CollectionOps.addToCollection(new ArrayList(1)));
    }

    @Test(expected = NullPointerException.class)
    public void addToCollection_NPE() {
        CollectionOps.addToCollection(null);
    }

    @Test
    public void filter() {
        Predicate<Number> p = Predicates.<Number> anyEquals(2, 3);
        Collection<Integer> col = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
        List<Integer> list = CollectionOps.filter(col, p);
        assertEquals(2, list.size());
        assertEquals(2, list.get(0).intValue());// some compilers need intValue()
        assertEquals(3, list.get(1).intValue());// some compilers need intValue()
    }

    @Test(expected = NullPointerException.class)
    public void filter_NPE1() {
        CollectionOps.filter(null, dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void filter_NPE2() {
        CollectionOps.filter(new ArrayList(), null);
    }

    @Test
    public void filterMap() {
        Predicate<Map.Entry<Integer, String>> p = new Predicate<Map.Entry<Integer, String>>() {
            public boolean op(Map.Entry<Integer, String> element) {
                return element.getKey().equals(2) || element.getValue().equals("3");
            }
        };
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        map.put(4, "4");
        Map<Integer, String> filteredMap = CollectionOps.filterMap(map, p);
        assertEquals(2, filteredMap.size());
        assertEquals("2", filteredMap.get(2));
        assertEquals("3", filteredMap.get(3));
    }

    @Test(expected = NullPointerException.class)
    public void filterMap_NPE1() {
        CollectionOps.filterMap(null, dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void filterMap_NPE2() {
        CollectionOps.filterMap(new HashMap(), null);
    }

    @Test
    public void filterMapKey() {
        Predicate<Number> p = new Predicate<Number>() {
            public boolean op(Number element) {
                return element.equals(2) || element.equals(3);
            }
        };
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        map.put(4, "4");
        Map<Integer, String> filteredMap = CollectionOps.filterMapKeys(map, p);
        assertEquals(2, filteredMap.size());
        assertEquals("2", filteredMap.get(2));
        assertEquals("3", filteredMap.get(3));
    }

    @Test(expected = NullPointerException.class)
    public void filterMapKey_NPE1() {
        CollectionOps.filterMapKeys(null, dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void filterMapKey_NPE2() {
        CollectionOps.filterMapKeys(new HashMap(), null);
    }

    @Test
    public void filterMapValue() {
        Predicate<String> p = new Predicate<String>() {
            public boolean op(String element) {
                return element.equals("2") || element.equals("3");
            }
        };
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        map.put(4, "4");
        Map<Integer, String> filteredMap = CollectionOps.filterMapValues(map, p);
        assertEquals(2, filteredMap.size());
        assertEquals("2", filteredMap.get(2));
        assertEquals("3", filteredMap.get(3));
    }

    @Test(expected = NullPointerException.class)
    public void filterMapValue_NPE1() {
        CollectionOps.filterMapValues(null, dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void filterMapValue_NPE2() {
        CollectionOps.filterMapValues(new HashMap(), null);
    }

    @Test
    public void isAllTrue() {
        Predicate<Number> p = Predicates.<Number> anyEquals(2, 3);
        Predicate<Number> p2 = Predicates.<Number> anyEquals(1, 2, 3, 4, 5);
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
        assertFalse(CollectionOps.isAllTrue(list, p));
        assertTrue(CollectionOps.isAllTrue(list, p2));
    }

    @Test(expected = NullPointerException.class)
    public void isAllTrue_NPE1() {
        CollectionOps.isAllTrue(null, dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void isAllTrue_NPE2() {
        CollectionOps.isAllTrue(new ArrayList(), null);
    }

    @Test
    public void isAnyTrue() {
        Predicate<Number> p = Predicates.<Number> anyEquals(2, 3);
        Predicate<Number> p2 = Predicates.<Number> anyEquals(5, 6, 7, 8);
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
        assertTrue(CollectionOps.isAnyTrue(list, p));
        assertFalse(CollectionOps.isAnyTrue(list, p2));
    }

    @Test(expected = NullPointerException.class)
    public void isAnyTrue_NPE1() {
        CollectionOps.isAnyTrue(null, dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void isAnyTrue_NPE2() {
        CollectionOps.isAnyTrue(new ArrayList(), null);
    }

    /**
     * Tests {@link Mappers#CONSTANT_MAPPER}.
     */
    @Test
    public void keyFromMap() {
        assertEquals(M1.getKey(), MAP_ENTRY_TO_KEY_OP.op(M1));
        assertEquals(null, MAP_ENTRY_TO_KEY_OP.op(NULL_A));
        assertSame(MAP_ENTRY_TO_KEY_OP, CollectionOps.mapEntryToKey());
        MAP_ENTRY_TO_KEY_OP.toString(); // does not fail
        assertIsSerializable(CollectionOps.mapEntryToKey());
        assertSame(MAP_ENTRY_TO_KEY_OP, TestUtil.serializeAndUnserialize(MAP_ENTRY_TO_KEY_OP));
    }

    @Test
    public void offerToQueue() {
        final Queue q = context.mock(Queue.class);
        context.checking(new Expectations() {
            {
                one(q).offer(-1);
            }
        });
        Procedure processor = CollectionOps.offerToQueue(q);
        processor.op(-1);
        assertIsSerializable(CollectionOps.offerToQueue(new ArrayBlockingQueue(1)));
    }
    

    @Test(expected = NullPointerException.class)
    public void offerToQueue_NPE() {
        CollectionOps.offerToQueue(null);
    }

    @Test
    public void retain() {
        Predicate<Number> p = Predicates.<Number>anyEquals(2, 3);
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
        CollectionOps.retain(list, p);
        assertEquals(2, list.size());
        assertEquals(2, list.get(0).intValue());
        assertEquals(3, list.get(1).intValue());
    }

    @Test(expected = NullPointerException.class)
    public void retain_NPE1() {
        CollectionOps.retain(null, dummy(Predicate.class));
    }

    @Test(expected = NullPointerException.class)
    public void retain_NPE2() {
        CollectionOps.retain(new ArrayList(), null);
    }

    /**
     * Tests {@link Mappers#CONSTANT_MAPPER}.
     */
    @Test
    public void valueFromMap() {
        assertEquals(M1.getValue(), MAP_ENTRY_TO_VALUE_OP.op(M1));
        assertEquals(null, MAP_ENTRY_TO_VALUE_OP.op(M1_NULL));
        assertSame(MAP_ENTRY_TO_VALUE_OP, CollectionOps.mapEntryToValue());
        MAP_ENTRY_TO_VALUE_OP.toString(); // does not fail
        assertIsSerializable(CollectionOps.mapEntryToValue());
        assertSame(MAP_ENTRY_TO_VALUE_OP, TestUtil.serializeAndUnserialize(MAP_ENTRY_TO_VALUE_OP));
    }
}
