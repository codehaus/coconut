package org.coconut.cache.policy.paging;

import static junit.framework.Assert.assertEquals;
import static org.coconut.cache.policy.PolicyTestUtils.addToPolicy;
import static org.coconut.cache.policy.PolicyTestUtils.empty;
import static org.coconut.test.CollectionUtils.seq;
import junit.framework.JUnit4TestAdapter;

import org.coconut.cache.policy.ReplacementPolicy;
import org.junit.Test;

/**
 * Test of the LFU policy.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class LFUPolicyTest {


    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LFUPolicyTest.class);
    }
    
    public ReplacementPolicy<Integer> createPolicy() {
        return new LFUPolicy<Integer>();
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

    // /**
    // * Test removal elements by index.
    // */
    // public void testRemoveIndex() {
    // CachePolicy<Integer> policy = createPolicy();
    // int[] data = addToPolicy(policy, 0, 9);
    // ((LFUPolicy) policy).print();
    // policy.remove(data[4]);
    // ((LFUPolicy) policy).print();
    // policy.add(4);
    // ((LFUPolicy) policy).print();
    // policy.remove(data[4]);
    // ((LFUPolicy) policy).print();
    //
    //        
    // policy.remove(data[7]);
    // policy.remove(data[0]);
    // policy.remove(data[9]);
    // for (int i=0;i<11;i++) {
    // for (int j=0;j<=i;j++) {
    // policy.touch(j);
    // }
    // }
    // ((LFUPolicy) policy).print();
    // assertEquals(asList(8, 6, 5, 3, 2, 1), policy.peek());
    //        
    // }
}