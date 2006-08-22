/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.impl.nio;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.coconut.aio.AsyncDatagram;
import org.coconut.aio.AsyncDatagramGroup;
import org.coconut.aio.AsyncDatagramSource;
import org.coconut.aio.ErroneousHandler;
import org.coconut.aio.ReadHandler;
import org.coconut.aio.impl.BaseDatagram;
import org.coconut.aio.impl.BaseDatagramGroup;
import org.coconut.aio.impl.util.AioFutureTask;
import org.coconut.aio.impl.util.ByteBufferUtil;
import org.coconut.aio.monitor.DatagramMonitor;
import org.coconut.core.EventHandler;
import org.coconut.core.Offerable;


/**
 * Todo implement
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class NioDatagram extends BaseDatagram {

    // -- General final socket fields --

    /** A socket-group indicating that the socket is closed */
    private static final BaseDatagramGroup CLOSED_GROUP = new BaseDatagramGroup(null, -1, null);

    /** An unique id for the datagram */
    private final long id;

    /** A reference to the main handler */
    private final DefaultAioSelector netHandler;

    /** The datagrams channel */
    private final DatagramChannel channel;

    /** The number of commited bytes for writing */
    private final AtomicLong commitedWriteBytes = new AtomicLong();

    /** Length of the write queue */
    private final AtomicInteger commitQueueLength = new AtomicInteger();

    /** 0 = open, 1 = close called, 2 both done */
    private final AtomicReference<ClosedEvent> closeFuture = new AtomicReference<ClosedEvent>();

    // -- General volatile user-mod datagram fields --

    /** The sockets default executor */
    volatile Executor defaultExecutor;

    /** The sockets default offerable */
    volatile Offerable< ? super AsyncDatagram.Event> defaultDestination;

    /** A long that indicates the maximum size of all outstanding writes in bytes */
    private volatile long writeByteLimit = Long.MAX_VALUE;

    /** An int that indicates the maximum size of all outstanding write events */
    private volatile int writeQueueLimit = Integer.MAX_VALUE;

    /** A user defined attachement */
    private volatile Object attachment;

    /** A user defined close handler */
    private volatile EventHandler<AsyncDatagram> closeHandler;

    /** A user defined server-socket monitor */
    private volatile DatagramMonitor monitor;

    // -- Fields used by the datagram group --

    /** A lock used to guard the update of the group this socket is a member of */
    private final Lock groupLock = new ReentrantLock();

    /** The group this datagram is a member of */
    private volatile BaseDatagramGroup group;

    // -- Fields used for Writing --

    /** Indicating the write state of the socket */
    private final AtomicInteger writeState = new AtomicInteger();
    // TODO document the various states

    /** the write lock used in cases where we try to write the elements fast */
    private final Lock writeLock = new ReentrantLock();

    /** The Queue we are enqueuing new write requests on */
    private final Queue<WrittenEvent> writes = new ConcurrentLinkedQueue<WrittenEvent>();

    /** The current event we are trying to write */
    private volatile NioDatagram.WrittenEvent currentWrite;

    /** call this to cancel write subscription */
    private Callable cancelWrite;

    /**
     * Number of times the selection process has choosen this socket but had
     * nothing to write
     */
    private int numberOfEmptyWriteSelects;

    /** Number of times we have tried to write the current event */
    private int writeAttempts;

    // -- Fields used by for Reading --

    /** The wrapper around our channel for reading */
    private final ReaderSource sourceAdapter = new ReaderSource();

    /** call this to cancel read subscription */
    private Callable cancelRead;

    /** A lock that must be held when trying to read */
    private final Lock readLock = new ReentrantLock();

    /** The reader that are called when data is available for reading */
    private volatile ReadHandler<AsyncDatagram> reader;

    private final NioAioProvider provider;

    // -- Constructors --

    /**
     * Constructor for a DefaultDatagram
     * 
     * @param handler the NetHandler for this socket
     * @param id the id of the socket
     * @param channel the socket's channel
     * @param monitor the socket's monitor
     * @param destination the default destination
     * @param executor the default executor
     */
    public NioDatagram(DefaultAioSelector handler, NioAioProvider provider, long id,
        DatagramChannel channel, DatagramMonitor monitor, Offerable< ? super Event> destination,
        Executor executor) {

        this.netHandler = handler;
        this.channel = channel;
        this.id = id;
        this.defaultExecutor = executor;
        this.defaultDestination = destination;
        this.monitor = monitor;
        this.provider = provider;
    }

    // -- Public methods --

    /**
     * @see org.coconut.aio.AsyncSocket#getId()
     */
    public long getId() {
        return id;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getColor()
     */
    public int getColor() {
        return (int) (id ^ (id >>> 32));
    }

    /**
     * @see org.coconut.aio.AsyncSocket#socket()
     */
    public DatagramSocket socket() {
        return channel.socket();
    }

    /**
     * @see org.coconut.aio.AsyncSocket#isConnected()
     */
    public boolean isConnected() {
        return channel.isConnected();
    }

    /**
     * @see org.coconut.aio.AsyncSocket#isOpen()
     */
    public boolean isOpen() {
        return closeFuture.get() == null;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return socket().toString();
    }

    /**
     * @see org.coconut.aio.AsyncSocket#setBufferLimit()
     */
    public AsyncDatagram setBufferLimit(long limit) {
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
    public AsyncDatagram setWriteQueueLimit(int limit) {
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
     * @see org.coconut.aio.AsyncSocket#setMonitor(org.coconut.aio.monitor.SocketMonitor)
     */
    public AsyncDatagram setMonitor(DatagramMonitor monitor) {
        this.monitor = monitor;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getMonitor()
     */
    public DatagramMonitor getMonitor() {
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
     * @see org.coconut.aio.AsyncSocket#setCloseHandler(org.coconut.core.Handler)
     */
    public AsyncDatagram setCloseHandler(EventHandler<AsyncDatagram> handler) {
        this.closeHandler = handler;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getCloseHandler()
     */
    public EventHandler<AsyncDatagram> getCloseHandler() {
        return closeHandler;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getGroup()
     */
    public AsyncDatagramGroup getGroup() {
        return group;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getSource()
     */
    public AsyncDatagramSource getSource() {
        return this.sourceAdapter;
    }
    /**
     * @see org.coconut.aio.AsyncSocket#getReader()
     */
    public ReadHandler<AsyncDatagram> getReader() {
        return reader;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#setGroup(org.coconut.aio.AsyncSocketGroup)
     */
    public AsyncDatagram setGroup(AsyncDatagramGroup group) {
        if (group != null && !(group instanceof BaseDatagramGroup))
            throw new IllegalArgumentException(
                "This group is not created with same provider as this socket");
        innerSetGroup((BaseDatagramGroup) group);
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#bind(java.net.SocketAddress)
     */
    public AsyncDatagram bind(SocketAddress address) throws IOException {
        final DatagramMonitor m = monitor;
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
     * @see org.coconut.aio.AsyncSocket#connect(java.net.SocketAddress)
     */
    public AsyncDatagram connect(SocketAddress address) throws IOException {
        final DatagramMonitor m = monitor;
        try {
            channel.connect(address);
        } catch (RuntimeException e) {
            if (m != null)
                m.connectFailed(this, address, e);
            throw e;
        } catch (IOException e) {
            if (m != null)
                m.connectFailed(this, address, e);
            throw e;
        }
        if (m != null)
            m.connected(this, address);
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#disconnect()
     */
    public AsyncDatagram disconnect() throws IOException {
        final DatagramMonitor m = monitor;
        try {
            channel.disconnect();
        } catch (RuntimeException e) {
            if (m != null)
                m.disconnected(this); // disconnectFailed(this, address, e);
            throw e;
        } catch (IOException e) {
            if (m != null)
                m.disconnected(this); // disconnectFailed(this, address, e);
            throw e;
        }
        if (m != null)
            m.disconnected(this);
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#writeAsync(java.nio.ByteBuffer)
     */
    public Written writeAsync(ByteBuffer buffer) {
        checkBufferLimit(ByteBufferUtil.calcSize(buffer));
        WrittenEvent future = new WrittenEvent(getRemoteSocketAddress(), buffer);
        writes.add(future);
        tryAndWriteSocketEvents();
        return future;
    }
    /**
     * Returns an list of outstanding writes. Not part of the public API.
     * 
     * @return a list of outstanding writes.
     */
    public List<Written> getOutstandingWrites() {
        // we will make modifications later, adding file->datagram
        // direct. so this is rather ugly.
        List<Written> w = new ArrayList<Written>(writes.size());
        List<Written> l = new ArrayList<Written>(w.size());
        Written current = currentWrite;
        // add currentwrite to list
        if (!(w.contains(current)) && current instanceof Written) {
            l.add((Written) current);
        }
        for (Written wr : w) {
            l.add((Written) wr);
        }
        return l;
    }
    /**
     * @see org.coconut.aio.AsyncSocket#writeAsync(java.nio.ByteBuffer[], int, int)
     */
    public Written writeAsync(ByteBuffer[] buffer, int offset, int length) {
        checkBufferLimit(ByteBufferUtil.calcSize(buffer));
        WrittenEvent future = new WrittenEvent(getRemoteSocketAddress(), buffer, offset, length);
        writes.add(future);
        tryAndWriteSocketEvents();
        return future;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#closeNow()
     */
    public Closed close() {
        final ClosedEvent future = new ClosedEvent(null);
        if (closeFuture.compareAndSet(null, future)) {
            future.run();
        }
        return closeFuture.get();
    }

    private void checkBufferLimit(long bytes) throws RejectedExecutionException {

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

    // -- Package private methods --

    /**
     * @see org.coconut.aio.AsyncSocket#setGroup(org.coconut.aio.AsyncGroup)
     */
    public boolean innerSetGroup(BaseDatagramGroup newGroup) {
        try {

            groupLock.lock();
            final BaseDatagramGroup currentGroup = group;
            // if socket is closed ignore
            if (currentGroup != CLOSED_GROUP) {
                if (currentGroup != newGroup) {
                    if (currentGroup != null) {
                        currentGroup.innerRemove(this);
                    }
                    if (newGroup != null && newGroup != CLOSED_GROUP) {

                        defaultExecutor = newGroup.getDefaultExecutor();
                        defaultDestination = newGroup.getDefaultDestination();

                        ReadHandler r = newGroup.getDefaultReader();
                        // TODO: this returns a AsyncCallbackFuture, figure out
                        // what to do
                        if (r != null) {
                            setReader(reader);
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
     * @param e
     */
    private void outerClose(Throwable e) {
        connectClose(e);
    }

    /**
     * @param e
     */
    private void readClose(Throwable e) {
        connectClose(e);
    }
    /**
     * @param e
     */
    private void writeClose(Throwable e) {
        connectClose(e);
    }

    private void connectClose(Throwable e) {
        final ClosedEvent future = new ClosedEvent(e);
        if (closeFuture.compareAndSet(null, future)) {
            future.run();
        }
    }

    /**
     * @see org.coconut.aio.AsyncSocket#setReader(org.coconut.core.Callback)
     */
    public ReaderSet setReader(final ReadHandler<AsyncDatagram> handler) {

        final ReaderSetEvent event = new ReaderSetEvent(handler);
        Runnable r = new Runnable() {
            public void run() {
                EventHandler h = new EventHandler() {
                    public void handle(Object ignore) {
                        readAvailable();
                    }
                };

                readLock.lock();
                try {
                    NioDatagram.this.reader = handler;
                    cancelRead = netHandler.datagramStartReading(NioDatagram.this, channel, h);
                    event.set(null);
                } catch (IOException e) {
                    event.setException(e);
                    readClose(e);
                } finally {
                    readLock.unlock();
                }
            }
        };
        netHandler.datagramRegisterReadCommand(r);
        return event;
    }

    private void readAvailable() {
        boolean gotLock = readLock.tryLock();
        if (gotLock) {
            try {
                if (reader != null) {
                    try {
                        reader.handle(this);
                    } catch (IOException e) {
                        readClose(e);
                        // System.out.println("error");
                    } catch (RuntimeException e) {
                        readClose(e);
                        // System.out.println("error");
                    }
                } else {
                    System.err.println("datagram: readAvailable"); // TODO
                    // fix
                }

            } finally {
                readLock.unlock();
            }
        }
    }

    private void tryAndWriteSocketEvents() {
        if (writeState.compareAndSet(0, 1)) {
            for (;;) {
                currentWrite = writes.poll();
                if (currentWrite == null) {
                    writeState.set(0); // make room for others
                    if (writes.size() == 0 || !writeState.compareAndSet(0, 1))
                        return;
                } else if (currentWrite.tryWrite() < 1) {
                    netHandler.datagramRegisterWriteCommand(currentWrite);
                    return;
                }
            }
        }
    }

    /**
     * This is closed once the socket has been closed This method ignores any
     * runtime exceptions thrown by the various handlers Should figure out a way
     * to handle them
     */
    private void closed(Throwable cause, IOException closeFailure) {
        final DatagramMonitor m = monitor;
        if (m != null) {
            try {
                m.closed(this, cause);
            } catch (RuntimeException ignore) {
                // Can't really do anything about this ,socket is closed
            }
        }

        final EventHandler<AsyncDatagram> handler = closeHandler;
        if (handler != null) {
            try {
                if (cause != null && handler instanceof ErroneousHandler)
                    ((ErroneousHandler<AsyncDatagram>) handler).handleFailed(this, cause);
                else {
                    handler.handle(this);
                }
            } catch (RuntimeException ignore) {
                // Can't really do anything about this ,socket is closed
            }
        }

        // notify net-handler
        provider.closed(this);
    }

    private class ReaderSource implements AsyncDatagramSource {
        public long read(ByteBuffer[] srcs, int offset, int length) {
            readLock.lock();
            try {
                DatagramMonitor m = monitor;
                if (m != null) {
                    try {
                        m.preRead(NioDatagram.this, srcs, offset, length);
                    } catch (RuntimeException e) {
                        readClose(e);
                        // TODO throw exception
                    }
                }

                final long read = channel.read(srcs, offset, length);

                if (m != null) {
                    try {
                        m.postRead(NioDatagram.this, read, srcs, offset, length, null);
                    } catch (RuntimeException e) {
                        readClose(e);
                        // TODO throw exception
                    }
                }

                if (read > 0) {
                    NioDatagram.this.bytesRead.addAndGet(read);
                    final BaseDatagramGroup grp = group;
                    if (group != null)
                        group.addNumberOfBytesRead(read);
                    provider.addBytesReadDatagram(read);
                }
                if (read == -1) {
                    // socket is messed up
                    if (cancelRead != null)
                        cancelRead.call();
                    readClose(new IOException("read returned -1"));
                }
                return read;
            } catch (Exception e) {
                readClose(e);
                return -1;
            } finally {
                readLock.unlock();
            }
        }

        public long read(ByteBuffer[] srcs) {
            return read(srcs, 0, srcs.length);
        }
        public int read(ByteBuffer src) {
            readLock.lock();
            try {
                DatagramMonitor m = monitor;

                if (m != null) {
                    try {
                        m.preRead(NioDatagram.this, new ByteBuffer[] { src }, 0, 1);
                    } catch (RuntimeException e) {
                        readClose(e);
                        // TODO throw exception
                    }
                }

                final int read = channel.read(src);

                if (m != null) {
                    try {
                        m.postRead(NioDatagram.this, read, new ByteBuffer[] { src }, 0, 1, null);
                    } catch (RuntimeException e) {
                        readClose(e);
                        // TODO throw exception
                    }
                }

                if (read > 0) {
                    NioDatagram.this.bytesRead.addAndGet(read);
                    final BaseDatagramGroup grp = group;
                    if (group != null)
                        group.addNumberOfBytesRead(read);
                    provider.addBytesReadDatagram(read);

                }
                if (read == -1) {
                    if (cancelRead != null)
                        cancelRead.call();
                    readClose(new IOException("read returned -1"));
                }
                return read;
            } catch (Exception e) {
                e.printStackTrace();
                readClose(e);
                return -1;
            } finally {
                readLock.unlock();
            }

        }

        /**
         * @see org.coconut.aio.AsyncDatagramSource#receive(java.nio.ByteBuffer)
         */
        public SocketAddress receive(ByteBuffer src) throws IOException {
            readLock.lock();
            try {
                DatagramMonitor m = monitor;

                if (m != null) {
                    try {
                        m.preRead(NioDatagram.this, new ByteBuffer[] { src }, 0, 1);
                    } catch (RuntimeException e) {
                        readClose(e);
                        // TODO throw exception
                    }
                }
                final int preRead = src.remaining();
                final SocketAddress adr = channel.receive(src);
                final int read = src.remaining() - preRead;
                if (m != null) {
                    try {
                        m.postRead(NioDatagram.this, 0, new ByteBuffer[] { src }, 0, 1, null);
                    } catch (RuntimeException e) {
                        readClose(e);
                        // TODO throw exception
                    }
                }

                if (read > 0) {
                    NioDatagram.this.bytesRead.addAndGet(read);
                    provider.addBytesReadDatagram(read);

                }
                return adr;
            } catch (Exception e) {
                readClose(e);
                return null;
            } finally {
                readLock.unlock();
            }

        }

        /**
         * @see java.nio.channels.Channel#isOpen()
         */
        public boolean isOpen() {
            return NioDatagram.this.isOpen();
        }
        /**
         * @see java.nio.channels.Channel#close()
         */
        public void close() throws IOException {
            NioDatagram.this.close();
        }

    }

    private abstract class BaseEvent<V> extends AioFutureTask<V, Event> implements Event {
        private BaseEvent() {
            super(defaultExecutor, defaultDestination);
        }
        public AsyncDatagram async() {
            return NioDatagram.this;
        }
        public int getColor() {
            return NioDatagram.this.getColor();

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
                    return NioDatagram.this.getColor();

                }
                public String getMessage() {
                    return t.getMessage();
                }

                public Event getEvent() {
                    return BaseEvent.this;
                }

                public AsyncDatagram async() {
                    return NioDatagram.this;
                }
            };
            dest.offer(error);
        }
    }

    private class ReaderSetEvent extends BaseEvent implements AsyncDatagram.ReaderSet {
        private final ReadHandler<AsyncDatagram> reader;

        private ReaderSetEvent(ReadHandler<AsyncDatagram> reader) {
            this.reader = reader;
        }
        protected void set(Object v) {
            super.set(v);
        }
        protected void setException(Throwable t) {
            super.setException(t);
        }
        /**
         * @see org.coconut.aio.AsyncSocket.ReaderSet#getReader()
         */
        public ReadHandler<AsyncDatagram> getReader() {
            return reader;
        }

    }

    private class ClosedEvent extends BaseEvent implements AsyncDatagram.Closed {
        private final Throwable cause;

        private ClosedEvent(Throwable cause) {
            this.cause = cause;
        }

        public Throwable getCause() {
            return cause;
        }
        /**
         * @see org.coconut.aio.impl.BaseCallable#call()
         */
        public Object call() throws Exception {
            try {
                innerSetGroup(CLOSED_GROUP);
                channel.close();
                closed(cause, null);
            } catch (IOException e) {
                closed(cause, e);
                throw e;
            }
            return null;
        }

    }

    class WrittenEvent extends BaseEvent<Long> implements AsyncDatagram.Written, EventHandler {
        private final ByteBuffer[] srcs;
        private final int offset;
        private final int length;
        private final SocketAddress address;
        volatile long bytesWritten;

        public int getLength() {
            return length;
        }
        /**
         * @return Returns the offset.
         */
        public int getOffset() {
            return offset;
        }
        /**
         * @return Returns the position.
         */
        public long getBytesWritten() {
            return bytesWritten;
        }
        /**
         * @see org.coconut.aio.AsyncDatagram.Written#getAddress()
         */
        public SocketAddress getAddress() {
            return address;
        }
        /**
         * @return Returns the srcs.
         */
        public ByteBuffer[] getSrcs() {
            return srcs;
        }
        public void handle(Object o) {
            for (;;) {
                if (currentWrite == null)
                    currentWrite = writes.poll();

                if (currentWrite == null) {
                    writeState.compareAndSet(2, 0);
                    // We need to check if somebody registered new events
                    // before returning from this method
                    if (writes.size() == 0 || !writeState.compareAndSet(0, 2)) {
                        deregisterSelector();
                        return;
                    }
                } else {
                    long bytesWritten = currentWrite.tryWrite();
                    if (bytesWritten > 0)
                        currentWrite = null;
                    else if (bytesWritten == 0) {
                        // handle 0 write
                        return;
                    } else
                        return;
                }
            }

        }
        private void deregisterSelector() {
            try {
                if (cancelWrite != null)
                    cancelWrite.call();
            } catch (Exception e) {
                writeClose(e);
            }
        }
        private void registerSelector() {
            try {
                /*
                 * TODO this throws a CancelledKeyException in rare situations
                 * at
                 * java.nio.channels.spi.AbstractSelectableChannel.register(AbstractSelectableChannel.java:175)
                 * at
                 * coconut.aio.defaults.AsyncSelector.registerChannel(AsyncSelector.java:161)
                 * at
                 * coconut.aio.defaults.DefaultNetHandler.socketStartWriting(DefaultNetHandler.java:265)
                 * at
                 * coconut.aio.defaults.DefaultSocket$WrittenEvent.registerSelector(DefaultSocket.java:1056)
                 * at
                 * coconut.aio.defaults.DefaultSocket$WrittenEvent.run(DefaultSocket.java:1078)
                 */
                cancelWrite = netHandler.datagramStartWriting(NioDatagram.this, channel, this);
            } catch (IOException ioe) {
                writeClose(ioe);
            }
        }
        public void run() {

            if (writeState.compareAndSet(1, 2)) {

                for (;;) {
                    if (currentWrite == null)
                        currentWrite = writes.poll();
                    if (currentWrite == null) {
                        writeState.compareAndSet(2, 0);
                        // We need to check if somebody registered new events
                        // before returning from this method

                        if (writes.size() == 0 || !writeState.compareAndSet(0, 2))
                            return;
                    } else {
                        long trywrite = currentWrite.tryWrite();
                        if (trywrite < 1) {
                            registerSelector();
                            return;
                        } else
                            currentWrite = null;
                    }
                }
            }
            // else hmm something is fishy
        }

        long tryWrite() {
            final long bytes;
            DatagramMonitor m = monitor;
            if (m != null)
                monitor.preWrite(NioDatagram.this, getSrcs(), getOffset(), getLength());
            try {
                if (getSrcs().length == 1) {
                    bytes = channel.write(getSrcs()[0]);
                } else {
                    bytes = channel.write(getSrcs(), getOffset(), getLength());
                }

            } catch (Exception e) {
                e.printStackTrace();
                // System.out.println("wrote " +e);
                if (m != null)
                    monitor.postWrite(NioDatagram.this, 0, getSrcs(), getOffset(), getLength(),
                        writeAttempts, e);
                setException(e);
                writeAttempts = 0;

                return 1;
            }
            if (m != null)
                monitor.postWrite(NioDatagram.this, bytes, getSrcs(), getOffset(), getLength(),
                    writeAttempts, null);
            if (bytes > 0) {
                bytesWritten += bytes;
                NioDatagram.this.bytesWritten.addAndGet(bytes);
                final BaseDatagramGroup grp = group;
                if (group != null)
                    group.addNumberOfBytesWritten(bytes);
                provider.datagramWriteFinished(this);
            }
            // System.out.println("wrote " + bytes);
            if (!hasRemaining()) {
                writeAttempts++;
                return -bytes;
            } else {
                writeAttempts = 0;
                set(new Long(bytesWritten));
                return 1;
            }
        }

        boolean hasRemaining() {
            for (int i = 0; i < length; i++) {
                if (srcs[i + offset].hasRemaining())
                    return false;
            }
            return true;
        }
        WrittenEvent(SocketAddress address, ByteBuffer[] srcs, int offset, int length) {
            this.srcs = srcs;
            this.offset = offset;
            this.length = length;
            this.address = address;
        }
        WrittenEvent(SocketAddress address, ByteBuffer src) {
            this(address, new ByteBuffer[] { src }, 0, 1);
        }

    }

    /**
     * @see org.coconut.aio.AsyncDatagram#send(java.nio.ByteBuffer,
     *      java.net.SocketAddress)
     */
    public Written send(ByteBuffer src, SocketAddress target) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.coconut.aio.AsyncDatagram#receive(java.nio.ByteBuffer,
     *      org.coconut.aio.ReadHandler)
     */
    public void receive(ByteBuffer dst, ReadHandler handler) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.coconut.aio.AsyncDatagram#receive(java.nio.ByteBuffer)
     */
    public SocketAddress receive(ByteBuffer dst) {
        return null;
    }
}