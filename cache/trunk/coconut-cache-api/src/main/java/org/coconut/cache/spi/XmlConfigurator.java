/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.coconut.cache.CacheConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * XmlConfigurator is used to load and save {@link org.coconut.cache.CacheConfiguration}
 * as XML. Normally users should not rely on this class but instead use
 * {@link CacheConfiguration#createConfiguration(InputStream)}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlConfigurator {

    /**
     * The key for which the type of the cache is specified in
     * CacheConfiguration.getProperty().
     */
    public static final String CACHE_INSTANCE_TYPE = "org.coconut.cache.type";

    /** The current version of the XML schema. */
    public static final String CURRENT_VERSION = "0.0.4";

    /** The name of cache. */
    static final String CACHE_NAME_ATTR = "name";

    /** The root tag for a cache instance. */
    static final String CACHE_TAG = "cache";

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
     */
    public <K, V> void readInto(CacheConfiguration<K, V> configuration, InputStream stream)
            throws Exception {
        if (configuration == null) {
            throw new NullPointerException("configuration is null");
        } else if (stream == null) {
            throw new NullPointerException("stream is null");
        }
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                stream);
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
    public void write(CacheConfiguration<?, ?> configuration, OutputStream stream)
            throws Exception {
        if (configuration == null) {
            throw new NullPointerException("configuration is null");
        } else if (stream == null) {
            throw new NullPointerException("stream is null");
        }
        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        Document doc = builder.newDocument();
        writeDocument(configuration, doc);
        prettyprint(doc, stream);
    }

    protected <K, V> AbstractCacheServiceConfiguration<K, V> readCacheService(
            Document doc, Element cache,
            Class<? extends AbstractCacheServiceConfiguration<K, V>> service)
            throws Exception {
        AbstractCacheServiceConfiguration<K, V> acsc = service.newInstance();
        Element e = (Element) cache.getElementsByTagName(acsc.getServiceName()).item(0);
        if (e != null) {
            acsc.fromXML(e);
        }
        return acsc;
    }

    protected <K, V> void readDocument(CacheConfiguration<K, V> base, Document doc)
            throws Exception {
        Element root = doc.getDocumentElement();
        int length = root.getElementsByTagName(CACHE_TAG).getLength();
        if (length == 0) {
            throw new IllegalStateException(
                    "No cache is defined in the specified document, "
                            + doc.getDocumentURI());
        }
        Node n = root.getElementsByTagName("cache").item(0);
        readSingleCache(base, doc, base.getServiceTypes(), (Element) n);
    }

    protected <K, V> void readSingleCache(
            CacheConfiguration<K, V> conf,
            Document doc,
            Collection<Class<? extends AbstractCacheServiceConfiguration<K, V>>> services,
            Element cache) throws Exception {
        if (cache.hasAttribute(CACHE_NAME_ATTR)
                && !cache.getAttribute(CACHE_NAME_ATTR).equals("")) {
            conf.setName(cache.getAttribute(CACHE_NAME_ATTR));
        }
        if (cache.hasAttribute(CACHE_TYPE_ATTR)) {
            conf.setProperty(XmlConfigurator.CACHE_INSTANCE_TYPE, cache
                    .getAttribute(CACHE_TYPE_ATTR));
        }
        for (Class<? extends AbstractCacheServiceConfiguration<K, V>> c : services) {
            AbstractCacheServiceConfiguration<K, V> acsc = readCacheService(doc, cache, c);
            if (acsc != null) {
                conf.addConfiguration(acsc);
            }
        }
    }

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

    protected void writeDocument(CacheConfiguration<?, ?> configuration, Document doc)
            throws Exception {
        Element root = doc.createElement(CONFIG_TAG);
        root.setAttribute(CONFIG_VERSION_ATTR, CURRENT_VERSION);
        doc.appendChild(root);

        Element cache = doc.createElement(CACHE_TAG);
        root.appendChild(cache);
        writeSingleCache(configuration, doc, cache);
    }

    protected void writeSingleCache(CacheConfiguration<?, ?> cc, Document doc,
            Element cache) throws Exception {
        cache.setAttribute(CACHE_NAME_ATTR, cc.getName());
        if (cc.getProperties().containsKey(XmlConfigurator.CACHE_INSTANCE_TYPE)) {
            cache.setAttribute(CACHE_TYPE_ATTR, cc.getProperty(CACHE_INSTANCE_TYPE)
                    .toString());
        }
        /* writeService other configurations */
        for (AbstractCacheServiceConfiguration<?, ?> p : cc.getAllConfigurations()) {
            Element n = writeCacheService(doc, p);
            if (n != null) {
                cache.appendChild(n);
            }
        }
    }

    static void prettyprint(Document doc, OutputStream stream)
            throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StreamResult result = new StreamResult(stream);
        Transformer f = TransformerFactory.newInstance().newTransformer();
        f.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        f.setOutputProperty(OutputKeys.INDENT, "yes");
        f.transform(domSource, result);
    }
}
