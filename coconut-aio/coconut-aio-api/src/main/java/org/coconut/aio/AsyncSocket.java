/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Queue;
import java.util.concurrent.Executor;

import org.coconut.aio.monitor.SocketMonitor;
import org.coconut.aio.spi.AioProvider;
import org.coconut.core.EventProcessor;
import org.coconut.core.Offerable;


/**
 * An asynchronous socket.
 * <p>
 * Asynchronous sockets are not a complete abstraction of connecting network
 * sockets. Manipulation of socket options must be done through an associated
 * {@link java.net.Socket}object obtained by invoking the
 * {@link #socket() socket}method. It is not possible to create a asynchronous
 * socket for an arbitrary, pre-existing socket, nor is it possible to specify
 * the {@link java.net.SocketImpl}object to be used by a socket associated with
 * a asynchronous socket.
 * </p>
 * <p>
 * An asynchronous socket is created by invoking one of the {@link #open() open}
 * methods of this class. A newly-created asynchronous socket is open but not
 * yet connected. An attempt to invoke a I/O operation method of an unbound
 * asynchronous socket will cause a {@link NotYetBoundException}to be thrown.
 * An asynchronous socket can be connected by invoking its
 * {@link #connect connect}method; once connected, an asynchronous socket
 * remains connected until it is closed. Whether or not a asynchronous socket is
 * connected may be determined by invoking its {@link #isConnected isConnected}
 * method.
 * <p>
 * Asynchronous sockets are safe for use by multiple concurrent threads.
 * </p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public abstract class AsyncSocket implements WritableByteChannel, ScatteringByteChannel {


    /**
     * Opens an asynchronous socket.
     * <p>
     * The new socket is created by invoking the
     * {@linkcoconut.aio.spi.AioProvider#openAsyncSocket openAsyncSocket}method
     * of the system-wide default {@linkcoconut.aio.spi.AioProvider}object.
     * 
     * @return a new asynchronous socket.
     * @throws IOException If an I/O error occurs
     */
    public static AsyncSocket open() throws IOException {
        return AioProvider.provider().openSocket();
    }

    /**
     * Opens an asynchronous socket and uses the supplied executor to execute
     * any callback.
     * <p>
     * The new socket is created by invoking the
     * {@linkcoconut.aio.spi.AioProvider#openAsyncSocket openAsyncSocket}method
     * of the system-wide default {@linkcoconut.aio.spi.AioProvider}object.
     * </p>
     * 
     * @return a new asynchronous socket.
     * @throws IOException If an I/O error occurs
     */
    public static AsyncSocket open(Executor executor) throws IOException {
        if (executor == null) {
            throw new NullPointerException("executor is null");
        }
        return AioProvider.provider().openSocket(executor);
    }
    /**
     * Opens an asynchronous socket and uses the offerable to post any event to.
     * <p>
     * The new socket is created by invoking the
     * {@linkcoconut.aio.spi.AioProvider#openAsyncSocket openAsyncSocket}method
     * of the system-wide default {@linkcoconut.aio.spi.AioProvider}object.
     * 
     * @return a new asynchronous socket.
     * @throws IOException If an I/O error occurs
     */
    public static AsyncSocket open(Offerable< ? super Event> destination) throws IOException {
        if (destination == null) {
            throw new NullPointerException("destination is null");
        }
        return AioProvider.provider().openSocket(destination);
    }

    /**
     * Opens an asynchronous socket and uses the queue to post any event to.
     * <p>
     * The new socket is created by invoking the
     * {@linkcoconut.aio.spi.AioProvider#openAsyncSocket openAsyncSocket}method
     * of the system-wide default {@linkcoconut.aio.spi.AioProvider}object.
     * 
     * @return a new asynchronous socket.
     * @throws IOException If an I/O error occurs
     */
    public static AsyncSocket open(Queue< ? super Event> queue) throws IOException {
        if (queue == null) {
            throw new NullPointerException("queue is null");
        }
        return AioProvider.provider().openSocket(queue);
    }
    /**
     * Returns the default SocketMonitor
     * 
     * @return the default SocketMonitor
     */
    public static SocketMonitor getDefaultMonitor() {
        return AioProvider.provider().getDefaultSocketMonitor();
    }


    /**
     * Sets the default SocketMonitor. All new Sockets will automatically have
     * this monitor set.
     * 
     * @param monitor the monitor
     */
    public static void setDefaultMonitor(SocketMonitor monitor) {
        AioProvider.provider().setDefaultMonitor(monitor);
    }

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
     * Binds the socket to a local address.
     * <P>
     * If the address is <code>null</code>, then the system will pick up an
     * ephemeral port and a valid local address to bind the socket.
     * 
     * @param bindpoint the <code>SocketAddress</code> to bind to
     * @throws IOException if the bind operation fails, or if the socket is
     *             already bound.
     * @throws IllegalArgumentException if bindpoint is a SocketAddress subclass
     *             not supported by this socket
     * @see #isBound
     */
    public abstract AsyncSocket bind(SocketAddress bindpoint) throws IOException;

    /**
     * Closes the socket. NOTE: Does currently not support any kind of linger
     * mechanism
     * 
     * @return a Closed future.
     */
    public abstract AioFuture<?, Event> closeNow();

    /**
     * Connects this socket to the server.
     * 
     * @param endpoint the <code>SocketAddress</code>
     * @return a Connected future.
     */
    public abstract AioFuture<?, Event> connect(SocketAddress endpoint);

    /**
     * Returns the byte limit.
     * 
     * @return the number of bytes
     */
    public abstract long getBufferLimit();

    /**
     * Return the sockets close handler.
     * 
     * @return the closedHandler
     */
    public abstract EventProcessor<AsyncSocket> getCloseHandler();

    /**
     * Returns the default Offerable or <tt>null</tt> is no Offerable is set.
     * 
     * @return the default Offerable for this socket
     */
    public abstract Offerable< ? super Event> getDefaultDestination();

    /**
     * Returns the default Executor or <tt>null</tt> is no Executor is set.
     * 
     * @return the default Executor for this socket
     */
    public abstract Executor getDefaultExecutor();

    /**
     * Returns the AsyncSocketGroup that this socket is a member of or
     * <tt>null</tt> if the socket is not a member of any group.
     * 
     * @return the AsyncSocketGroup that this socket is a member of or
     *         <tt>null</tt> if the socket is not a member of any group.
     */
    public abstract AsyncSocketGroup getGroup();
    
    /**
     * Retrieves an unique id associated with this asynchronous socket.
     * 
     * @return An unique socket id
     */
    public abstract long getId();

    /**
     * Returns the address to which the socket is connected.
     * 
     * @return the remote IP address to which this socket is connected, or
     *         <code>null</code> if the socket is not connected.
     */
    public abstract InetAddress getInetAddress();

    /**
     * Gets the local address to which this asynchronous socket is bound.
     * 
     * @return the local address to which the socket is bound or
     *         <code>InetAddress.anyLocalAddress()</code> if the socket is not
     *         bound yet.
     */
    public abstract InetAddress getLocalAddress();

    /**
     * Returns the local port to which this socket is bound.
     * 
     * @return the local port number to which this socket is bound or -1 if the
     *         socket is not bound yet.
     */
    public abstract int getLocalPort();

    /**
     * Returns the address of the endpoint this socket is bound to, or
     * <code>null</code> if it is not bound yet.
     * 
     * @return a <code>SocketAddress</code> representing the local endpoint of
     *         this socket, or <code>null</code> if it is not bound yet.
     * @see #getLocalAddress()
     * @see #getLocalPort()
     * @see #bind(SocketAddress)
     */
    public abstract SocketAddress getLocalSocketAddress();

    /**
     * Return the sockets monitor
     * 
     * @return the monitor
     */
    public abstract SocketMonitor getMonitor();

    /**
     * Returns the remote port to which this socket is connected.
     * 
     * @return the remote port number to which this socket is connected, or 0 if
     *         the socket is not connected yet.
     */
    public abstract int getPort();

    /**
     * Returns the ReadHandler or <tt>null</tt> if none is.
     * 
     * @return the ReadHandler or <tt>null</tt> if none is.
     */
    public abstract ReadHandler<AsyncSocket> getReader();

    /**
     * Returns the address of the endpoint this socket is connected to, or
     * <code>null</code> if it is unconnected.
     * 
     * @return a <code>SocketAddress</code> reprensenting the remote endpoint
     *         of this socket, or <code>null</code> if it is not connected
     *         yet.
     * @see #getInetAddress()
     * @see #getPort()
     * @see #connect(SocketAddress)
     */
    public abstract SocketAddress getRemoteSocketAddress();

    /**
     * Returns the write request limit.
     * 
     * @return the number of write requests
     */
    public abstract int getWriteQueueLimit();

    /**
     * Returns the binding state of the socket.
     * 
     * @return true if the socket successfuly bound to an address
     * @see #bind
     */
    public abstract boolean isBound();

    /**
     * Returns the connection state of the asynchronous socket.
     * 
     * @return true if the socket is connected to another machine
     */
    public abstract boolean isConnected();

    /**
     * @see java.nio.channels.Channel#isOpen()
     */
    public abstract boolean isOpen();

    /**
     * This method is used for setting the maximal number of outstanding
     * <tt>bytes</tt> that will be accepted before rejecting new write
     * entries.
     * 
     * @param bytes the maximal number of pending bytes.
     */
    public abstract AsyncSocket setBufferLimit(long bytes);

    /**
     * Sets a handler that will be once the socket is closed. If the particular
     * handler is an instance of ErroneousHandler and the socket closed due to
     * an exception the handler(exception) method is called. If an exception did
     * not caused the socket to close the handle method is called.
     * 
     * @param handler the closedHandler
     */
    public abstract AsyncSocket setCloseHandler(EventProcessor<AsyncSocket> handler);

    /**
     * Adds this socket to the provided group. If this socket is allready a
     * member of another group it will leave this group first.
     * 
     * @param group the AsyncSocketGroup
     * @return this socket.
     */
    public abstract AsyncSocket setGroup(AsyncSocketGroup group);

    /**
     * Sets the SocketMonitor for this socket.
     * 
     * @param monitor the monitor
     * @return this socket
     */
    public abstract AsyncSocket setMonitor(SocketMonitor monitor);

    /**
     * Starts reading from this socket.
     * 
     * @param reader the <code>ReadHandler</code>
     * @return a ReaderSet future.
     */
    public abstract void setReader(ReadHandler<AsyncSocket> reader);

    /**
     * This method is used for setting the maximal number of outstanding
     * <tt>write request</tt> that will be accepted before rejecting new write
     * entries.
     * 
     * @param bytes the maximal number of pending write requests.
     */
    public abstract AsyncSocket setWriteQueueLimit(int maxQueueLength);

    /**
     * Retrieves a socket associated with this asynchronous socket.
     * <p>
     * The returned object will not declare any public methods that are not
     * declared in the {@link java.net.Socket}class.
     * </p>
     * 
     * @return A socket associated with this asynchronous socket
     */
    public abstract Socket socket();

    /**
     * Writes a sequence of bytes to this socket from the given buffer.
     * <p>
     * An attempt is made to write up to <i>r </i> bytes to the socket, where
     * <i>r </i> is the number of bytes remaining in the buffer, that is,
     * <tt>dst.remaining()</tt>, at the moment this method is invoked.
     * <p>
     * Suppose that a byte sequence of length <i>n </i> is written, where
     * <tt>0</tt> &nbsp; <tt>&lt;=</tt> &nbsp; <i>n </i>&nbsp;
     * <tt>&lt;=</tt> &nbsp; <i>r </i>. This byte sequence will be transferred
     * from the buffer starting at index <i>p </i>, where <i>p </i> is the
     * buffer's position at the moment this method is invoked; the index of the
     * last byte written will be <i>p </i>&nbsp; <tt>+</tt> &nbsp; <i>n
     * </i>&nbsp; <tt>-</tt> &nbsp; <tt>1</tt>. Upon return the buffer's
     * position will be equal to <i>p </i>&nbsp; <tt>+</tt> &nbsp; <i>n </i>;
     * its limit will not have changed.
     * <p>
     * This method does not block
     * 
     * @param src The buffer from which bytes are to be retrieved
     * @return a Written future
     */
    public abstract AioFuture<Long, Event> writeAsync(ByteBuffer src);

    /**
     * Writes a sequence of bytes to this socket from the given buffers.
     * <p>
     * An invocation of this method of the form <tt>c.write(srcs)</tt> behaves
     * in exactly the same manner as the invocation <blockquote>
     * 
     * <pre>
     * c.write(srcs, 0, srcs.length);
     * </pre>
     * 
     * </blockquote>
     * 
     * @param srcs The buffers from which bytes are to be retrieved
     * @return a Written future
     */
    public AioFuture<Long, Event> writeAsync(ByteBuffer[] srcs) {
        return writeAsync(srcs, 0, srcs.length);
    }

    /**
     * Writes a sequence of bytes to this socket from a subsequence of the given
     * buffers.
     * <p>
     * An attempt is made to write up to <i>r </i> bytes to this socket, where
     * <i>r </i> is the total number of bytes remaining in the specified
     * subsequence of the given buffer array, that is, <blockquote>
     * 
     * <pre>
     * 
     *  
     *   
     *    
     *     srcs[offset].remaining()
     *         + srcs[offset+1].remaining()
     *         + ... + srcs[offset+length-1].remaining()
     *    
     *   
     *  
     * </pre>
     * 
     * </blockquote> at the moment that this method is invoked.
     * <p>
     * Suppose that a byte sequence of length <i>n </i> is written, where
     * <tt>0</tt> &nbsp; <tt>&lt;=</tt> &nbsp; <i>n </i>&nbsp;
     * <tt>&lt;=</tt> &nbsp; <i>r </i>. Up to the first
     * <tt>srcs[offset].remaining()</tt> bytes of this sequence are written
     * from buffer <tt>srcs[offset]</tt>, up to the next
     * <tt>srcs[offset+1].remaining()</tt> bytes are written from buffer
     * <tt>srcs[offset+1]</tt>, and so forth, until the entire byte sequence
     * is written. As many bytes as possible are written from each buffer, hence
     * the final position of each updated buffer, except the last updated
     * buffer, is guaranteed to be equal to that buffer's limit.
     * <p>
     * This method does not block
     * 
     * @param srcs The buffers from which bytes are to be retrieved
     * @param offset The offset within the buffer array of the first buffer from
     *            which bytes are to be retrieved; must be non-negative and no
     *            larger than <tt>srcs.length</tt>
     * @param length The maximum number of buffers to be accessed; must be
     *            non-negative and no larger than <tt>srcs.length</tt>
     *            &nbsp;-&nbsp; <tt>offset</tt>
     * @return A Written future
     * @throws IndexOutOfBoundsException If the preconditions on the
     *             <tt>offset</tt> and <tt>length</tt> parameters do not
     *             hold
     */
    public abstract AioFuture<Long, Event> writeAsync(ByteBuffer[] srcs, int offset, int length);

    
    /**
     * A Closed Future.
     */
    public interface Closed extends Event {
        /**
         * Returns the cause of the close or <tt>null</tt> if the socket was
         * closed by explicit by the user.
         * 
         * @return the cause.
         */
        Throwable getCause();
    }

    /**
     * A Connected future.
     */
    public interface Connected extends Event {
        /**
         * Returns the <code>SocketAddress</code> that this socket was
         * connected to.
         * 
         * @return the <code>SocketAddress</code> that this socket was
         *         connected to.
         */
        SocketAddress getSocketAddress();
    }

    /**
     * The default Event interface that all AsyncSocket events inherit.
     */
    public interface ErroneousEvent extends Event {
        /**
         * Returns the cause of the error.
         * 
         * @return the cause.
         */
        Throwable getCause();

        /**
         * Returns the event that caused this exception.
         * 
         * @return the event that caused this exception.
         */
        Event getEvent();

        /**
         * Returns the message of this event.
         * 
         * @return the message.
         */
        String getMessage();
    }

    /**
     * The default Event interface that all AsyncSocket events inherit.
     */
    public interface Event{

        /**
         * Returns the asynchronous socket that created this event.
         * 
         * @return the asynchronous socket that created this event.
         */
        AsyncSocket async();

    }

    /**
     * A Written future
     */
    public interface Read extends Event {

        /**
         * Returns the number of bytes written.
         * 
         * @return the number of bytes written
         */
        long getBytesRead();
        /**
         * Returns the maximum number of buffers that was used for reading.
         * 
         * @return the maximum number of buffers that was accessed.
         */
        int getLength();
        /**
         * Returns the offset within the buffer array of the first buffer from
         * which bytes are to be retrieved.
         * 
         * @return the maximum number of buffers that was accessed.
         */
        int getOffset();

        /**
         * Returns the buffers that was written.
         * 
         * @return the buffers that was written
         */
        ByteBuffer[] getSrcs();
    }

    /**
     * A Written future
     */
    public interface Written extends Event {

        /**
         * Returns the number of bytes written.
         * 
         * @return the number of bytes written
         */
        long getBytesWritten();
        /**
         * Returns the number of buffers that was used for writing.
         * 
         * @return the number of buffers that was used for writing.
         */
        int getLength();

        /**
         * Returns the offset within the buffer array of the first buffer from
         * which bytes were written from. This always corresponds to the offset
         * given when requesting the write.
         * 
         * @return the offset within the buffer array of the first buffer from
         *         which bytes were written from.
         */
        int getOffset();

        /**
         * Returns the buffers that was written.
         * 
         * @return the buffers that was written
         */
        ByteBuffer[] getSrcs();
    }
}