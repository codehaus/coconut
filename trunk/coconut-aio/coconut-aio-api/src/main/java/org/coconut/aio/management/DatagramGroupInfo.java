/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.management;

import java.io.Serializable;

/**
 * DatagramGroup information. <tt>DatagramGroupInfo</tt> contains the
 * information about a DatagramGroup including:
 * <h4>General group information</h4>
 * <ul>
 * <li>Group ID.</li>
 * <li>Number of datagrams in the group.</li>
 * </ul>
 * 
 * <h4>Execution information</h4>
 * <ul>
 * <li>Number of bytes read by datagrams in the group.</tt>
 * <li>Number of bytes written by datagrams in the group.</tt>
 * </ul>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DatagramGroupInfo implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -4357734734184446946L;

    /** The ID of the group */
    private final long id;

    /** The number of datagrams in the group */
    private final int size;

    /** The number of bytes that datagrams in this group has written */
    private final long bytesWritten;

    /** The number of bytes that datagrams in this group has read */
    private final long bytesRead;

    /**
     * Constructor of DatagramGroupInfo created by the AIO subsystem
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
    public DatagramGroupInfo(final long id, final int size, final long bytesRead, final long bytesWritten) {
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
     * Returns the number of datagrams that are members of this group.
     * 
     * @return Returns the number of datagrams.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the ID of the group associated with this
     * <tt>DatagramGroupInfo</tt>.
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
        builder.append("DatagramGroup[id=");
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