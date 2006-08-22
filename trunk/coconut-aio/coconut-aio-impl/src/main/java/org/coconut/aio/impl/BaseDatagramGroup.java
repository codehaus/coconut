/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.impl;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.aio.AsyncDatagram;
import org.coconut.aio.AsyncDatagramGroup;
import org.coconut.aio.ReadHandler;
import org.coconut.aio.management.DatagramGroupInfo;
import org.coconut.aio.monitor.DatagramGroupMonitor;
import org.coconut.core.EventHandler;
import org.coconut.core.Offerable;


/**
 * A implementation of a default socket group
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: DefaultAsyncDatagramGroup.java,v 1.4 2004/05/13 10:25:51 kasper
 *          Exp $
 */
public class BaseDatagramGroup extends AsyncDatagramGroup {

    private final ConcurrentHashMap<AsyncDatagram, AsyncDatagram> sockets = new ConcurrentHashMap<AsyncDatagram, AsyncDatagram>();
    private final AtomicLong bytesWritten = new AtomicLong();
    private final AtomicLong bytesRead = new AtomicLong();
    private volatile DatagramGroupMonitor monitor;
    private final long id;
    private final ManagedAioProvider mProvider;
    private volatile Executor e;
    private volatile Offerable< ? super AsyncDatagram.Event> offerable;
    private volatile ReadHandler<AsyncDatagram> reader;
    private volatile EventHandler<AsyncDatagram> joinHandler;
    private volatile EventHandler<AsyncDatagram> leaveHandler;

    public BaseDatagramGroup(ManagedAioProvider mProvider, long id, DatagramGroupMonitor monitor) {
        this.monitor = monitor;
        this.id = id;
        this.mProvider = mProvider;
    }

    /**
     * @see org.coconut.aio.AsyncDatagramGroup#setDefaultExecutor(java.util.concurrent.Executor)
     */
    public AsyncDatagramGroup setDefaultExecutor(Executor executor) {
        this.e = executor;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncDatagramGroup#setDefaultDestination(org.coconut.concurrent.Offerable)
     */
    public AsyncDatagramGroup setDefaultDestination(
        Offerable< ? super AsyncDatagram.Event> offerable) {
        this.offerable = offerable;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncDatagramGroup#setDefaultReader(org.coconut.aio.AsyncDatagramReader)
     */
    public AsyncDatagramGroup setDefaultReader(ReadHandler<AsyncDatagram> callback) {
        this.reader = callback;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncDatagramGroup#setAddHandler(coconut.event.Handler)
     */
    public AsyncDatagramGroup setJoinHandler(EventHandler<AsyncDatagram> handler) {
        this.joinHandler = handler;
        return this;
    }

    public void added(BaseDatagram s) {
        sockets.put(s, s);
        groupJoined(this, s);
        final EventHandler<AsyncDatagram> handler = joinHandler;
        if (handler != null) {
            try {
                handler.handle(s);
            } catch (Exception e) {
                // TODO handle exception
            }
        }
        final DatagramGroupMonitor m = monitor;
        if (m != null) {
            try {
                m.join(this, s);
            } catch (Exception e) {
                // TODO handle exception
            }
        }
    }

    public boolean add(AsyncDatagram socket) {
        if (!(socket instanceof BaseDatagram))
            throw new IllegalArgumentException(
                "This socket is not created with same provider as this group");
        BaseDatagram s = (BaseDatagram) socket;
        return s.innerSetGroup(this);
    }

    /**
     * @see org.coconut.aio.AsyncDatagramGroup#size()
     */
    public int size() {
        return sockets.size();
    }

    /**
     * @see org.coconut.aio.AsyncDatagramGroup#contains(org.coconut.aio.AsyncDatagram)
     */
    public boolean contains(Object element) {
        return sockets.containsKey(element);
    }

    /**
     * @see org.coconut.aio.AsyncDatagramGroup#setRemoveHandler(coconut.event.Handler)
     */
    public AsyncDatagramGroup setLeaveHandler(EventHandler<AsyncDatagram> handler) {
        this.leaveHandler = handler;
        return this;
    }

    public boolean innerRemove(Object o) {
        boolean removed = sockets.remove(o) != null;
        if (removed) {
            BaseDatagram socket = (BaseDatagram) o;
            final EventHandler<AsyncDatagram> handler = leaveHandler;
            if (handler != null)
                handler.handle(socket);
            final DatagramGroupMonitor m = monitor;
            if (m != null) {
                try {
                    m.leave(this, socket, null);
                } catch (Exception e) {
                    // TODO handle exception
                }
            }
        }
        return removed;
    }

    public boolean remove(Object o) {
        BaseDatagram socket = (BaseDatagram) sockets.get(o);
        if (socket != null) {
            socket.innerSetGroup(null);
            return true;
        } else
            return false;
    }

    /**
     * @see java.util.AbstractCollection#iterator()
     */
    public Iterator<AsyncDatagram> iterator() {
        return sockets.values().iterator();
    }

    /**
     * @see org.coconut.aio.AsyncDatagramGroup#getId()
     */
    public long getId() {
        return id;
    }

    /**
     * @see org.coconut.aio.AsyncDatagramGroup#getDefaultExecutor()
     */
    public Executor getDefaultExecutor() {
        return e;
    }

    /**
     * @see org.coconut.aio.AsyncDatagramGroup#getDefaultDestination()
     */
    public Offerable< ? super AsyncDatagram.Event> getDefaultDestination() {
        return offerable;
    }

    /**
     * @see org.coconut.aio.AsyncDatagramGroup#getDefaultReader()
     */
    public ReadHandler<AsyncDatagram> getDefaultReader() {
        return reader;
    }

    /**
     * @see org.coconut.aio.AsyncDatagramGroup#getAddHandler()
     */
    public EventHandler<AsyncDatagram> getJoinHandler() {
        return joinHandler;
    }

    /**
     * @see org.coconut.aio.AsyncDatagramGroup#getRemoveHandler()
     */
    public EventHandler<AsyncDatagram> getLeaveHandler() {
        return leaveHandler;
    }

    protected void finalize() {
        groupClosed(this);
    }
    public DatagramGroupMonitor getMonitor() {
        return monitor;
    }
    public AsyncDatagramGroup setMonitor(DatagramGroupMonitor monitor) {
        this.monitor = monitor;
        return this;
    }

    // -- Package private methods --

    /**
     * Returns info from this server-socket
     */
    DatagramGroupInfo getDatagramInfo() {
        return new DatagramGroupInfo(id, size(), bytesRead.get(), bytesWritten.get());
    }

    /**
     * Returns info from this server-socket
     */
    public long getNumberOfBytesRead() {
        return bytesRead.get();
    }

    public void addNumberOfBytesRead(long number) {
        bytesRead.addAndGet(number);
    }

    /**
     * Returns info from this server-socket
     */
    public long getNumberOfBytesWritten() {
        return bytesWritten.get();
    }
    public void addNumberOfBytesWritten(long number) {
        bytesWritten.addAndGet(number);
    }

    public void groupJoined(BaseDatagramGroup group, BaseDatagram socket) {
        mProvider.joined(group, socket);
    }
    public void groupClosed(BaseDatagramGroup group) {
        mProvider.closed(group);
    }
}