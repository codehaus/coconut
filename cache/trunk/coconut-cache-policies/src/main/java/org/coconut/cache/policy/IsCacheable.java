/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy;

import org.coconut.attribute.AttributeMap;

public interface IsCacheable<K, V> {
    boolean isCacheable(K key, V value, AttributeMap attributes);
}
