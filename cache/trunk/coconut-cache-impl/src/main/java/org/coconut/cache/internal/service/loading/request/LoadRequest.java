/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading.request;

import org.coconut.core.AttributeMap;
import org.coconut.core.Callback;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys used for loading values
 * @param <V>
 *            the type of values returned when loading
 */
public interface LoadRequest<K, V> extends Callback<V> {

    /**
     * Returns the key for which a corresponding value should be loaded.
     * 
     * @return the key for which a corresponding value should be loaded
     */
    K getKey();

    /**
     * Returns a map of attributes which can be used when loading the entry.
     * 
     * @return a map of attributes which can be used when loading the entry
     */
    AttributeMap getAttributes();
}
