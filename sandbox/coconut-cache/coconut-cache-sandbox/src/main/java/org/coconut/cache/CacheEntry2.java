/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheEntry2 extends CacheEntry{

    // void expireNow(boolean attemptToRefresh);
    // boolean isExpired()
    // Whats the purpose of this??? boolean isValid(); hmm isExpired w
    // 
    // boolean hasAttributes(); //if attribute map is lazy created check this
    // first
    /**
     * Returns a map of attributes (optional operation). This are valid only
     * doing lifetime and will not be persisted.
     */
    // Map<String, Object> getAttributes();
    

 // /**
 // * Returns a version counter. An implementation may use timestamps for this
 // * or an incrementing number. Timestamps usually have issues with
 // * granularity and are harder to use across clusteres or threads, so an
 // * incrementing counter is often safer.
 // *
 // * @return the version of the current entry
 // */
 // long getVersion();
}
