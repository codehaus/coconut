/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.impl;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.coconut.aio.AsyncDatagram;
import org.coconut.aio.AsyncDatagramGroup;
import org.coconut.aio.AsyncServerSocket;
import org.coconut.aio.AsyncSocket;
import org.coconut.aio.AsyncSocketGroup;
import org.coconut.aio.management.DatagramGroupInfo;
import org.coconut.aio.management.DatagramGroupMXBean;
import org.coconut.aio.management.DatagramInfo;
import org.coconut.aio.management.DatagramMXBean;
import org.coconut.aio.management.ServerSocketInfo;
import org.coconut.aio.management.ServerSocketMXBean;
import org.coconut.aio.management.SocketGroupInfo;
import org.coconut.aio.management.SocketGroupMXBean;
import org.coconut.aio.management.SocketInfo;
import org.coconut.aio.management.SocketMXBean;
import org.coconut.aio.monitor.DatagramGroupMonitor;
import org.coconut.aio.monitor.SocketGroupMonitor;
import org.coconut.internal.jmx.JmxEmitterSupport;


/**
 * Default Net monitor
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: DefaultNetworkInterfaceMonitor.java,v 1.2 2004/05/09 20:08:19
 *          kasper Exp $
 */
public abstract class ManagedAioProvider extends AbstractAioProvider {

    public static final String SERVERSOCKET_OBJECT_NAME = "coconut.aio.jmx.serverSocket.objectName";
    public static final String SOCKET_OBJECT_NAME = "coconut.aio.jmx.socket.objectName";
    public static final String SOCKET_GROUP_OBJECT_NAME = "coconut.aio.jmx.socketGroup.objectName";
    public static final String DATAGRAM_OBJECT_NAME = "coconut.aio.jmx.datagram.objectName";
    public static final String DATAGRAM_GROUP_OBJECT_NAME = "coconut.aio.jmx.datagramGroup.objectName";
    public static final String FILE_OBJECT_NAME = "coconut.aio.jmx.file.objectName";
    public static final String JMX_DISABLED = "coconut.aio.jmx.disabled";

    private final ConcurrentHashMap<Long, BaseServerSocket> serverSockets = new ConcurrentHashMap<Long, BaseServerSocket>();
    private final ConcurrentHashMap<Long, BaseSocket> sockets = new ConcurrentHashMap<Long, BaseSocket>();
    private final ConcurrentHashMap<Long, BaseDatagram> datagrams = new ConcurrentHashMap<Long, BaseDatagram>();
    private final ConcurrentHashMap<Long, SocketInf> socketGroups = new ConcurrentHashMap<Long, SocketInf>();
    private final ConcurrentHashMap<Long, DatagramInf> datagramGroups = new ConcurrentHashMap<Long, DatagramInf>();

    private final AtomicLong totalSockets = new AtomicLong();
    private final AtomicLong totalDatagrams = new AtomicLong();
    private final AtomicLong totalServerSockets = new AtomicLong();

    private final AtomicLong bytesWrittenOnSockets = new AtomicLong();
    private final AtomicLong bytesReadOnSockets = new AtomicLong();
    private final AtomicLong bytesWrittenOnDatagrams = new AtomicLong();
    private final AtomicLong bytesReadOnDatagrams = new AtomicLong();

    private final AtomicInteger peakSocketCount = new AtomicInteger();
    private final AtomicInteger peakServerSocketCount = new AtomicInteger();
    private final AtomicInteger peakDatagramCount = new AtomicInteger();

    private final AtomicLong totalAcceptedConnections = new AtomicLong();
    private final AtomicLong totalSocketConnections = new AtomicLong();

    private final Executor releaser;

    private final DefaultServerSocketMXBean mServerSocketbean;
    private final SocketMXBean mSocketbean;
    private final SocketGroupMXBean mSocketGroupbean;

    private final DatagramMXBean mDatagrambean;
    private final DatagramGroupMXBean mDatagramGroupbean;

    public ManagedAioProvider() {
        this(new RunnableExecutor());
    }

    public ManagedAioProvider(Executor releaser) {
        this.releaser = releaser;
        try {
            mSocketbean = new DefaultSocketMXBean();
            mSocketGroupbean = new DefaultSocketGroupMXBean();
            mDatagrambean = new DefaultDatagramMXBean();
            mServerSocketbean = new DefaultServerSocketMXBean();
            mDatagramGroupbean = new DefaultDatagramGroupMXBean();
        } catch (NotCompliantMBeanException mb) {
            throw new IllegalStateException(mb);
        }
    }

    public void run() {
        sockets.clear();
        serverSockets.clear();
        datagrams.clear();
    }

    public void opened(BaseSocket socket) {
        Long l = new Long(socket.getId());
        sockets.put(l, socket);
        totalSockets.incrementAndGet();
        int peak = sockets.size();
        if (peak > peakSocketCount.get())
            peakSocketCount.set(peak);
    }

    public void opened(BaseDatagram socket) {
        Long l = new Long(socket.getId());
        datagrams.put(l, socket);
        totalDatagrams.incrementAndGet();
        int peak = datagrams.size();
        if (peak > peakDatagramCount.get())
            peakDatagramCount.set(peak);
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.aio.impl.NetworkInterfaceMonitor#opened(coconut.aio.AsyncSocket)
     */

    public void socketWriteFinished(AsyncSocket.Written written) {
        bytesWrittenOnSockets.addAndGet(written.getBytesWritten());
    }
    public void socketReadFinished(long bytes) {
        bytesReadOnSockets.addAndGet(bytes);
    }
    public void datagramWriteFinished(AsyncDatagram.Written written) {
        bytesWrittenOnDatagrams.addAndGet(written.getBytesWritten());
    }
    public void addBytesReadDatagram(long bytes) {
        bytesReadOnDatagrams.addAndGet(bytes);
    }
    public void incrementAccepts() {
        totalAcceptedConnections.incrementAndGet();
    }
    public void socketConnectedTo(AsyncSocket.Connected event) {
        totalSocketConnections.incrementAndGet();
    }

    public void joined(BaseDatagramGroup group, BaseDatagram socket) {
        DatagramInf grp = datagramGroups.get(new Long(group.getId()));
        grp.totalCount.incrementAndGet();
        int size = group.size();
        if (size > grp.peekCount.get())
            grp.peekCount.set(size);
    }
    public void left(BaseDatagramGroup group, BaseDatagram socket) {
        DatagramInf grp = datagramGroups.get(new Long(group.getId()));
        grp.totalCount.decrementAndGet();
    }
    public void joined(BaseSocketGroup group, BaseSocket socket) {
        SocketInf grp = socketGroups.get(new Long(group.getId()));
        grp.totalCount.incrementAndGet();
        int size = group.size();
        if (size > grp.peekCount.get())
            grp.peekCount.set(size);
    }
    public void left(BaseSocketGroup group, BaseSocket socket) {
        SocketInf grp = socketGroups.get(new Long(group.getId()));
        grp.totalCount.decrementAndGet();
    }

    public void socketClosed(AsyncSocket.Closed closed) {
        Long l = new Long(closed.async().getId());
        sockets.remove(l);
    }
    public void closed(AsyncDatagram socket) {
        Long l = new Long(socket.getId());
        datagrams.remove(l);
    }
    public void closed(AsyncSocketGroup group) {
        Long l = new Long(group.getId());
        socketGroups.remove(l);
    }
    public void closed(AsyncDatagramGroup group) {
        Long l = new Long(group.getId());
        datagramGroups.remove(l);
    }

    /**
     * @return
     */
    public AsyncSocketGroup openSocketGroup() {
        final SocketGroupMonitor m = getDefaultSocketGroupMonitor();

        BaseSocketGroup group = new BaseSocketGroup(this, getNextId(), m);
        Long l = new Long(group.getId());
        socketGroups.put(l, new SocketInf(group));

        if (m != null)
            m.opened(group);
        return group;
    }
    /**
     * @return
     */
    public AsyncDatagramGroup openDatagramGroup() {
        final DatagramGroupMonitor m = getDefaultDatagramGroupMonitor();

        BaseDatagramGroup group = new BaseDatagramGroup(this, getNextId(), m);
        Long l = new Long(group.getId());
        datagramGroups.put(l, new DatagramInf(group));

        if (m != null)
            m.opened(group);
        return group;
    }

    // ServerSocket methods --

    protected void serverSocketClosed(AsyncServerSocket.Closed closed) {
        final Long l = new Long(closed.async().getId());
        serverSockets.remove(l);
    }

    protected void serverSocketOpened(BaseServerSocket socket) {
        final Long l = new Long(socket.getId());
        serverSockets.put(l, socket);
        final int peak = serverSockets.size();
        if (peak > peakServerSocketCount.get())
            peakServerSocketCount.set(peak);
        totalServerSockets.incrementAndGet();
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#getSocketMXBean()
     */
    public SocketMXBean getSocketMXBean() {
        return mSocketbean;
    }

    /**
     * @see org.coconut.aio.spi.AioProvider#getDatagramMXBean()
     */
    public DatagramMXBean getDatagramMXBean() {
        return mDatagrambean;
    }

    /**
     * @see org.coconut.aio.spi.AioProvider#getServerSocketMXBean()
     */
    public ServerSocketMXBean getServerSocketMXBean() {
        return mServerSocketbean;
    }

    /**
     * @see org.coconut.aio.spi.AioProvider#getSocketGroupMXBean()
     */
    public SocketGroupMXBean getSocketGroupMXBean() {
        return mSocketGroupbean;
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#getDatagramGroupMXBean()
     */
    public DatagramGroupMXBean getDatagramGroupMXBean() {
        return mDatagramGroupbean;
    }

    private final class DefaultServerSocketMXBean extends JmxEmitterSupport implements
        ServerSocketMXBean {
        /**
         * @param mxbeanInterface
         * @throws NotCompliantMBeanException
         */
        protected DefaultServerSocketMXBean() throws NotCompliantMBeanException {
            super(messages, ServerSocketMXBean.class);
        }
        /**
         * @see org.coconut.aio.impl.util.JmxEmitterSupport#getNotifInfo()
         */
        protected MBeanNotificationInfo[] getNotifInfo() {
            final String notifName = "java.management.Notification";
            final String[] notifTypes = {AsyncServerSocket.SocketAccepted.TYPE,
                AsyncServerSocket.Closed.TYPE };
            return new MBeanNotificationInfo[] { new MBeanNotificationInfo(notifTypes, notifName,
                "Coconut Cache Notification") };
        }
        /**
         * @see org.coconut.aio.management.ServerSocketMXBean#getAllServerSocketIds()
         */
        public long[] getAllServerSocketIds() {
            int i = 0;
            Collection<Long> values = new ArrayList<Long>(serverSockets.keySet());
            long[] result = new long[values.size()];
            for (Long val : values)
                result[i++] = val.longValue();
            return result;
        }

        /**
         * @see org.coconut.aio.management.ServerSocketMXBean#getTotalServerSocketsCount()
         */
        public long getTotalServerSocketsCount() {
            return totalServerSockets.get();
        }

        /**
         * @see org.coconut.aio.management.ServerSocketMXBean#getPeakServerSocketCount()
         */
        public int getPeakServerSocketCount() {
            return peakServerSocketCount.get();
        }

        /**
         * @see org.coconut.aio.management.ServerSocketMXBean#getServerSocketCount()
         */
        public int getServerSocketCount() {
            return serverSockets.size();
        }

        /**
         * @see org.coconut.aio.management.ServerSocketMXBean#getTotalAcceptCount()
         */
        public long getTotalAcceptCount() {
            return totalAcceptedConnections.get();
        }

        /**
         * @see org.coconut.aio.management.ServerSocketMXBean#getTotalAcceptCount(long)
         */
        public long getTotalAcceptCount(long id) {
            BaseServerSocket dss = serverSockets.get(new Long(id));
            return dss == null ? 0 : dss.getNumberOfAccepts();

        }

        /**
         * @see org.coconut.aio.management.ServerSocketMXBean#getServerSocketInfo(long)
         */
        public ServerSocketInfo getServerSocketInfo(long id) {
            BaseServerSocket serverSocket = serverSockets.get(new Long(id));

            if (serverSocket != null) {
                for (;;) {
                    ServerSocketInfo info = serverSocket.getServerSocketInfo();
                    if (info.isBound() == serverSocket.isBound())
                        return info;
                }
            } else
                return null;
        }

        /**
         * @see org.coconut.aio.management.ServerSocketMXBean#getServerSocketInfo(long[])
         */
        public ServerSocketInfo[] getServerSocketInfo(long[] ids) {
            for (;;) {
                ServerSocketInfo[] infos = new ServerSocketInfo[ids.length];
                for (int i = 0; i < infos.length; i++) {
                    ServerSocketInfo info = getServerSocketInfo(ids[i]);
                    if (info == null)
                        break;
                    infos[i] = info;
                }
                return infos;
            }
        }

        /**
         * @see org.coconut.aio.management.ServerSocketMXBean#resetPeakServerSocketCount()
         */
        public void resetPeakServerSocketCount() {
            peakServerSocketCount.set(getServerSocketCount());
        }

    }

    private final class DefaultDatagramGroupMXBean extends JmxEmitterSupport implements
        DatagramGroupMXBean {
        /**
         * @param mxbeanInterface
         * @throws NotCompliantMBeanException
         */
        protected DefaultDatagramGroupMXBean() throws NotCompliantMBeanException {
            super(messages, DatagramGroupMXBean.class);
        }

        /**
         * @see org.coconut.aio.impl.util.JmxEmitterSupport#getNotifInfo()
         */
        protected MBeanNotificationInfo[] getNotifInfo() {
            final String notifName = "java.management.Notification";
            final String[] notifTypes = {AsyncServerSocket.SocketAccepted.TYPE,
                AsyncServerSocket.Closed.TYPE };
            return new MBeanNotificationInfo[] { new MBeanNotificationInfo(notifTypes, notifName,
                "Coconut Cache Notification") };
        }
        /**
         * @see org.coconut.aio.management.SocketMXBean#getAllSocketGroupIds()
         */
        public long[] getAllDatagramGroupIds() {
            int i = 0;
            Collection<Long> values = new ArrayList<Long>(datagramGroups.keySet());
            long[] result = new long[values.size()];
            for (Long val : values)
                result[i++] = val.longValue();
            return result;
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getBytesWritten(long)
         */
        public long getBytesWritten(long id) {
            DatagramInf group = datagramGroups.get(new Long(id));
            return group == null ? 0 : group.group.getNumberOfBytesWritten();
        }
        /**
         * @see org.coconut.aio.management.SocketMXBean#getSocketsInGroup(long)
         */
        public long[] getDatagramsInGroup(long id) {
            DatagramInf group = datagramGroups.get(new Long(id));
            if (group != null) {
                Object o[] = group.group.toArray();
                long[] ids = new long[o.length];
                for (int i = 0; i < ids.length; i++) {
                    ids[i] = ((AsyncDatagram) o[i]).getId();
                }
                return ids;
            } else
                return new long[] {};
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getBytesRead(long)
         */
        public long getBytesRead(long id) {
            DatagramInf group = datagramGroups.get(new Long(id));
            return group == null ? 0 : group.group.getNumberOfBytesRead();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getSocketGroupInfo(long)
         */
        public DatagramGroupInfo getDatagramGroupInfo(long id) {
            DatagramInf group = datagramGroups.get(new Long(id));
            if (group != null)
                return group.group.getDatagramInfo();
            else
                return null;
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getSocketCount(long)
         */
        public int getSize(long id) {
            DatagramInf group = datagramGroups.get(new Long(id));
            if (group != null) {
                return group.group.size();
            } else {
                return 0;
            }
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getPeakSocketCount(long)
         */
        public int getPeakDatagramCount(long id) {
            DatagramInf group = datagramGroups.get(new Long(id));
            if (group != null) {
                return group.peekCount.get();
            } else {
                return 0;
            }
        }
        /**
         * @see org.coconut.aio.management.SocketMXBean#getTotalOpenedSocketCount(long)
         */
        public long getTotalDatagramCount(long id) {
            DatagramInf group = datagramGroups.get(new Long(id));
            if (group != null) {
                return group.totalCount.get();
            } else {
                return 0;
            }
        }
        /**
         * @see org.coconut.aio.management.SocketMXBean#resetPeakSocketCount(long)
         */
        public void resetPeakDatagramCount(long id) {
            DatagramInf group = datagramGroups.get(new Long(id));
            if (group != null) {
                group.peekCount.set(getSize(id));
            }
        }
    }

    private final class DefaultSocketGroupMXBean extends JmxEmitterSupport implements
        SocketGroupMXBean {

        /**
         * @param mxbeanInterface
         * @throws NotCompliantMBeanException
         */
        protected DefaultSocketGroupMXBean() throws NotCompliantMBeanException {
            super(messages, SocketGroupMXBean.class);
        }
        
        /**
         * @see org.coconut.aio.impl.util.JmxEmitterSupport#getNotifInfo()
         */
        protected MBeanNotificationInfo[] getNotifInfo() {
            final String notifName = "java.management.Notification";
            final String[] notifTypes = { AsyncServerSocket.SocketAccepted.TYPE,
                AsyncServerSocket.Closed.TYPE };
            return new MBeanNotificationInfo[] { new MBeanNotificationInfo(notifTypes, notifName,
                "Coconut Cache Notification") };
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getAllSocketGroupIds()
         */
        public long[] getAllSocketGroupIds() {
            int i = 0;
            Collection<Long> values = new ArrayList<Long>(socketGroups.keySet());
            long[] result = new long[values.size()];
            for (Long val : values)
                result[i++] = val.longValue();
            return result;
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getPeakSocketCount(long)
         */
        public int getPeakSocketCount(long id) {
            SocketInf group = socketGroups.get(new Long(id));
            if (group != null) {
                return group.peekCount.get();
            } else {
                return 0;
            }
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getSocketCount(long)
         */
        public int getSize(long id) {
            SocketInf group = socketGroups.get(new Long(id));
            if (group != null) {
                return group.group.size();
            } else {
                return 0;
            }
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getSocketGroupInfo(long)
         */
        public SocketGroupInfo getSocketGroupInfo(long id) {
            SocketInf group = socketGroups.get(new Long(id));
            if (group != null)
                return group.group.getGroupInfo();
            else
                return null;
        }
        /**
         * @see org.coconut.aio.management.SocketMXBean#getSocketsInGroup(long)
         */
        public long[] getSocketsInGroup(long id) {
            SocketInf group = socketGroups.get(new Long(id));
            if (group != null) {
                Object o[] = group.group.toArray();
                long[] ids = new long[o.length];
                for (int i = 0; i < ids.length; i++) {
                    ids[i] = ((AsyncSocket) o[i]).getId();
                }
                return ids;
            } else
                return new long[] {};
        }
        /**
         * @see org.coconut.aio.management.SocketMXBean#getTotalOpenedSocketCount(long)
         */
        public long getTotalSocketCount(long id) {
            SocketInf group = socketGroups.get(new Long(id));
            if (group != null) {
                return group.totalCount.get();
            } else {
                return 0;
            }
        }
        /**
         * @see org.coconut.aio.management.SocketMXBean#resetPeakSocketCount(long)
         */
        public void resetPeakSocketCount(long id) {
            SocketInf group = socketGroups.get(new Long(id));
            if (group != null) {
                group.peekCount.set(getSize(id));
            }
        }
        /**
         * @see org.coconut.aio.management.SocketGroupMXBean#getBytesRead(long)
         */
        public long getBytesRead(long id) {
            SocketInf group = socketGroups.get(new Long(id));
            return group == null ? 0 : group.group.getNumberOfBytesRead();

        }
        /**
         * @see org.coconut.aio.management.SocketGroupMXBean#getBytesWritten(long)
         */
        public long getBytesWritten(long id) {
            SocketInf group = socketGroups.get(new Long(id));
            return group == null ? 0 : group.group.getNumberOfBytesWritten();

        }
    }

    private final class DefaultSocketMXBean extends JmxEmitterSupport implements SocketMXBean {

        /**
         * @param mxbeanInterface
         * @throws NotCompliantMBeanException
         */
        protected DefaultSocketMXBean() throws NotCompliantMBeanException {
            super(messages, SocketMXBean.class);
        }
        /**
         * @see org.coconut.aio.impl.util.JmxEmitterSupport#getNotifInfo()
         */
        protected MBeanNotificationInfo[] getNotifInfo() {
            final String notifName = "java.management.Notification";
            final String[] notifTypes = {AsyncServerSocket.SocketAccepted.TYPE,
                AsyncServerSocket.Closed.TYPE };
            return new MBeanNotificationInfo[] { new MBeanNotificationInfo(notifTypes, notifName,
                "Coconut Cache Notification") };
        }
        /**
         * @see org.coconut.aio.management.SocketMXBean#getAllSocketIds()
         */
        public long[] getAllSocketIds() {
            int i = 0;
            Collection<Long> values = new ArrayList<Long>(sockets.keySet());
            long[] result = new long[values.size()];
            for (Long val : values)
                result[i++] = val.longValue();
            return result;
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getBytesWritten()
         */
        public long getBytesWritten() {
            return bytesWrittenOnSockets.get();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getBytesWritten(long)
         */
        public long getBytesWritten(long id) {
            BaseSocket socket = sockets.get(new Long(id));
            if (socket == null) {
                return 0;
            } else
                return socket.getNumberOfBytesWritten();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getBytesRead()
         */
        public long getBytesRead() {
            return bytesWrittenOnSockets.get();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getBytesRead(long)
         */
        public long getBytesRead(long id) {
            BaseSocket socket = sockets.get(new Long(id));
            if (socket == null) {
                return 0;
            } else
                return socket.getNumberOfBytesRead();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getTotalOpenedSocketCount()
         */
        public long getTotalSocketCount() {
            return totalSockets.get();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getPeakSocketCount()
         */
        public int getPeakSocketCount() {
            return peakSocketCount.get();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getSocketCount()
         */
        public int getSocketCount() {
            return sockets.size();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getTotalSocketConnectCount()
         */
        public long getTotalSocketConnectCount() {
            return totalSocketConnections.get();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getSocketInfo(long)
         */
        public SocketInfo getSocketInfo(long id) {
            BaseSocket socket = sockets.get(new Long(id));

            if (socket != null) {
                for (;;) {
                    SocketInfo info = socket.getSocketInfo();
                    if (info.isBound() == socket.isBound())
                        return info;
                }
            } else
                return null;
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getSocketInfo(long[])
         */
        public SocketInfo[] getSocketInfo(long[] ids) {
            for (;;) {
                SocketInfo[] infos = new SocketInfo[ids.length];
                for (int i = 0; i < infos.length; i++) {
                    SocketInfo info = getSocketInfo(ids[i]);
                    if (info == null)
                        break;
                    infos[i] = info;
                }
                return infos;
            }
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#resetPeakSocketCount()
         */
        public void resetPeakSocketCount() {
            peakSocketCount.set(getSocketCount());
        }
    }
    protected final static Properties messages = new Properties();

    private final class DefaultDatagramMXBean extends JmxEmitterSupport implements DatagramMXBean {


        /**
         * @param mxbeanInterface
         * @throws NotCompliantMBeanException
         */
        protected DefaultDatagramMXBean() throws NotCompliantMBeanException {
            
            super(messages, DatagramMXBean.class);
        }
        /**
         * @see org.coconut.aio.impl.util.JmxEmitterSupport#getNotifInfo()
         */
        protected MBeanNotificationInfo[] getNotifInfo() {
            final String notifName = "java.management.Notification";
            final String[] notifTypes = {AsyncServerSocket.SocketAccepted.TYPE,
                AsyncServerSocket.Closed.TYPE };
            return new MBeanNotificationInfo[] { new MBeanNotificationInfo(notifTypes, notifName,
                "Coconut Cache Notification") };
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getAllSocketIds()
         */
        public long[] getAllDatagramIds() {
            int i = 0;
            Collection<Long> values = new ArrayList<Long>(datagrams.keySet());
            long[] result = new long[values.size()];
            for (Long val : values)
                result[i++] = val.longValue();
            return result;
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getBytesWritten()
         */
        public long getBytesWritten() {
            return bytesWrittenOnDatagrams.get();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getBytesWritten(long)
         */
        public long getBytesWritten(long id) {
            BaseDatagram socket = datagrams.get(new Long(id));
            if (socket == null) {
                return 0;
            } else
                return socket.getNumberOfBytesWritten();
        }
        /**
         * @see org.coconut.aio.management.SocketMXBean#getBytesRead()
         */
        public long getBytesRead() {
            return bytesWrittenOnDatagrams.get();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getBytesRead(long)
         */
        public long getBytesRead(long id) {
            BaseDatagram socket = datagrams.get(new Long(id));
            if (socket == null) {
                return 0;
            } else
                return socket.getNumberOfBytesRead();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getTotalOpenedSocketCount()
         */
        public long getTotalDatagramCount() {
            return totalDatagrams.get();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getPeakSocketCount()
         */
        public int getPeakDatagramCount() {
            return peakDatagramCount.get();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getSocketCount()
         */
        public int getDatagramCount() {
            return datagrams.size();
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getSocketInfo(long)
         */
        public DatagramInfo getDatagramInfo(long id) {
            BaseDatagram socket = datagrams.get(new Long(id));

            if (socket != null) {
                for (;;) {
                    DatagramInfo info = socket.getSocketInfo();
                    if (info.isBound() == socket.isBound())
                        return info;
                }
            } else
                return null;
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#getSocketInfo(long[])
         */
        public DatagramInfo[] getDatagramInfo(long[] ids) {
            for (;;) {
                DatagramInfo[] infos = new DatagramInfo[ids.length];
                for (int i = 0; i < infos.length; i++) {
                    DatagramInfo info = getDatagramInfo(ids[i]);
                    if (info == null)
                        break;
                    infos[i] = info;
                }
                return infos;
            }
        }

        /**
         * @see org.coconut.aio.management.SocketMXBean#resetPeakSocketCount()
         */
        public void resetPeakDatagramCount() {
            peakDatagramCount.set(getDatagramCount());
        }

    }

    private static class DatagramInf {
        private final AtomicInteger peekCount = new AtomicInteger();
        private final AtomicLong totalCount = new AtomicLong();
        private final BaseDatagramGroup group;

        DatagramInf(BaseDatagramGroup group) {
            this.group = group;
        }
    }

    private static class SocketInf {
        private final AtomicInteger peekCount = new AtomicInteger();
        private final AtomicLong totalCount = new AtomicLong();
        private final BaseSocketGroup group;

        SocketInf(BaseSocketGroup group) {
            this.group = group;
        }
    }

    private static class RunnableExecutor implements Executor {
        public void execute(Runnable command) {
            command.run();
        }
    }

    protected abstract void stopManagedNet();
    protected abstract void stopManagedDisk();

    protected abstract void startupManagedNet() throws IOException;
    protected abstract void startupManagedDisk() throws IOException;
    /**
     * @see org.coconut.aio.impl.AbstractAioProvider#startupDisk()
     */
    protected void startupDisk() throws Exception {
        startupManagedDisk();
    }
    /**
     * @see org.coconut.aio.impl.AbstractAioProvider#startupNet()
     */
    protected void startupNet() throws Exception {
        checkJMXStartesd();
        startupManagedNet();
    }
    /**
     * @throws IOException
     * @see org.coconut.aio.impl.AbstractAioProvider#stopDisk()
     */
    protected void stopDisk() {
        stopManagedDisk();
        stopJMX();
    }
    /**
     * @throws IOException
     * @see org.coconut.aio.impl.AbstractAioProvider#stopNet()
     */
    protected void stopNet() {
        stopManagedNet();
        stopJMX();
    }

    private MBeanServer mBeanServer;
    private final CopyOnWriteArrayList<ObjectName> registeredMXBeans = new CopyOnWriteArrayList<ObjectName>();

    private void stopJMX() {
        if (mBeanServer == null) {
            for (ObjectName n : registeredMXBeans) {
                try {
                    mBeanServer.unregisterMBean(n);
                } catch (InstanceNotFoundException e) {
                    // ignore
                } catch (MBeanRegistrationException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void checkJMXStartesd() throws InstanceAlreadyExistsException,
        MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException,
        NullPointerException {
        if (mBeanServer == null) {
            String disabled = properties.getProperty(JMX_DISABLED, "-1");
            if (!Boolean.parseBoolean(disabled)) {
                String agentid = properties.getProperty("coconut.aio.jmx.agentid", "");
                final MBeanServer mbs;
                if (agentid.equals("")) {
                    mbs = ManagementFactory.getPlatformMBeanServer();
                } else {
                    ArrayList list = MBeanServerFactory.findMBeanServer(agentid);
                    if (list.size() == 0) {
                        throw new IllegalStateException(
                            "Could not find MBeanServer with agentid = " + agentid);
                    } else {
                        mbs = (MBeanServer) list.get(0);
                    }
                }
                mBeanServer = mbs;

                // -- Register ServerSocket
                String serverSocketName = properties.getProperty(SERVERSOCKET_OBJECT_NAME,
                    org.coconut.aio.management.ManagementFactory.SERVER_SOCKET_MXBEAN_NAME);
                register(mbs, mServerSocketbean, serverSocketName);

                // -- Register Socket
                String socketName = properties.getProperty(SOCKET_OBJECT_NAME,
                    org.coconut.aio.management.ManagementFactory.SOCKET_MXBEAN_NAME);
                register(mbs, mSocketbean, socketName);

                // -- Register Socket Group
                String socketGroupName = properties.getProperty(SOCKET_GROUP_OBJECT_NAME,
                    org.coconut.aio.management.ManagementFactory.SOCKET_GROUP_MXBEAN_NAME);
                register(mbs, mSocketGroupbean, socketGroupName);
                // -- Register Datagram
                String datagramName = properties.getProperty(DATAGRAM_OBJECT_NAME,
                    org.coconut.aio.management.ManagementFactory.DATAGRAM_MXBEAN_NAME);
                register(mbs, mDatagrambean, datagramName);
                // -- Register Datagram Group
                String datagramGroupName = properties.getProperty(DATAGRAM_GROUP_OBJECT_NAME,
                    org.coconut.aio.management.ManagementFactory.DATAGRAM_GROUP_MXBEAN_NAME);
                register(mbs, mDatagramGroupbean, datagramGroupName);
            }
        }
    }
    private void register(MBeanServer server, Object bean, String name)
        throws MBeanRegistrationException, NotCompliantMBeanException,
        InstanceAlreadyExistsException, MalformedObjectNameException {
        String ignoreInstanceE = properties.getProperty(
            "coconut.aio.jmx.InstanceAlreadyExistsException", "false");
        ObjectName oName = ObjectName.getInstance(name);
        try {
            server.registerMBean(bean, oName);
            registeredMXBeans.add(oName);
        } catch (InstanceAlreadyExistsException e) {
            if (ignoreInstanceE.equalsIgnoreCase("true")) {
                // ignore
            } else {
                throw e;
            }
        }
    }

}