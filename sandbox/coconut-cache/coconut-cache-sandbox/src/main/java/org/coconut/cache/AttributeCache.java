/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.Collection;
import java.util.Map;

import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface AttributeCache<K, V> extends Cache<K, V> {
	V get(Object key, AttributeMap map);

	void getAll(Collection<? extends K> keys, AttributeMap attributes);

	void putAll(Map<K, V> keys, AttributeMap attributes);

	V removeIt(K key, AttributeMap attributes);

	Map<K, V> removeAll(Collection<? extends K> keys, AttributeMap attributes);
}
