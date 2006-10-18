/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.expiration;

import org.coconut.cache.CacheEntry;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ExpirationFilter implements Filter<CacheEntry<Integer, String>> {

    volatile boolean isExpired = false;

    volatile CacheEntry<Integer, String> lastEntry;

    /**
     * @see org.coconut.filter.Filter#accept(E)
     */
    public boolean accept(CacheEntry<Integer, String> element) {
        lastEntry = element;
        return isExpired;
    }

}
