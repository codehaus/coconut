/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.management;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketAddress;

/**
 * Socketp information. <tt>SocketInfo</tt> contains the information about a
 * Socket including:
 * <h4>General socket information</h4>
 * <ul>
 * <li>Socket ID.</li>
 * <li>SocketGroup ID.</li>
 * <li>Timestamp from when this socket was created (not implemened yet).</li>
 * <li>Timestamp from when this socket was closed (not implemened yet).</li>
 * </ul>
 * <h4>Other information</h4>
 * <ul>
 * <li>Number of bytes read.</li>
 * <li>Number of bytes written.</li>
 * <li>Connection status (true/false).</li>
 * <li>Binding status (true/false).</li>
 * <li>Local socket address (if bound).</li>
 * <li>Remote socket address (if connected).</li>
 * <li>Local Internet address (if bound).</li>
 * <li>Remote Internet address (if connected).</li>
 * <li>Local port (if bound).</li>
 * <li>Remote port (if connected).</li>*
 * </ul>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class SocketInfo implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 8492940405220450485L;

    /** ID of this socket */
    private final long id;

    /** When this socket was created */
    private final long creationDate;

    /** When this socket died */
    private final long deadDate;

    /** Whether or not this socket is connected */
    private final boolean isConnected;

    /** Whether or not this socket is bound */
    private final boolean isBound;

    /** The id of the socket group */
    private final long socketGroupId;

    /** The remote inet address */
    private final InetAddress inetAddress;

    /** The local inet address */
    private final SocketAddress localSocketAddress;

    /** The remote port */
    private final int port;

    /** The local port */
    private final int localPort;

    /** The remote socket address */
    private final SocketAddress remoteSocketAddress;

    /** The local socket address */
    private final InetAddress localAddress;

    /** Number of bytes written */
    private final long bytesWritten;

    /** Number of bytes read */
    private final long bytesRead;

    /**
     * Constructor of SocketInfo created by the AIO subsystem
     * 
     * @param id id of the socket
     * @param creationDate creation date of the socket
     * @param deadDate die date of the socket
     * @param groupId the id of the group
     * @param socket The actual socket
     * @param bytesRead The number of bytes read
     * @param bytesWritten The number of bytes written
     */
    public SocketInfo(final long id, final long creationDate, final long deadDate,
        final long groupId, boolean isBound, boolean isConnected, InetAddress inetAddress,
        SocketAddress localSocketAddress, int port, int localPort,
        SocketAddress remoteSocketAddress, InetAddress localAddress, final long bytesRead,
        final long bytesWritten) {

        this.id = id;
        this.creationDate = creationDate;
        this.deadDate = deadDate;
        this.bytesWritten = bytesWritten;
        this.bytesRead = bytesRead;
        this.socketGroupId = groupId;

        // must be fixed for remote JMX
        this.isBound = isBound;
        this.isConnected = isConnected;
        this.inetAddress = inetAddress;
        this.localSocketAddress = localSocketAddress;
        this.port = port;
        this.localPort = localPort;
        this.remoteSocketAddress = remoteSocketAddress;
        this.localAddress = localAddress;
    }

    /**
     * Returns the number of bytes read from this socket.
     * 
     * @return Returns the number of bytes read.
     */
    public long getBytesRead() {
        return bytesRead;
    }

    /**
     * Returns the number of bytes written to this socket.
     * 
     * @return Returns the bytes written
     */
    public long getBytesWritten() {
        return bytesWritten;
    }
    /**
     * Returns when this socket was created. NOTE: currently returns 0.
     * 
     * @return the difference, measured in milliseconds, between the creation
     *         time and midnight, January 1, 1970 UTC.
     */
    public long getCreationDate() {
        return creationDate;
    }
    /**
     * Returns when this socket was closed. NOTE: currently returns 0.
     * 
     * @return the difference, measured in milliseconds, between the destruction
     *         time and midnight, January 1, 1970 UTC.
     */
    public long getDeadDate() {
        return deadDate;
    }

    /**
     * Returns the address to which the socket is connected.
     * 
     * @return the remote IP address to which this socket is connected, or
     *         <code>null</code> if the socket is not connected.
     */
    public InetAddress getInetAddress() {
        return inetAddress;
    }
    /**
     * Returns the address of the endpoint this socket is bound to, or
     * <code>null</code> if it is not bound yet.
     * 
     * @return a <code>SocketAddress</code> representing the local endpoint of
     *         this socket, or <code>null</code> if it is not bound yet.
     * @see #getLocalAddress()
     * @see #getLocalPort()
     */
    public SocketAddress getLocalSocketAddress() {
        return localSocketAddress;
    }

    /**
     * Returns the remote port to which this socket is connected.
     * 
     * @return the remote port number to which this socket is connected, or 0 if
     *         the socket is not connected yet.
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the local address to which this socket is bound.
     * 
     * @return the local address to which the socket is bound or <tt>null</tt>
     *         if the socket is not bound yet.
     */
    public InetAddress getLocalAddress() {
        return localAddress;
    }

    /**
     * Returns the address of the endpoint this socket is connected to, or
     * <code>null</code> if it is unconnected.
     * 
     * @return a <code>SocketAddress</code> reprensenting the remote endpoint
     *         of this socket, or <code>null</code> if it is not connected
     *         yet.
     * @see #getInetAddress()
     * @see #getPort()
     */
    public SocketAddress getRemoteSocketAddress() {
        return remoteSocketAddress;
    }

    /**
     * Returns the local port to which this socket is bound.
     * 
     * @return the local port number to which this socket is bound or -1 if the
     *         socket is not bound yet.
     */
    public int getLocalPort() {
        return localPort;
    }

    /**
     * Returns the connection state of the socket.
     * 
     * @return true if the socket is connected to another machine
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Returns the binding state of the socket.
     * 
     * @return true if the socket is bound to an address
     */
    public boolean isBound() {
        return isBound;
    }

    /**
     * Returns the ID of the group this socket is a member of or -1.
     * 
     * @return the ID of the associated group or -1.
     */
    public long getGroupId() {
        return socketGroupId;
    }

    /**
     * Returns the ID of the socket associated with this <tt>SocketInfo</tt>.
     * 
     * @return the ID of the associated socket.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns a string representation of this socket info.
     * 
     * @return a string representation of this socket info.
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AsyncSocket[id=");
        builder.append(id);
        if (isConnected) {
            builder.append(",addr=");
            builder.append(inetAddress);
            builder.append(":");
            builder.append(port);
            builder.append(",local=");
            builder.append(localAddress);
            builder.append(":");
            builder.append(localPort);
        } else {
            builder.append(",unconnected");
        }
        builder.append(",");
        builder.append(bytesRead);
        builder.append(" bytes read,");
        builder.append(bytesWritten);
        builder.append(" bytes written]");
        return builder.toString();
    }
}