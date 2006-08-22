/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.management;

import java.io.Serializable;

/**
 * File information. <tt>FileInfo</tt> contains the information about a File
 * including:
 * <h4>General file information</h4>
 * <ul>
 * <li>File ID.</li>
 * <li>File: An abstract representation of file and directory pathnames.</li>
 * </ul>
 * 
 * <h4>Other information</h4>
 * <ul>
 * <li>Number of bytes read.</li>
 * <li>Number of bytes written.</li>
 * </ul>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class FileInfo implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -7032776861086689359L;

    /** The id of the file */
    private final long id;

    /** The file path */
    private final String path ;

    /** The number of bytes that has been written to the file */
    private final long bytesWritten;

    /** The number of bytes that has been read from the file */
    private final long bytesRead;

    /**
     * @param id
     *            the id of the file
     * @param path
     *            the path used for opening the asynchronous file
     * @param bytesWritten
     *            number of bytes written
     * @param bytesRead
     *            number of bytes read
     */
    public FileInfo(final long id, final String path, final long bytesWritten, final long bytesRead) {
        this.id = id;
        this.path = path;
        this.bytesWritten = bytesWritten;
        this.bytesRead = bytesRead;
    }

    /**
     * Returns the number of bytes read from this file.
     * 
     * @return Returns the number of bytes read.
     */
    public long getBytesRead() {
        return bytesRead;
    }
    /**
     * Return the number of bytes written to this file.
     * 
     * @return Returns the number of bytes written.
     */
    public long geBytesWritten() {
        return bytesWritten;
    }

    /**
     * Returns an abstract representation of the files pathname.
     * 
     * @return an abstract representation of the files pathname.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the ID of the file associated with this <tt>FileInfo</tt>.
     * 
     * @return the ID of the associated file.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns a string representation of this file info.
     * 
     * @return a string representation of this file info.
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AsyncFile[id=");
        builder.append(id);
        builder.append(", file=");
        builder.append(path);
        builder.append(",");
        builder.append(bytesRead);
        builder.append(" bytes read,");
        builder.append(bytesWritten);
        builder.append(" bytes written]");
        return builder.toString();
    }
}