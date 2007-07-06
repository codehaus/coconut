/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.threading;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class CacheExecutorConfiguration<K, V> extends
        AbstractCacheServiceConfiguration<K, V> {

    public static final String SERVICE_NAME = "CacheExecutor";

    private CacheThreadManager executorFactory;

    public CacheExecutorConfiguration() {
        super(SERVICE_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fromXML(Element element) throws Exception {}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toXML(Document doc, Element parent) throws Exception {}

    public CacheThreadManager getExecutorFactory() {
        return executorFactory;
    }

    public void setExecutorFactory(CacheThreadManager executorFactory) {
        this.executorFactory = executorFactory;
    }

}
