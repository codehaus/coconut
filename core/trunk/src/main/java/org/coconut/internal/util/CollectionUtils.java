/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.util.Collection;
import java.util.Map;

/** 
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class CollectionUtils {
    ///CLOVER:OFF
    /** Cannot instantiate. */
    private CollectionUtils() {}
    ///CLOVER:ON
    
    public static void checkCollectionForNulls(Collection<?> col) {
        for (Object entry : col) {
            if (entry == null) {
                throw new NullPointerException("collection contains a null entry");
            }
        }
    }

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
