package org.coconut.cache.service.worker;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CacheWorkerConfiguration extends AbstractCacheServiceConfiguration {

    public static final String SERVICE_NAME = "worker";

    private CacheWorkerManager cacheWorkerManager;

    private Thread.UncaughtExceptionHandler handler;

    public CacheWorkerConfiguration() {
        super(SERVICE_NAME);
    }

    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return handler;
    }

    public CacheWorkerManager getWorkerManager() {
        return cacheWorkerManager;
    }

    /**
     * Sets a default Thread.UncaughtExceptionHandler. If no exception handler is set. The
     * cache will call CacheExceptionHandler#handleUn(CacheContext, Thread t, Throwable
     * e); Which logs the exception and lets the thread exit
     * <p>
     * Any uncaught exception handler set by this method will only applies to executor
     * services that are initialized by the cache itself. Any executor services that the
     * user creates them self must manually.
     * 
     * @param handler
     */
    public CacheWorkerConfiguration setUncaughtExceptionHandler(
            Thread.UncaughtExceptionHandler handler) {
        this.handler = handler;
        return this;
    }

    public CacheWorkerConfiguration setWorkerManager(CacheWorkerManager workerFactory) {
        this.cacheWorkerManager = workerFactory;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    protected void fromXML(Element element) throws Exception {}

    /** {@inheritDoc} */
    @Override
    protected void toXML(Document doc, Element parent) throws Exception {}

}
