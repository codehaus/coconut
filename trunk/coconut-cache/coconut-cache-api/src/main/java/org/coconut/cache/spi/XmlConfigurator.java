/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.core.Log;
import org.coconut.core.util.Logs;
import org.coconut.filter.Filter;
import org.coconut.internal.util.UnitOfTime;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlConfigurator {
    

    public static final String CACHE_INSTANCE_TYPE = "org.coconut.cache.type";
    
    
    public static class ExpirationPersister extends AbstractPersister {

        public final static String EXPIRATION_TAG = "expiration";

        public final static String DEFAULT_TIMEOUT_TAG = "expiration-timeout";

        public final static String EXPIRATION_FILTER_TAG = "expiration-filter";

        public final static String REFRESH_INTERVAL_TAG = "refresh-timer";

        public final static String REFRESH_FILTER_TAG = "refresh-filter";

        public CacheConfiguration.Expiration e() {
            return conf().expiration();
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#read()
         */
        @Override
        protected void read() throws Exception {
            Element e = getChild(EXPIRATION_TAG);
            /* Expiration timeout */
            Element defaultTimeout = getChild(DEFAULT_TIMEOUT_TAG, e);
            if (defaultTimeout != null) {
                long timeout = UnitOfTime.fromElement(defaultTimeout,
                        TimeUnit.MILLISECONDS);
                e().setDefaultTimeout(timeout, TimeUnit.MILLISECONDS);
            }
            /* Expiration Filter */
            Element filter = getChild(EXPIRATION_FILTER_TAG, e);
            if (filter != null) {
                Filter f = loadObject(filter, Filter.class);
                e().setFilter(f);
            }
            /* Refresh timer */
            Element refreshInterval = getChild(REFRESH_INTERVAL_TAG, e);
            if (refreshInterval != null) {
                long timeout = UnitOfTime.fromElement(refreshInterval,
                        TimeUnit.MILLISECONDS);
                e().setRefreshInterval(timeout, TimeUnit.MILLISECONDS);
            }
            
            /* Refresh Filter */
            Element refreshFilter = getChild(REFRESH_FILTER_TAG, e);
            if (refreshFilter != null) {
                Filter f = loadObject(refreshFilter, Filter.class);
                e().setRefreshFilter(f);
            }
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#write()
         */
        @Override
        protected void write() throws Exception {
            Element base = add(EXPIRATION_TAG);

            /* Expiration Timeout */
            long timeout = e().getDefaultTimeout(TimeUnit.MILLISECONDS);
            if (timeout != Cache.NEVER_EXPIRE) {
                UnitOfTime.toElementCompact(add(DEFAULT_TIMEOUT_TAG, base), timeout,
                        TimeUnit.MILLISECONDS);
            }

            /* Expiration Filter */
            Filter filter = e().getFilter();
            if (filter != null) {
                super.saveObject(add(EXPIRATION_FILTER_TAG, base), EXPIRATION_FILTER_TAG,
                        filter);
            }

            /* Refresh Timer */
            long refresh = e().getRefreshInterval(TimeUnit.MILLISECONDS);
            if (refresh > 0) {
                UnitOfTime.toElementCompact(add(REFRESH_INTERVAL_TAG, base), refresh,
                        TimeUnit.MILLISECONDS);
            }

            /* Refresh Filter */
            Filter refreshFilter = e().getFilter();
            if (refreshFilter != null) {
                super.saveObject(add(REFRESH_FILTER_TAG, base), REFRESH_FILTER_TAG, filter);
            }
        }
    }
    public static class ErrorHandlerPersister extends AbstractPersister {

        /**
         * @see org.coconut.cache.spi.XMLSupport.Persister#add(org.coconut.cache.CacheConfiguration,
         *      org.w3c.dom.Document, org.w3c.dom.Element)
         */
        protected void write() {
            CacheErrorHandler cee = conf().getErrorHandler();
            Element eh = add("errorhandler");
            if (cee.getClass().equals(CacheErrorHandler.class)) {
                if (cee.hasLogger()) {
                    Log log = cee.getLogger();
                    String name = log instanceof Logs.AbstractLogger ? ((Logs.AbstractLogger) log)
                            .getName()
                            : null;

                    final String logType;

                    if (Logs.Log4j.isLog4jLogger(log)) {
                        logType = "log4j";
                    } else if (Logs.Commons.isCommonsLogger(log)) {
                        logType = "commons";
                    } else if (Logs.JDK.isJDKLogger(log)) {
                        logType = "jdk";
                    } else {
                        addComment("errorhandler.notinstance2", eh, cee.getClass());
                        logType = null;
                    }
                    if (logType != null) {
                        add("log", eh, name).setAttribute("type", logType);
                    }

                }
            } else {
                addComment("errorhandler.notinstance1", cee.getClass());
            }
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#read()
         */
        @Override
        protected void read() {
            Element e = getChild("errorhandler");
            Element log = getChild("log", e);
            String type = log.getAttribute("type");
            if (type.equals("jdk")) {
                conf().setErrorHandler(
                        new CacheErrorHandler(Logs.JDK.from(log.getTextContent())));
            }
        }
    }
    
    public static abstract class AbstractPersister {

        private CacheConfiguration cc;

        private Document doc;

        private Element root;

        /**
         * @see org.coconut.cache.spi.XMLSupport.Persister#add(org.coconut.cache.CacheConfiguration,
         *      org.w3c.dom.Document, org.w3c.dom.Element)
         */
        void add(CacheConfiguration cc, Document doc, Element root) throws Exception {
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

        protected abstract void write() throws Exception;

        protected abstract void read() throws Exception;

        protected CacheConfiguration conf() {
            return cc;
        }

        protected Element getChild(String name) {
            for (int i = 0; i < root.getChildNodes().getLength(); i++) {
                if (root.getChildNodes().item(i).getNodeName().equals(name)) {
                    return (Element) root.getChildNodes().item(i);
                }
            }
            throw new IllegalArgumentException("Element with name (" + name
                    + ") could not be found");
        }

        protected Element getChild(String name, Element e) {
            for (int i = 0; i < e.getChildNodes().getLength(); i++) {
                if (e.getChildNodes().item(i).getNodeName().equals(name)) {
                    return (Element) e.getChildNodes().item(i);
                }
            }
            return null;
            // throw new IllegalArgumentException("Element with name (" + name
            // + ") could not be found");
        }

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

        protected void addComment(String comment, Object... o) {
            addComment(comment, root, o);
        }

        protected void addComment(String comment, Element e, Object... o) {
            String c = Ressources
                    .getString("org.coconut.cache.xml.comment." + comment, o);
            Comment eee = doc.createComment(c);
            e.appendChild(eee);
        }

        protected <T> T loadObject(Element e, Class<T> type) throws Exception {
            String c = e.getAttribute("type");
            Class<T> clazz = (Class) Class.forName(c);
            Constructor<T> con = clazz.getConstructor(null);
            return con.newInstance();
        }

        protected void saveObject(Element e, String name, Object o) {
            Constructor c = null;
            try {
                c = o.getClass().getConstructor(null);
            } catch (SecurityException e1) {
                addComment("missingconstructor", e, name, o.getClass());
                e.getParentNode().removeChild(e);
            } catch (NoSuchMethodException e1) {
                addComment("missingconstructor", e, name, o.getClass());
                e.getParentNode().removeChild(e);
            }
            if (c != null) {
                if ((!Modifier.isPublic(c.getModifiers()))) {
                    addComment("missingpublicconstructor", e, name, o.getClass());
                    e.getParentNode().removeChild(e);
                } else {
                    e.setAttribute("type", o.getClass().getName());
                }
            }
        }
    }

    private final static XmlConfigurator DEFAULT = new XmlConfigurator();

    public static final String CURRENT_VERSION = "1.0.0";

    public static final String CONFIG_TAG = "cache-config";

    public static final String CONFIG_VERSION_ATTR = "version";

    public static final String CACHE_TAG = "cache";

    public static final String CACHE_NAME_ATTR = "name";

    public static final String CACHE_TYPE_ATTR = "type";

    public static XmlConfigurator getInstance() {
        return DEFAULT;
    }

    public <K, V> CacheConfiguration<K, V> from(InputStream stream) throws Exception {
        CacheConfiguration<K, V> conf = CacheConfiguration.create();
        from(conf, stream);
        return conf;
    }

    public <K, V> void from(CacheConfiguration<K, V> base, InputStream stream)
            throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                stream);
        from(base, doc);
    }

    protected <K, V> void from(CacheConfiguration<K, V> base, Document doc)
            throws Exception {
        Element root = doc.getDocumentElement();
        int length = root.getElementsByTagName(CACHE_TAG).getLength();
        if (length == 0) {
            throw new IllegalStateException(
                    "No cache is defined in the specified document, "
                            + doc.getDocumentURI());
        } else if (length > 1) {
            throw new IllegalStateException("Only one cache can be defined, "
                    + doc.getDocumentURI());
        } else {
            Node n = root.getElementsByTagName("cache").item(0);
            from(base, doc, (Element) n);
        }
    }

    protected <K, V> void from(CacheConfiguration<K, V> conf, Document doc, Element cache)
            throws Exception {
        if (cache.hasAttribute(CACHE_NAME_ATTR)) {
            conf.setName(cache.getAttribute(CACHE_NAME_ATTR));
        }
        for (AbstractPersister p : getPersisters()) {
            p.read(conf, cache);
        }
    }

    protected List<AbstractPersister> getPersisters() {
        ArrayList<AbstractPersister> list = new ArrayList<AbstractPersister>();
        list.add(new ErrorHandlerPersister());
        list.add(new ExpirationPersister());
        return list;
    }

    public void to(CacheConfiguration<?, ?> cc, OutputStream stream) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        Document doc = builder.newDocument();
        doc.setDocumentURI("http://www.dr.dk");
        Element root = doc.createElement(CONFIG_TAG);
        root.setAttribute(CONFIG_VERSION_ATTR, CURRENT_VERSION);

        Element cache = doc.createElement(CACHE_TAG);
        root.appendChild(cache);
        doc.appendChild(root);
        to(cc, doc, cache);
        transform(doc, stream);
    }

    protected void to(CacheConfiguration<?, ?> cc, Document doc, Element cache)
            throws Exception {
        cache.setAttribute(CACHE_NAME_ATTR, cc.getName());
        if (cache.hasAttribute(CACHE_TYPE_ATTR)) {
            cc.setProperty(XmlConfigurator.CACHE_INSTANCE_TYPE, cache
                    .getAttribute(CACHE_TYPE_ATTR));
        }
        for (AbstractPersister p : getPersisters()) {
            p.add(cc, doc, cache);
        }
    }

    protected void transform(Document doc, OutputStream stream)
            throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StreamResult result = new StreamResult(stream);
        Transformer f = TransformerFactory.newInstance().newTransformer();
        f.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        f.setOutputProperty(OutputKeys.INDENT, "yes");
        f.transform(domSource, result);
    }
}
