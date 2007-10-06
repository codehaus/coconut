/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.eviction;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.asMap;
import static org.coconut.test.CollectionUtils.asSet;

import org.coconut.cache.policy.Policies;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class SimplePolicyEviction extends AbstractCacheTCKTest {

    @Test
    public void testSimpleSize() {
        c = newCache(newConf().eviction().setPolicy(Policies.newLRU()).setMaximumSize(5)
                .c());
        for (int i = 0; i < 5; i++) {
            put(i, Integer.toString(i));
        }
        assertEquals(5, c.size());
        for (int i = 5; i < 10; i++) {
            put(i, Integer.toString(i));
            assertEquals(5, c.size());
        }
    }

    @Test
    public void testEviction() {
        c = newCache(newConf().eviction().setPolicy(Policies.newLRU()).setMaximumSize(5)
                .c());
        for (int i = 0; i < 5; i++) {
            c.put(i, Integer.toString(i));
        }
        assertEquals(asSet(0, 1, 2, 3, 4), c.keySet());
        c.put(5, "");
        assertEquals(asSet(1, 2, 3, 4, 5), c.keySet());
        c.put(6, "");
        assertEquals(asSet(2, 3, 4, 5, 6), c.keySet());

        c.put(7, "");
        assertEquals(asSet(3, 4, 5, 6, 7), c.keySet());

        c.put(8, "");
        assertEquals(asSet(4, 5, 6, 7, 8), c.keySet());
    }

    @Test
    public void testTouch() {
        c = newCache(newConf().eviction().setPolicy(Policies.newLRU()).setMaximumSize(10)
                .c());
        for (int i = 0; i < 10; i++) {
            c.put(i, Integer.toString(i));
        }
        c.get(4);
        c.get(4);
        c.get(0);
        c.get(3);
        c.get(2);
        c.get(9);

        replaceAndCheck(10, 1);
        replaceAndCheck(11, 5);
        replaceAndCheck(12, 6);
        replaceAndCheck(13, 7);
        replaceAndCheck(14, 8);
        replaceAndCheck(15, 4);
        replaceAndCheck(16, 0);
        replaceAndCheck(17, 3);
        replaceAndCheck(18, 2);
        replaceAndCheck(19, 9);
    }

    @Test
    public void testPeek() {
        c = newCache(newConf().eviction().setPolicy(Policies.newLRU()).setMaximumSize(5)
                .c());
        for (int i = 0; i < 5; i++) {
            c.put(i, Integer.toString(i));
        }
        c.peek(0);
        c.peek(2);
        assertEquals(asSet(0, 1, 2, 3, 4), c.keySet());
        c.put(5, "");
        assertEquals(asSet(1, 2, 3, 4, 5), c.keySet());
        c.put(6, "");
        assertEquals(asSet(2, 3, 4, 5, 6), c.keySet());
    }

    void replaceAndCheck(Integer newKey, Integer oldKey) {
        assertNull(c.put(newKey, "foo"));
        assertFalse(c.containsKey(oldKey));
    }

    @Test
    public void testRejectEntry() {
        RejectEntriesPolicy rep = new RejectEntriesPolicy();
        c = newCache(newConf().eviction().setPolicy(rep).setMaximumSize(5).c().setClock(
                clock));

        c.put(1, "A");
        rep.reject = true;
        c.put(2, "B");
        rep.reject = false;
        c.put(3, "C");
        assertEquals(2, c.size());
        assertFalse(c.containsKey(2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRejectEntryPutAll() {
        c = newCache(newConf().eviction().setPolicy(new Reject2EntriesPolicy())
                .setMaximumSize(5).c().setClock(clock));
        // the reject2EntriesPolicy is kind of a hack until we are
        // clear with one goes into add() for the policy
        c.putAll(asMap(M1, M2, M3));
        assertEquals(2, c.size());
        assertFalse(c.containsKey(2));
    }

    // put of elements

    // 
    /**
     * Currently put is ignored with regards to cache policies, but should just inherit
     * the previous entry.
     */
    @Test
    public void testPutOverridesPreviousValue() {
        c = newCache(newConf().eviction().setPolicy(Policies.newLRU()).setMaximumSize(2)
                .c());
        c.put(M1.getKey(), M1.getValue());
        c.put(M2.getKey(), M2.getValue());
        c.get(M1.getKey());
        c.put(M2.getKey(), M3.getValue());
        c.get(M2.getKey());
        replaceAndCheck(10, M1.getKey());
        replaceAndCheck(11, M2.getKey());
    }

    @Test
    public void testPutAllOverridesPreviousValue() {
        c = newCache(newConf().eviction().setPolicy(Policies.newLRU()).setMaximumSize(3)
                .c());
        put(M1);
        put(M2);
        put(M3);
        get(M3);
        get(M2);
        get(M1);
        remove(M2);
        put(M4);
        get(M4);
        assertTrue(c.containsKey(M4.getKey()));
        assertTrue(c.containsKey(M3.getKey()));
        assertFalse(c.containsKey(M2.getKey()));
        assertTrue(c.containsKey(M1.getKey()));

        replaceAndCheck(10, M3.getKey());
        replaceAndCheck(11, M1.getKey());
        replaceAndCheck(12, M4.getKey());

    }

    @Test
    public void testRemoveEntry() {
        c = newCache(newConf().eviction().setPolicy(Policies.newLRU()).setMaximumSize(2)
                .c());
        c.put(M1.getKey(), M1.getValue());
        c.put(M2.getKey(), M2.getValue());
        c.get(M1.getKey());
        c.remove(M1.getKey());
        c.put(M3.getKey(), M3.getValue());
    }

    @Test
    public void testExpiration() {
        // cross check with expiration.

        c = newCache(newConf().eviction().setPolicy(Policies.newLRU()).setMaximumSize(5)
                .c().setClock(clock));

        for (int i = 0; i < 5; i++) {
            c.put(i, Integer.toString(i));
        }
        clock.incrementRelativeTime(3);
        expiration().purgeExpired();
        // ?? need to figure out how to handle puts
    }

    @SuppressWarnings( { "unchecked", "serial" })
    static class RejectEntriesPolicy extends LRUPolicy {
        boolean reject;

        @Override
        public int add(Object data) {
            if (!reject) {
                return super.add(data);
            } else {
                return -1;
            }
        }
    }

    @SuppressWarnings( { "unchecked", "serial" })
    static class Reject2EntriesPolicy extends LRUPolicy {
        @Override
        public int add(Object data) {
            if (data.equals(2) || data.equals(M2)) {
                return -1;
            } else {
                return super.add(data);
            }
        }
    }
}
