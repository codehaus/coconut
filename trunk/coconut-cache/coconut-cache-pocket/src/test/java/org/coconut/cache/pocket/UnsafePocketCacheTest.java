/**
 * 
 */
package org.coconut.cache.pocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.coconut.apm.sandbox.Usager;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class UnsafePocketCacheTest extends TestCase {

    /**
     * The number of elements to place in collections, arrays, etc.
     */
    static final int SIZE = 20;

    // Some convenient Integer constants

    static final Integer zero =Integer.valueOf(0);

    static final Integer one = Integer.valueOf(1);

    static final Integer two = Integer.valueOf(2);

    static final Integer three = Integer.valueOf(3);

    static final Integer four = Integer.valueOf(4);

    static final Integer five = Integer.valueOf(5);

    static final Integer six = Integer.valueOf(6);

    static final Integer seven = Integer.valueOf(7);

    static final Integer eight = Integer.valueOf(8);

    static final Integer nine = Integer.valueOf(9);
    
    static final Integer ten = Integer.valueOf(10);

    static final Integer m1 = Integer.valueOf(-1);

    static final Integer m2 = Integer.valueOf(-2);

    static final Integer m3 = Integer.valueOf(-3);

    static final Integer m4 = Integer.valueOf(-4);

    static final Integer m5 = Integer.valueOf(-5);

    static final Integer m6 =Integer.valueOf(-6);

    static final Integer m10 = Integer.valueOf(-10);

    static ValueLoader<Integer, String> vl= new IntegerToStringValueLoader();
    /**
     * Create a map from Integers 1-5 to Strings "A"-"E".
     */
    private static UnsafePocketCache<Integer, String> map5() {
        UnsafePocketCache<Integer, String> map = new UnsafePocketCache<Integer, String>(vl,10);
        assertTrue(map.isEmpty());
        map.put(one, "A");
        map.put(two, "B");
        map.put(three, "C");
        map.put(four, "D");
        map.put(five, "E");
        assertFalse(map.isEmpty());
        assertEquals(5, map.size());
        return map;
    }

    /**
     * clear removes all pairs
     */
    public void testClear() {
        UnsafePocketCache map = map5();
        map.clear();
        assertEquals(map.size(), 0);
    }

    /**
     * Maps with same contents are equal
     */
    public void testEquals() {
        UnsafePocketCache map1 = map5();
        UnsafePocketCache map2 = map5();
        assertEquals(map1, map2);
        assertEquals(map2, map1);
        map1.clear();
        assertFalse(map1.equals(map2));
        assertFalse(map2.equals(map1));
    }

    /**
     * containsKey returns true for contained key
     */
    public void testContainsKey() {
        UnsafePocketCache map = map5();
        assertTrue(map.containsKey(one));
        assertFalse(map.containsKey(zero));
    }

    /**
     * containsValue returns true for held values
     */
    public void testContainsValue() {
        UnsafePocketCache map = map5();
        assertTrue(map.containsValue("A"));
        assertFalse(map.containsValue("Z"));
    }

    /**
     * get returns the correct element at the given key, or null if not present
     */
    public void testGet() {
        UnsafePocketCache map = map5();
        assertEquals("A", (String) map.get(one));
        assertNull(map.get(6));
    }

    /**
     * isEmpty is true of empty map and false for non-empty
     */
    public void testIsEmpty() {
        UnsafePocketCache<Integer, String> empty = new UnsafePocketCache<Integer, String>(vl,10);
        UnsafePocketCache map = map5();
        assertTrue(empty.isEmpty());
        assertFalse(map.isEmpty());
    }

    /**
     * keySet returns a Set containing all the keys
     */
    public void testKeySet() {
        UnsafePocketCache map = map5();
        Set s = map.keySet();
        assertEquals(5, s.size());
        assertTrue(s.contains(one));
        assertTrue(s.contains(two));
        assertTrue(s.contains(three));
        assertTrue(s.contains(four));
        assertTrue(s.contains(five));
    }

    /**
     * keySet.toArray returns contains all keys
     */
    public void testKeySetToArray() {
        UnsafePocketCache<Integer, String> map = map5();
        Set<Integer> s = map.keySet();
        Object[] ar = s.toArray();
        assertTrue(s.containsAll(Arrays.asList(ar)));
        assertEquals(5, ar.length);
        ar[0] = m10;
        assertFalse(s.containsAll(Arrays.asList(ar)));
    }

    /**
     * Values.toArray contains all values
     */
    public void testValuesToArray() {
        UnsafePocketCache<Integer, String> map = map5();
        Collection<String> v = map.values();
        String[] ar = v.toArray(new String[0]);
        ArrayList<String> s = new ArrayList<String>(Arrays.asList(ar));
        assertEquals(5, ar.length);
        assertTrue(s.contains("A"));
        assertTrue(s.contains("B"));
        assertTrue(s.contains("C"));
        assertTrue(s.contains("D"));
        assertTrue(s.contains("E"));
    }

    /**
     * entrySet.toArray contains all entries
     */
    public void testEntrySetToArray() {
        UnsafePocketCache map = map5();
        Set s = map.entrySet();
        Object[] ar = s.toArray();
        assertEquals(5, ar.length);
        for (int i = 0; i < 5; ++i) {
            assertTrue(map.containsKey(((Map.Entry) (ar[i])).getKey()));
            assertTrue(map.containsValue(((Map.Entry) (ar[i])).getValue()));
        }
    }

    /**
     * values collection contains all values
     */
    public void testValues() {
        UnsafePocketCache map = map5();
        Collection s = map.values();
        assertEquals(5, s.size());
        assertTrue(s.contains("A"));
        assertTrue(s.contains("B"));
        assertTrue(s.contains("C"));
        assertTrue(s.contains("D"));
        assertTrue(s.contains("E"));
    }

    /**
     * entrySet contains all pairs
     */
    public void testEntrySet() {
        UnsafePocketCache map = map5();
        Set s = map.entrySet();
        assertEquals(5, s.size());
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            assertTrue((e.getKey().equals(one) && e.getValue().equals("A"))
                    || (e.getKey().equals(two) && e.getValue().equals("B"))
                    || (e.getKey().equals(three) && e.getValue().equals("C"))
                    || (e.getKey().equals(four) && e.getValue().equals("D"))
                    || (e.getKey().equals(five) && e.getValue().equals("E")));
        }
    }

    /**
     * putAll adds all key-value pairs from the given map
     */
    public void testPutAll() {
        UnsafePocketCache<Integer, String> empty = new UnsafePocketCache<Integer, String>(vl,10);
        UnsafePocketCache<Integer, String> map = map5();
        empty.putAll(map);
        assertEquals(5, empty.size());
        assertTrue(empty.containsKey(one));
        assertTrue(empty.containsKey(two));
        assertTrue(empty.containsKey(three));
        assertTrue(empty.containsKey(four));
        assertTrue(empty.containsKey(five));
    }

    /**
     * putAll adds all key-value pairs from the given map
     */
    public void testHardLimit() {
        UnsafePocketCache<Integer, String> empty = new UnsafePocketCache<Integer, String>(vl, 3);
        UnsafePocketCache<Integer, String> map = map5();
        empty.putAll(map);
        assertEquals(3, empty.size());
    }
    
    /**
     * putAll adds all key-value pairs from the given map
     */
    public void testHardLimitOrder() {
        UnsafePocketCache<Integer, String> map = new UnsafePocketCache<Integer, String>(vl, 3);
        map.put(one, "A");
        map.put(two, "B");
        map.put(three, "C");
        map.put(four, "D");
        map.put(five, "E");
        assertTrue(map.containsKey(five));
        assertTrue(map.containsKey(four));
        assertTrue(map.containsKey(three));
        assertFalse(map.containsKey(two));
        assertFalse(map.containsKey(one));
    }
    
    /**
     * putAll adds all key-value pairs from the given map
     */
    public void testHardLimitAccess() {
        UnsafePocketCache<Integer, String> map = new UnsafePocketCache<Integer, String>(vl, 5);
        map.put(one, "A");
        map.put(two, "B");
        map.put(three, "C");
        map.put(four, "D");
        map.put(five, "E");
        map.get(two);
        map.get(four);
        map.peek(one);

        map.put(six, "F");
        assertFalse(map.containsKey(one));
        
        map.put(seven, "G");
        assertFalse(map.containsKey(three));

        map.put(eight, "H");
        assertFalse(map.containsKey(five));

        map.put(nine, "I");
        assertFalse(map.containsKey(two));

        map.put(ten, "J");
        assertFalse(map.containsKey(four));

    }
    /**
     * putIfAbsent works when the given key is not present
     */
    public void testPutIfAbsent() {
        UnsafePocketCache<Integer, String> map = map5();
        map.putIfAbsent(six, "Z");
        assertTrue(map.containsKey(six));
    }

    /**
     * putIfAbsent does not add the pair if the key is already present
     */
    public void testPutIfAbsent2() {
        UnsafePocketCache<Integer, String> map = map5();
        assertEquals("A", map.putIfAbsent(one, "Z"));
    }

    /**
     * replace fails when the given key is not present
     */
    public void testReplace() {
        UnsafePocketCache<Integer, String> map = map5();
        assertNull(map.replace(six, "Z"));
        assertFalse(map.containsKey(six));
    }

    /**
     * replace succeeds if the key is already present
     */
    public void testReplace2() {
        UnsafePocketCache<Integer, String> map = map5();
        assertNotNull(map.replace(one, "Z"));
        assertEquals("Z", map.get(one));
    }

    /**
     * replace value fails when the given key not mapped to expected value
     */
    public void testReplaceValue() {
        UnsafePocketCache<Integer, String> map = map5();
        assertEquals("A", map.get(one));
        assertFalse(map.replace(one, "Z", "Z"));
        assertEquals("A", map.get(one));
    }

    /**
     * replace value succeeds when the given key mapped to expected value
     */
    public void testReplaceValue2() {
        UnsafePocketCache<Integer, String> map = map5();
        assertEquals("A", map.get(one));
        assertTrue(map.replace(one, "A", "Z"));
        assertEquals("Z", map.get(one));
    }

    /**
     * remove removes the correct key-value pair from the map
     */
    public void testRemove() {
        UnsafePocketCache map = map5();
        map.remove(five);
        assertEquals(4, map.size());
        assertFalse(map.containsKey(five));
    }

    /**
     * remove(key,value) removes only if pair present
     */
    public void testRemove2() {
        UnsafePocketCache map = map5();
        map.remove(five, "E");
        assertEquals(4, map.size());
        assertFalse(map.containsKey(five));
        map.remove(four, "A");
        assertEquals(4, map.size());
        assertTrue(map.containsKey(four));

    }

    /**
     * size returns the correct values
     */
    public void testSize() {
        UnsafePocketCache map = map5();
        UnsafePocketCache<Integer, String> empty = new UnsafePocketCache<Integer, String>(vl,10);
        assertEquals(0, empty.size());
        assertEquals(5, map.size());
    }

    /**
     * toString contains toString of elements
     */
    public void testToString() {
        UnsafePocketCache map = map5();
        String s = map.toString();
        for (int i = 1; i <= 5; ++i) {
            assertTrue(s.indexOf(String.valueOf(i)) >= 0);
        }
    }

    // Exception tests

    /**
     * Cannot create with negative capacity
     */
    public void testConstructor1() {
        try {
            new UnsafePocketCache<Integer, String>(vl, -1, 0);
            shouldThrow();
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Cannot create with negative concurrency level
     */
    public void testConstructor2() {
        try {
            new UnsafePocketCache<Integer, String>(vl, 1, 0.0f);
            shouldThrow();
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Cannot create with negative concurrency level
     */
    public void testConstructor3() {
        try {
            new UnsafePocketCache<Integer, String>(vl, 10, Float.NaN);
            shouldThrow();
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Cannot create with only negative capacity
     */
    public void testConstructor4() {
        try {
            new UnsafePocketCache<Integer, String>(vl, -1);
            shouldThrow();
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * get(null) throws NPE
     */
    public void testGet_NullPointerException() {
        try {
            UnsafePocketCache<Integer, String> c = new UnsafePocketCache<Integer, String>(vl, 5);
            c.get(null);
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * containsKey(null) throws NPE
     */
    public void testContainsKey_NullPointerException() {
        try {
            UnsafePocketCache<Integer, String> c = new UnsafePocketCache<Integer, String>(vl, 5);
            c.containsKey(null);
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * containsValue(null) throws NPE
     */
    public void testContainsValue_NullPointerException() {
        try {
            UnsafePocketCache<Integer, String> c = new UnsafePocketCache<Integer, String>(vl, 5);
            c.containsValue(null);
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * put(null,x) throws NPE
     */
    public void testPut1_NullPointerException() {
        try {
            UnsafePocketCache<Integer, String> c = new UnsafePocketCache<Integer, String>(vl, 5);
            c.put(null, "whatever");
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * put(x, null) throws NPE
     */
    public void testPut2_NullPointerException() {
        try {
            UnsafePocketCache c = new UnsafePocketCache(vl, 5);
            c.put("whatever", null);
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * putIfAbsent(null, x) throws NPE
     */
    public void testPutIfAbsent1_NullPointerException() {
        try {
            UnsafePocketCache c = new UnsafePocketCache(vl, 5);
            c.putIfAbsent(null, "whatever");
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * replace(null, x) throws NPE
     */
    public void testReplace_NullPointerException() {
        try {
            UnsafePocketCache c = new UnsafePocketCache(vl, 5);
            c.replace(null, "whatever");
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * replace(null, x, y) throws NPE
     */
    public void testReplaceValue_NullPointerException() {
        try {
            UnsafePocketCache c = new UnsafePocketCache(vl, 5);
            c.replace(null, one, "whatever");
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * putIfAbsent(x, null) throws NPE
     */
    public void testPutIfAbsent2_NullPointerException() {
        try {
            UnsafePocketCache c = new UnsafePocketCache(vl, 5);
            c.putIfAbsent("whatever", null);
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * replace(x, null) throws NPE
     */
    public void testReplace2_NullPointerException() {
        try {
            UnsafePocketCache c = new UnsafePocketCache(vl, 5);
            c.replace("whatever", null);
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * replace(x, null, y) throws NPE
     */
    public void testReplaceValue2_NullPointerException() {
        try {
            UnsafePocketCache c = new UnsafePocketCache(vl, 5);
            c.replace("whatever", null, "A");
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * replace(x, y, null) throws NPE
     */
    public void testReplaceValue3_NullPointerException() {
        try {
            UnsafePocketCache c = new UnsafePocketCache(vl, 5);
            c.replace("whatever", one, null);
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * remove(null) throws NPE
     */
    public void testRemove1_NullPointerException() {
        try {
            UnsafePocketCache c = new UnsafePocketCache(vl, 5);
            c.put("sadsdf", "asdads");
            c.remove(null);
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * remove(null, x) throws NPE
     */
    public void testRemove2_NullPointerException() {
        try {
            UnsafePocketCache c = new UnsafePocketCache(vl, 5);
            c.put("sadsdf", "asdads");
            c.remove(null, "whatever");
            shouldThrow();
        } catch (NullPointerException e) {
        }
    }

    /**
     * remove(x, null) returns false
     */
    public void testRemove3() {
        try {
            UnsafePocketCache c = new UnsafePocketCache(vl, 5);
            c.put("sadsdf", "asdads");
            c.remove("sadsdf", null);
            shouldThrow();
        } catch (NullPointerException e) {
            /* ignore */
        }
    }

    private void shouldThrow() {
        fail("Should throw exception");
    }
}
