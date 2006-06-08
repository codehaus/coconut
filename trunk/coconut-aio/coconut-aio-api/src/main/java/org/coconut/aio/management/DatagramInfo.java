package org.coconut.aio.management;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketAddress;

/**
 * Datagram information. <tt>DatagramInfo</tt> contains the information about
 * a Datagram including:
 * <h4>General datagram information</h4>
 * <ul>
 * <li>Datagram ID.</li>
 * <li>DatagramGroup ID.</li>
 * <li>Timestamp from when this datagram was created (not implemened yet).
 * </li>
 * <li>Timestamp from when this datagram was closed (not implemened yet).</li>
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
public class DatagramInfo implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -5453026246255534412L;

    /** ID of this datagram */
    private final long id;

    /** When this datagram was created */
    private final long creationDate;

    /** When this datagram died */
    private final long deadDate;

    /** Whether or not this datagram is connected */
    private final boolean isConnected;

    /** Whether or not this datagram is bound */
    private final boolean isBound;

    /** The id of the datagram group */
    private final long datagramGroupId;

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
     * Constructor of DatagramInfo created by the AIO subsystem
     * 
     * @param id id of the socket
     * @param creationDate creation date of the socket
     * @param deadDate die date of the socket
     * @param socket The actual socket
     * @param bytesRead The number of bytes read
     * @param bytesWritten The number of bytes written
     */
    public DatagramInfo(final long id, final long groupId, final long creationDate,
        final long deadDate, boolean isBound, boolean isConnected, InetAddress inetAddress,
        SocketAddress localSocketAddress, int port, int localPort,
        SocketAddress remoteSocketAddress, InetAddress localAddress, final long bytesRead,
        final long bytesWritten) {

        this.id = id;
        this.creationDate = creationDate;
        this.deadDate = deadDate;
        this.bytesWritten = bytesWritten;
        this.bytesRead = bytesRead;
        this.datagramGroupId = groupId;

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
     * Returns the number of bytes read from this datagram.
     * 
     * @return Returns the number of bytes read.
     */
    public long getBytesRead() {
        return bytesRead;
    }

    /**
     * Returns the number of bytes written to this datagram.
     * 
     * @return Returns the bytes written
     */
    public long getBytesWritten() {
        return bytesWritten;
    }
    /**
     * Returns when this datagram was created. NOTE: currently returns 0.
     * 
     * @return the difference, measured in milliseconds, between the creation
     *         time and midnight, January 1, 1970 UTC.
     */
    public long getCreationDate() {
        return creationDate;
    }
    /**
     * Returns when this datagram was closed. NOTE: currently returns 0.
     * 
     * @return the difference, measured in milliseconds, between the destruction
     *         time and midnight, January 1, 1970 UTC.
     */
    public long getDeadDate() {
        return deadDate;
    }

    /**
     * Returns the address to which the datagram is connected.
     * 
     * @return the remote IP address to which this datagram is connected, or
     *         <code>null</code> if the datagram is not connected.
     */
    public InetAddress getInetAddress() {
        return inetAddress;
    }
    /**
     * Returns the address of the endpoint this datagram is bound to, or
     * <code>null</code> if it is not bound yet.
     * 
     * @return a <code>SocketAddress</code> representing the local endpoint of
     *         this datagram, or <code>null</code> if it is not bound yet.
     * @see #getLocalAddress()
     * @see #getLocalPort()
     */
    public SocketAddress getLocalSocketAddress() {
        return localSocketAddress;
    }

    /**
     * Returns the remote port to which this datagram is connected.
     * 
     * @return the remote port number to which this datagram is connected, or 0
     *         if the datagram is not connected yet.
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the local address to which this datagram is bound.
     * 
     * @return the local address to which the datagram is bound or <tt>null</tt>
     *         if the socket is not bound yet.
     */
    public InetAddress getLocalAddress() {
        return localAddress;
    }

    /**
     * Returns the address of the endpoint this datagram is connected to, or
     * <code>null</code> if it is unconnected.
     * 
     * @return a <code>SocketAddress</code> reprensenting the remote endpoint
     *         of this datagram, or <code>null</code> if it is not connected
     *         yet.
     * @see #getInetAddress()
     * @see #getPort()
     */
    public SocketAddress getRemoteSocketAddress() {
        return remoteSocketAddress;
    }

    /**
     * Returns the local port to which this datagram is bound.
     * 
     * @return the local port number to which this datagram is bound or -1 if
     *         the datagram is not bound yet.
     */
    public int getLocalPort() {
        return localPort;
    }

    /**
     * Returns the connection state of the datagram.
     * 
     * @return true if the datagram is connected to another machine
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Returns the binding state of the datagram.
     * 
     * @return true if the datagram is bound to an address
     */
    public boolean isBound() {
        return isBound;
    }

    /**
     * Returns the ID of the group this datagram is a member of or -1.
     * 
     * @return the ID of the associated group or -1.
     */
    public long getGroupId() {
        return datagramGroupId;
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
        builder.append("AsyncDatagram[id=");
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