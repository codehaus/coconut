/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheLoadingConfiguration<K, V> extends
        AbstractCacheServiceConfiguration<K, V> {

    final static String LOADING_TAG = "loading";

    final static String LOADER_TAG = "loader";

    final static String BACKEND_TAG = "backed";

    final static String EXTENDED_LOADER_TAG = "entryloader";

    private CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> extendedLoader;

    private CacheLoader<? super K, ? extends V> loader;

    public CacheLoadingConfiguration() {
        super(LOADING_TAG, CacheLoadingService.class);
    }

    /**
     * Returns the CacheLoader that the cache should use for loading new
     * key-value bindings. If this method returns <code>null</code> no initial
     * loader will be set.
     */
    public CacheLoader<? super K, ? extends V> getBackend() {
        return loader;
    }

    public CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> getExtendedBackend() {
        return extendedLoader;
    }

    /**
     * Sets the loader that should be used for loading new elements into the
     * cache. If the specified loader is <code>null</code> no loader will be
     * used for loading new key-value bindings. All values must then put into
     * the cache by using put or putAll.
     * 
     * @param loader
     *            the loader to set
     * @return the current CacheConfiguration
     * @throws IllegalStateException
     *             if an extended loader has already been set, using
     *             {@link #setExtendedBackend(CacheLoader)}
     */
    public CacheLoadingConfiguration<K, V> setBackend(
            CacheLoader<? super K, ? extends V> loader) {
        if (extendedLoader != null) {
            throw new IllegalStateException(
                    "extended loader already set, cannot set an ordinary loader");
        }
        this.loader = loader;
        return this;
    }

    /**
     * Sets the loader that should be used for loading new elements into the
     * cache. If the specified loader is <code>null</code> no loader will be
     * used for loading new key-value bindings. All values must then put into
     * the cache by using put or putAll.
     * 
     * @param loader
     *            the loader to set
     * @return the current CacheConfiguration
     * @throws IllegalStateException
     *             if an ordinary loader has already been set, using
     *             {@link #setBackend(CacheLoader)}
     */
    public CacheLoadingConfiguration setExtendedBackend(
            CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader) {
        if (this.loader != null) {
            throw new IllegalStateException(
                    "loader already set, cannot set an extended loader");
        }
        extendedLoader = loader;
        return this;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#fromXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void fromXML(Document doc, Element parent) throws Exception {
        Element loaderE = getChild(LOADER_TAG, parent);
        if (loaderE != null) {
            CacheLoader loader = loadObject(loaderE, CacheLoader.class);
            setBackend(loader);
        }

        Element extendedLoader = getChild(EXTENDED_LOADER_TAG, parent);
        if (extendedLoader != null) {
            CacheLoader loader = loadObject(extendedLoader, CacheLoader.class);
            setExtendedBackend(loader);
        }
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#toXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void toXML(Document doc, Element parent) {
        if (loader != null) {
            Element e = add(doc, LOADER_TAG, parent);
            saveObject(doc, e, "backend.cannotPersistLoader", loader);
        }
        if (extendedLoader != null) {
            Element e = add(doc, EXTENDED_LOADER_TAG, parent);
            saveObject(doc, e, "backend.cannotPersistLoader", extendedLoader);
        }
    }
}
