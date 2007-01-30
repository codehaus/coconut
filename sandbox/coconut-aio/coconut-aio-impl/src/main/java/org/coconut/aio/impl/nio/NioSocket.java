/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.impl.nio;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.coconut.aio.AioFuture;
import org.coconut.aio.AsyncSocket;
import org.coconut.aio.ReadHandler;
import org.coconut.aio.impl.BaseSocket;
import org.coconut.aio.impl.BaseSocketGroup;
import org.coconut.aio.impl.util.AioFutureTask;
import org.coconut.aio.impl.util.ByteBufferUtil;
import org.coconut.aio.monitor.SocketMonitor;
import org.coconut.core.EventProcessor;
import org.coconut.core.Offerable;


/**
 * An actual implementation of an asynchronous server-socket.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
final class NioSocket extends BaseSocket {

    // -- General final socket fields --

    /** A reference to the main handler */
    private final DefaultAioSelector netHandler;

    /** The sockets channel, package private because of file transferFrom/To */
    public final SocketChannel channel;

    // -- Fields used for Writing --

    /** Indicating the write state of the socket */
    private final AtomicInteger writeState = new AtomicInteger();
    /** Value representing that no writes are in progress */
    private static final int WRITE_NOOP = 0;
    /** Value representing that the writing-invoking thread is writing */
    private static final int WRITE_USER_THREAD = 1;
    /** Value representing that we running in the selector thread */
    private static final int WRITE_SELECTOR_THREAD = 2;
    /** Value representing that we are transfering data directly from a file */
    private static final int WRITE_FILE_TRANSFER = 3;

    /** The Queue we are enqueuing new write requests on */
    private final Queue<Writeable> writes = new ConcurrentLinkedQueue<Writeable>();

    /** The current event we are trying to write */
    private volatile Writeable currentWrite;

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

    /** A lock that must be held when trying to read */
    private final Lock readLock = new ReentrantLock();

    /** call this to cancel read subscription */
    private Callable cancelRead;

    // -- Constructors --

    /**
     * Constructor for a NioSocket
     * 
     * @param handler the NetHandler for this socket
     * @param id the id of the socket
     * @param channel the socket's channel
     * @param monitor the socket's monitor
     * @param destination the default destination
     * @param executor the default executor
     */
    NioSocket(DefaultAioSelector handler, NioAioProvider provider, long id, SocketChannel channel,
        SocketMonitor monitor, Offerable< ? super Event> destination, Executor executor) {
        super(id, monitor, provider, destination, executor);
        this.netHandler = handler;
        this.channel = channel;
    }

    // -- Public methods --

    /**
     * @see org.coconut.aio.AsyncSocket#socket()
     */
    public Socket socket() {
        return channel.socket();
    }

    /**
     * @see org.coconut.aio.AsyncSocket#isConnected()
     */
    public boolean isConnected() {
        return channel.isConnected();
    }

    protected boolean tryQuickConnect(final ConnectedEvent c) throws IOException {
        return channel.connect(c.getSocketAddress());
    }
    /**
     * @see org.coconut.aio.AsyncSocket#connect(java.net.SocketAddress)
     */
    protected void asynchronousConnect(final ConnectedEvent c) {
        final Runnable runnable =new Runnable() {
            public void run() {
                try {
                    netHandler.socketStartConnecting(NioSocket.this, channel, new EventProcessor() {
                        public void process(Object key) {
                            try {
                                if (!channel.finishConnect()) {
                                    // this is a bug in java nio.
                                    return;
                                }
                                ((SelectionKey) key).cancel();
                                // finish
                                c.set(NioSocket.this);
                            } catch (RuntimeException e) {
                                c.setException(e);
                                connectClose(e);
                            } catch (IOException e) {
                                c.setException(e);
                                connectClose(e);
                            }

                        }
                    });
                } catch (RuntimeException e) {
                    c.setException(e);
                    connectClose(e);
                } catch (IOException e) {
                    c.setException(e);
                    connectClose(e);
                }
            }
        };
		
		netHandler.socketRegisterConnectCommand(runnable);
    }

    /**
     * @see org.coconut.aio.AsyncSocket#writeAsync(java.nio.ByteBuffer[], int, int)
     */
    public AioFuture<Long, Event> writeAsync(ByteBuffer[] buffer, int offset, int length) {
        checkBufferLimit(ByteBufferUtil.calcSize(buffer));
        WrittenEvent future = new WrittenEvent(this, buffer, offset, length);
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
        List<Writeable> w = new ArrayList<Writeable>(writes.size());
        List<Written> l = new ArrayList<Written>(w.size());
        Writeable current = currentWrite;
        // add currentwrite to list
        if (!(w.contains(current)) && current instanceof Written) {
            l.add((Written) current);
        }
        for (Writeable wr : w) {
            if (wr instanceof Written) {
                l.add((Written) wr);
            }
        }
        return l;
    }

    // -- Package private methods --

    /**
     * @param e
     */
    protected void outerClose(Throwable e) {
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

    /**
     * @see org.coconut.aio.AsyncSocket#setReader(org.coconut.core.Callback)
     */
    public void setReader(final ReadHandler<AsyncSocket> handler) {
        readLock.lock();
        try {
            setBaseReader(handler);
        } finally {
            readLock.unlock();
        }
        Runnable r = new Runnable() {
            public void run() {
                EventProcessor h = new EventProcessor() {
                    public void process(Object ignore) {
                        readAvailable();
                    }
                };
                readLock.lock();
                try {
                    cancelRead = netHandler.socketStartReading(NioSocket.this, channel, h);
                } catch (IOException e) {
                    try {
                        // TODO Make sure all reads fail
                        handler.handle(NioSocket.this);
                    } catch (IOException e1) {
                        // Ignore
                    }
                    readClose(e);
                } finally {
                    readLock.unlock();
                }
            }
        };
        netHandler.socketRegisterReadCommand(r);
    }

    private void readAvailable() {
        boolean gotLock = readLock.tryLock();
        if (gotLock) {
            try {
                ReadHandler<AsyncSocket> r = getReader();
                if (r != null) {
                    try {
                        r.handle(this);
                    } catch (IOException e) {
                        readClose(e);
                        // System.out.println("error");
                    } catch (RuntimeException e) {
                        readClose(e);
                        // System.out.println("error");
                    }
                } else {
                    // System.err.println("ServerSocket: readAvailable"); //TODO
                    // fix
                }

            } finally {
                readLock.unlock();
            }
        }
    }

    public long read(ByteBuffer[] srcs, int offset, int length) {
        readLock.lock();
        try {
            SocketMonitor m = getMonitor();
            if (m != null) {
                try {
                    m.preRead(NioSocket.this, srcs, offset, length);
                } catch (RuntimeException e) {
                    readClose(e);
                    // TODO throw exception
                }
            }

            final long read = channel.read(srcs, offset, length);

            if (m != null) {
                try {
                    m.postRead(NioSocket.this, read, srcs, offset, length, null);
                } catch (RuntimeException e) {
                    readClose(e);
                    // TODO throw exception
                }
            }

            if (read > 0) {
                NioSocket.this.bytesRead.addAndGet(read);
                mProvider.socketReadFinished(read);
                final BaseSocketGroup grp = (BaseSocketGroup) getGroup();
                if (grp != null) {
                    addNumberOfBytesRead(grp, read);
                }

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
            SocketMonitor m = getMonitor();

            if (m != null) {
                try {
                    m.preRead(NioSocket.this, new ByteBuffer[] { src }, 0, 1);
                } catch (RuntimeException e) {
                    readClose(e);
                    // TODO throw exception
                }
            }

            final int read = channel.read(src);

            if (m != null) {
                try {
                    m.postRead(NioSocket.this, read, new ByteBuffer[] { src }, 0, 1, null);
                } catch (RuntimeException e) {
                    readClose(e);
                    // TODO throw exception
                }
            }

            if (read > 0) {
                NioSocket.this.bytesRead.addAndGet(read);
                mProvider.socketReadFinished(read);
                final BaseSocketGroup grp = getGroup();
                if (grp != null)
                    addNumberOfBytesRead(grp, read);
            }
            if (read == -1) {
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

    public static abstract class BaseNioEvent<V> extends AioFutureTask<V, Event> implements
        AsyncSocket.Event, AioFuture<V, Event> {
        private final AsyncSocket socket;

        public BaseNioEvent(AsyncSocket socket) {
            super(socket.getDefaultExecutor(), socket.getDefaultDestination());
            this.socket = socket;
        }
        public AsyncSocket async() {
            return socket;
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
                public String getMessage() {
                    return t.getMessage();
                }
                public Event getEvent() {
                    return BaseNioEvent.this;
                }
                public AsyncSocket async() {
                    return socket;
                }
            };
            dest.offer(error);
        }
    }

    protected void closeCommandRun(AsyncSocket.Closed task) throws IOException {
        channel.close();
    }

    protected void tryAndWriteSocketEvents() {
        if (isConnected() && writeState.compareAndSet(WRITE_NOOP, WRITE_USER_THREAD))
            handleWrite();
    }

    private interface Writeable {
        boolean runAndContinue(int state);
    }

    private void handleWrite() {
        final int state = writeState.get();
        for (;;) {
            if (currentWrite == null)
                currentWrite = writes.poll();

            if (currentWrite == null) {
                writeState.set(WRITE_NOOP);
                // We need to check if somebody registered new events
                // before returning from this method
                if (writes.peek() == null || !writeState.compareAndSet(WRITE_NOOP, state)) {
                    if (state == WRITE_SELECTOR_THREAD) {
                        try {
                            if (cancelWrite != null) {
                                cancelWrite.call();
                                cancelWrite = null;
                            }
                        } catch (Exception e) {
                            writeClose(e);
                        }
                    }
                    return;
                }
            } else {
                if (!currentWrite.runAndContinue(state))
                    return;
            }
        }
    }

    class WrittenEvent extends BaseNioEvent<Long> implements AsyncSocket.Written, EventProcessor,
        Writeable {
        private final ByteBuffer[] srcs;
        private final int offset;
        private final int length;
        private volatile long bytesWritten;

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
         * @return Returns the srcs.
         */
        public ByteBuffer[] getSrcs() {
            return srcs;
        }
        public void process(Object o) {
            handleWrite();
        }
        public void run() {
            writeState.set(WRITE_SELECTOR_THREAD);
            handleWrite();
        }
        /**
         * @see org.coconut.aio.impl.nio.NioSocket.Writeable#runAndContinue(int)
         */
        public boolean runAndContinue(final int state) {
            int result = tryWrite();
            if (result == -1) {
                currentWrite = null;
                return true;
            } else if (result == 0) {
                if (state == WRITE_USER_THREAD) {
                    netHandler.socketRegisterWriteCommand(this);
                } else if (cancelWrite == null) {
                    try {
                        cancelWrite = netHandler.socketStartWriting(NioSocket.this, channel, this);
                    } catch (IOException ioe) {
                        writeClose(ioe);
                    }
                }
            }
            return false;
        }
        private int tryWrite() {
            final long bytes;
            SocketMonitor m = getMonitor();
            if (m != null)
                m.preWrite(NioSocket.this, getSrcs(), getOffset(), getLength());
            try {
                if (getSrcs().length == 1) {
                    bytes = channel.write(getSrcs()[0]);
                } else {
                    bytes = channel.write(getSrcs(), getOffset(), getLength());
                }

            } catch (Exception e) {
                // System.out.println("wrote " +e);
                if (m != null)
                    m.postWrite(NioSocket.this, 0, getSrcs(), getOffset(), getLength(),
                        writeAttempts, e);
                setException(e);
                writeAttempts = 0;
                return 1;
            }
            if (m != null)
                m.postWrite(NioSocket.this, bytes, getSrcs(), getOffset(), getLength(),
                    writeAttempts, null);
            if (bytes > 0) {
                bytesWritten += bytes;
                writeFinished(this);
            }
            // System.out.println("wrote " + bytes);
            if (!hasRemaining()) {
                writeAttempts++;
                return 0;
            } else {
                writeAttempts = 0;
                set(Long.valueOf(bytesWritten));
                return -1;
            }
        }

        boolean hasRemaining() {
            for (int i = 0; i < length; i++) {
                if (srcs[i + offset].hasRemaining())
                    return false;
            }
            return true;
        }
        WrittenEvent(AsyncSocket socket, ByteBuffer[] srcs, int offset, int length) {
            super(socket);
            this.srcs = srcs;
            this.offset = offset;
            this.length = length;

        }
        WrittenEvent(AsyncSocket socket, ByteBuffer src) {
            this(socket, new ByteBuffer[] { src }, 0, 1);
        }
    }

    TransferFromFileEvent createTransferFrom(CountDownLatch latch) {
        TransferFromFileEvent rffe = new TransferFromFileEvent(latch);
        writes.add(rffe);
        tryAndWriteSocketEvents();
        return rffe;
    }

    private class TransferFromFileEvent implements Writeable, Runnable {
        private final CountDownLatch latch;

        private TransferFromFileEvent(CountDownLatch latch) {
            this.latch = latch;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            writeState.set(WRITE_USER_THREAD); // FIXME no write in file thread
            currentWrite = null;
            handleWrite();
        }
        /**
         * @see org.coconut.aio.impl.nio.NioSocket.Writeable#runAndContinue(int)
         */
        public boolean runAndContinue(int state) {
            writeState.set(WRITE_FILE_TRANSFER);
            latch.countDown();
            return false;
        }
    }
}