/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import org.coconut.attribute.AttributeMap;
import org.coconut.core.Callback;

/**
 * A callback that is used to asynchronously load values.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys used for loading values
 * @param <V>
 *            the type of values returned when loading
 */
public interface CacheLoaderCallback<K, V> extends Callback<V> {

    /**
     * Returns <tt>true</tt> if the loading has completed. Completion may be due to
     * normal termination or an exception -- in both these cases, this method will return
     * <tt>true</tt>.
     * 
     * @return <tt>true</tt> if this task completed
     */
    boolean isDone();

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
