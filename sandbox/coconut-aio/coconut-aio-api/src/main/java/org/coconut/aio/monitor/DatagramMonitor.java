/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.monitor;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

import org.coconut.aio.AsyncDatagram;

/**
 * A <tt>DatagramMonitor</tt> is used for monitoring important datagram
 * events.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DatagramMonitor {

    /**
     * This method is called whenever a new <tt>AsyncDatagram</tt> is opened.
     * This method is only called if the monitor is set as the default monitor
     * for the <tt>AsyncDatagram</tt> class.
     * 
     * @param datagram
     *            the datagram that was opened
     */
    public void opened(AsyncDatagram datagram) {
    }

    /**
     * Called whenever a datagram socket was succesfully bound to an address.
     * 
     * @param socket
     *            the datagram socket that was bound
     * @param address
     *            the address that the datagram was bound to
     */
    public void bound(AsyncDatagram socket, SocketAddress address) {

    }
    /**
     * Called whenever a datagram socket fails to bind to a specific address.
     * The predominant reason for this failure is cases where another socket is
     * already bound to the particular address.
     * 
     * @param socket
     *            the datagram socket that failed to bind
     * @param address
     *            the address that the datagram was supposed to bind to
     * @param cause
     *            the reason for the exception
     */
    public void bindFailed(AsyncDatagram socket, SocketAddress address, Throwable cause) {
    }

    /**
     * Called whenever a datagram socket succesfully connected to a remote
     * address.
     * 
     * @param socket
     *            the datagram socket that was bound
     * @param address
     *            the address that the datagram was bound to
     */
    public void connected(AsyncDatagram socket, SocketAddress address) {
    }

    /**
     * Called whenever a datagram socket failed to connect to a remote address.
     * 
     * @param datagram
     *            the datagram that failed to connect
     * @param remote
     *            the remote address the datagram was supposed to be connected
     *            to
     * @param cause
     *            the reason for the connect failure
     */
    public void connectFailed(AsyncDatagram datagram, SocketAddress remote, Throwable cause) {
    }

    /**
     * Called whenever a datagram is disconnected. This method is only called if
     * the datagrams disconnect method is called. If a connected datagram is
     * closed this method is not called
     * 
     * @param datagram
     *            the datagram that was disconnected
     */
    public void disconnected(AsyncDatagram datagram) {
    }

    /**
     * Called whenever a datagram is closed either explicitly by the user or due
     * to some exception doing reading/writing or other datagram methods.
     * 
     * @param datagram
     *            the datagram that was closed
     * @param cause
     *            the cause of the close or <tt>null</tt> if the datagram was
     *            closed by the user
     */
    public void closed(AsyncDatagram datagram, Throwable cause) {
    }

    /**
     * This method is called before a datagram socket does the actual write.
     * Every call to preWrite() has a corresponding call to a postWrite() method
     * even if the write fails. For a given datagram socket there will be no
     * interleaving calls to any preWrite() methods or postWrite() methods
     * before the postWrite() method has been called.
     * 
     * @param datagram
     *            the datagram socket that the data is being written to.
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
    public void preWrite(AsyncDatagram datagram, ByteBuffer[] srcs, int offset, int length) {
    }

    /**
     * This method is called after a datagram socket completes (or fails) a
     * write. Every call of postWrite() matches a corresponding preWrite() call
     * 
     * @param datagram
     *            the datagram socket that the data was written to.
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
     * @param throwable
     *            any exception that was thrown doing the writing or
     *            <tt>null</tt> if the write completed succesfully
     */
    public void postWrite(AsyncDatagram datagram, long bytes, ByteBuffer[] srcs, int offset, int length, int attempts,
            Throwable throwable) {
    }

    /**
     * This method is called before a datagram socket does the actual sending of
     * the data. Every call to preSend() has a corresponding call to a
     * postSend() method even if the send fails. For a given datagram socket
     * there will be no interleaving calls to any preSend() methods or
     * postSend() methods before the postSend() method has been called.
     * 
     * @param datagram
     *            the datagram socket that the data is being send to
     * @param address
     *            the destination of the send
     * @param src
     *            The buffer from which bytes are to be retrieved
     *  
     */
    public void preSend(AsyncDatagram datagram, SocketAddress address, ByteBuffer src) {
    }

    /**
     * This method is called after a datagram socket completes (or fails) a
     * send. Every call of postWrite() matches a corresponding preWrite() call
     * 
     * @param datagram
     *            the datagram socket that the data was written to.
     * @param address
     *            the destination of the send
     * @param bytes
     *            the number of bytes that was send
     * @param src
     *            The buffer from which the bytes were retrieved
     * @param attempts
     *            the number of attempts that used for writing the bytes.
     * @param throwable
     *            any exception that was thrown doing the writing or
     *            <tt>null</tt> if the write completed succesfully
     */
    public void postSend(AsyncDatagram datagram, SocketAddress address, long bytes, ByteBuffer src, int attempts,
            Throwable throwable) {
    }

    /**
     * This method is called before a datagram socket does the actual read.
     * Every call to preRead() has a corresponding call to a postRead() method
     * even if the write fails. For a given datagram socket there will be no
     * interleaving calls to any preRead() methods or postRead() methods before
     * the corresponding postRead() method has been called.
     * 
     * @param datagram
     *            the datagram socket that the data is being read from.
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
    public void preRead(AsyncDatagram datagram, ByteBuffer[] dsts, int offset, int length) {
    }

    /**
     * This method is called after a datagram socket completes (or fails) a
     * read. Every call of postRead() matches a corresponding preRead() call.
     * 
     * @param datagram
     *            the datagram socket that the data is being read from.
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
    public void postRead(AsyncDatagram datagram, long bytes, ByteBuffer[] dsts, int offset, int length,
            Throwable throwable) {
    }

    /**
     * This method is called before a datagram socket does the actual receive.
     * Every call to preRead() has a corresponding call to a postRead() method
     * even if the write fails. For a given datagram socket there will be no
     * interleaving calls to any preRead() methods or postRead() methods before
     * the corresponding postRead() method has been called.
     * 
     * @param datagram
     *            the datagram socket that the data is being read from.
     * @param address
     *            the source of the receive
     * @param dst
     *            the buffer into which bytes are to be transfered.
     */
    public void preReceive(AsyncDatagram datagram, SocketAddress address, ByteBuffer dst) {
    }

    /**
     * This method is called after a datagram socket completes (or fails) a
     * read. Every call of postReceive() matches a corresponding preReceive()
     * call.
     * 
     * @param datagram
     *            the datagram socket that the data is being read from.
     * @param address
     *            the source of the receive
     * @param bytes
     *            the number of bytes that was read
     * @param dst
     *            the buffer into which bytes were transfered.
     * @param attempts
     *            the number of attempts that used for writing the bytes.
     * @param throwable
     *            any exception that was thrown doing the writing or
     *            <tt>null</tt> if the write completed succesfully
     */
    public void postReceive(AsyncDatagram datagram, SocketAddress address, long bytes, ByteBuffer dst, int attempts,
            Throwable throwable) {
    }

}