/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.costsize;

import org.coconut.annotation.ThreadSafe;
import org.coconut.cache.policy.CostSizeObject;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.policy.util.ReplacementPolicyDecorator;


 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@ThreadSafe(false)
public class ThresholdPolicy<T extends CostSizeObject> extends ReplacementPolicyDecorator<T> {

    /** serialVersionUID */
    private static final long serialVersionUID = 6311084306282914906L;

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
        super(policy);
        if (threshold <= 0) {
            throw new IllegalArgumentException(
                    "threshold must be a positive number, was " + threshold);
        }
        this.threshold = threshold;
    }

    /**
     * @see org.coconut.cache.CachePolicy#add(null)
     */
    public int add(T element) {
        long size = element.getSize();
        if (size > threshold) {
            return -1;
        }
        return super.add(element);
    }


    /**
     * Returns the maximum size of an element that will be accepted.
     */
    public long getThreshold() {
        return threshold;
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

    // JMX
    public Object getInfo() {
        // lav det som open bean, stardard requires interface
        // det kunne også være fint hvis man kunne decore metoderne
        // feks have en lock omkring dem.
        return null;
    }

}