package org.coconut.aio.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.coconut.aio.AcceptPolicy;
import org.coconut.aio.AioFuture;
import org.coconut.aio.AsyncServerSocket;
import org.coconut.aio.AsyncSocket;
import org.coconut.aio.AsyncSocketGroup;
import org.coconut.aio.impl.util.AioFutureTask;
import org.coconut.aio.management.ServerSocketInfo;
import org.coconut.aio.monitor.ServerSocketMonitor;
import org.coconut.core.Callback;
import org.coconut.core.ErroneousHandler;
import org.coconut.core.EventHandler;
import org.coconut.core.Offerable;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public abstract class BaseServerSocket extends AsyncServerSocket {

    /** A policy used for accepting all sockets that are trying to connect. */
    private static final AcceptPolicy ACCEPT_ALL_POLICY = new AcceptPolicy() {
        public int acceptNext(AsyncServerSocket socket) {
            return Integer.MAX_VALUE; // no limit
        }
    };

    // -- General final socket fields --

    /** An unique id for the server-socket */
    private final long id;

    /** A counter that counts the number of accepted Sockets */
    private final AtomicLong acceptanceCount = new AtomicLong();

    /** The AioProvider used for this server-socket */
    private final ManagedAioProvider provider;

    /** An atomic refence to a future used for closing down the socket */
    private final AtomicReference<ClosedEvent> closeFuture = new AtomicReference<ClosedEvent>();

    /** The accept-lock for a server-socket */
    protected final Lock acceptLock = new ReentrantLock();

    // -- General volatile socket fields --

    /** A user defined server-socket monitor */
    private volatile ServerSocketMonitor monitor;

    /** A user defined attachement */
    private volatile Object attachment;

    /** A user defined close handler */
    private volatile EventHandler<AsyncServerSocket> closeHandler;

    /** The sockets default executor */
    private volatile Executor defaultExecutor;

    /** The sockets default offerable */
    private volatile Offerable<? super Event> defaultOfferable;

    /** The default Socket group that accepted sockets will be a member of */
    private volatile BaseSocketGroup defaultAcceptedSocketGroup;

    // -- Accept Fields, must only be used while holding the accept-lock --

    /** The destination for newly accepted sockets */
    private Offerable<? super AsyncServerSocket.Event> acceptanceSink;

    /** The callback for newly accepted sockets */
    private Callback<AsyncSocket> acceptanceCallback;

    /** The executor for newly accepted sockets */
    private Executor acceptanceExecutor;

    /** The accept isAccepting field */
    protected final AtomicBoolean isAccepting = new AtomicBoolean();

    /** The policy for accepting new events */
    protected AcceptPolicy acceptPolicy;

    /**
     * Constructs a new BaseServerSocket.
     */
    public BaseServerSocket(ManagedAioProvider provider, final long id,
            final ServerSocketMonitor monitor, Offerable<? super Event> queue,
            Executor executor) {
        this.id = id;
        this.provider = provider;
        this.monitor = monitor;
        this.defaultExecutor = executor;
        this.defaultOfferable = queue;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getId()
     */
    @Override
    public long getId() {
        return id;
    }

    /**
     * @see coconut.event.ActiveObject#isOpen()
     */
    @Override
    public boolean isOpen() {
        return closeFuture.get() == null;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#isAccepting()
     */
    @Override
    public boolean isAccepting() {
        return isAccepting.get();
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getColor()
     */
    public int getColor() {
        return (int) (id ^ (id >>> 32));
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#attach(java.lang.Object)
     */
    @Override
    public Object attach(Object attachment) {
        Object o = this.attachment;
        this.attachment = attachment;
        return o;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getAttachment()
     */
    @Override
    public Object attachment() {
        return attachment;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#isBound()
     */
    @Override
    public boolean isBound() {
        return socket().isBound();
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getInetAddress()
     */
    @Override
    public InetAddress getInetAddress() {
        return socket().getInetAddress();
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getLocalSocketAddress()
     */
    @Override
    public SocketAddress getLocalSocketAddress() {
        return socket().getLocalSocketAddress();
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#bind(java.net.SocketAddress)
     */
    @Override
    public AsyncServerSocket bind(SocketAddress address) throws IOException {
        final ServerSocketMonitor m = getMonitor();
        try {
            socket().bind(address);
        } catch (RuntimeException e) {
            if (m != null) {
                m.bindFailed(this, address, e);
            }
            throw e;
        } catch (IOException e) {
            if (m != null) {
                m.bindFailed(this, address, e);
            }
            throw e;
        }
        if (m != null) {
            m.bound(this, address);
        }
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#bind(java.net.SocketAddress, int)
     */
    @Override
    public AsyncServerSocket bind(SocketAddress address, int backlog)
            throws IOException {
        final ServerSocketMonitor m = getMonitor();
        try {
            socket().bind(address, backlog);
        } catch (RuntimeException e) {
            if (m != null) {
                m.bindFailed(this, address, e);
            }
            throw e;
        } catch (IOException e) {
            if (m != null) {
                m.bindFailed(this, address, e);
            }
            throw e;
        }
        if (m != null) {
            m.bound(this, address);
        }
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getLocalPort()
     */
    @Override
    public int getLocalPort() {
        return socket().getLocalPort();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return socket().toString();
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#setMonitor(org.coconut.aio.monitor.ServerSocketMonitor)
     */
    @Override
    public AsyncServerSocket setMonitor(ServerSocketMonitor monitor) {
        this.monitor = monitor;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getMonitor()
     */
    @Override
    public ServerSocketMonitor getMonitor() {
        return monitor;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getDefaultSink()
     */
    @Override
    public Offerable<? super Event> getDefaultDestination() {
        return defaultOfferable;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getDefaultExecutor()
     */
    @Override
    public Executor getDefaultExecutor() {
        return defaultExecutor;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#setCloseHandler(org.coconut.core.Handler)
     */
    @Override
    public AsyncServerSocket setCloseHandler(
            EventHandler<AsyncServerSocket> handler) {
        this.closeHandler = handler;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getCloseHandler()
     */
    @Override
    public EventHandler<AsyncServerSocket> getCloseHandler() {
        return closeHandler;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#setDefaultSocketGroup(org.coconut.aio.AsyncSocketGroup)
     */
    @Override
    public AsyncServerSocket setDefaultSocketGroup(AsyncSocketGroup group) {
        if (group != null && !(group instanceof BaseSocketGroup))
            throw new IllegalArgumentException(
                    "This group is not created with same provider as this socket");
        this.defaultAcceptedSocketGroup = ((BaseSocketGroup) group);
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getDefaultSocketGroup()
     */
    @Override
    public BaseSocketGroup getDefaultSocketGroup() {
        return defaultAcceptedSocketGroup;
    }

    protected void accepted(final BaseSocket socket) {
        acceptanceCount.incrementAndGet();
        final ServerSocketMonitor m = getMonitor();
        if (m != null) {
            try {
                m.accepted(this, socket);
            } catch (RuntimeException e) {
                aioThreadClose(e);
                return;
            }
        }
        final Offerable<? super AsyncServerSocket.Event> sink = acceptanceSink;
        if (sink != null) {
            SocketAcceptedEvent event = new SocketAcceptedEvent(this, socket);
            try {
                sink.offer(event);
            } catch (RuntimeException e) {
                aioThreadClose(e);
                return;
            }
        } else {
            final Executor executor = acceptanceExecutor;
            final Callback<AsyncSocket> callback = acceptanceCallback;
            if (executor != null && callback != null) {
                try {
                    executor.execute(new Runnable() {
                        public void run() {
                            try {
                                callback.completed(socket);
                            } catch (RuntimeException e) {
                                userThreadClose(e);
                            }
                        }
                    });
                } catch (RuntimeException e) {
                    aioThreadClose(e);
                    return;
                }
            } else {
                // regular accept()
            }
        }
    }

    // -- Future methods --

    /**
     * @see org.coconut.aio.AsyncServerSocket#startAccepting(java.util.concurrent.Executor,
     *      org.coconut.core.Callback)
     */
    public AioFuture<?, Event> startAccepting(Executor executor,
            Callback<AsyncSocket> callback) {
        return startAccepting(executor, callback, ACCEPT_ALL_POLICY);
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#startAccepting(org.coconut.core.Offerable)
     */
    public AioFuture<?, Event> startAccepting(Offerable<? super Event> offerable) {
        return startAccepting(offerable, ACCEPT_ALL_POLICY);
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#startAccepting(java.util.concurrent.Executor,
     *      org.coconut.core.Callback, org.coconut.aio.AcceptPolicy)
     */
    public AioFuture<?, Event> startAccepting(Executor executor,
            Callback<AsyncSocket> callback, AcceptPolicy policy) {
        if (executor == null)
            throw new NullPointerException("executor");
        if (callback == null)
            throw new NullPointerException("callback");
        if (policy == null)
            throw new NullPointerException("policy");
        AcceptanceStartedEvent event = new AcceptanceStartedEvent(this,
                executor, callback, null, policy);
        startAcceptingRequest(event);
        return event;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#startAccepting(org.coconut.core.Offerable,
     *      org.coconut.aio.AcceptPolicy)
     */
    public AioFuture<?, Event> startAccepting(
            Offerable<? super Event> offerable, AcceptPolicy policy) {
        if (offerable == null)
            throw new NullPointerException("offerable");
        if (policy == null)
            throw new NullPointerException("policy");
        AcceptanceStartedEvent event = new AcceptanceStartedEvent(this, null,
                null, offerable, policy);
        startAcceptingRequest(event);
        return event;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#stopAccepting()
     */
    public AioFuture<?, Event> stopAccepting() {
        AcceptanceStoppedEvent<?> event = new AcceptanceStoppedEvent(this);
        stopAcceptingRequest(event);
        return event;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#close()
     */
    public AioFuture<?, Event> close() {
        ClosedEvent event = new ClosedEvent(this, null);
        if (closeFuture.compareAndSet(null, event)) {
            closeRequest(event);
            return event;
        } else {
            return closeFuture.get();
        }
    }

    // -- Package private methods --

    /**
     * Returns info from this server-socket
     */
    ServerSocketInfo getServerSocketInfo() {
        return new ServerSocketInfo(getId(), 0, 0, getNumberOfAccepts(),
                isBound(), getInetAddress(), getLocalPort(),
                getLocalSocketAddress());
    }

    /**
     * Returns info from this server-socket
     */
    long getNumberOfAccepts() {
        return acceptanceCount.get();
    }

    // -- Abstract startAccepting/stopAccepting methods --
    protected abstract void startAcceptingRequest(AioFutureTask task);

    protected abstract void startAcceptingRun(Executor e,
            Callback<AsyncSocket> c, Offerable<? super Event> o,
            AcceptPolicy policy) throws IOException;

    protected abstract void stopAcceptingRequest(AioFutureTask event);

    protected abstract void stopAcceptingRun(AioFutureTask event)
            throws Exception;

    protected abstract void closeRequest(AioFutureTask task);

    protected abstract void closeCommandRun(AsyncServerSocket.Closed task)
            throws IOException;

    protected void userThreadClose(Throwable ioe) {
        ClosedEvent event = new ClosedEvent(this, ioe);
        if (closeFuture.compareAndSet(null, event)) {
            closeRequest(event);
        }
    }

    protected void aioThreadClose(Throwable ioe) {
        ClosedEvent event = new ClosedEvent(this, ioe);
        if (closeFuture.compareAndSet(null, event)) {
            event.run();
        }
    }

    /**
     * This is called once the socket has been closed This method ignores any
     * runtime exceptions thrown by the various handlers Should figure out a way
     * to handle them
     */
    private void dispose(AsyncServerSocket.Closed event, Throwable closeFailure) {
        final ServerSocketMonitor m = getMonitor();
        if (m != null) {
            try {
                m.closed(this, event.getCause());
            } catch (RuntimeException e) {
                provider.unhandledException(this, "closed() called on monitor",
                        e);
            }
        }

        final EventHandler<AsyncServerSocket> handler = getCloseHandler();
        if (handler != null) {
            try {
                if (event.getCause() != null
                        && handler instanceof ErroneousHandler) {
                    ((ErroneousHandler<AsyncServerSocket>) handler)
                            .handleFailed(this, event.getCause());
                } else {
                    handler.handle(this);
                }
            } catch (RuntimeException e) {
                provider.unhandledException(this,
                        "handle() called on close monitor", e);
            }
        }

        // notify net-handler
        provider.serverSocketClosed(event);
    }

    // -- Implementations of AsyncServerSocket events.

    /**
     * The base event class for all server-sockets events
     */
    private static abstract class BaseEvent<V> extends
            AioFutureTask<V, AsyncServerSocket.Event> implements
            AsyncServerSocket.Event {

        /** The AsyncServerSocket that this event refeers to. */
        private final BaseServerSocket socket;

        /**
         * Constructs a new BaseEvent.
         * 
         * @param socket
         *            the AsyncServerSocket that this event refeers to
         */
        protected BaseEvent(BaseServerSocket socket) {
            super(socket.getDefaultExecutor(), socket.getDefaultDestination());
            this.socket = socket;
        }

        /**
         * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
         */
        public BaseServerSocket async() {
            return socket;
        }

        /**
         * @see org.coconut.aio.impl.util.AioFutureTask#getColor()
         */
        public int getColor() {
            return socket.getColor();
        }

        /**
         * @see org.coconut.aio.AsyncServerSocket.Event#setDestination(org.coconut.core.Offerable)
         */
        public void setDestination(Offerable<? super Event> dest) {
            super.setDest(dest);
        }

        protected void deliverFailure(Offerable<? super Event> dest,
                final Throwable t) {
            dest.offer(new ErrorEvent<V>(this, t));
        }
    }

    private static final class ErrorEvent<V> implements ErroneousEvent {
        private final Throwable cause;

        private final BaseEvent<V> event;

        /**
         * @param cause
         * @param event
         */
        public ErrorEvent(final BaseEvent<V> event, final Throwable cause) {
            this.cause = cause;
            this.event = event;
        }

        /**
         * @see org.coconut.aio.AsyncServerSocket.ErroneousEvent#getCause()
         */
        public Throwable getCause() {
            return cause;
        }

        /**
         * @see org.coconut.aio.AsyncServerSocket.ErroneousEvent#getMessage()
         */
        public String getMessage() {
            return cause.getMessage();
        }

        /**
         * @see org.coconut.aio.AsyncServerSocket.ErroneousEvent#getEvent()
         */
        public Event getEvent() {
            return event;
        }

        /**
         * @see org.coconut.aio.AsyncServerSocket.Event#async()
         */
        public AsyncServerSocket async() {
            return event.async();
        }

        /**
         * @see org.coconut.core.Colored#getColor()
         */
        public int getColor() {
            return event.getColor();
        }
    }

    /**
     * An event indicating that a socket was succesfully accepted
     */
    private static final class SocketAcceptedEvent extends BaseEvent implements
            AsyncServerSocket.SocketAccepted {

        /** The AsyncSocket that was accepted. */
        private final AsyncSocket socket;

        public SocketAcceptedEvent(BaseServerSocket serverSocket,
                AsyncSocket socket) {
            super(serverSocket);
            this.socket = socket;
        }

        /**
         * @see org.coconut.aio.AsyncServerSocket.SocketAccepted#getAcceptedSocket()
         */
        public AsyncSocket getAcceptedSocket() {
            return socket;
        }
    }

    /**
     * An acceptance started event
     */
    private static final class AcceptanceStartedEvent extends
            BaseEvent<AsyncServerSocket> implements AcceptingStarted {
        private final AcceptPolicy policy;

        private final Executor aExecutor;

        private final Offerable<? super Event> AOfferable;

        private final Callback<AsyncSocket> aCallback;

        private AcceptanceStartedEvent(BaseServerSocket socket, Executor e,
                Callback<AsyncSocket> c, Offerable<? super Event> o,
                AcceptPolicy policy) {
            super(socket);
            this.aCallback = c;
            this.aExecutor = e;
            this.AOfferable = o;
            this.policy = policy;
        }

        /**
         * @see org.coconut.aio.AsyncServerSocket.AcceptingStarted#getPolicy()
         */
        public AcceptPolicy getPolicy() {
            return policy;
        }

        /**
         * (non-Javadoc)
         * 
         * @see org.coconut.aio.impl.BaseCallable#call()
         */
        public AsyncServerSocket call() throws IOException {

            if (async().acceptLock.tryLock()) {
                try {
                    async().startAcceptingRun(aExecutor, aCallback, AOfferable,
                            policy);
                    async().acceptPolicy = policy;
                    async().acceptanceCallback = aCallback;
                    async().acceptanceExecutor = aExecutor;
                    async().acceptanceSink = AOfferable;
                } finally {
                    async().acceptLock.unlock();
                }
            } else {
                IllegalStateException ee = new IllegalStateException(
                        "tried to asynchronously start accepting while already blocking accepting");
                async().aioThreadClose(ee);
                throw ee;
            }
            return null;
        }
    }

    /**
     * An event indicating that acceptance was stopped on this socket.
     */
    private static final class AcceptanceStoppedEvent<V> extends BaseEvent<V>
            implements AcceptingStopped {
        private AcceptanceStoppedEvent(BaseServerSocket socket) {
            super(socket);
        }

        /**
         * @see org.coconut.aio.impl.util.AioFutureTask#call()
         */
        public V call() throws Exception {
            boolean gotLock = super.socket.acceptLock.tryLock();
            if (gotLock)
                try {
                    super.socket.isAccepting.set(false);
                    // exception
                    super.socket.acceptPolicy = null;
                    super.socket.acceptanceCallback = null;
                    super.socket.acceptanceExecutor = null;
                    super.socket.acceptanceSink = null;
                    // just ignore stopAccepting if not accepting
                    super.socket.stopAcceptingRun(this);
                } catch (Exception e) {
                    super.socket.aioThreadClose(e);
                    throw e;
                } finally {
                    super.socket.acceptLock.unlock();
                }
            else {
                IllegalStateException e = new IllegalStateException(
                        "tried to asynchronously stop accepting while already blocking accepting");
                super.socket.aioThreadClose(e);
                throw e;
            }
            return null;
        }
    }

    /**
     * An event indicating that the server-socket was closed.
     */
    private static final class ClosedEvent extends BaseEvent<AsyncServerSocket>
            implements Closed {

        /** The cause of the close or null. */
        private final Throwable cause;

        /**
         * Constructs a new ClosedEvent.
         * 
         * @param socket
         *            the AsyncServerSocket that was closed.
         * @param cause
         *            the cause for closing this socket or <code>null</code>
         *            if this socket was closed normally
         */
        private ClosedEvent(BaseServerSocket socket, Throwable cause) {
            super(socket);
            this.cause = cause;
        }

        /**
         * @see org.coconut.aio.AsyncServerSocket.Closed#getCause()
         */
        public Throwable getCause() {
            return cause;
        }

        /**
         * @see org.coconut.aio.impl.BaseCallable#call()
         */
        public AsyncServerSocket call() {
            try {
                super.socket.closeCommandRun(this);
                super.socket.dispose(this, null);
            } catch (Exception e) {
                super.socket.dispose(this, e);
            } finally {
                super.socket.isAccepting.set(false);
            }
            return super.socket;
        }
    }
}
