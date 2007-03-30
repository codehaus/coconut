/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal;

import java.util.HashMap;

import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultAttributeMap extends HashMap<String, Object> implements AttributeMap {

    /**
     * @see org.coconut.cache.service.loading.AttributeMap#getLong(java.lang.String)
     */
    public long getLong(String key) {
        return getLong(key,0);
    }

    /**
     * @see org.coconut.cache.service.loading.AttributeMap#putLong(java.lang.String,
     *      long)
     */
    public void putLong(String key, long value) {
        put(key, value);
    }
    
    /**
     * @see org.coconut.core.AttributeMap#getDouble(java.lang.String)
     */
    public double getDouble(String key) {
        return getDouble(key,0);
    }

    /**
     * @see org.coconut.core.AttributeMap#putDouble(java.lang.String, double)
     */
    public void putDouble(String key, double value) {
        put(key, value);
    }

    /**
     * @see org.coconut.core.AttributeMap#getDouble(java.lang.String, double)
     */
    public double getDouble(String key, double defaultValue) {
        Object o = get(key);
        return o == null ? defaultValue : (Double) o;
    }

    /**
     * @see org.coconut.core.AttributeMap#getLong(java.lang.String, long)
     */
    public long getLong(String key, long defaultValue) {
        Object o = get(key);
        return o == null ? defaultValue : (Long) o;
    }
}
