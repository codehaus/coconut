/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.core;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AttributeMaps {

    public final static AttributeMap EMPTY_MAP = new EmptyMap();

    public static <K> Map<K, AttributeMap> createMap(K... a) {
        return createMap(Arrays.asList(a));
    }

    public static <K> Map<K, AttributeMap> createMap(Collection<? extends K> col) {
        HashMap<K, AttributeMap> map = new HashMap<K, AttributeMap>();
        for (K k : col) {
            map.put(k, new DefaultAttributeMap());
        }
        return map;
    }

    public static final class EmptyMap extends AbstractMap<String, Object> implements
            AttributeMap, Serializable {

        private static final long serialVersionUID = 6428348081105594320L;

        public int size() {
            return 0;
        }

        public boolean isEmpty() {
            return true;
        }

        public boolean containsKey(Object key) {
            return false;
        }

        public boolean containsValue(Object value) {
            return false;
        }

        public Object get(Object key) {
            return null;
        }

        public Set<String> keySet() {
            return Collections.<String> emptySet();
        }

        public Collection<Object> values() {
            return Collections.<Object> emptySet();
        }

        public Set<Map.Entry<String, Object>> entrySet() {
            return Collections.emptySet();
        }

        public boolean equals(Object o) {
            return (o instanceof Map) && ((Map) o).size() == 0;
        }

        public int hashCode() {
            return 0;
        }

        // Preserves singleton property
        private Object readResolve() {
            return EMPTY_MAP;
        }

        public long getLong(String key) {
            return 0;
        }

        public void putLong(String key, long value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /**
         * @see org.coconut.core.AttributeMap#getDouble(java.lang.String)
         */
        public double getDouble(String key) {
            return 0;
        }

        /**
         * @see org.coconut.core.AttributeMap#putDouble(java.lang.String,
         *      double)
         */
        public void putDouble(String key, double value) {
            throw new UnsupportedOperationException("map is immutable");
        }

        /**
         * @see org.coconut.core.AttributeMap#getDouble(java.lang.String,
         *      double)
         */
        public double getDouble(String key, double defaultValue) {
            return defaultValue;
        }

        /**
         * @see org.coconut.core.AttributeMap#getLong(java.lang.String, long)
         */
        public long getLong(String key, long defaultValue) {
            return defaultValue;
        }
    }

    public static class DefaultAttributeMap extends HashMap<String, Object> implements
            AttributeMap {

        /**
         * @see org.coconut.cache.service.loading.AttributeMap#getLong(java.lang.String)
         */
        public long getLong(String key) {
            return getLong(key, 0);
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
            return getDouble(key, 0);
        }

        /**
         * @see org.coconut.core.AttributeMap#putDouble(java.lang.String,
         *      double)
         */
        public void putDouble(String key, double value) {
            put(key, value);
        }

        /**
         * @see org.coconut.core.AttributeMap#getDouble(java.lang.String,
         *      double)
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
}
