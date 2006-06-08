package org.coconut.cache.defaults.util;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M5;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheEntryMapTest {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CacheEntryMapTest.class);
    }

    protected CacheEntryMapStub c0;

    protected CacheEntryMapStub c1;

    protected CacheEntryMapStub c2;

    protected CacheEntryMapStub c3;

    protected CacheEntryMapStub c4;

    protected CacheEntryMapStub c5;

    protected CacheEntryMapStub c6;

    @Before
    public void setUp() throws Exception {
        c0 = new CacheEntryMapStub();
        c1 = new CacheEntryMapStub(CacheTestBundle.createMap(1));
        c2 = new CacheEntryMapStub(CacheTestBundle.createMap(2));
        c3 = new CacheEntryMapStub(CacheTestBundle.createMap(3));
        c4 = new CacheEntryMapStub(CacheTestBundle.createMap(4));
        c5 = new CacheEntryMapStub(CacheTestBundle.createMap(5));
        c6 = new CacheEntryMapStub(CacheTestBundle.createMap(6));
    }

    /**
     * size returns the correct values.
     */
    @Test
    public void testSize() {
        assertEquals(0, c0.size());
        assertEquals(5, c5.size());
    }

    /**
     * isEmpty is true of empty map and false for non-empty.
     */
    @Test
    public void testIsEmpty() {
        assertTrue(c0.isEmpty());
        assertFalse(c5.isEmpty());
    }

    /**
     * containsKey returns true for contained key.
     */
    @Test
    public void testContainsKey() {
        assertTrue(c5.containsKey(1));
        assertFalse(c5.containsKey(6));
    }

    /**
     * containsKey(null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testContainsKey_NullPointerException() {
        c5.containsKey(null);
    }

    /**
     * containsValue returns true for held values.
     */
    @Test
    public void testContainsValue() {
        assertTrue(c5.valueContainsValue("A"));
        assertFalse(c5.valueContainsValue("Z"));
    }

    /**
     * containsValue(null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testContainsValue_NullPointerException() {
        c5.valueContainsValue(null);
    }

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
    // System.out.println(s);
    // throw ar;
    // }
    // }

    /**
     * get(null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testGetNull() {
        c5.get(null);
    }

    /**
     * Test simple get.
     */
    @Test
    public void testGet() {
        assertEquals(M1.getValue(), c5.getValue(M1.getKey()));
        assertEquals(M5.getValue(), c5.getValue(M5.getKey()));
    }

    static class CacheEntryMapStub
            extends
            CacheEntryMap<Integer, String, CacheEntryMap.Entry<Integer, String>> {

        public CacheEntryMapStub() {
        }

        public CacheEntryMapStub(Map<? extends Integer, ? extends String> m) {
            for (Map.Entry<? extends Integer, ? extends String> entry : m
                    .entrySet()) {
                EntryStub stub = new EntryStub(entry.getKey(), entry.getValue());
                put(stub.getKey(), stub);
            }
        }

    }

    static class EntryStub extends CacheEntryMap.Entry<Integer, String> {

        public EntryStub(Integer key, String value) {
            super(key, value);
        }
    }
}
