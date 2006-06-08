/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.tck.expiration;

import org.coconut.cache.CacheEntry;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ExpirationFilter implements Filter<CacheEntry<Integer, String>> {

    boolean doAccept = false;

    CacheEntry<Integer, String> lastEntry;

    /**
     * @see org.coconut.filter.Filter#accept(E)
     */
    public boolean accept(CacheEntry<Integer, String> element) {
        lastEntry = element;
        return doAccept;
    }

}
