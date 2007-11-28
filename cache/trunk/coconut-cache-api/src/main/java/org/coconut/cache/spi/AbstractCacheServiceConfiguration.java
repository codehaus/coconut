/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

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

    /** The default resource bundle used to look up resources. */
    private final ResourceBundle bundle;

    /** The parent cache configuration. */
    private CacheConfiguration<K, V> conf;

    /** The short name of this service. */
    private final String serviceName;

    /**
     * Creates a new AbstractCacheServiceConfiguration.
     * 
     * @param serviceName
     *            the name of the service
     */
    public AbstractCacheServiceConfiguration(String serviceName) {
        this(serviceName, CacheSPI.DEFAULT_CACHE_BUNDLE);
    }

    /**
     * Creates a new AbstractCacheServiceConfiguration.
     * 
     * @param serviceName
     *            the name of the service
     * @param bundle
     *            a ResourceBundle used for looking up text strings
     */
    public AbstractCacheServiceConfiguration(String serviceName, ResourceBundle bundle) {
        if (serviceName == null) {
            throw new NullPointerException("serviceName is null");
        }
        this.serviceName = serviceName;
        this.bundle = bundle;
    }

    /**
     * @return the parent {@link CacheConfiguration}, or <tt>null</tt> if this
     *         configuration has not been registered yet.
     */
    public CacheConfiguration<K, V> c() {
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
     * @return the ResourceBundle that is used by this configuration, or <code>null</code>
     *         if no resource bundle is used.
     */
    protected ResourceBundle getResourceBundle() {
        return bundle;
    }

    /**
     * Attempts to lookup a resource bundle entry for the specified key.
     * 
     * @param key
     *            the key to lookup
     * @return the string matching the key
     * @throws IllegalStateException
     *             if no resource bundle has been specified when calling the constructor
     *             of this class
     * @throws MissingResourceException
     *             if no entry could be found for specified key
     */
    protected String lookup(String key) {
        if (bundle == null) {
            throw new IllegalStateException("No bundle has been defined");
        }
        return bundle.getString(key);
    }

    /**
     * Saves this configuration to xml.
     * 
     * @param doc
     *            the Document to write to
     * @param parent
     *            the top element of this configuration
     * @throws Exception
     *             this configuration could not be probably saved
     */
    protected void toXML(Document doc, Element parent) throws Exception {}

    /**
     * Sets the parent cache configuration which is available when calling {@link #c()}.
     * 
     * @param conf
     *            the parent cache configuration
     */
    void setConfiguration(CacheConfiguration<K, V> conf) {
        this.conf = conf;
    }
}
