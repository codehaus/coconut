/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.threading;

import static org.coconut.internal.util.XmlUtil.add;
import static org.coconut.internal.util.XmlUtil.addComment;
import static org.coconut.internal.util.XmlUtil.getChild;
import static org.coconut.internal.util.XmlUtil.loadObject;
import static org.coconut.internal.util.XmlUtil.saveObject;

import java.util.Comparator;
import java.util.HashMap;
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

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.util.UnitOfTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheThreadingConfiguration<K, V> extends
        AbstractCacheServiceConfiguration<K, V> {
    private final static CacheThreadingConfiguration DEFAULT = new CacheThreadingConfiguration();

    private final static HashMap<Class, String> policy = new HashMap<Class, String>();
    static {
        policy.put(ThreadPoolExecutor.AbortPolicy.class, "abort");
        policy.put(ThreadPoolExecutor.CallerRunsPolicy.class, "callerRuns");
        policy.put(ThreadPoolExecutor.DiscardOldestPolicy.class, "discardOldest");
        policy.put(ThreadPoolExecutor.DiscardPolicy.class, "discard");
    }

    public final static String CORE_SIZE_ATRB = "core-size";

    public final static String MAX_SIZE_ATRB = "max-size";

    public final static String EXECUTOR_TAG = "threadpool";

    public final static String THREAD_FACTORY_TAG = "thread-factory";

    public final static String SCHEDULED_EXECUTOR_TAG = "scheduled-threadpool";

    public final static String REJECTED_EXECUTION_HANDLER_TAG = "rejectedExecutionHandler";

    public final static String SERVICE_NAME = "threading";

    public final static String QUEUE_TAG = "queue";

    public final static String SHUTDOWN_EXECUTOR_SERVICE_TAG = "shutdown-executor-service";

    public final static String SCHEDULE_EVICT_TAG = "schedule-evict";

    private final static Class DEFAULT_REH = ThreadPoolExecutor.AbortPolicy.class;

    private boolean shutdownExecutor;

    private Executor executor;

    long scheduleEvictionAtFixedRateNanos = 0;

    /**
     * @param tag
     * @param c
     */
    public CacheThreadingConfiguration() {
        super(SERVICE_NAME, CacheThreadingService.class);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#fromXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void fromXML(Document doc, Element e) throws Exception {

        /* Register */
        if (e.hasAttribute(SHUTDOWN_EXECUTOR_SERVICE_TAG)) {
            setShutdownExecutorService(Boolean.parseBoolean(e
                    .getAttribute(SHUTDOWN_EXECUTOR_SERVICE_TAG)));
        }

        Element evict = getChild(SCHEDULE_EVICT_TAG, e);
        if (evict != null) {
            long timeout = UnitOfTime.fromElement(evict, TimeUnit.NANOSECONDS);
            setScheduledEvictionAtFixedRate(timeout, TimeUnit.NANOSECONDS);
        }
        Element threadPool = getChild(EXECUTOR_TAG, e);
        Element sceduledThreadPool = getChild(SCHEDULED_EXECUTOR_TAG, e);
        Element common = threadPool == null ? sceduledThreadPool : threadPool;
        if (common != null) {
            Element tfElement = getChild(THREAD_FACTORY_TAG, common);
            ThreadFactory tf = tfElement == null ? Executors.defaultThreadFactory()
                    : loadObject(tfElement, ThreadFactory.class);

            Element rejectElement = getChild(REJECTED_EXECUTION_HANDLER_TAG, common);
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
                    maximumSize = Integer
                            .parseInt(threadPool.getAttribute(MAX_SIZE_ATRB));
                }
                long timeout = 0;
                if (threadPool.hasAttribute("keepAlive")) {
                    timeout = UnitOfTime.fromAttributes(threadPool, TimeUnit.NANOSECONDS,
                            "keepAlive", "keepAliveUnit");
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
            setExecutor(ee);
        }
    }

    private void writeExecutor(Document doc, Element base, ThreadPoolExecutor tpe,
            boolean isScheduled) {

        Element exTag = isScheduled ? add(doc, SCHEDULED_EXECUTOR_TAG, base) : add(doc,
                EXECUTOR_TAG, base);
        if (!"java.util.concurrent.Executors.DefaultThreadFactory".equals(tpe
                .getThreadFactory().getClass().getCanonicalName())) {
            Element tfTag = add(doc, THREAD_FACTORY_TAG, exTag);
            saveObject(doc, tfTag, "threading.cannotPersistThreadFactory", tpe
                    .getThreadFactory());
        }
        /* RejectedExecutionHandler */
        RejectedExecutionHandler reh = tpe.getRejectedExecutionHandler();
        if (!DEFAULT_REH.equals(reh.getClass())) {
            Element rehTag = add(doc, REJECTED_EXECUTION_HANDLER_TAG, exTag);
            if (policy.containsKey(reh.getClass())) {
                rehTag.setAttribute("type", policy.get(reh.getClass()));
            } else {
                saveObject(doc, rehTag, "threading.cannotPersistREH", reh);
            }
        }

        /* Queue */
        if (!isScheduled) {
            Class c = tpe.getQueue().getClass();
            if (c.equals(ArrayBlockingQueue.class)) {
                ArrayBlockingQueue q = (ArrayBlockingQueue) tpe.getQueue();
                Element abq = add(doc, "array-queue", exTag);
                abq.setAttribute("size", Integer.toString(q.size()
                        + q.remainingCapacity()));
            } else if (c.equals(LinkedBlockingQueue.class)) {
                // default
            } else if (c.equals(PriorityBlockingQueue.class)) {
                PriorityBlockingQueue q = (PriorityBlockingQueue) tpe.getQueue();
                Element qElement = add(doc, "priorityQueue", exTag);
                Comparator comp = q.comparator();
                if (comp != null
                        && !saveObject(doc, qElement,
                                "threading.cannotPersistComperator", comp)) {
                    // Queue cannot be set on ThreadPoolExecutor
                    base.removeChild(exTag);
                    return;
                }
            } else if (c.equals(SynchronousQueue.class)) {
                add(doc, "synchronous-queue", exTag);
            } else {
                Element q = add(doc, "queue", exTag);
                if (!saveObject(doc, q, "threading.cannotPersistQueue", tpe.getQueue())) {
                    base.removeChild(exTag);
                    return;
                }
            }
        }

        /* Attributes */
        exTag.setAttribute(CORE_SIZE_ATRB, Integer.toString(tpe.getCorePoolSize()));
        if (tpe.getCorePoolSize() != tpe.getMaximumPoolSize()) {
            exTag.setAttribute(MAX_SIZE_ATRB, Integer.toString(tpe.getMaximumPoolSize()));
        }
        if (tpe.getKeepAliveTime(TimeUnit.NANOSECONDS) != 0) {
            UnitOfTime.toElementAttributes(exTag, tpe
                    .getKeepAliveTime(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS,
                    "keepAlive", "keepAliveUnit");
        }
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#toXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void toXML(Document doc, Element base) throws Exception {
        /* Register */
        Executor e = executor;
        if (e != DEFAULT.getExecutor()) {
            boolean isThreadPoolExecutor = e.getClass().equals(ThreadPoolExecutor.class);
            if (isThreadPoolExecutor
                    || e.getClass().equals(ScheduledThreadPoolExecutor.class)) {
                writeExecutor(doc, base, (ThreadPoolExecutor) e, !isThreadPoolExecutor);
            } else {
                addComment(doc, "threading.cannotPersistExecutor", base, e.getClass()
                        .getCanonicalName());
            }
        }
        if (getShutdownExecutorService() != DEFAULT.getShutdownExecutorService()) {
            base.setAttribute(SHUTDOWN_EXECUTOR_SERVICE_TAG, Boolean
                    .toString(getShutdownExecutorService()));
        }

        /* Refresh Timer */
        long evict = getScheduledEvictionAtFixedRate(TimeUnit.NANOSECONDS);
        if (evict != DEFAULT.getScheduledEvictionAtFixedRate(TimeUnit.NANOSECONDS)) {
            UnitOfTime.toElementCompact(add(doc, SCHEDULE_EVICT_TAG, base), evict,
                    TimeUnit.NANOSECONDS);
        }

    }

    public Executor getExecutor() {
        return executor;
    }

    public long getScheduledEvictionAtFixedRate(TimeUnit unit) {
        return unit.convert(scheduleEvictionAtFixedRateNanos, TimeUnit.NANOSECONDS);
    }

    public boolean getShutdownExecutorService() {
        return shutdownExecutor;
    }

    public CacheThreadingConfiguration setExecutor(Executor e) {
        executor = e;
        return this;
    }

    public CacheThreadingConfiguration setScheduledEvictionAtFixedRate(long period,
            TimeUnit unit) {
        if (period < 0) {
            throw new IllegalArgumentException("period must be 0 or greater, was "
                    + period);
        }
        scheduleEvictionAtFixedRateNanos = unit.toNanos(period);
        return this;
    }

    public CacheThreadingConfiguration setShutdownExecutorService(boolean shutdown) {
        shutdownExecutor = shutdown;
        return this;
    }
}
