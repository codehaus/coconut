/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.paging;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.policy.PolicyTestUtils.addToPolicy;
import static org.coconut.cache.policy.PolicyTestUtils.empty;
import static org.coconut.test.CollectionUtils.asList;
import static org.coconut.test.CollectionUtils.seq;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of the LRU policy.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class LRUPolicyTest {

    LRUPolicy<Integer> policy;

    @Before
    public void setup() {
        policy = new LRUPolicy<Integer>();
    }

    /**
     * Test adding of new elements.
     */
    @Test
    public void testAdd() {
        addToPolicy(policy, 0, 9);
        assertEquals(seq(0, 9), policy.peekAll());
    }

    /**
     * Test removal of elements.
     */
    @Test
    public void testRemove() {
        addToPolicy(policy, 0, 9);
        assertEquals(seq(0, 9), empty(policy));
    }

    /**
     * Test removal elements by index.
     */
    @Test
    public void testRemoveIndex() {
        int[] data = addToPolicy(policy, 0, 9);
        policy.remove(data[4]);
        policy.remove(data[7]);
        policy.remove(data[0]);
        policy.remove(data[9]);
        assertEquals(asList(1, 2, 3, 5, 6, 8), empty(policy));
    }

    /**
     * Test refreshing of elements.
     */
    @Test
    public void testRefresh() {
        int[] data = addToPolicy(policy, 0, 9);
        policy.touch(data[4]);
        assertEquals(asList(0, 1, 2, 3, 5, 6, 7, 8, 9, 4), policy.peekAll());
        policy.touch(data[4]);
        assertEquals(asList(0, 1, 2, 3, 5, 6, 7, 8, 9, 4), policy.peekAll());
        policy.touch(data[0]);
        assertEquals(asList(1, 2, 3, 5, 6, 7, 8, 9, 4, 0), policy.peekAll());
        policy.touch(data[3]);
        policy.touch(data[2]);
        policy.touch(data[9]);
        assertEquals(asList(1, 5, 6, 7, 8, 4, 0, 3, 2, 9), empty(policy));
    }

    /**
     * Test the copy constructor.
     */
    @Test
    public void testCopyConstructor() {
        addToPolicy(policy, 0, 9);
        LRUPolicy<Integer> copy = new LRUPolicy<Integer>(policy);
        assertEquals(policy.peekAll(), copy.peekAll());
    }

    /**
     * Test the clone method.
     */
    @Test
    public void testClone() {
        addToPolicy(policy, 0, 9);
        LRUPolicy<Integer> copy = policy.clone();
        assertEquals(policy.peekAll(), copy.peekAll());
    }

    /**
     * Test illegal specification of the initial size.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalSize() {
        new LRUPolicy<Integer>(-1);
    }

    /**
     * Test the single peek object method.
     */
    @Test
    public void testPeek() {
        addToPolicy(policy, 0, 9);
        assertEquals(0, policy.peek().intValue());
    }

    /**
     * Test the get size method.
     */
    @Test
    public void testGetSize() {
        addToPolicy(policy, 0, 9);
        assertEquals(10, policy.getSize());
    }

    /**
     * Test that toString doesn't fail
     */
    @Test
    public void testToString() {
        addToPolicy(policy, 0, 9);
        assertTrue(policy.toString().contains("" + policy.getSize()));
    }

    @Test
    public void testClear() {
        addToPolicy(policy, 0, 9);
        policy.clear();
        assertNull(policy.evictNext());
        assertEquals(0, policy.getSize());
    }

    @Test
    public void testUpdate() {
        int[] result = addToPolicy(policy, 0, 9);
        policy.update(result[4], 123);
        assertEquals(123, policy.evict(5).get(4).intValue());
    }
}
