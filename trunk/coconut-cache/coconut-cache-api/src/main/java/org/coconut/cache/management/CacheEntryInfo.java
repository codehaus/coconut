/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.management;

/**
 * okay, we can either retrieve one element a time using a long id, a string
 * that gets transformed or a object making up a composite data.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheEntryInfo {
    /**
     * Returns the time of creation for the specific cache entry.
     */
    long getCreationTime();

    // if retrieveKeyValuestring=true otherwise null
    String getKey();

    String getValue();
}
