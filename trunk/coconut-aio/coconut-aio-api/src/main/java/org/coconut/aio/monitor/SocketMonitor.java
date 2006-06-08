package org.coconut.aio.monitor;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

import org.coconut.aio.AsyncSocket;

/**
 * A <tt>SocketMonitor</tt> is used for monitoring important socket events.
 * 
 * <p>
 * All methods needs to thread-safe as multiple events might be posted
 * concurrently.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class SocketMonitor {

    /**
     * This method is called whenever a new <tt>AsyncSocket</tt> is opened.
     * This method is only called if the monitor is set as the default monitor
     * for the <tt>AsyncSocket</tt> class.
     * 
     * @param socket
     *            the socket that was opened
     */
    public void opened(AsyncSocket socket) {
    }

    /**
     * Called whenever a socket was succesfully bound to an address.
     * 
     * @param socket
     *            the socket that was bound
     * @param address
     *            the address that the socket was bound to
     */
    public void bound(AsyncSocket socket, SocketAddress address) {

    }

    /**
     * Called whenever a socket fails to bind to a specific address. The
     * predominant reason for this failure is cases where another socket is
     * already bound to the particular address.
     * 
     * @param socket
     *            the socket that failed to bind
     * @param address
     *            the address that the socket was supposed to bind to
     * @param cause
     *            the reason for the exception
     */
    public void bindFailed(AsyncSocket socket, SocketAddress address, Throwable cause) {

    }

    /**
     * Called whenever a socket succesfully connected to a remote address.
     * 
     * @param socket
     *            the socket that was bound
     * @param address
     *            the address that the socket was bound to
     */
    public void connected(AsyncSocket socket, SocketAddress address) {
    }

    /**
     * Called whenever a socket failed to connect to a remote address.
     * 
     * @param socket
     *            the socket that failed to connect
     * @param remote
     *            the remote address the socket was supposed to be connected to
     * @param cause
     *            the reason for the connect failure
     */
    public void connectFailed(AsyncSocket socket, SocketAddress remote, Throwable cause) {
    }

    /**
     * Called whenever a socket is disconnected. This method is only called if
     * the sockets disconnect method is called. If a connected socket is closed
     * this method is not called.
     * 
     * @param socket
     *            the socket that was disconnected
     */
    public void disconnected(AsyncSocket socket) {
    }

    /**
     * Called whenever a socket is closed either explicitly by the user or due
     * to some exception doing reading/writing or other socket methods.
     * 
     * @param socket
     *            the socket that was closed
     * @param cause
     *            the cause of the close or <tt>null</tt> if the socket was
     *            closed by the user
     */
    public void closed(AsyncSocket socket, Throwable cause) {
    }

    /**
     * This method is called before a socket does the actual write. Every call
     * to preWrite() has a corresponding call to a postWrite() method even if
     * the write fails. For a given socket there will be no interleaving calls
     * to any preWrite() methods or postWrite() methods before the postWrite()
     * method has been called.
     * 
     * @param socket
     *            the socket that the data is being written to.
     * @param srcs
     *            The buffers from which bytes are to be retrieved
     * 
     * @param offset
     *            The offset within the buffer array of the first buffer from
     *            which bytes are to be retrieved; is non-negative and no larger
     *            than <tt>srcs.length</tt>
     * 
     * @param length
     *            The maximum number of buffers to be accessed; is non-negative
     *            and no larger than <tt>srcs.length</tt> &nbsp;-&nbsp;
     *            <tt>offset</tt>
     */
    public void preWrite(AsyncSocket socket, ByteBuffer[] srcs, int offset, int length) {
    }

    /**
     * This method is called after a socket completes (or fails) a write. Every
     * call of postWrite() matches a corresponding preWrite() call
     * 
     * @param socket
     *            the socket that the data was written to.
     * @param bytes
     *            the number of bytes that was written
     * @param srcs
     *            The buffers from which the bytes were retrieved
     * 
     * @param offset
     *            The offset within the buffer array of the first buffer from
     *            which the bytes were retrieved; is non-negative and no larger
     *            than <tt>srcs.length</tt>
     * 
     * @param length
     *            The number of buffers that was accessed; is non-negative and
     *            no larger than <tt>srcs.length</tt> &nbsp;-&nbsp;
     *            <tt>offset</tt>
     * @param attempts
     *            the number of attempts that used for writing the bytes.
     * @param cause
     *            any exception that was thrown doing the writing or
     *            <tt>null</tt> if the write completed succesfully
     */
    public void postWrite(AsyncSocket socket, long bytes, ByteBuffer[] srcs, int offset, int length, int attempts,
            Throwable cause) {
    }

    /**
     * This method is called before a socket does the actual read. Every call to
     * preRead() has a corresponding call to a postRead() method even if the
     * write fails. For a given socket there will be no interleaving calls to
     * any preRead() methods or postRead() methods before the corresponding
     * postRead() method has been called.
     * 
     * @param socket
     *            the socket that the data is being read from.
     * @param dsts
     *            The buffers into which bytes are to be transferred
     * 
     * @param offset
     *            The offset within the buffer array of the first buffer into
     *            which bytes are to be transferred; is non-negative and no
     *            larger than <tt>dsts.length</tt>
     * 
     * @param length
     *            The maximum number of buffers to be accessed; is non-negative
     *            and no larger than <tt>dsts.length</tt> &nbsp;-&nbsp;
     *            <tt>offset</tt>
     */
    public void preRead(AsyncSocket socket, ByteBuffer[] dsts, int offset, int length) {
    }

    /**
     * This method is called after a socket completes (or fails) a read. Every
     * call of postRead() matches a corresponding preRead() call
     * 
     * @param socket
     *            the socket that the data is being read from.
     * @param bytes
     *            the number of bytes that was read
     * @param dsts
     *            The buffers into which bytes are to be transferred
     * 
     * @param offset
     *            The offset within the buffer array of the first buffer into
     *            which bytes are to be transferred; is non-negative and no
     *            larger than <tt>dsts.length</tt>
     * 
     * @param length
     *            The maximum number of buffers to be accessed; is non-negative
     *            and no larger than <tt>dsts.length</tt> &nbsp;-&nbsp;
     *            <tt>offset</tt>
     * @param throwable
     *            any exception that occured doing reading
     */
    public void postRead(AsyncSocket socket, long bytes, ByteBuffer[] dsts, int offset, int length,
            Throwable throwable) {
    }
}