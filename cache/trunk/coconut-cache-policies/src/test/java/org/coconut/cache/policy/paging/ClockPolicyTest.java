/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.paging;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.policy.PolicyTestUtils.addToPolicy;
import static org.coconut.cache.policy.PolicyTestUtils.empty;
import static org.coconut.test.CollectionTestUtil.asList;
import static org.coconut.test.CollectionTestUtil.seq;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of Clock policy.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class ClockPolicyTest {

    ClockPolicy<Integer> policy;

    @Before
    public void setUp() {
        policy = new ClockPolicy<Integer>();
    }

    /**
     * Test adding of new elements.
     */
    @Test
    public void testAddAndPeekAll() {
        addToPolicy(policy, 0, 9);
        assertEquals(seq(0, 9), policy.peekAll());
    }

    @Test
    public void testEmptyPeek() {
        ClockPolicy<Integer> policy = new ClockPolicy<Integer>(1);
        assertEquals(null, policy.peek());
    }
    
    @Test
    public void testEmptyPeekAll() {
        ClockPolicy<Integer> policy = new ClockPolicy<Integer>(1);
        assertEquals(0, policy.peekAll().size());
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
        assertEquals(asList(1, 2, 3, 5, 6, 7, 8, 9, 0, 4), policy.peekAll());

        policy.touch(data[3]);
        policy.touch(data[2]);
        policy.touch(data[9]);
        assertEquals(asList(1, 5, 6, 7, 8, 0, 2, 3, 4, 9), policy.peekAll());
    }

    @Test
    public void testRefresh2() {
        ClockPolicy<Integer> list = new ClockPolicy<Integer>(5);
        int i = list.add(1);
        list.add(2);
        int i3 = list.add(3);
        int i4 = list.add(4);

        list.touch(i3);
        list.touch(i);
        list.touch(i);
        list.touch(i4);
        list.touch(i3);
        list.touch(i);
        assertEquals(4, list.getSize());

        assertEquals(2, list.evictNext().intValue());
        assertEquals(1, list.evictNext().intValue());
        assertEquals(3, list.evictNext().intValue());
        assertEquals(4, list.evictNext().intValue());
        assertEquals(0, list.getSize());
    }

    /**
     * Test removal of elements.
     */
    @Test
    public void testRemove() {
        addToPolicy(policy, 0, 9);
        assertEquals(seq(0, 9), empty(policy));
    }

    @Test
    public void testRemove2() {
        ClockPolicy<Integer> list = new ClockPolicy<Integer>(1);
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(1, list.evictNext().intValue());
        assertEquals(2, list.getSize());

        assertEquals(2, list.evictNext().intValue());
        assertEquals(1, list.getSize());

        assertEquals(3, list.evictNext().intValue());
        assertEquals(0, list.getSize());
    }

    @Test
    public void testRemoveEmpty() {
        ClockPolicy<Integer> list = new ClockPolicy<Integer>(1);
        assertNull(list.evictNext());
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

    @Test
    public void testRemoveIndex2() {
        ClockPolicy<Integer> list = new ClockPolicy<Integer>(1);
        int i1 = list.add(1);
        list.add(2);
        int i3 = list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        int i7 = list.add(7);
        list.add(8);
        int i9 = list.add(9);

        assertEquals(3, list.remove(i3).intValue());
        assertEquals(7, list.remove(i7).intValue());
        assertEquals(1, list.remove(i1).intValue());
        assertEquals(9, list.remove(i9).intValue());

        assertEquals(2, list.evictNext().intValue());
        assertEquals(4, list.evictNext().intValue());
        assertEquals(5, list.evictNext().intValue());
        assertEquals(6, list.evictNext().intValue());
        assertEquals(8, list.evictNext().intValue());
        assertEquals(0, list.getSize());
    }

    @Test
    public void testResize() {
        ClockPolicy<Integer> policy = new ClockPolicy<Integer>(1);
        policy.add(1);
        policy.add(2);
        policy.add(3);
        policy.add(4);
        policy.add(5);
        policy.add(6);
        policy.add(7);
        assertEquals(7, policy.getSize());
    }

    @Test
    public void testSize() {
        ClockPolicy<Integer> policy = new ClockPolicy<Integer>(5);
        policy.add(1);
        assertEquals(1, policy.getSize());

        policy.add(2);
        assertEquals(2, policy.getSize());

        policy.add(3);
        assertEquals(3, policy.getSize());
    }

    /**
     * Test the copy constructor.
     */
    @Test
    public void testCopyConstructor() {
        addToPolicy(policy, 0, 9);
        ClockPolicy<Integer> copy = new ClockPolicy<Integer>(policy);
        assertEquals(policy.peekAll(), copy.peekAll());
    }

    /**
     * Test the clone method.
     */
    @Test
    public void testClone() {
        addToPolicy(policy, 0, 9);
        ClockPolicy<Integer> copy = policy.clone();
        assertEquals(policy.peekAll(), copy.peekAll());
    }

    /**
     * Test illegal specification of the initial size.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalSize() {
        new ClockPolicy<Integer>(-1);
    }

    /**
     * Test the single peek object method.
     */
    @Test
    public void testPeek() {
        addToPolicy(policy, 0, 9);
        assertEquals(0, policy.peek().intValue());
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
    /**
     * Test that toString doesn't fail.
     */
    @Test
    public void testToString() {
        addToPolicy(policy, 0, 9);
        assertTrue(policy.toString().contains("" + policy.getSize()));
    }
}
