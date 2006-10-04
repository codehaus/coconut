/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.sandbox;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheRequest.java 38 2006-08-22 10:09:08Z kasper $
 */
public class CacheRequest {

    public interface EvictEntriesRequest {
        int numberOfEntries();
    }
    
    
}
