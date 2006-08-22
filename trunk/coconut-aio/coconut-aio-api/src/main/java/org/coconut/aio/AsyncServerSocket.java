/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.Executor;

import org.coconut.aio.monitor.ServerSocketMonitor;
import org.coconut.aio.spi.AioProvider;
import org.coconut.core.Callback;
import org.coconut.core.Colored;
import org.coconut.core.EventHandler;
import org.coconut.core.Offerable;


/**
 * An asynchronous server-socket
 * <p>
 * Asynchronous server-sockets are not a complete abstraction of listening
 * network sockets. Manipulation of socket options must be done through an
 * associated {@link java.net.ServerSocket}object obtained by invoking the
 * {@link #socket() socket}method. It is not possible to create a asynchronous
 * server-socket for an arbitrary, pre-existing server socket, nor is it
 * possible to specify the {@link java.net.SocketImpl}object to be used by a
 * server socket associated with an asynchronous server-socket.
 * </p>
 * <p>
 * An asynchronous server-socket is created by invoking one of the
 * {@link #open() open}methods of this class. A newly-created asynchronous
 * server-socket is open but not yet bound. An attempt to invoke the
 * {@link #startAccepting() startAccepting}method of an unbound asynchronous
 * server-socket will cause a {@link NotYetBoundException}to be thrown. An
 * asynchronous server-socket can be bound by invoking one of the
 * {@link #bind(java.net.SocketAddress,int) bind}methods.
 * <p>
 * Asynchronous server-sockets are safe for use by multiple concurrent threads.
 * </p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public abstract class AsyncServerSocket implements Colored {

    /**
     * Retrieves an unique id associated with this server-socket.
     * 
     * @return An unique server-socket id
     */
    public abstract long getId();

    /**
     * Retrieves a server-socket associated with this asynchronous
     * server-socket.
     * <p>
     * The returned object will not declare any public methods that are not
     * declared in the {@link java.net.ServerSocket}class.
     * </p>
     * 
     * @return A server-socket associated with this asynchronous server-socket
     */
    public abstract ServerSocket socket();

    /**
     * Tells whether or not this socket accepts new connections.
     * 
     * @return true if, and only if, this server-socket is accepting new
     *         connections.
     */
    public abstract boolean isAccepting();

    /**
     * Asynchronously starts accepting new connections on this server-socket.
     * <p>
     * NOTICE: If the server-socket was opened inCallbackFuture-Mode the get()
     * method of the CallbackFuture returned from this method will keep
     * returning newly accepted AsyncSockets. Calling get() will block until a
     * new connection has been made to the server-socket or the current thread
     * has been interrupted. If the get() method returns <code>null</code> it
     * means that the server-socket has stopped accepting new AsyncSockets
     * either through the <tt>cancel()</tt> method of the
     * returnedCallbackFuture or through the
     * {@linkorg.codehaus.aio.AsyncServerSocket#stopAccepting() stopAccepting}
     * method. Even if the socket start accepting new request for some reason -
     * for example a new call to startAccepting - the returnedCallbackFuture
     * will keep on returning null.
     * </p>
     * 
     * @return aCallbackFuture representing pending completion of the task, and
     *         whose <tt>get()</tt> method will return newly accepted
     *         connections.
     */
    public abstract AsyncSocket accept() throws IOException;

    /**
     * Starts accepting new connections on this socket
     * <p>
     * Calling this method two times in a row each with a different policy will
     * return two CallbackFutures that both returns AsyncSockets from the same
     * pool of accepted sockets. That is a socket has just been accepted
     * internally and <tt>get()</tt> is called on bothCallbackFutures, only
     * one of them (choosen non-deterministically) will return the socket. The
     * policy used for accepting new sockets will be the one provided by the
     * last call to <tt>startAccepting</tt>. assuming that there is a
     * <tt>happens-before</tt> relationship between them.
     * </p>
     * 
     * @param policy The policy used for accepting new connections.
     * @return aCallbackFuture representing pending completion of the task, and
     *         whose <tt>get()</tt> method will return newly accepted
     *         connections.
     */
    public abstract AioFuture<?, Event> startAccepting(Executor executor, Callback<AsyncSocket> callback);
    public abstract AioFuture<?, Event> startAccepting(Executor executor, Callback<AsyncSocket> callback,
        AcceptPolicy policy);

    public abstract AioFuture<?, Event> startAccepting(Offerable< ? super Event> destination);
    public abstract AioFuture<?, Event> startAccepting(Offerable< ? super Event> destination,
        AcceptPolicy policy);
    /**
     * Asynchronously stops accepting new connections on this socket.
     * 
     * @return aCallbackFuture representing pending completion of the task, and
     *         whose <tt>get()</tt> will return <code>null</code> when the
     *         acceptance has been stopped.
     */
    public abstract AioFuture<?, Event> stopAccepting();

    // public abstract AsyncSocket accept() throws IOException;
    // public abstract AsyncSocket accept(long timeout, TimeUnit unit) throws
    // IOException;

    public abstract AioFuture<?, Event> close();

    public static void setDefaultMonitor(ServerSocketMonitor monitor) {
        AioProvider.provider().setDefaultMonitor(monitor);
    }
    public static ServerSocketMonitor getDefaultMonitor() {
        return AioProvider.provider().getDefaultServerSocketMonitor();
    }

    /**
     * Sets the server-sockets monitor
     * </p>
     * 
     * @param monitor The server-socket monitor; may be <tt>null</tt>
     */
    public abstract AsyncServerSocket setMonitor(ServerSocketMonitor monitor);

    /**
     * Retrieves the current server-socket monitor.
     * </p>
     * 
     * @return The current server-socket monitor, or <tt>null</tt> if there is
     *         no monitor
     */
    public abstract ServerSocketMonitor getMonitor();

    /**
     * Attaches the given object to this key.
     * <p>
     * An attached object may later be retrieved via the {@link #attachment
     * attachment} method. Only one object may be attached at a time; invoking
     * this method causes any previous attachment to be discarded. The current
     * attachment may be discarded by attaching <tt>null</tt>.
     * </p>
     * 
     * @param ob The object to be attached; may be <tt>null</tt>
     * @return The previously-attached object, if any, otherwise <tt>null</tt>
     */
    public abstract Object attach(Object ob);

    /**
     * Retrieves the current attachment.
     * </p>
     * 
     * @return The object currently attached to this key, or <tt>null</tt> if
     *         there is no attachment
     */
    public abstract Object attachment();

    /**
     * Returns the default destination for server-socket events.
     * 
     * @return The default Executor
     */
    public abstract Offerable< ? super Event> getDefaultDestination();

    /**
     * Returns the default Executor.
     * 
     * @return The default Executor
     */
    public abstract Executor getDefaultExecutor();

    /**
     * This is used to set a defaults AsyncSocketGroup that all accepted sockets
     * will automatically join once they are accepted. This group can be used to
     * set default properties for accepted socket
     * 
     * @return This server-socket
     */
    public abstract AsyncServerSocket setDefaultSocketGroup(AsyncSocketGroup group);

    /**
     * Returns the the socket group that newly accepted sockets automatically
     * joins.
     * 
     * @return the default socket group for this server-socket
     */
    public abstract AsyncSocketGroup getDefaultSocketGroup();

    /**
     * Returns whether or not this server-socket is open. A server-socket is
     * open from it is created until is closed by user or due to some exception.
     * 
     * @return whether or not this server-socket is open
     */
    public abstract boolean isOpen();

    /**
     * Sets a handler that will be once the server-socket is closed. If the
     * particular handler is an instance of ErroneousHandler and the socket
     * closed due to an exception the handler(exception) method is called. If an
     * exception did not caused the socket to close the handle method is called.
     * 
     * @param handler the closedHandler
     */
    public abstract AsyncServerSocket setCloseHandler(EventHandler<AsyncServerSocket> handler);

    /**
     * Return the server-sockets close handler.
     * 
     * @return the closedHandler
     */
    public abstract EventHandler<AsyncServerSocket> getCloseHandler();

    /**
     * Binds the <code>ServerSocket</code> to a specific address (IP address
     * and port number).
     * <p>
     * If the address is <code>null</code>, then the system will pick up an
     * ephemeral port and a valid local address to bind the socket.
     * <p>
     * 
     * @param endpoint The IP address & port number to bind to.
     * @throws IOException if the bind operation fails, or if the socket is
     *             already bound.
     * @throws SecurityException if a <code>SecurityManager</code> is present
     *             and its <code>checkListen</code> method doesn't allow the
     *             operation.
     * @throws IllegalArgumentException if endpoint is a SocketAddress subclass
     *             not supported by this socket
     */
    public abstract AsyncServerSocket bind(SocketAddress endpoint) throws IOException;

    /**
     * Binds the <code>ServerSocket</code> to a specific address (IP address
     * and port number).
     * <p>
     * If the address is <code>null</code>, then the system will pick up an
     * ephemeral port and a valid local address to bind the socket.
     * <P>
     * The <code>backlog</code> argument must be a positive value greater than
     * 0. If the value passed if equal or less than 0, then the default value
     * will be assumed.
     * 
     * @param endpoint The IP address & port number to bind to.
     * @param backlog The listen backlog length.
     * @throws IOException if the bind operation fails, or if the socket is
     *             already bound.
     * @throws SecurityException if a <code>SecurityManager</code> is present
     *             and its <code>checkListen</code> method doesn't allow the
     *             operation.
     * @throws IllegalArgumentException if endpoint is a SocketAddress subclass
     *             not supported by this socket
     */
    public abstract AsyncServerSocket bind(SocketAddress endpoint, int backlog) throws IOException;

    /**
     * Opens an asynchronous server-socket.
     * <p>
     * The new socket is created by invoking the {@link
     * org.coconut.aio.spi.AioProvider#openAsyncServerSocket openAsyncServerSocket}
     * method of the system-wide default {@linkcoconut.aio.spi.AioProvider}
     * object.
     * <p>
     * The new asynchronous server-socket is initially unbound; it must be bound
     * to a specific address via one of its {@link #bind(SocketAddress) bind}
     * methods before connections can be accepted.
     * </p>
     * 
     * @return a new asynchronous server-socket.
     * @throws IOException If an I/O error occurs
     */
    public static AsyncServerSocket open() throws IOException {
        return AioProvider.provider().openServerSocket();
    }
    /**
     * Opens a asynchronous server-socket.
     * <p>
     * The new socket is created by invoking the {@link
     * org.coconut.aio.spi.AioProvider#openAsyncServerSocket openAsyncServerSocket}
     * method of the system-wide default {@linkcoconut.aio.spi.AioProvider}
     * object.
     * <p>
     * The new asynchronous socket is initially unbound; it must be bound to a
     * specific address via one of its {@link #bind(SocketAddress) bind}methods
     * before connections can be accepted.
     * </p>
     * 
     * @param destination The Offerable where events are enqueued unto
     * @return a new asynchronous server-socket.
     * @throws IOException If an I/O error occurs
     */
    public static AsyncServerSocket open(Offerable< ? super Event> destination) throws IOException {
        if (destination == null) {
            throw new NullPointerException("qeueu is null");
        }
        return AioProvider.provider().openServerSocket(destination);
    }

    /**
     * Opens a asynchronous server-socket in Callback-mode.
     * <p>
     * The new asynchronous socket is initially unbound; it must be bound to a
     * specific address via one of its {@link
     * AsyncServerSocket#bind(SocketAddress) bind} methods before connections
     * can be accepted.
     * </p>
     * 
     * @param executor The default Executor to use when running callbacks.
     * @return a new asynchronous server-socket.
     * @throws IOException If an I/O error occurs
     */
    public static AsyncServerSocket open(Executor executor) throws IOException {
        if (executor == null) {
            throw new NullPointerException("executor is null");
        }
        return AioProvider.provider().openServerSocket(executor);
    }

    public static AsyncServerSocket open(Queue< ? super Event> queue) throws IOException {
        if (queue == null) {
            throw new NullPointerException("qeueu is null");
        }
        return AioProvider.provider().openServerSocket(queue);
    }

    /**
     * Returns the binding state of the AsyncServerSocket.
     * 
     * @return true if the AsyncServerSocket succesfuly bound to an address
     */
    public abstract boolean isBound();

    /**
     * Returns the local address of this server socket.
     * 
     * @return the address to which this socket is bound, or <code>null</code>
     *         if the socket is unbound.
     */
    public abstract InetAddress getInetAddress();

    /**
     * Returns the address of the endpoint this socket is bound to, or
     * <code>null</code> if it is not bound yet.
     * 
     * @return a <code>SocketAddress</code> representing the local endpoint of
     *         this socket, or <code>null</code> if it is not bound yet.
     * @see #getInetAddress()
     * @see #getLocalPort()
     */
    public abstract SocketAddress getLocalSocketAddress();

    /**
     * Returns the port on which this socket is listening.
     * 
     * @return the port number to which this socket is listening or -1 if the
     *         socket is not bound yet.
     */
    public abstract int getLocalPort();

    /**
     * The base event used for all asynchronous server-socket events.
     */
    public interface Event extends Colored {
        /**
         * Returns the asynchronous server-socket that created this event.
         * 
         * @return the asynchronous server-socket that created this event.
         */
        AsyncServerSocket async();

    }

    /**
     * An event indicating that some problem occured while performing an
     * operation. The particular operation in question can be retrieved by
     * calling getEvent();
     */
    public interface ErroneousEvent extends Event {
        public static final String TYPE = "aio.serversocket.ErroneousEvent";

        /**
         * Returns the cause of the error.
         * 
         * @return the cause.
         */
        Throwable getCause();

        /**
         * Returns the message of this event.
         * 
         * @return the message.
         */
        String getMessage();

        /**
         * Returns the event that caused this exception.
         * 
         * @return the event that caused this exception.
         */
        Event getEvent();
    }

    /**
     * An event indicating the acceptance of a socket.
     */
    public interface SocketAccepted extends Event {
        public static final String TYPE = "aio.serversocket.SocketAccepted";

        /**
         * Returns the newly accepted socket
         * 
         * @return the newly accepted socket.
         */
        AsyncSocket getAcceptedSocket();
    }

    /**
     * An event indicating that the server-socket has been closed.
     */
    public interface Closed extends Event {
        public static final String TYPE = "aio.serversocket.Closed";

        /**
         * Returns the cause of the close or <tt>null</tt> if the socket was
         * closed by explicit by the user.
         * 
         * @return the cause.
         */
        Throwable getCause();
    }

    /**
     * An event indicating that acceptance has stopped.
     */
    public interface AcceptingStopped extends Event {
        public static final String TYPE = "aio.serversocket.AcceptingStopped";
    }

    /**
     * An event indicating that acceptance has started.
     */
    public interface AcceptingStarted extends Event {
        public static final String TYPE = "aio.serversocket.AcceptingStarted";

        /**
         * Returns the policy for accepting new sockets.
         * 
         * @return the AcceptPolicy.
         */
        AcceptPolicy getPolicy();
    }
}