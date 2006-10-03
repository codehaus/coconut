/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy;

/**
 * A mix-in style interface for marking objects that maintains statistics about
 * how many time it has been succesfully requested. A cache policy might use
 * this information for optimization.
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
    
    //TODO long getMisses()????, for example we want to keep entries, that
    //have lots of misses
}
