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
}
