/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.Executor;

import org.coconut.aio.monitor.DatagramMonitor;
import org.coconut.aio.spi.AioProvider;
import org.coconut.core.Colored;
import org.coconut.core.EventHandler;
import org.coconut.core.Offerable;

/**
 * An asynchronous datagram.
 * <p>
 * Asynchronous sockets are not a complete abstraction of network datagram
 * sockets. Manipulation of socket options must be done through an associated
 * {@link java.net.Datagram}object obtained by invoking the
 * {@link #socket() socket}method. It is not possible to create a asynchronous
 * datagram for an arbitrary, pre-existing socket, nor is it possible to specify
 * the {@link java.net.DatagramImpl}object to be used by a datagram socket
 * associated with a asynchronous datagram socket.
 * </p>
 * <p>
 * An asynchronous datagram is created by invoking the {@link #open open} method
 * of this class. A newly-created asynchronous datagram is open but not
 * connected. A asynchronous datagram need not be connected in order for the
 * {@link #send send}to be used. A datagram channel may be connected, by
 * invoking its {@link #connect connect} method, in order to avoid the overhead
 * of the security checks are otherwise performed as part of every send and
 * receive operation. A datagram channel must be connected in order to use the
 * {@link #read(java.nio.ByteBuffer) read} and {@link
 * #writeAsync(java.nio.ByteBuffer) write} methods, since those methods do not
 * accept or return socket addresses.
 * <p>
 * Once connected, a asynchronous datagram remains connected until it is disconnected
 * or closed. Whether or not a asynchronous datagram is connected may be determined
 * by invoking its {@link #isConnected isConnected} method.
 * <p>
 * Asynchronous datagram sockets are safe for use by multiple concurrent
 * threads.
 * </p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public abstract class AsyncDatagram implements Colored {

    /**
     * Retrieves an unique id associated with this asynchronous socket.
     * 
     * @return An unique socket id
     */
    public abstract long getId();

    /**
     * Retrieves a socket associated with this asynchronous socket.
     * <p>
     * The returned object will not declare any public methods that are not
     * declared in the {@link java.net.Socket}class.
     * </p>
     * 
     * @return A socket associated with this asynchronous socket
     */
    public abstract DatagramSocket socket();

    /**
     * Returns the address to which the socket is connected.
     * 
     * @return the remote IP address to which this socket is connected, or
     *         <code>null</code> if the socket is not connected.
     */
    public abstract InetAddress getInetAddress();

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
     * Returns the remote port to which this socket is connected.
     * 
     * @return the remote port number to which this socket is connected, or 0 if
     *         the socket is not connected yet.
     */
    public abstract int getPort();

    /**
     * Gets the local address to which this asynchronous socket is bound.
     * 
     * @return the local address to which the socket is bound or
     *         <code>InetAddress.anyLocalAddress()</code> if the socket is not
     *         bound yet.
     */
    public abstract InetAddress getLocalAddress();

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
     * Returns the local port to which this socket is bound.
     * 
     * @return the local port number to which this socket is bound or -1 if the
     *         socket is not bound yet.
     */
    public abstract int getLocalPort();

    /**
     * Binds the socket to a local address.
     * <P>
     * If the address is <code>null</code>, then the system will pick up an
     * ephemeral port and a valid local address to bind the socket.
     * 
     * @param bindpoint
     *            the <code>SocketAddress</code> to bind to
     * @throws IOException
     *             if the bind operation fails, or if the socket is already
     *             bound.
     * @throws IllegalArgumentException
     *             if bindpoint is a SocketAddress subclass not supported by
     *             this socket
     * @see #isBound
     */
    public abstract AsyncDatagram bind(SocketAddress bindpoint)
            throws IOException;

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
     * @return true if the socket successfuly connected to a server
     */
    public abstract boolean isConnected();

    /**
     * Limits the number of _bytes_ writes will queue up
     * 
     * @param bytes
     *            the maximal number of pending bytes.
     */
    public abstract AsyncDatagram setBufferLimit(long bytes);

    /**
     * Returns the byte limit.
     * 
     * @return the number of bytes
     */
    public abstract long getBufferLimit();

    /**
     * Sets the number of write _requests_ that are allowed to queue
     * 
     * @param maxQueueLength
     *            the maximal number of pending bytes.
     */
    public abstract AsyncDatagram setWriteQueueLimit(int maxQueueLength);

    /**
     * Returns the write request limit.
     * 
     * @return the number of write requests
     */
    public abstract int getWriteQueueLimit();

    /**
     * Opens an asynchronous socket in Callback-mode. But does not provide a
     * default Executor, attempting to call
     * <p>
     * The new channel is created by invoking the
     * {@link org.coconut.aio.spi.AioProvider#openSocket()} method
     * of the system-wide default {@linkcoconut.aio.spi.AioProvider}object.
     * 
     * @return a new asynchronous socket.
     * @throws IOException
     *             If an I/O error occurs
     */
    public static AsyncDatagram open() throws IOException {
        return AioProvider.provider().openDatagram();
    }

    /**
     * Opens an asynchronous socket in Queue-mode.
     * <p>
     * The ne w channel is created by invoking the
     * {@link org.coconut.aio.spi.AioProvider#openSocket() openAsyncSocket}method
     * of the system-wide default {@linkcoconut.aio.spi.AioProvider}object.
     * 
     * @return a new asynchronous socket.
     * @throws IOException
     *             If an I/O error occurs
     */
    public static AsyncDatagram open(Offerable<? super Event> queue)
            throws IOException {
        if (queue == null) {
            throw new NullPointerException("qeueu is null");
        }
        return AioProvider.provider().openDatagram(queue);
    }

    /**
     * Opens an asynchronous datagram and uses the queue to post any event to.
     * <p>
     * The new socket is created by invoking the
     * {@link org.coconut.aio.spi.AioProvider#openDatagram() openAsyncDatagram}
     * method of the system-wide default {@linkcoconut.aio.spi.AioProvider}
     * object.
     * 
     * @return a new asynchronous datagram.
     * @throws IOException
     *             If an I/O error occurs
     */
    public static AsyncDatagram open(Queue<? super Event> queue)
            throws IOException {
        if (queue == null) {
            throw new NullPointerException("qeueu is null");
        }
        return AioProvider.provider().openDatagram(queue);
    }

    /**
     * Opens an asynchronous socket in Callback-mode.
     * <p>
     * The new channel is created by invoking the
     * {@link org.coconut.aio.spi.AioProvider#openSocket() openAsyncSocket}method
     * of the system-wide default {@linkcoconut.aio.spi.AioProvider}object.
     * 
     * @return a new asynchronous socket.
     * @throws IOException
     *             If an I/O error occurs
     */
    public static AsyncDatagram open(Executor executor) throws IOException {
        if (executor == null) {
            throw new NullPointerException("executor is null");
        }
        return AioProvider.provider().openDatagram(executor);
    }

    /**
     * Connects this datagram.
     * <p>
     * The socket is configured so that it only receives datagrams from, and
     * sends datagrams to, the given remote <i>peer </i> address. Once
     * connected, datagrams may not be received from or sent to any other
     * address. A datagram socket remains connected until it is explicitly
     * disconnected or until it is closed.
     * <p>
     * This method performs exactly the same security checks as the {@link
     * java.net.DatagramSocket#connect connect} method of the {@link
     * java.net.DatagramSocket} class. That is, if a security manager has been
     * installed then this method verifies that its {@link
     * java.lang.SecurityManager#checkAccept checkAccept} and {@link
     * java.lang.SecurityManager#checkConnect checkConnect} methods permit
     * datagrams to be received from and sent to, respectively, the given remote
     * address.
     * <p>
     * This method may be invoked at any time. It will not have any effect on
     * read or write operations that are already in progress at the moment that
     * it is invoked.
     * </p>
     * 
     * @param address
     *            The remote address to which this channel is to be connected
     * @return This datagram
     * @throws ClosedChannelException
     *             If this datagram is closed
     * @throws AsynchronousCloseException
     *             If another thread closes this datagram while the connect
     *             operation is in progress
     * @throws ClosedByInterruptException
     *             If another thread interrupts the current thread while the
     *             connect operation is in progress, thereby closing the
     *             datagram and setting the current thread's interrupt status
     * @throws SecurityException
     *             If a security manager has been installed and it does not
     *             permit access to the given remote address
     * @throws IOException
     *             If some other I/O error occurs
     */
    public abstract AsyncDatagram connect(SocketAddress address)
            throws IOException;

    /**
     * Disconnects this datagram.
     * <p>
     * The socket is configured so that it can receive datagrams from, and sends
     * datagrams to, any remote address so long as the security manager, if
     * installed, permits it.
     * <p>
     * This method may be invoked at any time. It will not have any effect on
     * read or write operations that are already in progress at the moment that
     * it is invoked.
     * <p>
     * If this socket is not connected, or if the socket is closed, then
     * invoking this method has no effect.
     * </p>
     * 
     * @return This datagram socket
     * @throws IOException
     *             If some other I/O error occurs
     */
    public abstract AsyncDatagram disconnect() throws IOException;

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
     * @param src
     *            The buffer from which bytes are to be retrieved
     * @return a Written future
     */
    public abstract Written writeAsync(ByteBuffer src);

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
     * @param srcs
     *            The buffers from which bytes are to be retrieved
     * @return a Written future
     */
    public Written writeAsync(ByteBuffer[] srcs) {
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
     *      srcs[offset].remaining()
     *          + srcs[offset+1].remaining()
     *          + ... + srcs[offset+length-1].remaining()
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
     * @param srcs
     *            The buffers from which bytes are to be retrieved
     * @param offset
     *            The offset within the buffer array of the first buffer from
     *            which bytes are to be retrieved; must be non-negative and no
     *            larger than <tt>srcs.length</tt>
     * @param length
     *            The maximum number of buffers to be accessed; must be
     *            non-negative and no larger than <tt>srcs.length</tt>
     *            &nbsp;-&nbsp; <tt>offset</tt>
     * @return A Written future
     * @throws IndexOutOfBoundsException
     *             If the preconditions on the <tt>offset</tt> and
     *             <tt>length</tt> parameters do not hold
     */
    public abstract Written writeAsync(ByteBuffer[] srcs, int offset, int length);

    public abstract Written send(ByteBuffer src, SocketAddress target);

    public abstract SocketAddress receive(ByteBuffer dst);

    /**
     * Starts reading from this socket.
     * 
     * @param reader
     *            the <code>ReadHandler</code>
     * @return a ReaderSet future.
     */
    public abstract ReaderSet setReader(ReadHandler<AsyncDatagram> reader);

    /**
     * Returns the ReadHandler or <tt>null</tt> if none is.
     * 
     * @return the ReadHandler or <tt>null</tt> if none is.
     */
    public abstract ReadHandler<AsyncDatagram> getReader();

    /**
     * Closes the socket. NOTE: Does currently not support any kind of linger
     * mechanism
     * 
     * @return a Closed future.
     */
    public abstract Closed close();

    /**
     * Adds this socket to the provided group. If this socket is allready a
     * member of another group it will leave this group first.
     * 
     * @param group
     *            the AsyncDatagramGroup
     * @return a Closed future.
     */
    public abstract AsyncDatagram setGroup(AsyncDatagramGroup group);

    /**
     * Returns the AsyncDatagramGroup that this socket is a member of or
     * <tt>null</tt> if the socket is not a member of any group.
     * 
     * @return the AsyncDatagramGroup that this socket is a member of or
     *         <tt>null</tt> if the socket is not a member of any group.
     */
    public abstract AsyncDatagramGroup getGroup();

    /**
     * Sets the default DatagramMonitor. All new datagrams will automatically
     * have this monitor set.
     * 
     * @param monitor
     *            the monitor
     */
    public static void setDefaultMonitor(DatagramMonitor monitor) {
        AioProvider.provider().setDefaultMonitor(monitor);
    }

    /**
     * Returns the default DatagramMonitor
     * 
     * @return the default DatagramMonitor
     */
    public static DatagramMonitor getDefaultMonitor() {
        return AioProvider.provider().getDefaultDatagramMonitor();
    }

    /**
     * Sets the DatagramMonitor for this socket.
     * 
     * @param monitor
     *            the monitor
     * @return this datagram
     */
    public abstract AsyncDatagram setMonitor(DatagramMonitor monitor);

    /**
     * Return the datagrams monitor
     * 
     * @return the monitor
     */
    public abstract DatagramMonitor getMonitor();

    /**
     * Attaches the given object to this key.
     * <p>
     * An attached object may later be retrieved via the {@link #attachment
     * attachment} method. Only one object may be attached at a time; invoking
     * this method causes any previous attachment to be discarded. The current
     * attachment may be discarded by attaching <tt>null</tt>.
     * </p>
     * 
     * @param ob
     *            The object to be attached; may be <tt>null</tt>
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
     * Returns the default Offerable or <tt>null</tt> is no Offerable is set.
     * 
     * @return the default Offerable for this socket
     */
    public abstract Offerable<? super Event> getDefaultDestination();

    /**
     * Returns the default Executor or <tt>null</tt> is no Executor is set.
     * 
     * @return the default Executor for this socket
     */
    public abstract Executor getDefaultExecutor();

    /**
     * Sets a handler that will be once the socket is closed. If the particular
     * handler is an instance of ErroneousHandler and the socket closed due to
     * an exception the handler(exception) method is called. If an exception did
     * not caused the socket to close the handle method is called.
     * 
     * @param handler
     *            the closedHandler
     */
    public abstract AsyncDatagram setCloseHandler(
            EventHandler<AsyncDatagram> handler);

    /**
     * Return the sockets close handler.
     * 
     * @return the closedHandler
     */
    public abstract EventHandler<AsyncDatagram> getCloseHandler();

    /**
     * Returns whether or not this socket is open. A socket is open from it is
     * created until is closed by user or due to some exception.
     * 
     * @return whether or not this socket is open
     */
    public abstract boolean isOpen();

    /**
     * Returns the sockets source.
     * 
     * @return the source for data
     */
    public abstract AsyncDatagramSource getSource();

    public interface Event extends Colored {

        /**
         * Returns the asynchronous socket that created this event.
         * 
         * @return the asynchronous socket that created this event.
         */
        AsyncDatagram async();

    }

    public interface ErroneousEvent extends Event {
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

    public interface Closed extends Event, AioFuture {
        /**
         * Returns the cause of the close or <tt>null</tt> if the socket was
         * closed by explicit by the user.
         * 
         * @return the cause.
         */
        Throwable getCause();
    }

    public interface ReaderSet extends Event, AioFuture {
        /**
         * Returns the <code>ReadHandler</code>.
         * 
         * @return the <code>ReadHandler</code>.
         */
        ReadHandler<AsyncDatagram> getReader();
    }

    /**
     * A Written future
     */
    public interface Written extends Event, AioFuture<Long, Event> {
        /**
         * Returns the maximum number of buffers that was accessed.
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
         * Returns the destination of this socket.
         * 
         * @return the address that was written to.
         */
        SocketAddress getAddress();

        /**
         * Returns the number of bytes written.
         * 
         * @return the number of bytes written
         */
        long getBytesWritten();

        /**
         * Returns the buffers that was written.
         * 
         * @return the buffers that was written
         */
        ByteBuffer[] getSrcs();
    }
}