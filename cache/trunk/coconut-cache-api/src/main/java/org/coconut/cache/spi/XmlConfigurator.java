/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheErrorHandler;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.service.statistics.CacheStatisticsConfiguration;
import org.coconut.cache.service.threading.CacheThreadingConfiguration;
import org.coconut.core.Log;
import org.coconut.core.util.Logs;
import org.coconut.internal.util.LogHelper;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * XmlConfigurator is used to load and save
 * {@link org.coconut.cache.CacheConfiguration} as XML. Normally users do not
 * use this class but instead rely on
 * {@link CacheConfiguration#create(InputStream)},
 * {@link CacheConfiguration#createAndInstantiate(InputStream)} or
 * {@link CacheConfiguration#createInstantiateAndStart(InputStream)} methods in
 * {@link CacheConfiguration}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlConfigurator {

    /**
     * The key for which the type of the cache is specified in
     * CacheConfiguration.getProperty()
     */
    public static final String CACHE_INSTANCE_TYPE = "org.coconut.cache.type";

    /** The name of cache */
    public static final String CACHE_NAME_ATTR = "name";

    /** The root tag for a cache instance */
    public static final String CACHE_TAG = "cache";

    /** The type of the cache */
    public static final String CACHE_TYPE_ATTR = "type";

    /** The root tag */
    public static final String CONFIG_TAG = "cache-config";

    /** The cache-config->version tag */
    public static final String CONFIG_VERSION_ATTR = "version";

    /** The current version of the XML schema */
    public static final String CURRENT_VERSION = "0.0.1";

    private final static XmlConfigurator DEFAULT = new XmlConfigurator();

    final static CacheConfiguration CONF = CacheConfiguration.create();

    private final static List<Class> services = new ArrayList<Class>();
    static {
        // services.add(CacheEventConfiguration.class);
        services.add(CacheManagementConfiguration.class);
        services.add(CacheStatisticsConfiguration.class);
        services.add(CacheLoadingConfiguration.class);
        services.add(CacheExpirationConfiguration.class);
        services.add(CacheThreadingConfiguration.class);
        services.add(CacheEvictionConfiguration.class);
    }

    /**
     * Returns the default instance of a XmlConfigurator.
     */
    public static XmlConfigurator getInstance() {
        return DEFAULT;
    }

    /**
     * Reads the XML configuration from the specified InputStream and populates
     * the specified CacheConfiguration.
     * 
     * @param configuration
     *            the CacheConfiguration to populate
     * @param stream
     *            the InputStream to load the configuration from
     * @throws Exception
     *             some Exception prevented the CacheConfiguration from being
     *             populated
     */
    public <K, V> void from(CacheConfiguration<K, V> configuration, InputStream stream)
            throws Exception {
        if (configuration == null) {
            throw new NullPointerException("configuration is null");
        } else if (stream == null) {
            throw new NullPointerException("stream is null");
        }
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                stream);
        from(configuration, doc);
    }

    /**
     * Reads the XML configuration from the specified InputStream and returns a
     * new populated CacheConfiguration.
     * 
     * @param stream
     *            the InputStream to load the configuration from
     * @return the CacheConfiguration as read from the specified InputStream
     * @throws Exception
     *             some Exception prevented the CacheConfiguration from being
     *             populated
     */
    public <K, V> CacheConfiguration<K, V> from(InputStream stream) throws Exception {
        CacheConfiguration<K, V> conf = CacheConfiguration.create();
        from(conf, stream);
        return conf;
    }

    /**
     * Serializes the specified CacheConfiguration to the specified
     * OutputStream.
     * 
     * @param cc
     *            the CacheConfiguration to serialize to XML
     * @param stream
     *            the OutputStream to write the serialized CacheConfiguration
     * @throws Exception
     *             some Exception prevented the CacheConfiguration from being
     *             serialized
     */
    public void to(CacheConfiguration<?, ?> cc, OutputStream stream) throws Exception {
        if (cc == null) {
            throw new NullPointerException("cc is null");
        } else if (stream == null) {
            throw new NullPointerException("stream is null");
        }
        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        Document doc = builder.newDocument();
        doc.setDocumentURI("http://www.dr.dk");

        Element root = doc.createElement(CONFIG_TAG);
        root.setAttribute(CONFIG_VERSION_ATTR, CURRENT_VERSION);
        doc.appendChild(root);

        Element cache = doc.createElement(CACHE_TAG);
        root.appendChild(cache);

        to(cc, doc, cache);
        transform(doc, stream);
    }

    <K, V> void from(CacheConfiguration<K, V> base, Document doc) throws Exception {
        Element root = doc.getDocumentElement();
        // int length = root.getElementsByTagName(CACHE_TAG).getLength();
        // if (length == 0) {
        // throw new IllegalStateException(
        // "No cache is defined in the specified document, "
        // + doc.getDocumentURI());
        // } else if (length > 1) {
        // throw new IllegalStateException("Only one cache can be defined, "
        // + doc.getDocumentURI());
        // } else {
        Node n = root.getElementsByTagName("cache").item(0);
        from(base, doc, (Element) n);
    }

    <K, V> void from(CacheConfiguration<K, V> conf, Document doc, Element cache)
            throws Exception {
        if (cache.hasAttribute(CACHE_NAME_ATTR)) {
            conf.setName(cache.getAttribute(CACHE_NAME_ATTR));
        }
        if (cache.hasAttribute(CACHE_TYPE_ATTR)) {
            conf.setProperty(XmlConfigurator.CACHE_INSTANCE_TYPE, cache
                    .getAttribute(CACHE_TYPE_ATTR));
        }
        new ErrorHandlerConfigurator().read(conf, cache);
        for (Class c : services) {
            AbstractCacheServiceConfiguration acsc = (AbstractCacheServiceConfiguration) c
                    .newInstance();
            Element e = (Element) cache.getElementsByTagName(acsc.tag).item(0);
            if (e != null) {
                conf.addService(acsc);
                acsc.fromXML(doc, e);
            }
        }
    }

    void to(CacheConfiguration<?, ?> cc, Document doc, Element cache) throws Exception {
        cache.setAttribute(CACHE_NAME_ATTR, cc.getName());
        if (cc.getProperties().containsKey(XmlConfigurator.CACHE_INSTANCE_TYPE)) {
            cache.setAttribute(CACHE_TYPE_ATTR, cc.getProperty(CACHE_INSTANCE_TYPE)
                    .toString());
        }
        new ErrorHandlerConfigurator().write(cc, doc, cache);
        for (AbstractCacheServiceConfiguration p : cc.getServices()) {
            Element ee = doc.createElement(p.tag);
            cache.appendChild(ee);
            p.toXML(doc, ee);
        }
    }

    void transform(Document doc, OutputStream stream) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StreamResult result = new StreamResult(stream);
        Transformer f = TransformerFactory.newInstance().newTransformer();
        f.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        f.setOutputProperty(OutputKeys.INDENT, "yes");
        f.transform(domSource, result);
    }

    static abstract class AbstractConfigurator {

        private CacheConfiguration cc;

        Document doc;

        Element root;

        protected Element add(String name) {
            return add(name, root);
        }

        protected Element add(String name, Element parent) {
            Element ee = doc.createElement(name);
            parent.appendChild(ee);
            return ee;
        }

        protected Element add(String name, Element parent, String text) {
            Element ee = doc.createElement(name);
            parent.appendChild(ee);
            ee.setTextContent(text);
            return ee;
        }

        protected void addComment(String comment, Node e, Object... o) {
            String c = Resources.lookup(XmlConfigurator.class, comment, o);
            Comment eee = doc.createComment(c);
            e.appendChild(eee);
        }

        protected CacheConfiguration conf() {
            return cc;
        }

        protected Element getChild(String name) {
            return getChild(name, root);
        }

        protected Element getChild(String name, Element e) {
            for (int i = 0; i < e.getChildNodes().getLength(); i++) {
                if (e.getChildNodes().item(i).getNodeName().equals(name)) {
                    return (Element) e.getChildNodes().item(i);
                }
            }
            return null;
        }

        protected abstract void read() throws Exception;

        protected abstract void write() throws Exception;

        /**
         * @see org.coconut.cache.spi.XMLSupport.Persister#add(org.coconut.cache.CacheConfiguration,
         *      org.w3c.dom.Document, org.w3c.dom.Element)
         */
        void write(CacheConfiguration cc, Document doc, Element root) throws Exception {
            this.cc = cc;
            this.doc = doc;
            this.root = root;
            write();

        }

        /**
         * @see org.coconut.cache.spi.XMLSupport.Persister#read(org.coconut.cache.CacheConfiguration,
         *      org.w3c.dom.Element)
         */
        void read(CacheConfiguration cc, Element root) throws Exception {
            this.cc = cc;
            this.root = root;
            read();
        }
    }

    static class ErrorHandlerConfigurator extends AbstractConfigurator {

        public final static String LOG_TYPE_ATRB = "type";

        public final static String LOG_TAG = "log";

        public final static String ERRORHANDLER_TAG = "errorhandler";

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#read()
         */
        @Override
        protected void read() {
            Element e = getChild(ERRORHANDLER_TAG);
            if (e != null) {
                Element log = getChild(LOG_TAG, e);
                if (log != null) {
                    String type = log.getAttribute(LOG_TYPE_ATRB);
                    if (type.equals("jdk")) {
                        conf()
                                .setErrorHandler(
                                        new CacheErrorHandler(Logs.JDK.from(log
                                                .getTextContent())));
                    } else if (type.equals("log4j")) {
                        conf().setErrorHandler(
                                new CacheErrorHandler(LogHelper.fromLog4j(log
                                        .getTextContent())));
                    } else {
                        // commons, this should guaranteed by schema validation
                        Log l = LogHelper.fromCommons(log.getTextContent());
                        conf().setErrorHandler(new CacheErrorHandler(l));
                    }
                }
            }
        }

        /**
         * @see org.coconut.cache.spi.XMLSupport.Persister#add(org.coconut.cache.CacheConfiguration,
         *      org.w3c.dom.Document, org.w3c.dom.Element)
         */
        protected void write() {
            CacheErrorHandler cee = conf().getErrorHandler();
            if (cee.getClass().equals(CacheErrorHandler.class)) {
                if (cee.hasLogger()) {
                    Element eh = add(ERRORHANDLER_TAG);
                    Log log = cee.getLogger();
                    String name = Logs.getName(log);

                    final String logType;

                    if (Logs.Log4j.isLog4jLogger(log)) {
                        logType = "log4j";
                    } else if (Logs.Commons.isCommonsLogger(log)) {
                        logType = "commons";
                    } else if (Logs.JDK.isJDKLogger(log)) {
                        logType = "jdk";
                    } else {
                        addComment("errorHandler.notInstanceLog", eh, log.getClass());
                        logType = null;
                    }
                    if (logType != null) {
                        add(LOG_TAG, eh, name).setAttribute(LOG_TYPE_ATRB, logType);
                    }
                }
            } else {
                addComment("errorHandler.notInstance", root, cee.getClass());
            }
        }
    }
   
}
