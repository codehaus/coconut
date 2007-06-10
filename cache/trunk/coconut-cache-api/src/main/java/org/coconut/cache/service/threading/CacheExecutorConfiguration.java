package org.coconut.cache.service.threading;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CacheExecutorConfiguration<K, V> extends
        AbstractCacheServiceConfiguration<K, V> {

    public static final String SERVICE_NAME = "CacheExecutor";

    private CacheExecutorFactory executorFactory;
    public CacheExecutorConfiguration() {
        super(SERVICE_NAME);
    }

    
    @Override
    protected void fromXML(Element element) throws Exception {}

    @Override
    protected void toXML(Document doc, Element parent) throws Exception {}


    public CacheExecutorFactory getExecutorFactory() {
        return executorFactory;
    }


    public void setExecutorFactory(CacheExecutorFactory executorFactory) {
        this.executorFactory = executorFactory;
    }

}
