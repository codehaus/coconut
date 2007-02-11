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

import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of Clock policy.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class RandomPolicyTest {

    RandomPolicy<Integer> policy;

    @Before
    public void setup() {
        policy = new RandomPolicy<Integer>();
    }

    /**
     * Test adding of new elements.
     */
    @Test
    public void testAdd() {
        addToPolicy(policy, 0, 9);
        assertTrue(policy.peekAll().containsAll(seq(0, 9)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIAE() {
        new RandomPolicy<Integer>(-1);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNPE() {
        new RandomPolicy<Integer>(null);
    }

    @Test
    public void testAdd2() {
        RandomPolicy<Integer> list = new RandomPolicy<Integer>(5);
        list.add(1);
        assertEquals(1, list.getSize());

        list.add(2);
        assertEquals(2, list.getSize());

        list.add(3);
        assertEquals(3, list.getSize());
    }

    @Test
    public void testIntens2() {
        RandomPolicy<Integer> list = new RandomPolicy<Integer>(1);
        Set<Integer> s = new TreeSet<Integer>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        for (int i = 0; i < 40; i++) {
            s.add(i);
        }
        // list.print();
        for (int i = 0; i < 400; i++) {
            Integer data = list.evictNext();
            list.add(data);
            if (i % 20 == 0)
                list.add(i / 20 + 20);
        }
        for (;;) {
            Integer i = list.evictNext();
            if (i != null)
                s.remove(i);
            else
                break;
        }
        assertEquals(0, s.size());
        assertEquals(0, list.getSize());

    }

    /**
     * Test refreshing of elements.
     */
    @Test
    public void testRefresh() {
        int[] data = addToPolicy(policy, 0, 9);
        policy.touch(data[4]);
        policy.touch(data[4]);
        policy.touch(data[0]);
        policy.touch(data[3]);
        policy.touch(data[2]);
        policy.touch(data[9]);
        assertTrue(empty(policy).containsAll(seq(0, 9)));
    }

    /**
     * Test removal of elements.
     */
    @Test
    public void testRemove() {
        addToPolicy(policy, 0, 9);
        assertTrue(empty(policy).containsAll(seq(0, 9)));
    }

    @Test
    public void testRemove2() {
        RandomPolicy<Integer> list = new RandomPolicy<Integer>(1);
        Set<Integer> l = new TreeSet<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        l.add(1);
        l.add(2);
        l.add(3);

        l.remove(list.evictNext());
        assertEquals(2, list.getSize());

        l.remove(list.evictNext());
        assertEquals(1, list.getSize());

        l.remove(list.evictNext());
        assertEquals(0, list.getSize());

        assertEquals(0, l.size());
    }

    @Test
    public void testRemoveEmpty2() {
        RandomPolicy<Integer> list = new RandomPolicy<Integer>(1);
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
        assertTrue(empty(policy).containsAll(asList(1, 2, 3, 5, 6, 8)));
    }

    @Test
    public void testRemoveIndex2() {
        RandomPolicy<Integer> list = new RandomPolicy<Integer>(15);
        Set<Integer> l = new TreeSet<Integer>();
        for (int i = 1; i < 10; i++) {
            l.add(i);
        }
        int i1 = list.add(1);
        list.add(2);
        int i3 = list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        int i7 = list.add(7);
        list.add(8);
        int i9 = list.add(9);

        l.remove(list.remove(i3));
        l.remove(list.remove(i7));
        l.remove(list.remove(i1));
        l.remove(list.remove(i9));

        l.remove(list.evictNext());
        l.remove(list.evictNext());
        l.remove(list.evictNext());
        l.remove(list.evictNext());
        l.remove(list.evictNext());
        assertEquals(0, list.getSize());
        assertEquals(0, l.size());
    }

    @Test
    public void testRemoveNonExistingIndex2() {
        RandomPolicy<Integer> list = new RandomPolicy<Integer>(1);
        list.add(1);
        assertNull(list.remove(3));
    }

    @Test
    public void testResize2() {
        RandomPolicy<Integer> list = new RandomPolicy<Integer>(1);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        assertEquals(7, list.getSize());
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
        Integer i = -1;
        while (i != null) {
            if (i == 123) {
                return;
            }
            i = policy.evictNext();
        }
        assertTrue(false);
    }

    @Test
    public void testUpdate1() {
        RandomPolicy rp = new RandomPolicy(3);
        rp.add(6);
        int i2 = rp.add(5);
        rp.update(i2, 7);
    }
}