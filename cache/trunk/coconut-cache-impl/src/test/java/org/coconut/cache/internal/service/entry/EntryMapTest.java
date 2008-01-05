/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.internal.service.entry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.coconut.test.TestUtil.dummy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.coconut.attribute.Attributes;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.internal.service.servicemanager.ServiceComposer;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@RunWith(JMock.class)
public class EntryMapTest {
    ServiceComposer sc = ServiceComposer.compose(new UnsynchronizedCache(),
            dummy(InternalCacheSupport.class), CacheConfiguration.create(), Collections.EMPTY_LIST);

    Mockery context = new JUnit4Mockery();

    @Test(expected = ConcurrentModificationException.class)
    public void concurrentModification() {

        final InternalCacheSupport ics = context.mock(InternalCacheSupport.class);
        final Cache c = context.mock(Cache.class);
        context.checking(new Expectations() {
            {
                allowing(ics).checkRunning("iterator", false);
            }
        });
        EntryMap s = new EntryMap(sc, null, null, ics);
        s.put(new EntryStub(1, "A"));
        s.put(new EntryStub(2, "B"));
        Iterator iter1 = s.keySet(c).iterator();
        iter1.next();
        s._remove(1, null);
        iter1.next();
    }

    @Test
    public void rehash() {
        InternalCacheSupport ics = null;
        EntryMap s = new EntryMap(sc, null, null, ics);
        Random r = new Random(12312312);
        ArrayList<Integer> al = new ArrayList<Integer>();
        for (int i = 0; i < 20; i++) {
            int ia = r.nextInt();
            al.add(ia);
            s.put(new EntryStub(ia, "" + ia));
        }
        Collections.shuffle(al);
        while (al.size() > 0) {
            int take = al.remove(al.size() - 1);
            Map.Entry o = s._remove(take, null);
            assertEquals("" + take, o.getValue());
        }
    }

    @Test
    public void testNotsa() {
        EntryMap<Integer, String> s = new EntryMap<Integer, String>(sc, null, null, null);
        s.put(new EntryStub(-348653132, "a"));
        s.put(new EntryStub(772636595, "b"));
        CacheEntry<?, ?> ac1 = s._remove(-348653132, null);
        CacheEntry<?, ?> ac2 = s._remove(772636595, null);
        assertNotNull(ac1);
        assertNotNull(ac2);
    }

    public boolean stNots() {
        EntryMap<Integer, String> s = new EntryMap<Integer, String>(sc, null, null, null);
        Random r = new Random();
        ArrayList<Integer> al = new ArrayList<Integer>();
        for (int i = 0; i < 30; i++) {
            int ia = r.nextInt();
            al.add(ia);
            s.put(new EntryStub(ia, "foo" + ia));
        }
        ArrayList<Integer> removeOrder = new ArrayList<Integer>(al);
        Collections.shuffle(removeOrder);
        ArrayList<Integer> removeOrderCopy = new ArrayList<Integer>(removeOrder);
        while (removeOrder.size() > 0) {
            int take = removeOrder.remove(removeOrder.size() - 1);
            Map.Entry o = s._remove(take, null);
            if (o == null) {
                System.out.println(al);
                System.out.println(removeOrderCopy);
                return false;
            }
        }
        return true;
    }

    // protected CacheEntryMapStub c0;
    //
    // protected CacheEntryMapStub c1;
    //
    // protected CacheEntryMapStub c2;
    //
    // protected CacheEntryMapStub c3;
    //
    // protected CacheEntryMapStub c4;
    //
    // protected CacheEntryMapStub c5;
    //
    // protected CacheEntryMapStub c6;
    //
    // public static junit.framework.Test suite() {
    // return new JUnit4TestAdapter(CacheEntryMapTest.class);
    // }
    //
    // @Before
    // public void setUp() throws Exception {
    // c0 = new CacheEntryMapStub();
    // c1 = new CacheEntryMapStub(CacheTestBundle.createMap(1));
    // c2 = new CacheEntryMapStub(CacheTestBundle.createMap(2));
    // c3 = new CacheEntryMapStub(CacheTestBundle.createMap(3));
    // c4 = new CacheEntryMapStub(CacheTestBundle.createMap(4));
    // c5 = new CacheEntryMapStub(CacheTestBundle.createMap(5));
    // c6 = new CacheEntryMapStub(CacheTestBundle.createMap(6));
    // }

    // /**
    // * size returns the correct values.
    // */
    // @Test
    // public void testSize() {
    // assertEquals(0, c0.size());
    // assertEquals(5, c5.size());
    // }
    //
    // /**
    // * isEmpty is true of empty map and false for non-empty.
    // */
    // @Test
    // public void testIsEmpty() {
    // assertTrue(c0.isEmpty());
    // assertFalse(c5.isEmpty());
    // }
    //
    // /**
    // * containsKey returns true for contained key.
    // */
    // @Test
    // public void testContainsKey() {
    // assertTrue(c5.containsKey(1));
    // assertFalse(c5.containsKey(6));
    // }
    //
    // /**
    // * containsKey(null) throws NPE.
    // */
    // @Test(expected = NullPointerException.class)
    // public void testContainsKey_NullPointerException() {
    // c5.containsKey(null);
    // }
    //
    // /**
    // * containsValue returns true for held values.
    // */
    // @Test
    // public void testContainsValue() {
    // assertTrue(c5.valueContainsValue("A"));
    // assertFalse(c5.valueContainsValue("Z"));
    // }
    //
    // /**
    // * containsValue(null) throws NPE.
    // */
    // @Test(expected = NullPointerException.class)
    // public void testContainsValue_NullPointerException() {
    // c5.valueContainsValue(null);
    // }

    // /**
    // * Just test that the toString() method works.
    // */
    // @Test
    // public void testToString() {
    // String s = c5.toString();
    // try {
    // for (int i = 1; i < 6; i++) {
    // assertTrue(s.indexOf(String.valueOf(i)) >= 0);
    // assertTrue(s.indexOf("" + (char) (i + 64)) >= 0);
    // }
    // } catch (AssertionError ar) {
    // throw ar;
    // }
    // }

    /**
     * get(null) throws NPE.
     */
    // @Test(expected = NullPointerException.class)
    // public void testGetNull() {
    // c5.get(null);
    // }
    //
    // /**
    // * Test simple get.
    // */
    // @Test
    // public void testGet() {
    // assertEquals(M1.getValue(), c5.getValue(M1.getKey()));
    // assertEquals(M5.getValue(), c5.getValue(M5.getKey()));
    // }
    //
    static class EntryStub extends UnsynchronizedCacheEntry<Integer, String> {

        public EntryStub(Integer key, String value) {
            super(key, value, -1, 0, 0, 0, 0, Attributes.EMPTY_ATTRIBUTE_MAP);
        }
    }
}
