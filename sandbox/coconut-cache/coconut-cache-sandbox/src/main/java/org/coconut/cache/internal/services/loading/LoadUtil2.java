/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.services.loading;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoadUtil2 {
//
//
//    static class ValueLoadedCallback<K, V> extends AbstractLoadedCallback<K, V, V> {
//        private final K key;
//
//        private final CacheLoader<? super K, ? extends V> loader;
//
//        /**
//         * @param ac
//         * @param key
//         */
//        public ValueLoadedCallback(final K key, EventProcessor<V> eh,
//                CacheLoader<? super K, ? extends V> loader,
//                CacheErrorHandler<K, V> errorHandler) {
//            super(eh, errorHandler);
//            if (key == null) {
//                throw new NullPointerException("key is null");
//            } else if (loader == null) {
//                throw new NullPointerException("loader is null");
//            }
//            this.key = key;
//            this.loader = loader;
//        }
//
//        /**
//         * @see org.coconut.core.Callback#failed(java.lang.Throwable)
//         */
//        public void failed(Throwable cause) {
//            V v =(V) ((CacheErrorHandler2) errorHandler).loadFailed2(loader, key, false, cause);
//            completed(v);
//        }
//    }
//
//    static class ValuesLoadedCallback<K, V> extends
//            AbstractLoadedCallback<K, V, Map<K, V>> {
//        private final Collection<? extends K> keys;
//
//        private final CacheLoader<? super K, ? extends V> loader;
//
//        /**
//         * @param ac
//         * @param key
//         */
//        public ValuesLoadedCallback(final Collection<? extends K> keys,
//                EventProcessor<Map<K, V>> eh, CacheLoader<? super K, ? extends V> loader,
//                CacheErrorHandler2<K, V> errorHandler) {
//            super(eh, errorHandler);
//            this.keys = keys;
//            this.loader = loader;
//        }
//
//        /**
//         * @see org.coconut.core.Callback#failed(java.lang.Throwable)
//         */
//        public void failed(Throwable cause) {
//            Map<K, V> v = ((CacheErrorHandler2) errorHandler).loadAllFailed2(loader, keys, true, cause);
//            completed(v);
//        }
//    }
//    
//
//    public static <K, V> Callback<V> valueLoadedCallback(final K key,
//            EventProcessor<V> eh, final CacheLoader<? super K, ? extends V> loader,
//            CacheErrorHandler<K, V> errorHandler) {
//        return new ValueLoadedCallback<K, V>(key, eh, loader, errorHandler);
//    }
//
//    public static <K, V> Callback<Map<K, V>> valuesLoadedCallback(
//            final Collection<? extends K> keys, EventProcessor<Map<K, V>> eh,
//            final CacheLoader<? super K, ? extends V> loader,
//            CacheErrorHandler2<K, V> errorHandler) {
//        return new ValuesLoadedCallback<K, V>(keys, eh, loader, errorHandler);
//    }
//    
//    public static <K, V> EventProcessor<V> valueNonNullIntoMap(K key, Map<K, V> map) {
//        return new ValueNonNullIntoMap<K, V>(map, key);
//    }
//
//    public static <K, V> EventProcessor<Map<K, V>> valuesNonNullIntoMap(Map<K, V> map) {
//        return new ValuesNonNullIntoMap<K, V>(map);
//    }
//
//    static class ValuesNonNullIntoMap<K, V> implements EventProcessor<Map<K, V>> {
//        private final Map<K, V> map;
//
//        /**
//         * @param cache
//         */
//        public ValuesNonNullIntoMap(final Map<K, V> map) {
//            if (map == null) {
//                throw new NullPointerException("map is null");
//            }
//            this.map = map;
//        }
//
//        /**
//         * @see org.coconut.core.EventHandler#handle(java.lang.Object)
//         */
//        public void process(Map<K, V> map) {
//            Map<K, V> noNullsMap = new HashMap<K, V>(map.size());
//            for (Map.Entry<K, V> e : map.entrySet()) {
//                V value = e.getValue();
//                if (value != null) {
//                    noNullsMap.put(e.getKey(), value);
//                }
//            }
//            if (noNullsMap.size() > 0) {
//                this.map.putAll(noNullsMap);
//            }
//        }
//    }
//    
//    
//    static class ValueNonNullIntoMap<K, V> implements EventProcessor<V> {
//        private final Map<K, V> cache;
//
//        private final K key;
//
//        /**
//         * @param cache
//         */
//        public ValueNonNullIntoMap(final Map<K, V> map, final K key) {
//            if (map == null) {
//                throw new NullPointerException("map is null");
//            } else if (key == null) {
//                throw new NullPointerException("key is null");
//            }
//            this.key = key;
//            this.cache = map;
//        }
//
//        /**
//         * @see org.coconut.core.EventHandler#handle(java.lang.Object)
//         */
//        public void process(V value) {
//            if (value != null) {
//                cache.put(key, value);
//            }
//        }
//    }

}
