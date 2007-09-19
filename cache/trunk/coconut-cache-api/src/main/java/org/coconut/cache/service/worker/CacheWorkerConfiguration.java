package org.coconut.cache.service.worker;

import static org.coconut.internal.util.XmlUtil.addAndsaveObject;
import static org.coconut.internal.util.XmlUtil.loadOptional;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CacheWorkerConfiguration extends AbstractCacheServiceConfiguration {

    /** The name of this service. */
    public static final String SERVICE_NAME = "worker";

    /** The XML tag for the cache worker manager. */
    private final static String CACHE_WORKER_MANAGER_TAG = "cache-worker-manager";

    private CacheWorkerManager cacheWorkerManager;

    public CacheWorkerConfiguration() {
        super(SERVICE_NAME);
    }

    /**
     * Returns the CacheWorkerManager or <code>null</code> if no cache worker manager
     * has been configured.
     * 
     * @return the CacheWorkerManager or <code>null</code> if no cache worker manager
     *         has been configured
     */
    public CacheWorkerManager getWorkerManager() {
        return cacheWorkerManager;
    }

    /**
     * Sets the CacheWorkerManager used by the cache. If no CacheWorkerManager is set??
     * 
     * @param workerManager
     * @return
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
        addAndsaveObject(doc, parent, CACHE_WORKER_MANAGER_TAG, getResourceBundle(),
                SERVICE_NAME + ".saveOfCacheWorkerManagerFailed", cacheWorkerManager);
    }

}
