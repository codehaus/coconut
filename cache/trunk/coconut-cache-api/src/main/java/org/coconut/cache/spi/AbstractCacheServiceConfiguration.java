/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.internal.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * All services should define a Configuration class that is used to configure the service.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public abstract class AbstractCacheServiceConfiguration<K, V> {

    /** The parent cache configuration. */
    private transient CacheConfiguration<K, V> conf;

    /** The short name of this service. */
    private final String serviceName;

    /**
     * Creates a new AbstractCacheServiceConfiguration.
     *
     * @param serviceName
     *            the name of the service
     */
    public AbstractCacheServiceConfiguration(String serviceName) {
        if (serviceName == null) {
            throw new NullPointerException("serviceName is null");
        }
        this.serviceName = serviceName;
    }

    /**
     * @return the parent {@link CacheConfiguration}, or <tt>null</tt> if this
     *         configuration has not been registered yet.
     */
    public final CacheConfiguration<K, V> c() {
        return conf;
    }

    /**
     * Returns the unique name of this service. For example,
     * {@link org.coconut.cache.service.expiration.CacheExpirationConfiguration} returns '{@value
     * org.coconut.cache.service.expiration.CacheExpirationConfiguration#SERVICE_NAME}'.
     *
     * @return the unique name of this service
     */
    public String getServiceName() {
        return serviceName;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement(serviceName);
            doc.appendChild(root);
            toXML(doc, root);
            return XmlUtil.prettyprint(doc);
        } catch (Exception e) {
            if (getClass().getPackage().getName().startsWith(Cache.class.getPackage().getName())) {
                // speciel exception for Coconut Cache implementations.
                throw new CacheException(CacheSPI.HIGHLY_IRREGULAR_MSG, e);
            } else {
                throw new CacheException(
                        "A String representation of this package could not be obtained, because it could not be serialized to XML",
                        e);
            }
        }
    }

    /**
     * Reads this configuration from an XML element.
     *
     * @param element
     *            The XML Element to read the configuration from
     * @throws Exception
     *             if the configuration could not be properly read
     */
    protected void fromXML(Element element) throws Exception {}

    /**
     * Saves this configuration to xml.
     *
     * @param doc
     *            the Document to write to
     * @param element
     *            the top element of this configuration
     * @throws Exception
     *             this configuration could not be probably saved
     */
    protected void toXML(Document doc, Element element) throws Exception {}

    /**
     * Sets the parent cache configuration which is available when calling {@link #c()}.
     *
     * @param conf
     *            the parent cache configuration
     */
    void setConfiguration(CacheConfiguration<K, V> conf) {
        this.conf = conf;
        initialize(conf);
    }

    /**
     * Called after this configuration has been registered with an
     * {@link CacheConfiguration} instance.
     *
     * @param conf
     *            the cache configuration that this configuration is part
     */
    protected void initialize(CacheConfiguration<K, V> conf) {}
}
