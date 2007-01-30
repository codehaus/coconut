/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ConnectionPendingException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.coconut.aio.AioFuture;
import org.coconut.aio.AsyncSocket;
import org.coconut.aio.AsyncSocketGroup;
import org.coconut.aio.ErroneousHandler;
import org.coconut.aio.ReadHandler;
import org.coconut.aio.impl.util.AioFutureTask;
import org.coconut.aio.management.SocketInfo;
import org.coconut.aio.monitor.SocketMonitor;
import org.coconut.core.EventProcessor;
import org.coconut.core.Offerable;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public abstract class BaseSocket extends AsyncSocket {

    // -- General final socket fields --

    /** A socket-group indicating that the socket is closed */
    private static final BaseSocketGroup CLOSED_GROUP = new BaseSocketGroup(null, -1, null);

    /** An unique id for the server-socket */
    private final long id;

    /** The number of commited bytes for writing */
    private final AtomicLong bytesWritten = new AtomicLong();

    /** The number of commited bytes for writing */
    protected final AtomicLong bytesRead = new AtomicLong();

    /** A Managed AioProvider use for notication of events */
    protected final ManagedAioProvider mProvider;

    /** The number of commited bytes for writing */
    private final AtomicLong commitedWriteBytes = new AtomicLong();

    /** Length of the write queue */
    private final AtomicInteger commitQueueLength = new AtomicInteger();

    /** the write lock used in cases where we try to write the elements fast */
    private final Lock writeLock = new ReentrantLock();

    /** 0 = open, 1 = close called, 2 both done */
    private final AtomicReference<ClosedEvent> closeFuture = new AtomicReference<ClosedEvent>();

    /** The task pending for an outgoing connection */
    private ConnectedEvent connectionCallback;

    // -- General volatile user-mod datagram fields --

    /** A user defined attachement */
    private volatile Object attachment;

    /** The reader that are called when data is available for reading */
    private volatile ReadHandler<AsyncSocket> reader;

    /** A user defined server-socket monitor */
    private volatile SocketMonitor monitor;

    /** The sockets default executor */
    private volatile Executor defaultExecutor;

    /** The sockets default offerable */
    private volatile Offerable< ? super Event> defaultDestination;

    /** A long that indicates the maximum size of all outstanding writes in bytes */
    private volatile long writeByteLimit = Long.MAX_VALUE;

    /** An int that indicates the maximum size of all outstanding write events */
    private volatile int writeQueueLimit = Integer.MAX_VALUE;

    /** A user defined close handler */
    private volatile EventProcessor<AsyncSocket> closeHandler;

    /** An int indicating the connectstate if a connect has finished */
    private volatile ConnectState connectState;

    // -- Fields used by the socket group --

    /** A lock used to guard the update of the group this socket is a member of */
    private final Lock groupLock = new ReentrantLock();

    /** The group this socket is a member of */
    private volatile BaseSocketGroup group;

    /**
     * @param id
     */
    public BaseSocket(final long id, final SocketMonitor monitor,
        final ManagedAioProvider provider, final Offerable< ? super Event> destination,
        final Executor executor) {
        this.id = id;
        this.monitor = monitor;
        this.mProvider = provider;
        this.defaultDestination = destination;
        this.defaultExecutor = executor;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getId()
     */
    public long getId() {
        return id;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#isOpen()
     */
    public boolean isOpen() {
        return closeFuture.get() == null;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getColor()
     */
    public int getColor() {
        return (int) (id ^ (id >>> 32));
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getInetAddress()
     */
    public InetAddress getInetAddress() {
        return socket().getInetAddress();
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getLocalSocketAddress()
     */
    public SocketAddress getLocalSocketAddress() {
        return socket().getLocalSocketAddress();
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getPort()
     */
    public int getPort() {
        return socket().getPort();
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getLocalAddress()
     */
    public InetAddress getLocalAddress() {
        return socket().getLocalAddress();
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getRemoteSocketAddress()
     */
    public SocketAddress getRemoteSocketAddress() {
        return socket().getRemoteSocketAddress();
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getLocalPort()
     */
    public int getLocalPort() {
        return socket().getLocalPort();
    }

    /**
     * @see org.coconut.aio.AsyncSocket#isBound()
     */
    public boolean isBound() {
        return socket().isBound();
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getReader()
     */
    public ReadHandler<AsyncSocket> getReader() {
        return reader;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return socket().toString();
    }
    /**
     * @see org.coconut.aio.AsyncSocket#setMonitor(org.coconut.aio.monitor.SocketMonitor)
     */
    public AsyncSocket setMonitor(SocketMonitor monitor) {
        this.monitor = monitor;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getMonitor()
     */
    public SocketMonitor getMonitor() {
        return monitor;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#attach(java.lang.Object)
     */
    public Object attach(Object attachment) {
        Object o = this.attachment;
        this.attachment = attachment;
        return o;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getAttachment()
     */
    public Object attachment() {
        return attachment;
    }

    /**
     * Returns info from this server-socket
     */
    public long getNumberOfBytesRead() {
        return bytesRead.get();
    }

    /**
     * Returns info from this server-socket
     */
    public long getNumberOfBytesWritten() {
        return bytesWritten.get();
    }

    /**
     * @see org.coconut.aio.AsyncSocket#setBufferLimit()
     */
    public AsyncSocket setBufferLimit(long limit) {
        if (limit < 0)
            throw new IllegalArgumentException("limit must be 0 or greater");
        writeByteLimit = limit;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getBufferLimit()
     */
    public long getBufferLimit() {
        return writeByteLimit;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#setWriteQueueLimit()
     */
    public AsyncSocket setWriteQueueLimit(int limit) {
        if (limit < 0)
            throw new IllegalArgumentException("limit must be 0 or greater");
        writeQueueLimit = limit;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getWriteQueueLimit()
     */
    public int getWriteQueueLimit() {
        return writeQueueLimit;
    }

    protected void checkBufferLimit(long bytes) throws RejectedExecutionException {

        for (;;) // check number of writes
        {
            final int currentSize = commitQueueLength.get();
            if (currentSize != Integer.MAX_VALUE && currentSize >= writeQueueLimit)
                throw new RejectedExecutionException();
            if (commitQueueLength.compareAndSet(currentSize, currentSize + 1))
                break;
        }
        for (;;)// check number of bytes queued
        {
            final long currentSize = commitedWriteBytes.get();
            if (currentSize != Long.MAX_VALUE && currentSize + bytes > writeByteLimit) {
                commitQueueLength.decrementAndGet(); // rollback
                throw new RejectedExecutionException();
            }
            if (commitedWriteBytes.compareAndSet(currentSize, currentSize + bytes))
                break;
        }
    }

    /**
     * Returns info from this server-socket
     */
    SocketInfo getSocketInfo() {
        final BaseSocketGroup group = getGroup();
        final Socket socket = socket();
        return new SocketInfo(getId(), 0, 0, group == null ? 0 : group.getId(), socket.isBound(),
            socket.isConnected(), socket.getInetAddress(), socket.getLocalSocketAddress(), socket
                .getPort(), socket.getLocalPort(), socket.getRemoteSocketAddress(), socket
                .getLocalAddress(), getNumberOfBytesRead(), getNumberOfBytesWritten());
    }
    /**
     * @see org.coconut.aio.AsyncSocket#getDefaultSink()
     */
    public Offerable< ? super Event> getDefaultDestination() {
        return defaultDestination;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getDefaultExecutor()
     */
    public Executor getDefaultExecutor() {
        return defaultExecutor;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#bind(java.net.SocketAddress)
     */
    public AsyncSocket bind(SocketAddress address) throws IOException {
        final SocketMonitor m = getMonitor();
        try {
            socket().bind(address);
        } catch (RuntimeException e) {
            if (m != null)
                m.bindFailed(this, address, e);
            throw e;
        } catch (IOException e) {
            if (m != null)
                m.bindFailed(this, address, e);
            throw e;
        }
        if (m != null)
            m.bound(this, address);
        return this;
    }

    /**
     * Sets the default Executor. Used for group membership.
     * 
     * @param executor the Executor
     */
    void setDefaultExecutor(Executor executor) {
        this.defaultExecutor = executor;
    }

    /**
     * Sets the default destination. Used for group membership.
     * 
     * @param destination the Destination.
     */
    void setDefaultDestination(Offerable< ? super Event> destination) {
        this.defaultDestination = destination;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#setCloseHandler(org.coconut.core.Handler)
     */
    public AsyncSocket setCloseHandler(EventProcessor<AsyncSocket> handler) {
        this.closeHandler = handler;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getCloseHandler()
     */
    public EventProcessor<AsyncSocket> getCloseHandler() {
        return closeHandler;
    }
    /**
     * @see org.coconut.aio.AsyncSocket#setGroup(org.coconut.aio.AsyncGroup)
     */
    public boolean innerSetGroup(BaseSocketGroup newGroup) {
        try {

            groupLock.lock();
            final BaseSocketGroup currentGroup = group;
            // if socket is closed ignore
            if (currentGroup != CLOSED_GROUP) {
                if (currentGroup != newGroup) {
                    if (currentGroup != null) {
                        currentGroup.innerRemove(this);
                    }
                    if (newGroup != null && newGroup != CLOSED_GROUP) {

                        setDefaultExecutor(newGroup.getDefaultExecutor());
                        setDefaultDestination(newGroup.getDefaultDestination());

                        ReadHandler<AsyncSocket> r = newGroup.getDefaultReader();
                        // TODO: this returns a AsyncCallbackFuture, figure out
                        // what to do
                        if (r != null) {
                            setReader(r);
                        }

                        newGroup.added(this);
                    }
                    this.group = newGroup;
                    return true;
                }
            }
        } finally {
            groupLock.unlock();
        }
        return false; // group did not change
    }

    /**
     * @see org.coconut.aio.AsyncSocket#setGroup(org.coconut.aio.AsyncSocketGroup)
     */
    public AsyncSocket setGroup(AsyncSocketGroup group) {
        if (group != null && !(group instanceof BaseSocketGroup))
            throw new IllegalArgumentException(
                "This group is not created with same provider as this socket");
        innerSetGroup((BaseSocketGroup) group);
        return this;
    }

    public void close() throws IOException {
        closeNow().getIO();
    }
    /**
     * @see org.coconut.aio.AsyncSocket#getGroup()
     */
    public BaseSocketGroup getGroup() {
        return group;
    }
    protected void closeBase() {
        innerSetGroup(CLOSED_GROUP);
    }
    protected void setBaseReader(ReadHandler<AsyncSocket> reader) {
        this.reader = reader;
    }

    public AioFuture< ? , Event> connect(SocketAddress address) {
        final ConnectedEvent c = new ConnectedEvent(this, address);
        writeLock.lock();
        try {
            if (connectState == ConnectState.CONNECTING) {
                throw new ConnectionPendingException();
            }
            if (connectState == ConnectState.CONNECTED) {
                throw new AlreadyConnectedException();
            }
            if (tryQuickConnect(c)) {
                c.set(this); // connected without blocking
            } else {
                connectState = ConnectState.CONNECTING;
                asynchronousConnect(c); // we need to block
                connectionCallback = c;
            }
        } catch (IOException e) {
            c.setException(e);
            outerClose(e);
        } finally {
            writeLock.unlock();
        }
        return c;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#closeNow()
     */
    public AioFuture< ? , Event> closeNow() {
        final ClosedEvent<?> future = new ClosedEvent(this, null);
        if (closeFuture.compareAndSet(null, future)) {
            future.run();
        }
        return closeFuture.get();
    }

    protected abstract boolean tryQuickConnect(ConnectedEvent c) throws IOException;
    protected abstract void asynchronousConnect(final ConnectedEvent c);

    protected abstract void outerClose(Throwable t);
    protected abstract void tryAndWriteSocketEvents();

    protected void monitorFailed(RuntimeException e, String msg) {

    }
    
    /**
     * @see org.coconut.aio.AsyncSocket#writeAsync(java.nio.ByteBuffer)
     */
    public AioFuture<Long, Event> writeAsync(ByteBuffer buffer) {
        return writeAsync(new ByteBuffer[] { buffer }, 0, 1);
    }

    public int write(ByteBuffer src) throws IOException {
        return writeAsync(src).getIO().intValue();
    }
    /**
     * This is closed once the socket has been closed This method ignores any
     * runtime exceptions thrown by the various handlers Should figure out a way
     * to handle them
     */
    protected void dispose(AsyncSocket.Closed event, IOException closeFailure) {
        
        //TODO: check if this is nessary
        ConnectedEvent ce = connectionCallback;
        if (ce != null && !ce.isDone()) {
            ce.setException(new AsynchronousCloseException());
        }
        final SocketMonitor m = getMonitor();
        if (m != null) {
            try {
                m.closed(this, event.getCause());
            } catch (RuntimeException ignore) {
                // Can't really do anything about this ,socket is closed
            }
        }

        final EventProcessor<AsyncSocket> handler = getCloseHandler();
        if (handler != null) {
            try {
                if (event.getCause() != null && handler instanceof ErroneousHandler)
                    ((ErroneousHandler<AsyncSocket>) handler).handleFailed(this, event.getCause());
                else {
                    handler.process(this);
                }
            } catch (RuntimeException ignore) {
                // Can't really do anything about this ,socket is closed
            }
        }

        // notify net-handler
        mProvider.socketClosed(event);
    }

    protected void writeFinished(AsyncSocket.Written written) {
        long bytes = written.getBytesWritten();
        bytesWritten.addAndGet(bytes);
        final BaseSocketGroup grp = getGroup();
        if (grp != null)
            grp.addNumberOfBytesWritten(bytes);
        mProvider.socketWriteFinished(written);
    }

    public static abstract class BaseEvent<V> extends AioFutureTask<V, Event> implements
        AsyncSocket.Event, AioFuture<V, Event> {
        private final BaseSocket socket;

        public BaseEvent(BaseSocket socket) {
            super(socket.getDefaultExecutor(), socket.getDefaultDestination());
            this.socket = socket;
        }
        public BaseSocket async() {
            return socket;
        }
        public int getColor() {
            return socket.getColor();
        }
        /**
         * @see org.coconut.aio.AsyncServerSocket.Event#setDestination(org.coconut.core.Offerable)
         */
        public void setDestination(Offerable< ? super Event> dest) {
            super.setDest(dest);
        }
        protected void deliverFailure(Offerable< ? super Event> dest, final Throwable t) {
            Event error = new ErroneousEvent() {
                public Throwable getCause() {
                    return t;
                }
                public int getColor() {
                    return socket.getColor();
                }
                public String getMessage() {
                    return t.getMessage();
                }
                public Event getEvent() {
                    return BaseEvent.this;
                }
                public AsyncSocket async() {
                    return socket;
                }
            };
            dest.offer(error);
        }
    }

    private class ClosedEvent<V> extends BaseEvent<V> implements AsyncSocket.Closed {
        private final Throwable cause;

        private ClosedEvent(BaseSocket socket, Throwable cause) {
            super(socket);
            this.cause = cause;
        }

        public Throwable getCause() {
            return cause;
        }
        /**
         * @see org.coconut.aio.impl.BaseCallable#call()
         */
        public V call() throws Exception {
            try {
                closeBase();
                closeCommandRun(this);
                dispose(this, null);
            } catch (IOException e) {
                dispose(this, e);
                throw e;
            }
            return null;
        }
    }

    protected void addNumberOfBytesRead(BaseSocketGroup grp, long number) {
        grp.addNumberOfBytesRead(number);
    }

    
    protected void connectClose(Throwable e) {
        final ClosedEvent future = new ClosedEvent(this, e);
        if (closeFuture.compareAndSet(null, future)) {
            future.run();
        }
    }
    protected abstract void closeCommandRun(AsyncSocket.Closed event) throws IOException;

    protected final static class ConnectedEvent extends BaseEvent<AsyncSocket> implements
        AsyncSocket.Connected {
        private final SocketAddress address;

        private ConnectedEvent(BaseSocket socket, SocketAddress address) {
            super(socket);
            this.address = address;
        }

        public SocketAddress getSocketAddress() {
            return address;
        }

        public void setException(Throwable t) {
            async().connectState = ConnectState.NOT_CONNECTED;
            super.setException(t);
            SocketMonitor m = async().getMonitor();
            if (m != null) {
                try {
                    m.connectFailed(async(), address, t);
                } catch (RuntimeException e) {
                    super.socket.monitorFailed(e, "connectFailed");
                    super.socket.connectClose(e);
                }
            }
        }
        public void set(AsyncSocket result) {
            async().connectState = ConnectState.CONNECTED;
            super.socket.mProvider.socketConnectedTo(this);
            super.set(result); // must be called after the call to mProvider
            // otherwise the future might be released early that is before
            // the connect count is increased. This will fail tests that rely
            // on getIO() to rendezvous before checking the count.
            SocketMonitor m = async().getMonitor();
            if (m != null) {
                try {
                    m.connected(async(), address);
                } catch (RuntimeException e) {
                    super.socket.monitorFailed(e, "connected");
                    super.socket.connectClose(e);
                }
            }
            async().tryAndWriteSocketEvents();
        }
    }

    enum ConnectState {
        NOT_CONNECTED, CONNECTING, CONNECTED;
    }
}
