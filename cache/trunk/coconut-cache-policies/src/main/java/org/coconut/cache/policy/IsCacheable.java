/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy;

import org.coconut.attribute.AttributeMap;

/**
 * Can be used to control what values a data structure that maintains a key -> value
 * mapping should cache.
 * <p>
 * See {@link IsCacheables} for various implementations.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys that are being cached
 * @param <V>
 *            the type of values that are being cached
 * @see IsCacheables
 */
public interface IsCacheable<K, V> {

    /**
     * Returns <code>true</code> if the value should be cached otherwise
     * <code>false</code>.
     * 
     * @param key
     *            the key that is being added to the cache
     * @param value
     *            the value that is being added to the cache
     * @param attributes
     *            the attributes of the key value pair being added to the cache
     * @return <code>true</code> if the value should be cached otherwise
     *         <code>false</code>
     */
    boolean isCacheable(K key, V value, AttributeMap attributes);
}
