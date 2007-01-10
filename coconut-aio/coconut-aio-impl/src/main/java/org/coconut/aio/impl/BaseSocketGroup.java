/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.impl;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.aio.AsyncSocket;
import org.coconut.aio.AsyncSocketGroup;
import org.coconut.aio.ReadHandler;
import org.coconut.aio.management.SocketGroupInfo;
import org.coconut.aio.monitor.SocketGroupMonitor;
import org.coconut.core.EventHandler;
import org.coconut.core.Offerable;


/**
 * The default implementation of a <tt>AsyncSocketGroup</tt>.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: DefaultAsyncSocketGroup.java,v 1.4 2004/05/13 10:25:51 kasper
 *          Exp $
 */
public class BaseSocketGroup extends AsyncSocketGroup {

    /** The id of this group. */
    private final long id;

    /** The AIO provider for this group. */
    private final ManagedAioProvider mProvider;

    /** The map of sockets. */
    private final ConcurrentHashMap<BaseSocket, BaseSocket> sockets = new ConcurrentHashMap<BaseSocket, BaseSocket>();

    /** The number of bytes that has been written to this group. */
    private final AtomicLong bytesWritten = new AtomicLong();

    /** The number of bytes that has been read from this group. */
    private final AtomicLong bytesRead = new AtomicLong();

    /** The monitor defined for this group. */
    private volatile SocketGroupMonitor monitor;

    /** The default Executor that joining sockets will get. */
    private volatile Executor e;

    /** The default Offerable that joining sockets will get. */
    private volatile Offerable< ? super AsyncSocket.Event> offerable;

    /** The default ReadHandler that joining sockets will get. */
    private volatile ReadHandler<AsyncSocket> reader;

    /** An EventHandler called every time a socket joins this group. */
    private volatile EventHandler<AsyncSocket> joinHandler;

    /** An EventHandler called every time a socket leaves this group. */
    private volatile EventHandler<AsyncSocket> leaveHandler;

    /**
     * Constructs a new BaseSocketGroup.
     * 
     * @param provider The ManagedAioProvider
     * @param id the id of the group
     * @param monitor the SocketGroup Monitor for this group
     */
    public BaseSocketGroup(ManagedAioProvider provider, long id, SocketGroupMonitor monitor) {
        this.monitor = monitor;
        this.id = id;
        this.mProvider = provider;
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#setDefaultExecutor(java.util.concurrent.Executor)
     */
    public AsyncSocketGroup setDefaultExecutor(Executor executor) {
        this.e = executor;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#setDefaultDestination(org.coconut.concurrent.Offerable)
     */
    public AsyncSocketGroup setDefaultDestination(Offerable< ? super AsyncSocket.Event> offerable) {
        this.offerable = offerable;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#setDefaultReader(org.coconut.aio.AsyncSocketReader)
     */
    public AsyncSocketGroup setDefaultReader(ReadHandler<AsyncSocket> callback) {
        this.reader = callback;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#setAddHandler(coconut.event.Handler)
     */
    public AsyncSocketGroup setJoinHandler(EventHandler<AsyncSocket> handler) {
        this.joinHandler = handler;
        return this;
    }

    /**
     * @see java.util.Set#add(Object)
     */
    public boolean add(AsyncSocket socket) {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
        if (!(socket instanceof BaseSocket))
            throw new IllegalArgumentException(
                "This socket is not created with same provider as this group");

        BaseSocket s = (BaseSocket) socket;
        return s.innerSetGroup(this);
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#size()
     */
    public int size() {
        return sockets.size();
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#contains(org.coconut.aio.AsyncSocket)
     */
    public boolean contains(Object element) {
        return sockets.containsKey(element);
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#setRemoveHandler(coconut.event.Handler)
     */
    public AsyncSocketGroup setLeaveHandler(EventHandler<AsyncSocket> handler) {
        this.leaveHandler = handler;
        return this;
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        if (o == null) {
            throw new NullPointerException("o is null");
        }
        BaseSocket socket = sockets.get(o);
        if (socket != null) {
            socket.innerSetGroup(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @see java.util.AbstractCollection#iterator()
     */
    public Iterator<AsyncSocket> iterator() {
        Iterator i = sockets.values().iterator();
        return i; // Compiler problems
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#getId()
     */
    public long getId() {
        return id;
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#getDefaultExecutor()
     */
    public Executor getDefaultExecutor() {
        return e;
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#getDefaultDestination()
     */
    public Offerable< ? super AsyncSocket.Event> getDefaultDestination() {
        return offerable;
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#getDefaultReader()
     */
    public ReadHandler<AsyncSocket> getDefaultReader() {
        return reader;
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#getAddHandler()
     */
    public EventHandler<AsyncSocket> getJoinHandler() {
        return joinHandler;
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#getRemoveHandler()
     */
    public EventHandler<AsyncSocket> getLeaveHandler() {
        return leaveHandler;
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#getMonitor()
     */
    public SocketGroupMonitor getMonitor() {
        return monitor;
    }

    /**
     * @see org.coconut.aio.AsyncSocketGroup#setMonitor(org.coconut.aio.monitor.SocketGroupMonitor)
     */
    public AsyncSocketGroup setMonitor(SocketGroupMonitor monitor) {
        this.monitor = monitor;
        return this;
    }

    /**
     * We don't expect to have so many groups opened at one time so that it
     * becomes a performance bottleneck.
     */
    protected void finalize() {
        mProvider.closed(this);
    }

    // -- Package private methods --

    /**
     * Removes the specified socket from this group if it is present.
     * @param socket the socket to removed
     * @return <tt>true</>> if the group contained the specified element.
     */
    boolean innerRemove(BaseSocket socket) {
        boolean removed = sockets.remove(socket) != null;
        if (removed) {
            final EventHandler<AsyncSocket> handler = leaveHandler;
            if (handler != null) {
                try {
                    handler.handle(socket);
                } catch (Exception e) {
                    // TODO handle exception
                }
            }

            final SocketGroupMonitor m = monitor;
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

    /**
     * Adds the socket to this group.
     * @param s the socket to add.
     */
    void added(BaseSocket s) {
        sockets.put(s, s);
        mProvider.joined(this, s);
        final EventHandler<AsyncSocket> handler = joinHandler;
        if (handler != null) {
            try {
                handler.handle(s);
            } catch (Exception e) {
                // TODO handle exception
            }
        }
        final SocketGroupMonitor m = monitor;
        if (m != null) {
            try {
                m.join(this, s);
            } catch (Exception e) {
                // TODO handle exception
            }
        }
    }

    /**
     * Adds the number of bytes written to the total number of bytes written.
     * 
     * @param number the number of bytes
     */
    void addNumberOfBytesWritten(long number) {
        bytesWritten.addAndGet(number);
    }

    /**
     * Adds the number of bytes read to the total number of bytes read.
     * 
     * @param number the number of bytes
     */
    void addNumberOfBytesRead(long number) {
        bytesRead.addAndGet(number);
    }

    /**
     * Returns info from this group.
     */
    SocketGroupInfo getGroupInfo() {
        return new SocketGroupInfo(id, size(), bytesRead.get(), bytesWritten.get());
    }

    /**
     * Returns the number of bytes read by members of this group.
     * 
     * @return the number of bytes read by members of this group.
     */
    long getNumberOfBytesRead() {
        return bytesRead.get();
    }

    /**
     * Returns the number of bytes written by members of this group.
     * 
     * @return the number of bytes written by members of this group.
     */
    long getNumberOfBytesWritten() {
        return bytesWritten.get();
    }
}