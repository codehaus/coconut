/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CostSizeObject {

    /** The default cost of an element to fetch if no cost is specified. */
    public static final double DEFAULT_COST = 1;

    /** The default size of an element if no size is specified. */
    public static final long DEFAULT_SIZE = 1;

    /**
     * Returns the size of this entry. Implementations might include overhead of
     * cacheentry, only the value. Does not neccessarily have anything to do
     * with the actual number of bytes used to represent the value.
     * 
     * @return the size of the entry
     */
    long getSize();

    /**
     * Returns the expected cost of fetching this entry again. Assigning costs
     * to an entry is context dependent. The cost does not neccessarily have to
     * be measured in any form of time duration, might be.
     * 
     * @return the expected cost of fetching this entry again or
     *         {@link org.coconut.cache.policy.CostSizePolicy#DEFAULT_COST} if
     *         no cost is associated with this entry
     * @see org.coconut.cache.policy.CostSizePolicy
     */
    double getCost();
}
