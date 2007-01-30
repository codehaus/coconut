/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.management;

/**
 * The management interface for asynchronous files for the AIO subsystem.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface FileMXBean {

    /**
     * @return Returns the id of all open files.
     */
    long[] getAllIds();

    /**
     * @return Returns the total number of bytes written from any file.
     */
    long getTotalBytesWritten();

    /**
     * @return Returns the total number of bytes read from any file.
     */
    long getTotalBytesRead();

    /**
     * @return Returns the info from the file.
     */
    FileInfo getFileInfo(long id);

    /**
     * @return Returns the info of a number of files.
     */
    FileInfo[] getFileInfo(long[] ids);

    /**
     * This method returns the number of bytes that has been written to a
     * particular file. If no file exist with the id this method returns 0.
     * 
     * @param id
     *            the unique id of the file
     * @return Returns the number of bytes read for a file.
     */
    long getBytesWritten(long id);

    /**
     * This method returns the number of bytes that has been read on a
     * particular file. If no file exist with the d this method returns 0.
     * 
     * @param id
     *            the unique id of the file
     * @return Returns the number of bytes read for a file.
     */
    long getBytesRead(long id);
    
    
    /**
     * Returns the total number of files opened since the Java virtual machine
     * started.
     * 
     * @return the total number of files opened.
     */
    long getTotalFileCount();

    /**
     * Returns the peak open file count since the Java virtual machine started
     * or peak was reset.
     * 
     * @return the peak live file count.
     */
    int getPeakFileCount();


    /**
     * Returns the current number of open files.
     * 
     * @return the current number of open files.
     */
    int getFileCount();

    /**
     * Resets the peak file count to the current number of open files.
     * 
     * @throws java.lang.SecurityException
     *             if a security manager exists and the caller does not have
     *             ManagementPermission("control").
     * 
     * @see #getPeakFileCount
     * @see #getSocketCount
     */
    void resetPeakFileCount();
}