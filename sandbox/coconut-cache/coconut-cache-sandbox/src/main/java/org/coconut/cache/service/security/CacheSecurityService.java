/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.security;

import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheSecurityService<K, V> {
	
	void checkPut(K key, V value, AttributeMap attributes);
	void checkLoad(K key, V value, AttributeMap attributes);
	
	void checkGet(K key, V value, AttributeMap attributes);
    void beforePut(); // check SystemSecurity

    void beforeClear();

    void beforeLoad();

    void beforeRemove();

    // we probably need information on element level
    // id

    void beforePut(K key, V value, AttributeMap attributes);
}
