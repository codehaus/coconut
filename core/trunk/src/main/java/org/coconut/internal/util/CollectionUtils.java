/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.util.Collection;
import java.util.Map;

/**
 * Various collection utility functions.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class CollectionUtils {
    // /CLOVER:OFF
    /** Cannot instantiate. */
    private CollectionUtils() {}

    // /CLOVER:ON

    /**
     * Checks whether or not the specified collection contains a <code>null</code>.
     * 
     * @param col
     *            the collection to check
     * @throws NullPointerException
     *             if the specified collection contains a null
     */
    public static void checkCollectionForNulls(Collection<?> col) {
        for (Object entry : col) {
            if (entry == null) {
                throw new NullPointerException("collection contains a null entry");
            }
        }
    }

    /**
     * Checks whether or not the specified map contains a null key or value
     * <code>null</code>.
     * 
     * @param map
     *            the map to check
     * @throws NullPointerException
     *             if the specified map contains a null key or value
     */
    public static void checkMapForNulls(Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() == null) {
                throw new NullPointerException("map contains a null key");
            } else if (entry.getValue() == null) {
                throw new NullPointerException("map contains a null value for key = "
                        + entry.getKey());
            }
        }
    }
}
