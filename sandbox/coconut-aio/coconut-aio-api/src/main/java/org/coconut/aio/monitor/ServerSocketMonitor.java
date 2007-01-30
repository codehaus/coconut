/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.monitor;

import java.net.SocketAddress;

import org.coconut.aio.AsyncServerSocket;
import org.coconut.aio.AsyncSocket;


/**
 * A <tt>ServerSocketMonitor</tt> is used for monitoring important
 * server-socket events.
 * 
 * <p>
 * All methods needs to thread-safe as multiple events might be posted
 * concurrently.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$ 
 */
public class ServerSocketMonitor {

    /**
     * This method is called whenever a new <tt>AsyncServerSocket</tt> is
     * opened. This method is only called if the monitor is set as the default
     * monitor for the <tt>AsyncServerSocket</tt> class.
     * 
     * @param socket
     *            the server-socket that was opened
     */
    public void opened(AsyncServerSocket socket) {

    }

    /**
     * Called whenever a server-socket accepts a new socket.
     * 
     * @param socket
     *            the server-socket that accepted the new socket
     * @param acceptedSocket
     *            the socket that was accepted
     */
    public void accepted(AsyncServerSocket socket, AsyncSocket acceptedSocket) {

    }

    /**
     * Called whenever a server-socket was succesfully bound to an address.
     * 
     * @param socket
     *            the server-socket that was bound
     * @param address
     *            the address that the server-socket was bound to
     */
    public void bound(AsyncServerSocket socket, SocketAddress address) {

    }

    /**
     * Called whenever a server-socket fails to bind to a specific address. The
     * predominant reason for this failure is cases where another socket is
     * already bound to the particular address.
     * 
     * @param socket
     *            the server-socket that failed to bind
     * @param address
     *            the address that the server-socket was supposed to bind to
     * @param cause
     *            the reason for the exception
     */
    public void bindFailed(AsyncServerSocket socket, SocketAddress address, Throwable cause) {

    }

    /**
     * Called whenever a server-socket is closed either explicitly by the user
     * or due to some exception doing reading/writing or other socket methods.
     * 
     * @param socket
     *            the server-socket that was closed
     * @param cause
     *            the cause of the close or <tt>null</tt> if the server-socket
     *            was closed by the user
     */
    public void closed(AsyncServerSocket socket, Throwable cause) {
    }
}