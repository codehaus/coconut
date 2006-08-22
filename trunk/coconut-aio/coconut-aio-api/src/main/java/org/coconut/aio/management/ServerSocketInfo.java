/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.management;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketAddress;

/**
 * ServerSocket information. <tt>ServerSocketInfo</tt> contains the
 * information about a ServerSocket including:
 * <h4>General server-socket information</h4>
 * <ul>
 * <li>ServerSocket ID.</li>
 * <li>Timestamp from when this server-socket was created (not implemened yet).
 * </li>
 * <li>Timestamp from when this server-socket was closed (not implemened yet).
 * </li>
 * </ul>
 * 
 * <h4>Other information</h4>
 * <ul>
 * <li>Number of accepted sockets.</li>
 * <li>Binding status (true/false).</li>
 * <li>Local socket address (if bound).</li>
 * <li>Local Internet address (if bound).</li>
 * <li>Local port (if bound).</li>
 * </ul>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ServerSocketInfo implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -8243249992008275571L;

    /** ID of this datagram */
    private final long id;

    /** When this datagram was created */
    private final long creationDate;

    /** When this datagram died */
    private final long deadDate;

    /** Whether or not this datagram is bound */
    private final boolean isBound;

    /** The local inet address */
    private final SocketAddress localSocketAddress;

    /** The local port */
    private final int localPort;

    /** The local socket address */
    private final InetAddress localAddress;

    /** Total number of accepted sockets */
    private final long totalAcceptedSockets;

    /**
     * @param id
     *            id of the socket
     * @param creationDate
     *            creation date of the socket
     * @param deadDate
     *            die date of the socket
     * @param totalAcceptedSockets
     *            total number of accepted sockets
     * @param isBound
     *            bound status
     * @param inetAddress
     *            local inet address
     * @param localPort
     *            local port
     * @param address
     *            local socket address
     */
    public ServerSocketInfo(final long id, final long creationDate, final long deadDate,
            final long totalAcceptedSockets, boolean isBound, InetAddress inetAddress, int localPort,
            SocketAddress address) {
        this.creationDate = creationDate;
        this.deadDate = deadDate;
        this.id = id;
        this.totalAcceptedSockets = totalAcceptedSockets;
        this.localAddress = inetAddress;
        this.localSocketAddress = address;
        this.localPort = localPort;
        this.isBound = isBound;
    }

    /**
     * Returns when this server-socket was created. NOTE: currently returns 0.
     * 
     * @return the difference, measured in milliseconds, between the creation
     *         time and midnight, January 1, 1970 UTC.
     */
    public long getCreationDate() {
        return creationDate;
    }
    /**
     * Returns when this server-socket was closed. NOTE: currently returns 0.
     * 
     * @return the difference, measured in milliseconds, between the destruction
     *         time and midnight, January 1, 1970 UTC.
     */
    public long getDeadDate() {
        return deadDate;
    }

    /**
     * Gets the local address to which this server-socket is bound.
     * 
     * @return the local address to which the server-socket is bound or
     *         <tt>null</tt> if the socket is not bound yet.
     */
    public InetAddress getInetAddress() {
        return localAddress;
    }

    /**
     * Returns the binding state of the server-socket.
     * 
     * @return true if the server-socket is bound to an address
     */
    public boolean isBound() {
        return isBound;
    }

    /**
     * Returns the local port to which this server-socket is bound.
     * 
     * @return the local port number to which this server-socket is bound or -1
     *         if the server-socket is not bound yet.
     */
    public int getLocalPort() {
        return localPort;
    }

    /**
     * Returns the address of the endpoint this socket is bound to, or
     * <code>null</code> if it is not bound yet.
     * 
     * @return a <code>SocketAddress</code> representing the local endpoint of
     *         this socket, or <code>null</code> if it is not bound yet.
     * @see #getInetAddress()
     * @see #getLocalPort()
     */
    public SocketAddress getLocalSocketAddress() {
        return localSocketAddress;
    }

    /**
     * Returns the total number of accepts on this server-socket.
     * 
     * @return the total number of accepts.
     */
    public long getTotalAccepts() {
        return totalAcceptedSockets;
    }

    /**
     * Returns the ID of the datagram associated with this <tt>DatagramInfo</tt>.
     * 
     * @return the ID of the associated datagram.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns a string representation of this datagram info.
     * 
     * @return a string representation of this datagram info.
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AsyncServerSocket[id=");
        builder.append(id);
        if (isBound) {
            builder.append(",addr=");
            builder.append(localAddress);
            builder.append(":");
            builder.append(localPort);
        } else {
            builder.append(",unbound");
        }
        return builder.toString();
    }
}