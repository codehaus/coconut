/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.worker;

import static org.coconut.internal.util.XmlUtil.addAndsaveObject;
import static org.coconut.internal.util.XmlUtil.loadOptional;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to configure the worker service prior to usage.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheWorkerConfiguration extends AbstractCacheServiceConfiguration {

    /** The name of this service. */
    public static final String SERVICE_NAME = "worker";

    /** The XML tag for the cache worker manager. */
    private final static String CACHE_WORKER_MANAGER_TAG = "cache-worker-manager";

    /** The cache worker manager to use. */
    private CacheWorkerManager cacheWorkerManager;

    /**
     * Creates a new instance of CacheWorkerConfiguration.
     */
    public CacheWorkerConfiguration() {
        super(SERVICE_NAME);
    }

    /**
     * Returns the CacheWorkerManager or <code>null</code> if no cache worker manager
     * has been set.
     * 
     * @return the CacheWorkerManager or <code>null</code> if no cache worker manager
     *         has been set
     */
    public CacheWorkerManager getWorkerManager() {
        return cacheWorkerManager;
    }

    /**
     * Sets the CacheWorkerManager used by the cache. If no CacheWorkerManager is set the
     * cache will create a worker manager to use.
     * 
     * @param workerManager
     *            the worker manager to use for the cache
     * @return this configuration
     */
    public CacheWorkerConfiguration setWorkerManager(CacheWorkerManager workerManager) {
        this.cacheWorkerManager = workerManager;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    protected void fromXML(Element element) throws Exception {
        /* CacheWorkerManager */
        cacheWorkerManager = loadOptional(element, CACHE_WORKER_MANAGER_TAG,
                CacheWorkerManager.class);
    }

    /** {@inheritDoc} */
    @Override
    protected void toXML(Document doc, Element parent) throws Exception {
        /* CacheWorkerManager */
        addAndsaveObject(doc, parent, CACHE_WORKER_MANAGER_TAG, getResourceBundle(), getClass(),
                "saveOfCacheWorkerManagerFailed", cacheWorkerManager);
    }
}
