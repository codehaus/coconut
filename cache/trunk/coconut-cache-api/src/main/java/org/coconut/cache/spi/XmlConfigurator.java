/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
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
import org.coconut.cache.CacheLoader;
import org.coconut.core.Log;
import org.coconut.core.util.Logs;
import org.coconut.filter.Filter;
import org.coconut.internal.util.UnitOfTime;
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
        for (AbstractConfigurator p : getPersisters()) {
            p.read(conf, cache);
        }
    }

    List<AbstractConfigurator> getPersisters() {
        ArrayList<AbstractConfigurator> list = new ArrayList<AbstractConfigurator>();
        list.add(new BackendConfigurator());
        list.add(new ErrorHandlerConfigurator());
        list.add(new ExpirationConfigurator());
        list.add(new EvictionConfigurator());
        list.add(new JMXConfigurator());
        list.add(new StatisticsConfigurator());
        list.add(new ThreadConfigurator());
        return list;
    }

    void to(CacheConfiguration<?, ?> cc, Document doc, Element cache) throws Exception {
        cache.setAttribute(CACHE_NAME_ATTR, cc.getName());
        if (cc.getProperties().containsKey(XmlConfigurator.CACHE_INSTANCE_TYPE)) {
            cache.setAttribute(CACHE_TYPE_ATTR, cc.getProperty(CACHE_INSTANCE_TYPE)
                    .toString());
        }
        for (AbstractConfigurator p : getPersisters()) {
            p.write(cc, doc, cache);
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

        protected <T> T loadObject(Element e, Class<T> type) throws Exception {
            String c = e.getAttribute("type");
            Class<T> clazz = (Class) Class.forName(c);
            Constructor<T> con = clazz.getConstructor(null);
            return con.newInstance();
        }

        protected abstract void read() throws Exception;

        boolean saveObject(Element e, String commentName, String atrbName, Object o) {
            Constructor c = null;
            try {
                c = o.getClass().getConstructor(null);
                e.setAttribute(atrbName, o.getClass().getName());
                return true;
            } catch (NoSuchMethodException e1) {
                addComment(commentName, e.getParentNode(), o.getClass());
                e.getParentNode().removeChild(e);
            }
            return false;
        }

        boolean saveObject(Element e, String name, Object o) {
            return saveObject(e, name, "type", o);
        }

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
                                new CacheErrorHandler(Logs
                                        .fromLog4j(log.getTextContent())));
                    } else {
                        // commons, this should guaranteed by schema validation
                        conf().setErrorHandler(
                                new CacheErrorHandler(Logs.fromCommons(log
                                        .getTextContent())));
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

    static class ThreadConfigurator extends AbstractConfigurator {

        public final static String CORE_SIZE_ATRB = "core-size";

        public final static String MAX_SIZE_ATRB = "max-size";

        public final static String EXECUTOR_TAG = "threadpool";

        public final static String THREAD_FACTORY_TAG = "thread-factory";

        public final static String SCHEDULED_EXECUTOR_TAG = "scheduled-threadpool";

        public final static String REJECTED_EXECUTION_HANDLER_TAG = "rejectedExecutionHandler";

        public final static String THREADING_TAG = "threading";

        public final static String QUEUE_TAG = "queue";

        public final static String SHUTDOWN_EXECUTOR_SERVICE_TAG = "shutdown-executor-service";

        public final static String SCHEDULE_EVICT_TAG = "schedule-evict";

        private final static Class DEFAULT_REH = ThreadPoolExecutor.AbortPolicy.class;

        public CacheConfiguration.Threading t() {
            return conf().threading();
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#read()
         */
        @Override
        protected void read() throws Exception {
            Element e = getChild(THREADING_TAG);
            if (e != null) {
                /* Register */
                if (e.hasAttribute(SHUTDOWN_EXECUTOR_SERVICE_TAG)) {
                    t().setShutdownExecutorService(
                            Boolean.parseBoolean(e
                                    .getAttribute(SHUTDOWN_EXECUTOR_SERVICE_TAG)));
                }

                Element evict = getChild(SCHEDULE_EVICT_TAG, e);
                if (evict != null) {
                    long timeout = UnitOfTime.fromElement(evict, TimeUnit.NANOSECONDS);
                    t().setScheduledEvictionAtFixedRate(timeout, TimeUnit.NANOSECONDS);
                }
                Element threadPool = getChild(EXECUTOR_TAG, e);
                Element sceduledThreadPool = getChild(SCHEDULED_EXECUTOR_TAG, e);
                Element common = threadPool == null ? sceduledThreadPool : threadPool;
                if (common != null) {
                    Element tfElement = getChild(THREAD_FACTORY_TAG, common);
                    ThreadFactory tf = tfElement == null ? Executors
                            .defaultThreadFactory() : loadObject(tfElement,
                            ThreadFactory.class);

                    Element rejectElement = getChild(REJECTED_EXECUTION_HANDLER_TAG,
                            common);
                    final RejectedExecutionHandler reh;
                    if (rejectElement == null
                            || rejectElement.getAttribute("type").equals("abort")) {
                        reh = new ThreadPoolExecutor.AbortPolicy();
                    } else if (rejectElement.getAttribute("type").equals("callerRuns")) {
                        reh = new ThreadPoolExecutor.CallerRunsPolicy();
                    } else if (rejectElement.getAttribute("type").equals("discardOldest")) {
                        reh = new ThreadPoolExecutor.DiscardOldestPolicy();
                    } else if (rejectElement.getAttribute("type").equals("discard")) {
                        reh = new ThreadPoolExecutor.DiscardPolicy();
                    } else {
                        reh = loadObject(rejectElement, RejectedExecutionHandler.class);
                    }

                    int coreSize = Integer.parseInt(common.getAttribute(CORE_SIZE_ATRB));
                    final Executor ee;
                    if (sceduledThreadPool == null) {
                        int maximumSize = coreSize;
                        if (threadPool.hasAttribute(MAX_SIZE_ATRB)) {
                            maximumSize = Integer.parseInt(threadPool
                                    .getAttribute(MAX_SIZE_ATRB));
                        }
                        long timeout = 0;
                        if (threadPool.hasAttribute("keepAlive")) {
                            timeout = UnitOfTime.fromAttributes(threadPool,
                                    TimeUnit.NANOSECONDS, "keepAlive", "keepAliveUnit");
                        }
                        BlockingQueue q = new LinkedBlockingQueue();

                        Element bq = getChild("array-queue", threadPool);
                        if (bq != null) {
                            int capacity = Integer.parseInt(bq.getAttribute("size"));
                            q = new ArrayBlockingQueue(capacity);
                        }
                        Element pq = getChild("priorityQueue", threadPool);
                        if (pq != null) {
                            Comparator c = null;
                            if (pq.hasAttribute("type")) {
                                c = loadObject(pq, Comparator.class);
                            }
                            q = new PriorityBlockingQueue(11, c);
                        }
                        Element sq = getChild("synchronous-queue", threadPool);
                        if (sq != null) {
                            q = new SynchronousQueue();
                        }

                        Element qq = getChild("queue", threadPool);
                        if (qq != null) {
                            q = loadObject(qq, BlockingQueue.class);
                        }

                        ee = new ThreadPoolExecutor(coreSize, maximumSize, timeout,
                                TimeUnit.NANOSECONDS, q, tf, reh);
                    } else {
                        ee = new ScheduledThreadPoolExecutor(coreSize, tf, reh);
                    }
                    t().setExecutor(ee);
                }
            }
        }

        private final static HashMap<Class, String> policy = new HashMap<Class, String>();
        static {
            policy.put(ThreadPoolExecutor.AbortPolicy.class, "abort");
            policy.put(ThreadPoolExecutor.CallerRunsPolicy.class, "callerRuns");
            policy.put(ThreadPoolExecutor.DiscardOldestPolicy.class, "discardOldest");
            policy.put(ThreadPoolExecutor.DiscardPolicy.class, "discard");
        }

        private void writeExecutor(Element base, ThreadPoolExecutor tpe,
                boolean isScheduled) {

            Element exTag = isScheduled ? add(SCHEDULED_EXECUTOR_TAG, base) : add(
                    EXECUTOR_TAG, base);
            if (!"java.util.concurrent.Executors.DefaultThreadFactory".equals(tpe
                    .getThreadFactory().getClass().getCanonicalName())) {
                Element tfTag = add(THREAD_FACTORY_TAG, exTag);
                saveObject(tfTag, "threading.cannotPersistThreadFactory", tpe
                        .getThreadFactory());
            }
            /* RejectedExecutionHandler */
            RejectedExecutionHandler reh = tpe.getRejectedExecutionHandler();
            if (!DEFAULT_REH.equals(reh.getClass())) {
                Element rehTag = add(REJECTED_EXECUTION_HANDLER_TAG, exTag);
                if (policy.containsKey(reh.getClass())) {
                    rehTag.setAttribute("type", policy.get(reh.getClass()));
                } else {
                    saveObject(rehTag, "threading.cannotPersistREH", reh);
                }
            }

            /* Queue */
            if (!isScheduled) {
                Class c = tpe.getQueue().getClass();
                if (c.equals(ArrayBlockingQueue.class)) {
                    ArrayBlockingQueue q = (ArrayBlockingQueue) tpe.getQueue();
                    Element abq = add("array-queue", exTag);
                    abq.setAttribute("size", Integer.toString(q.size()
                            + q.remainingCapacity()));
                } else if (c.equals(LinkedBlockingQueue.class)) {
                    // default
                } else if (c.equals(PriorityBlockingQueue.class)) {
                    PriorityBlockingQueue q = (PriorityBlockingQueue) tpe.getQueue();
                    Element qElement = add("priorityQueue", exTag);
                    Comparator comp = q.comparator();
                    if (comp != null
                            && !saveObject(qElement, "threading.cannotPersistComperator",
                                    comp)) {
                        // Queue cannot be set on ThreadPoolExecutor
                        base.removeChild(exTag);
                        return;
                    }
                } else if (c.equals(SynchronousQueue.class)) {
                    add("synchronous-queue", exTag);
                } else {
                    Element q = add("queue", exTag);
                    if (!saveObject(q, "threading.cannotPersistQueue", tpe.getQueue())) {
                        base.removeChild(exTag);
                        return;
                    }
                }
            }

            /* Attributes */
            exTag.setAttribute(CORE_SIZE_ATRB, Integer.toString(tpe.getCorePoolSize()));
            if (tpe.getCorePoolSize() != tpe.getMaximumPoolSize()) {
                exTag.setAttribute(MAX_SIZE_ATRB, Integer.toString(tpe
                        .getMaximumPoolSize()));
            }
            if (tpe.getKeepAliveTime(TimeUnit.NANOSECONDS) != 0) {
                UnitOfTime.toElementAttributes(exTag, tpe
                        .getKeepAliveTime(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS,
                        "keepAlive", "keepAliveUnit");
            }
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#write()
         */
        @Override
        protected void write() throws Exception {
            Element base = doc.createElement(THREADING_TAG);

            /* Register */
            Executor e = t().getExecutor();
            if (e != CONF.threading().getExecutor()) {
                boolean isThreadPoolExecutor = e.getClass().equals(
                        ThreadPoolExecutor.class);
                if (isThreadPoolExecutor
                        || e.getClass().equals(ScheduledThreadPoolExecutor.class)) {
                    writeExecutor(base, (ThreadPoolExecutor) e, !isThreadPoolExecutor);
                } else {
                    addComment("threading.cannotPersistExecutor", base, e.getClass()
                            .getCanonicalName());
                }
            }
            if (t().getShutdownExecutorService() != CONF.threading()
                    .getShutdownExecutorService()) {
                base.setAttribute(SHUTDOWN_EXECUTOR_SERVICE_TAG, Boolean.toString(t()
                        .getShutdownExecutorService()));
            }

            /* Refresh Timer */
            long evict = t().getScheduledEvictionAtFixedRate(TimeUnit.NANOSECONDS);
            if (evict != CONF.threading().getScheduledEvictionAtFixedRate(
                    TimeUnit.NANOSECONDS)) {
                UnitOfTime.toElementCompact(add(SCHEDULE_EVICT_TAG, base), evict,
                        TimeUnit.NANOSECONDS);
            }

            if (base.hasChildNodes() || base.hasAttributes()) {
                root.appendChild(base);
            }
        }
    }

    static class StatisticsConfigurator extends AbstractConfigurator {

        public final static String ENABLED_ATRB = "enabled";

        public final static String STATISTICS_TAG = "statistics";

        public CacheConfiguration.Statistics s() {
            return conf().statistics();
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#read()
         */
        @Override
        protected void read() throws Exception {
            Element e = getChild(STATISTICS_TAG);
            if (e != null) {
                /* Register */
                if (e.hasAttribute(ENABLED_ATRB)) {
                    s().setEnabled(Boolean.parseBoolean(e.getAttribute(ENABLED_ATRB)));
                }
            }
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#write()
         */
        @Override
        protected void write() throws Exception {
            /* Register */
            if (s().isEnabled() != CONF.statistics().isEnabled()) {
                Element base = doc.createElement(STATISTICS_TAG);
                base.setAttribute(ENABLED_ATRB, Boolean.toString(s().isEnabled()));
                root.appendChild(base);
            }
        }
    }

    static class JMXConfigurator extends AbstractConfigurator {

        public final static String REGISTER_ATRB = "auto-register";

        public final static String DOMAIN_TAG = "domain";

        public final static String EXPIRATION_TAG = "expiration";

        public final static String JMX_TAG = "jmx";

        public CacheConfiguration.JMX j() {
            return conf().jmx();
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#read()
         */
        @Override
        protected void read() throws Exception {
            Element e = getChild(JMX_TAG);
            if (e != null) {
                /* Register */
                if (e.hasAttribute(REGISTER_ATRB)) {
                    j().setAutoRegister(
                            Boolean.parseBoolean(e.getAttribute(REGISTER_ATRB)));
                }
                /* Domain */
                Element domain = getChild(DOMAIN_TAG, e);
                if (domain != null) {
                    j().setDomain(domain.getTextContent());
                }
            }
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#write()
         */
        @Override
        protected void write() throws Exception {
            Element base = doc.createElement(JMX_TAG);

            /* Register */
            if (j().getAutoRegister() != CONF.jmx().getAutoRegister()) {
                base.setAttribute(REGISTER_ATRB, Boolean.toString(j().getAutoRegister()));
            }

            /* Domain Filter */
            if (!(j().getDomain().equals(CONF.jmx().getDomain()))) {
                add(DOMAIN_TAG, base, j().getDomain());
            }
            /* MBeanServer */
            if (j().getMBeanServer() != CONF.jmx().getMBeanServer()) {
                addComment("management.cannotPersistMBeanServer", base);
            }

            if (base.hasChildNodes() || base.hasAttributes()) {
                root.appendChild(base);
            }
        }
    }

    static class BackendConfigurator extends AbstractConfigurator {

        public final static String LOADER_TAG = "loader";

        public final static String BACKEND_TAG = "backed";

        public final static String EXTENDED_LOADER_TAG = "extended-loader";

        public CacheConfiguration.Backend b() {
            return conf().backend();
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#read()
         */
        @Override
        protected void read() throws Exception {
            Element e = getChild(BACKEND_TAG);
            if (e != null) {
                Element loaderE = getChild(LOADER_TAG, e);
                if (loaderE != null) {
                    CacheLoader loader = loadObject(loaderE, CacheLoader.class);
                    b().setBackend(loader);
                }

                Element extendedLoader = getChild(EXTENDED_LOADER_TAG, e);
                if (extendedLoader != null) {
                    CacheLoader loader = loadObject(extendedLoader, CacheLoader.class);
                    b().setExtendedBackend(loader);
                }
            }
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#write()
         */
        @Override
        protected void write() throws Exception {
            Element base = doc.createElement(BACKEND_TAG);

            if (b().getBackend() != null) {
                saveObject(add(LOADER_TAG, base), "backend.cannotPersistLoader", b()
                        .getBackend());
            }
            if (b().getExtendedBackend() != null) {
                saveObject(add(EXTENDED_LOADER_TAG, base), "backend.cannotPersistLoader",
                        b().getExtendedBackend());
            }

            if (base.hasChildNodes()) {
                root.appendChild(base);
            }
        }
    }

    static class ExpirationConfigurator extends AbstractConfigurator {

        public final static String DEFAULT_TIMEOUT_TAG = "default-timeout";

        public final static String EXPIRATION_FILTER_TAG = "filter";

        public final static String EXPIRATION_TAG = "expiration";

        public final static String REFRESH_FILTER_TAG = "refresh-filter";

        public final static String REFRESH_INTERVAL_TAG = "refresh-timer";

        public CacheConfiguration.Expiration e() {
            return conf().expiration();
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#read()
         */
        @Override
        protected void read() throws Exception {
            Element e = getChild(EXPIRATION_TAG);
            if (e != null) {
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
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#write()
         */
        @Override
        protected void write() throws Exception {
            Element base = doc.createElement(EXPIRATION_TAG);

            /* Expiration Timeout */
            long timeout = e().getDefaultTimeout(TimeUnit.MILLISECONDS);
            if (timeout != Cache.NEVER_EXPIRE) {
                UnitOfTime.toElementCompact(add(DEFAULT_TIMEOUT_TAG, base), timeout,
                        TimeUnit.MILLISECONDS);
            }

            /* Expiration Filter */
            Filter filter = e().getFilter();
            if (filter != null) {
                super.saveObject(add(EXPIRATION_FILTER_TAG, base),
                        "expiration.cannotPersistFilter", filter);
            }

            /* Refresh Timer */
            long refresh = e().getRefreshInterval(TimeUnit.MILLISECONDS);
            if (refresh > 0) {
                UnitOfTime.toElementCompact(add(REFRESH_INTERVAL_TAG, base), refresh,
                        TimeUnit.MILLISECONDS);
            }

            /* Refresh Filter */
            Filter refreshFilter = e().getRefreshFilter();
            if (refreshFilter != null) {
                super.saveObject(add(REFRESH_FILTER_TAG, base),
                        "expiration.cannotPersistRefreshFilter", refreshFilter);
            }
            if (base.hasChildNodes()) {
                root.appendChild(base);
            }
        }
    }

    static class EvictionConfigurator extends AbstractConfigurator {

        public final static String MAXIMUM_CAPACITY = "max-capacity";

        public final static String MAXIMUM_SIZE = "max-size";

        public final static String EVICTION_TAG = "eviction";

        public final static String PREFERABLE_CAPACITY = "preferable-capacity";

        public final static String PREFERABLE_SIZE = "preferable-size";

        public CacheConfiguration.Eviction e() {
            return conf().eviction();
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#read()
         */
        @Override
        protected void read() throws Exception {
            Element e = getChild(EVICTION_TAG);
            if (e != null) {
                /* Maximum Capacity */
                Element maximumCapacity = getChild(MAXIMUM_CAPACITY, e);
                if (maximumCapacity != null) {
                    e().setMaximumCapacity(
                            Long.parseLong(maximumCapacity.getTextContent()));
                }

                /* Preferable Capacity */
                Element preferableCapacity = getChild(PREFERABLE_CAPACITY, e);
                if (preferableCapacity != null) {
                    e().setPreferableCapacity(
                            Long.parseLong(preferableCapacity.getTextContent()));
                }

                /* Maximum Size */
                Element maximumSize = getChild(MAXIMUM_SIZE, e);
                if (maximumSize != null) {
                    e().setMaximumSize(Integer.parseInt(maximumSize.getTextContent()));
                }

                /* Preferable Size */
                Element preferableSize = getChild(PREFERABLE_SIZE, e);
                if (preferableSize != null) {
                    e().setPreferableSize(
                            Integer.parseInt(preferableSize.getTextContent()));
                }

            }
        }

        /**
         * @see org.coconut.cache.spi.xml.AbstractPersister#write()
         */
        @Override
        protected void write() throws Exception {
            Element base = doc.createElement(EVICTION_TAG);

            /* Maximum Capacity */
            long maximumCapacity = e().getMaximumCapacity();
            if (maximumCapacity != CONF.eviction().getMaximumCapacity()) {
                add(MAXIMUM_CAPACITY, base)
                        .setTextContent(Long.toString(maximumCapacity));
            }

            /* Preferable Capacity */
            long preferableCapacity = e().getPreferableCapacity();
            if (preferableCapacity != CONF.eviction().getPreferableCapacity()) {
                add(PREFERABLE_CAPACITY, base).setTextContent(
                        Long.toString(preferableCapacity));
            }

            /* Maximum Size */
            int maximumSize = e().getMaximumSize();
            if (maximumSize != CONF.eviction().getMaximumSize()) {
                add(MAXIMUM_SIZE, base).setTextContent(Integer.toString(maximumSize));
            }

            /* Preferable Size */
            int preferableSize = e().getPreferableSize();
            if (preferableSize != CONF.eviction().getPreferableSize()) {
                add(PREFERABLE_SIZE, base).setTextContent(
                        Integer.toString(preferableSize));
            }

            if (base.hasChildNodes()) {
                root.appendChild(base);
            }
        }
    }
}
