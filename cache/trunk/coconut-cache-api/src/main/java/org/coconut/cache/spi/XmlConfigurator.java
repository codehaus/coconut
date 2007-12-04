/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.coconut.cache.CacheConfiguration;
import org.coconut.internal.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XmlConfigurator is used to load and save {@link org.coconut.cache.CacheConfiguration}
 * as XML. Normally users should not rely on this class but instead use
 * {@link CacheConfiguration#loadConfigurationFrom(InputStream)}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class XmlConfigurator {

    /** The current version of the XML schema. */
    public static final String CURRENT_VERSION = "0.0.4";

    /** The name of cache. */
    static final String CACHE_NAME_ATTR = "name";

    /** The root tag for a cache instance. */
    static final String CACHE_TAG = "cache";

    /** The properties tag for a cache instance. */
    static final String PROPERTIES_TAG = "properties";

    /** The property tag for a cache instance. */
    static final String PROPERTY_TAG = "property";

    /** The key attribute for a property tag. */
    static final String PROPERTY_KEY_ATTR = "key";

    /** The type of the cache. */
    static final String CACHE_TYPE_ATTR = "type";

    /** The root tag. */
    static final String CONFIG_TAG = "cache-config";

    /** The cache-config->version tag. */
    static final String CONFIG_VERSION_ATTR = "version";

    /**
     * Reads the XML configuration from the specified InputStream and populates the
     * specified CacheConfiguration with the options.
     * 
     * @param configuration
     *            the CacheConfiguration to read value into
     * @param stream
     *            the InputStream to load the configuration from
     * @throws Exception
     *             some Exception prevented the CacheConfiguration from being properly
     *             populated
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    public <K, V> void readInto(CacheConfiguration<K, V> configuration, InputStream stream)
            throws Exception {
        if (configuration == null) {
            throw new NullPointerException("configuration is null");
        } else if (stream == null) {
            throw new NullPointerException("stream is null");
        }
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
        readDocument(configuration, doc);
    }

    /**
     * Serializes the specified CacheConfiguration to the specified OutputStream.
     * 
     * @param configuration
     *            the CacheConfiguration to serialize to XML
     * @param stream
     *            the OutputStream to write the serialized CacheConfiguration
     * @throws Exception
     *             some Exception prevented the CacheConfiguration from being serialized
     */
    public void write(CacheConfiguration<?, ?> configuration, OutputStream stream) throws Exception {
        if (configuration == null) {
            throw new NullPointerException("configuration is null");
        } else if (stream == null) {
            throw new NullPointerException("stream is null");
        }
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        writeDocument(configuration, doc);
        XmlUtil.prettyprint(doc, stream);
    }

    /**
     * Reads the configuration of a specified service type from the specified XML element.
     * 
     * @param cache
     *            the xml element from where to read the configuration of the specified
     *            service type
     * @param configurationObject
     *            the configuration object we are loading into
     * @return the service configuration that was read
     * @throws Exception
     *             something when wrong while reading parsing the xml element
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    protected <K, V> AbstractCacheServiceConfiguration<K, V> readCacheService(Element cache,
            AbstractCacheServiceConfiguration<K, V> configurationObject) throws Exception {
        Element e = (Element) cache.getElementsByTagName(configurationObject.getServiceName())
                .item(0);
        if (e != null) {
            configurationObject.fromXML(e);
        }
        return configurationObject;
    }

    /**
     * Reads a xml document.
     * 
     * @param base
     *            the cache configuration to add settings on
     * @param doc
     *            the xml based document of the cache
     * @throws Exception
     *             something went wrong while constructing the cache configuration
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    protected <K, V> void readDocument(CacheConfiguration<K, V> base, Document doc)
            throws Exception {
        Element root = doc.getDocumentElement();
        int length = root.getElementsByTagName(CACHE_TAG).getLength();
        if (length == 0) {
            throw new IllegalArgumentException("No cache is defined in the specified document, "
                    + doc.getDocumentURI());
        }
        Node n = root.getElementsByTagName("cache").item(0);
        readSingleCache(base, (Element) n);
    }

    /**
     * Reads a single cache from a xml document.
     * 
     * @param base
     *            the cache configuration to add settings on
     * @param cacheElement
     *            the xml element describing the cache
     * @throws Exception
     *             something went wrong while constructing the cache configuration
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    protected <K, V> void readSingleCache(CacheConfiguration<K, V> base, Element cacheElement)
            throws Exception {
        if (cacheElement.hasAttribute(CACHE_NAME_ATTR)
                && !cacheElement.getAttribute(CACHE_NAME_ATTR).equals("")) {
            base.setName(cacheElement.getAttribute(CACHE_NAME_ATTR));
        }
        if (cacheElement.hasAttribute(CACHE_TYPE_ATTR)) {
            base.setCacheType((Class) Class.forName(cacheElement.getAttribute(CACHE_TYPE_ATTR)));
        }
        Element properties = (Element) cacheElement.getElementsByTagName(PROPERTIES_TAG).item(0);
        if (properties != null) {
            NodeList list = cacheElement.getElementsByTagName(PROPERTY_TAG);
            for (int i = 0; i < list.getLength(); i++) {
                Element e = ((Element) list.item(i));
                String key = e.getAttribute(PROPERTY_KEY_ATTR);
                String value = e.getTextContent();
                base.setProperty(key, value);
            }
        }
        for (AbstractCacheServiceConfiguration<K, V> c : base.getAllConfigurations()) {
            readCacheService(cacheElement, c);
        }
    }

    /**
     * Writes a single single service as a xml element.
     * 
     * @param doc
     *            the xml based document to write to
     * @param configuration
     *            the service
     * @return an element containg the configuration of the service
     * @throws Exception
     *             something went wrong while writing the cache configuration
     */
    protected Element writeCacheService(Document doc,
            AbstractCacheServiceConfiguration<?, ?> configuration) throws Exception {
        Element ee = doc.createElement(configuration.getServiceName());
        configuration.toXML(doc, ee);
        if (ee.hasAttributes() || ee.hasChildNodes()) {
            return ee;
        } else {
            return null;
        }
    }

    /**
     * Writes a xml document.
     * 
     * @param configuration
     *            the cache configuration to read settings from
     * @param doc
     *            the xml based document to write to
     * @throws Exception
     *             something went wrong while writing the cache configuration
     */
    protected void writeDocument(CacheConfiguration<?, ?> configuration, Document doc)
            throws Exception {
        Element root = doc.createElement(CONFIG_TAG);
        root.setAttribute(CONFIG_VERSION_ATTR, CURRENT_VERSION);
        doc.appendChild(root);

        Element cache = doc.createElement(CACHE_TAG);
        root.appendChild(cache);
        writeSingleCache(configuration, doc, cache);
    }

    /**
     * Writes a single cache to a xml document.
     * 
     * @param cc
     *            the cache configuration to read settings from
     * @param doc
     *            the xml based document to write to
     * @param cacheElement
     *            the xml element to write the cache information to
     * @throws Exception
     *             something went wrong while writing the cache configuration
     */
    protected void writeSingleCache(CacheConfiguration<?, ?> cc, Document doc, Element cacheElement)
            throws Exception {
        cacheElement.setAttribute(CACHE_NAME_ATTR, cc.getName());
        if (cc.getCacheType() != null) {
            cacheElement.setAttribute(CACHE_TYPE_ATTR, cc.getCacheType().getName());
        }
        if (cc.getProperties().size() > 0) {
            Element ee = doc.createElement(PROPERTIES_TAG);

            for (Map.Entry<String, String> e : new TreeMap<String, String>(cc.getProperties())
                    .entrySet()) {
                Element property = doc.createElement(PROPERTY_TAG);
                property.setAttribute(PROPERTY_KEY_ATTR, e.getKey());
                property.setTextContent(e.getValue());
                ee.appendChild(property);
            }
            cacheElement.appendChild(ee);
        }
        /* writeService other configurations */
        for (AbstractCacheServiceConfiguration<?, ?> p : cc.getAllConfigurations()) {
            Element n = writeCacheService(doc, p);
            if (n != null) {
                cacheElement.appendChild(n);
            }
        }
    }
}
