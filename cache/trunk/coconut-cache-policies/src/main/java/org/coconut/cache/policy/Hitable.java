/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy;

/**
 * A mix-in style interface for marking objects that maintains statistics about
 * how many time it has been succesfully requested. A replacement policy might
 * use this information for deciding when to evict the object.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface Hitable {

    /**
     * Returns the number of times the object has been previously succesfully
     * requested.
     * 
     * @return the number of times the object has been previously succesfully
     *         requested.
     */
    long getHits();
}
