/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CostSizeObject {

    /** The default cost of fetching an element if no cost is specified. */
    public static final double DEFAULT_COST = 1;

    /** The default size of an element if no size is specified. */
    public static final long DEFAULT_SIZE = 1;

    /**
     * Returns the size of this element. Implementations are free to include
     * overhead of storing the element or just the size of the element itself.
     * <p>
     * The size returned does not necessarily represent the actual number of
     * bytes used to store the element.
     * 
     * @return the size of the element. If the size of the object cannot be
     *         determined
     *         {@link org.coconut.cache.policy.CostSizePolicy#DEFAULT_SIZE}
     *         should be returned
     */
    long getSize();

    /**
     * Returns the expected cost of fetching this element. Assigning costs to an
     * element is context dependent. Examples include
     * <ul>
     * <li>Time duration for loading the value from disk.</li>
     * <li>Price for transfering x number of bytes from an external storage
     * service.
     * <li>Number of calculations used to create the element</li>
     * </ul>
     * <p>
     * The cost does not necessarily represent the actual time to fetch the
     * element. However, this is often the case.
     * 
     * @return the expected cost of fetching this element or
     *         {@link org.coconut.cache.policy.CostSizePolicy#DEFAULT_COST} if
     *         no cost is associated with this element
     * @see org.coconut.cache.policy.CostSizePolicy
     */
    double getCost();
}
