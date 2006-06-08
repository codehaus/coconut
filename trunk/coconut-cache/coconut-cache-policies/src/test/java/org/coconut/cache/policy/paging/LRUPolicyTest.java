package org.coconut.cache.policy.paging;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.policy.PolicyTestUtils.addToPolicy;
import static org.coconut.cache.policy.PolicyTestUtils.empty;
import static org.coconut.test.CollectionUtils.asList;
import static org.coconut.test.CollectionUtils.seq;
import junit.framework.JUnit4TestAdapter;

import org.coconut.cache.policy.ReplacementPolicy;
import org.junit.Test;

/**
 * Test of the LRU policy.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class LRUPolicyTest {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LRUPolicyTest.class);
    }

    public LRUPolicy<Integer> createPolicy() {
        return new LRUPolicy<Integer>();
    }

    /**
     * Test adding of new elements.
     */
    @Test
    public void testAdd() {
        ReplacementPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        assertEquals(seq(0, 9), policy.peekAll());
    }

    /**
     * Test removal of elements.
     */
    @Test
    public void testRemove() {
        ReplacementPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        assertEquals(seq(0, 9), empty(policy));
    }

    /**
     * Test removal elements by index.
     */
    @Test
    public void testRemoveIndex() {
        ReplacementPolicy<Integer> policy = createPolicy();
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
        ReplacementPolicy<Integer> policy = createPolicy();
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
        LRUPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        LRUPolicy<Integer> copy = new LRUPolicy<Integer>(policy);
        assertEquals(policy.peekAll(), copy.peekAll());
    }

    /**
     * Test the clone method.
     */
    @Test
    public void testClone() {
        LRUPolicy<Integer> policy = createPolicy();
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
        ReplacementPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        assertEquals(0, policy.peek().intValue());
    }

    /**
     * Test the get size method.
     */
    @Test
    public void testGetSize() {
        LRUPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        assertEquals(10, policy.getSize());
    }

    /**
     * Test that toString doesn't fail
     */
    @Test
    public void testToString() {
        LRUPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        assertTrue(policy.toString().contains("" + policy.getSize()));
    }

    @Test
    public void tetss() {
        LRUPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
    }
}