/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy;

/**
 * A mix-in style interface for marking objects that maintains various
 * statistics about how many time it has been succesfully requested. A
 * replacement policy might use this information for deciding when to evict the
 * object.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface PolicyObject {

    /** The default cost of fetching an element if no cost is specified. */
    public static final double DEFAULT_COST = 1.0;

    /** The default size of an element if no size is specified. */
    public static final long DEFAULT_SIZE = 1;

    /** The initial number of hits for an element. */
    public static final long DEFAULT_HITS = 0;

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

    /**
     * Returns the number of times the object has been previously succesfully
     * requested.
     * 
     * @return the number of times the object has been previously succesfully
     *         requested.
     */
    long getHits();
}
