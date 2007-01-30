/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.management;

import java.io.Serializable;

/**
 * SocketGroup information. <tt>SocketGroupInfo</tt> contains the information
 * about a SocketGroup including:
 * <h4>General group information</h4>
 * <ul>
 * <li>Group ID.</li>
 * <li>Number of sockets in the group.</li>
 * </ul>
 * 
 * <h4>Execution information</h4>
 * <ul>
 * <li>Number of bytes read by sockets in the group.</tt>
 * <li>Number of bytes written by sockets in the group.</tt>
 * </ul>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class SocketGroupInfo implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 4171669721484851680L;

    /** The ID of the group */
    private final long id;

    /** The number of sockets in the group */
    private final int size;

    /** The number of bytes that sockets in this group has written */
    private final long bytesWritten;

    /** The number of bytes that sockets in this group has read */
    private final long bytesRead;

    /**
     * Constructor of SocketGroupInfo created by the AIO subsystem
     * 
     * @param id
     *            id of the group
     * @param size
     *            size of the group
     * @param bytesRead
     *            bytes read by members of the group
     * @param bytesWritten
     *            bytes written by members of the group
     */
    public SocketGroupInfo(final long id, final int size, final long bytesRead, final long bytesWritten) {
        this.id = id;
        this.size = size;
        this.bytesWritten = bytesWritten;
        this.bytesRead = bytesRead;
    }

    /**
     * Returns the number of bytes read by members of this group.
     * 
     * @return the number of bytes read.
     */
    public long getBytesRead() {
        return bytesRead;
    }

    /**
     * Returns the number of bytes written by members of this group.
     * 
     * @return Returns the number of bytes written.
     */
    public long getBytesWritten() {
        return bytesWritten;
    }

    /**
     * Returns the number of sockets that are members of this group.
     * 
     * @return Returns the number of sockets.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the ID of the group associated with this <tt>SocketGroupInfo</tt>.
     * 
     * @return the ID of the associated group.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns a string representation of this group info.
     * 
     * @return a string representation of this group info.
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SocketGroup[id=");
        builder.append(id);
        builder.append(", size=");
        builder.append(size);
        builder.append(",");
        builder.append(bytesRead);
        builder.append(" bytes read,");
        builder.append(bytesWritten);
        builder.append(" bytes written]");
        return builder.toString();
    }
}