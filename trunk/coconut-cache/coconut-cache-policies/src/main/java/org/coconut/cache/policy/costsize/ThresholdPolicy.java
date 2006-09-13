/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.costsize;

import java.util.List;

import org.coconut.annotation.ThreadSafe;
import org.coconut.cache.policy.CostSizeObject;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.policy.spi.AbstractPolicy;

/**
 * This replacement decorates another cache policy rejecting any documents
 * larger than a certain threshold size.
 * <p>
 * This policy is based on a ACM SIGCOMM '96 paper: <a
 * href="http://ei.cs.vt.edu/~succeed/96sigcomm/">Removal Policies in Network
 * Caches for World-Wide Web Documents</a> by Stephen Williams et al. However,
 * this version is generalized for any replacement policies not just LRU.
 * <p>
 * If no cache policy is specified in the constructors a LRU replacement policy
 * is being wrapped (as in the original paper).
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@ThreadSafe(false)
public class ThresholdPolicy<T extends CostSizeObject> extends AbstractPolicy<T> {

    /** serialVersionUID */
    private static final long serialVersionUID = 6311084306282914906L;

    private final ReplacementPolicy<T> policy;

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#clear()
     */
    public void clear() {
        policy.clear();
    }

    private long threshold;

    /**
     * Constructs a new ThresholdPolicy policy.
     * 
     * @param threshold
     *            the threshold for which entries will be accepted into this
     *            policy. Entries with a size greater then the specified
     *            threshold will be rejected
     */
    public ThresholdPolicy(long threshold) {
        this(new LRUPolicy<T>(), threshold);
    }

    public ThresholdPolicy(ReplacementPolicy<T> policy, long threshold) {
        if (policy == null) {
            throw new NullPointerException("policy is null");
        }
        if (threshold <= 0) {
            throw new IllegalArgumentException(
                    "threshold must be a positive number, was " + threshold);
        }
        this.threshold = threshold;
        this.policy = policy;
    }

    /**
     * @see org.coconut.cache.CachePolicy#add(null)
     */
    public int add(T element) {
        long size = element.getSize();
        if (size > threshold) {
            return -1;
        }
        return policy.add(element);
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#evictNext()
     */
    public T evictNext() {
        return policy.evictNext();
    }

    /**
     * Returns the maximum size of an element that will be accepted.
     */
    public long getThreshold() {
        return threshold;
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#peek()
     */
    public T peek() {
        return policy.peek();
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#peekAll()
     */
    public List<T> peekAll() {
        return policy.peekAll();
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#remove(int)
     */
    public T remove(int index) {
        return policy.remove(index);
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#touch(int)
     */
    public void touch(int index) {
        policy.touch(index);
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#update(int,
     *      java.lang.Object)
     */
    public boolean update(int index, T newElement) {
        long size = newElement.getSize();
        if (size > threshold) {
            remove(index);
            return false;
        }
        update(index, newElement);
        return true;
    }

    public void setThreshold(long threshold) {
        if (threshold <= 0) {
            throw new IllegalArgumentException(
                    "threshold must be a positive number, was " + threshold);
        }
        this.threshold = threshold;
    }

    public ReplacementPolicy<T> getPolicy() {
        return policy;
    }

    // JMX
    public Object getInfo() {
        // lav det som open bean, stardard requires interface
        // det kunne også være fint hvis man kunne decore metoderne
        // feks have en lock omkring dem.
        return null;
    }

    /**
     * @see org.coconut.cache.policy.spi.AbstractPolicy#getSize()
     */
    public int getSize() {
        return policy.getSize();
    }

}