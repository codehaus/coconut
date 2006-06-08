package org.coconut.aio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;
/**
 * A datagram-source that can read bytes into a sequence of buffers.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface AsyncDatagramSource extends ScatteringByteChannel {

    /**
     * Receives a datagram via this asynchronous datagram.
     * 
     * <p>
     * The datagram is transferred into the given byte buffer starting at its
     * current position, as if by a regular {@link
     * ReadableByteChannel#read(java.nio.ByteBuffer) read} operation. If there
     * are fewer bytes remaining in the buffer than are required to hold the
     * datagram then the remainder of the datagram is silently discarded.
     * 
     * <p>
     * This method performs exactly the same security checks as the {@link
     * java.net.DatagramSocket#receive receive} method of the {@link
     * java.net.DatagramSocket} class. That is, if the socket is not connected
     * to a specific remote address and a security manager has been installed
     * then for each datagram received this method verifies that the source's
     * address and port number are permitted by the security manager's {@link
     * java.lang.SecurityManager#checkAccept checkAccept} method. The overhead
     * of this security check can be avoided by first connecting the socket via
     * the {@link #connect connect}method.
     * 
     * <p>
     * This method may be invoked at any time. If another thread has already
     * initiated a read operation upon this channel, however, then an invocation
     * of this method will block until the first operation is complete.
     * </p>
     * 
     * @param dst
     *            The buffer into which the datagram is to be transferred
     * 
     * @return The datagram's source address, or <tt>null</tt> if no datagram
     *         was immediately available
     * 
     * 
     * @throws AsynchronousCloseException
     *             If another thread closes this datagram while the read
     *             operation is in progress
     * 
     * @throws ClosedByInterruptException
     *             If another thread interrupts the current thread while the
     *             read operation is in progress, thereby closing the datagram
     *             and setting the current thread's interrupt status
     * 
     * @throws SecurityException
     *             If a security manager has been installed and it does not
     *             permit datagrams to be accepted from the datagram's sender
     * 
     * @throws IOException
     *             If some other I/O error occurs
     */
    SocketAddress receive(ByteBuffer dst) throws IOException;

}