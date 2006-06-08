/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda.spi;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.coconut.event.seda.Stage;
import org.coconut.event.seda.management.StageEdgeInfo;
import org.coconut.event.seda.management.StageManagerMXBean;
import org.coconut.event.seda.management.StageStatistics;
import org.coconut.internal.jmx.JmxEmitterSupport;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AbstractManagedStageManager extends AbstractStageManager {

    private final static ThreadMXBean threadInfo = ManagementFactory
            .getThreadMXBean();

    protected final static long nanoTimeOverhead;

    protected final static long cpuTimeOverhead;

    protected final static long userTimeOverhead;
    static {
        final int counts = 11;
        long[] l = new long[counts];
        for (int i = 0; i < counts; i++) {
            long start = System.nanoTime();
            for (int j = 0; j < 99; j++) {
                System.nanoTime();
            }
            long finish = System.nanoTime();
            l[i] = (finish - start) / 100;
        }
        Arrays.sort(l);
        nanoTimeOverhead = l[counts / 2];

        for (int i = 0; i < counts; i++) {
            long start = System.nanoTime();
            for (int j = 0; j < 100; j++) {
                threadInfo.getCurrentThreadCpuTime();
            }
            long finish = System.nanoTime();
            l[i]= (finish - start - nanoTimeOverhead) / 100;
        }
        Arrays.sort(l);
        cpuTimeOverhead = l[counts / 2];

        for (int i = 0; i < counts; i++) {
            long start = System.nanoTime();
            for (int j = 0; j < 100; j++) {
                threadInfo.getCurrentThreadUserTime();
            }
            long finish = System.nanoTime();
            l[i]= (finish - start - nanoTimeOverhead) / 100;
        }
        Arrays.sort(l);
        userTimeOverhead = l[counts / 2];
    }
    final AtomicLong totalCpuTime = new AtomicLong();

    volatile long startNanoTime;

    volatile long startTime;

    volatile long stopNanoTime;

    public void start() {
        super.start();
        startNanoTime = System.nanoTime();
        startTime = System.currentTimeMillis();
    }

    private StageManagerMXBean bean;

    public StageManagerMXBean getInfo() {
        if (bean == null) {
            bean = AbstractMXBean.newInstance(this);
        }
        return bean;
    }

    protected void terminated() {
        stopNanoTime = System.nanoTime();
    }

    public void registerJMX(String name) throws InstanceAlreadyExistsException,
            MBeanRegistrationException, MalformedObjectNameException,
            NotCompliantMBeanException, NullPointerException {
        registerJMX(ManagementFactory.getPlatformMBeanServer(), ObjectName
                .getInstance(name));
    }

    public void registerJMX(MBeanServer server, ObjectName name)
            throws InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException {
        server.registerMBean(getInfo(), name);
    }

    protected static class AbstractMXBean extends JmxEmitterSupport implements
            StageManagerMXBean {
        protected final static Properties messages = new Properties();

        private final AbstractManagedStageManager sm;

        /**
         * @param mxbeanInterface
         * @throws NotCompliantMBeanException
         */
        private AbstractMXBean(AbstractManagedStageManager pipeline)
                throws NotCompliantMBeanException {
            super(messages, StageManagerMXBean.class);
            this.sm = pipeline;
        }

        public static AbstractMXBean newInstance(
                AbstractManagedStageManager manager) {
            try {
                return new AbstractMXBean(manager);
            } catch (NotCompliantMBeanException e) {
                throw new Error("Internal error", e);
            }
        }

        protected List<? extends AbstractManagedStage> getAllStages() {
            return (List<? extends AbstractManagedStage>) sm.getStages();
        }

        protected AbstractManagedStage getStage(String name) {
            return (AbstractManagedStage) sm.getStage(name);
        }

        /**
         * @see org.coconut.aio.impl.util.JmxEmitterSupport#getNotifInfo()
         */
        protected MBeanNotificationInfo[] getNotifInfo() {
            final String notifName = "java.management.Notification";
            // final String[] notifTypes =
            // {AsyncServerSocket.SocketAccepted.TYPE,
            // AsyncServerSocket.Closed.TYPE };
            // return new MBeanNotificationInfo[] { new
            // MBeanNotificationInfo(notifTypes, notifName,
            // "Coconut Cache Notification") };
            return new MBeanNotificationInfo[] {};
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getAllStageNames()
         */
        public String[] getAllStageNames() {
            List<? extends Stage> l = getAllStages();
            Stage[] s = l.toArray(new Stage[0]);
            String[] ss = new String[s.length];
            for (int i = 0; i < s.length; i++) {
                ss[i] = s[i].getName();
            }
            return ss;
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getStageCount()
         */
        public int getStageCount() {
            return getAllStages().size();
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getStageInfo(java.lang.String)
         */
        public StageStatistics getStageInfo(String stage) {
            for (AbstractManagedStage ps : getAllStages()) {
                if (ps.getName().equals(stage)) {
                    return createInfo(ps);
                }
            }
            return null;
        }

        private StageStatistics createInfo(AbstractManagedStage ps) {
            long uptime = getUptime();
            long processed = ps.getProcessed();
            long failed = ps.getFailed();
            final double avgEvents;
            if (uptime <= 0) {
                avgEvents = processed;
            } else {
                avgEvents = ((double) processed) / uptime * 1000;
            }
            return new StageStatistics(ps.getName(), processed + failed,
                    processed, failed, ps.getTime(), ps.getCpuTime(), ps
                            .getUserTime(), 0, avgEvents);

        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getStageInfo(java.lang.String[])
         */
        public StageStatistics[] getStageInfo(String[] stage) {
            StageStatistics[] ss = new StageStatistics[stage.length];
            for (int i = 0; i < stage.length; i++) {
                ss[i] = getStageInfo(stage[i]);
            }
            return ss;
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getTotalCpuTime()
         */
        public long getTotalCpuTime() {
            Lock lock = sm.mainLock;
            lock.lock();
            try {
                long total = sm.totalCpuTime.get();
                for (ThreadWorker tw : sm.allWorkers) {
                    total += sm.currentCpuTime(tw.getThread());
                }
                return total;
            } finally {
                lock.unlock();
            }

        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getTotalUserTime()
         */
        public long getTotalUserTime() {
            long total = 0;
            for (AbstractManagedStage ps : getAllStages()) {
                total += ps.getUserTime();
            }
            return total;
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getStageCpuTime(java.lang.String)
         */
        public long getStageCpuTime(String stage) {
            AbstractManagedStage ps = getStage(stage);
            if (ps != null) {
                return ps.getCpuTime();
            } else {
                return -1;
            }
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getStageUserTime(java.lang.String)
         */
        public long getStageUserTime(String stage) {
            AbstractManagedStage ps = getStage(stage);
            if (ps != null) {
                return ps.getUserTime();
            } else {
                return -1;
            }
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getIncomingStages(java.lang.String)
         */
        public String[] getIncomingStages(String stage) {
            AbstractManagedStage ps = getStage(stage);
            if (ps != null) {
                AbstractManagedStage[] ms = ps.getIncoming();
                String[] s = new String[ms.length];
                for (int i = 0; i < ms.length; i++) {
                    s[i] = ms[i].getName();
                }
                return s;
            } else {
                return null;
            }
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getIncomingEdges(java.lang.String)
         */
        public StageEdgeInfo[] getIncomingEdges(String stage) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getActiveCount(java.lang.String)
         */
        public int getActiveCount(String stage) {
            AbstractManagedStage ps = getStage(stage);
            if (ps != null) {
                return ps.getActiveThreadCount();
            } else {
                return -1;
            }
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getUptime()
         */
        public long getUptime() {
            long stopTime = sm.stopNanoTime == 0 ? System.nanoTime()
                    : sm.stopNanoTime;
            return TimeUnit.MILLISECONDS.convert(stopTime - sm.startNanoTime,
                    TimeUnit.NANOSECONDS);
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getStartTime()
         */
        public long getStartTime() {
            return sm.startTime;
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getActiveCount()
         */
        public int getActiveCount() {
            return sm.getActiveCount();
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getTotalCount()
         */
        public int getTotalCount() {
            return sm.getPoolSize();
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getLargestTotalCount()
         */
        public int getLargestTotalCount() {
            return sm.getLargestPoolSize();
        }

        /**
         * @see org.coconut.event.seda.management.StageManagerMXBean#getStageInfo()
         */
        public StageStatistics[] getStageInfo() {
            ArrayList<StageStatistics> list = new ArrayList<StageStatistics>();
            for (AbstractManagedStage ps : getAllStages()) {
                list.add(createInfo(ps));
            }
            return list.toArray(new StageStatistics[0]);
        }
    }

    @Override
    protected void workerRemoved(ThreadWorker w) {
        totalCpuTime.addAndGet(currentCpuTime(w.getThread()));
    }

    protected long currentCpuTime() {
        return threadInfo.getCurrentThreadCpuTime();
    }

    protected long currentCpuTime(Thread t) {
        return threadInfo.getThreadCpuTime(t.getId());
    }

    protected long currentUserTime() {
        return threadInfo.getCurrentThreadUserTime();
    }

    protected long currentUserTime(Thread t) {
        return threadInfo.getThreadUserTime(t.getId());
    }

}
