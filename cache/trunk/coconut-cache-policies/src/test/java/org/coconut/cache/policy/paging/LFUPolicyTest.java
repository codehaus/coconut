/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.paging;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.policy.PolicyTestUtils.addToPolicy;
import static org.coconut.cache.policy.PolicyTestUtils.empty;
import static org.coconut.test.CollectionUtils.seq;

import java.util.Arrays;

import org.coconut.cache.policy.ReplacementPolicy;
import org.junit.Test;

/**
 * Test of the LFU policy.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */

public class LFUPolicyTest {

    public LFUPolicy<Integer> createPolicy() {
        return new LFUPolicy<Integer>();
    }

    /**
     * Test the copy constructor.
     */
    @Test
    public void LFUPolicyCopy() {
        LFUPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        LFUPolicy<Integer> copy = new LFUPolicy<Integer>(policy);
        assertEquals(policy.peekAll(), copy.peekAll());
    }

    /**
     * Tests clone.
     */
    @Test
    public void LFUPolicyClone() {
        LFUPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        assertEquals(policy.peekAll(), policy.clone().peekAll());
    }

    /**
     * Tests clone.
     */
    @Test
    public void peek() {
        LFUPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        while (policy.getSize() > 0) {
            assertEquals(policy.peek(), policy.evictNext());
        }
    }

    /**
     * Tests clone.
     */
    @Test
    public void replace() {
        LFUPolicy<Integer> policy = createPolicy();
        int[] data = addToPolicy(policy, 0, 2);
        policy.update(data[1], 5);
        assertTrue(policy.peekAll().contains(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addIAE() {
        LFUPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        policy.add(3434, -1);
    }

    /**
     * Test that toString doesn't fail.
     */
    @Test
    public void testToString() {
        LFUPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        assertTrue(policy.toString().contains("" + policy.getSize()));
    }

    /**
     * Test adding of new elements.
     */
    @Test
    public void testAdd() {
        ReplacementPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j <= i; j++) {
                policy.touch(j);
            }
        }
        assertEquals(seq(9, 0), policy.peekAll());
    }

    /**
     * Test illegal specification of the initial size.
     */
    @Test(expected = IllegalArgumentException.class)
    public void LFUPolicyIAE() {
        new LFUPolicy<Integer>(-1);
    }

    /**
     * Test adding of new elements.
     */
    @Test
    public void clear() {
        ReplacementPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        assertEquals(10, policy.getSize());
        policy.clear();
        assertEquals(0, policy.getSize());
    }

    /**
     * Test removal of elements.
     */
    @Test
    public void testRemove() {
        ReplacementPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j <= i; j++) {
                policy.touch(j);
            }
        }
        assertEquals(seq(9, 0), empty(policy));
    }

    /**
     * Test removal elements by index.
     */
    @Test
    public void testRemoveIndex() {
        LFUPolicy<Integer> policy = createPolicy();
        int[] data = addToPolicy(policy, 0, 9);
        policy.remove(data[4]);
        policy.add(4);
        policy.remove(data[4]);

        policy.remove(data[7]);
        policy.remove(data[0]);
        policy.remove(data[9]);
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j <= i; j++) {
                policy.touch(j);
            }
        }
        assertEquals(Arrays.asList(8, 6, 5, 3, 2, 1), policy.peekAll());

    }
}
