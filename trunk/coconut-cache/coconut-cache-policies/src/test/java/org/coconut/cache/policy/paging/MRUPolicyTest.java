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
 * Test of the MRU policy.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class MRUPolicyTest {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(MRUPolicyTest.class);
    }

    public MRUPolicy<Integer> createPolicy() {
        return new MRUPolicy<Integer>();
    }

    /**
     * Test adding of new elements.
     */
    @Test
    public void testAddAndPeekAll() {
        ReplacementPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        assertEquals(seq(9, 0), policy.peekAll());
    }

    /**
     * Test removal of elements.
     */
    @Test
    public void testRemove() {
        ReplacementPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        assertEquals(seq(9, 0), empty(policy));
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
        assertEquals(asList(8, 6, 5, 3, 2, 1), empty(policy));
    }

    /**
     * Test refreshing of elements.
     */
    @Test
    public void testRefresh() {
        ReplacementPolicy<Integer> policy = createPolicy();
        int[] data = addToPolicy(policy, 0, 9);
        policy.touch(data[4]);
        assertEquals(asList(4, 9, 8, 7, 6, 5, 3, 2, 1, 0), policy.peekAll());
        policy.touch(data[4]);
        assertEquals(asList(4, 9, 8, 7, 6, 5, 3, 2, 1, 0), policy.peekAll());
        policy.touch(data[0]);
        assertEquals(asList(0, 4, 9, 8, 7, 6, 5, 3, 2, 1), policy.peekAll());
        policy.touch(data[3]);
        policy.touch(data[2]);
        policy.touch(data[9]);
        assertEquals(asList(9, 2, 3, 0, 4, 8, 7, 6, 5, 1), empty(policy));
    }

    /**
     * Test the copy constructor.
     */
    @Test
    public void testCopyConstructor() {
        MRUPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        MRUPolicy<Integer> copy = new MRUPolicy<Integer>(policy);
        assertEquals(policy.peekAll(), copy.peekAll());
    }

    /**
     * Test the clone method.
     */
    @Test
    public void testClone() {
        MRUPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        MRUPolicy<Integer> copy = policy.clone();
        assertEquals(policy.peekAll(), copy.peekAll());
    }

    /**
     * Test illegal specification of the initial size.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalSize() {
        new MRUPolicy<Integer>(-1);
    }

    /**
     * Test the single peek object method.
     */
    @Test
    public void testPeek() {
        ReplacementPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        assertEquals(9, policy.peek().intValue());
    }

    /**
     * Test the get size method.
     */
    @Test
    public void testGetSize() {
        MRUPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        assertEquals(10, policy.getSize());
    }

    /**
     * Test that toString doesn't fail
     */
    @Test
    public void testToString() {
        MRUPolicy<Integer> policy = createPolicy();
        addToPolicy(policy, 0, 9);
        assertTrue(policy.toString().contains("" + policy.getSize()));
    }
}