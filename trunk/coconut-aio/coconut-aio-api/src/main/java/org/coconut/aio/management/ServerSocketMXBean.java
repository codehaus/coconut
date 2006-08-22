/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.management;

/**
 * The management interface for asynchronous server-sockets for Coconut AIO.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public interface ServerSocketMXBean {

    /**
     * Returns all live server-socket IDs. Some server-socket included in the
     * returned array may have been terminated when this method returns.
     * 
     * @return an array of <tt>long</tt>, each is a server-socket ID.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    long[] getAllServerSocketIds();

    /**
     * Returns the total number of server-sockets opened since the Java virtual
     * machine started.
     * 
     * @return the total number of server-sockets opened.
     */
    long getTotalServerSocketsCount();

    /**
     * Returns the peak open server-socket count since the Java virtual machine
     * started or the peak was reset.
     * 
     * @return the peak live server-socket count.
     */
    int getPeakServerSocketCount();

    /**
     * Returns the current number of open server-sockets.
     * 
     * @return the current number of open server-sockets.
     */
    int getServerSocketCount();

    /**
     * Returns the total number of sockets accepted by all server-sockets since
     * the Java virtual machine started.
     * 
     * @return the total number of server-sockets opened.
     */
    long getTotalAcceptCount();

    /**
     * Returns the total number of sockets accepted by a server-socket with the
     * specified <tt>id</tt>.
     * 
     * @param id the ID of the server-socket. Must be positive.
     * @return the total number of server-sockets opened.
     */
    long getTotalAcceptCount(long id);

    /**
     * Returns the server-socket info for a server-socket of the specified
     * <tt>id</tt>.
     * <p>
     * This method returns a <tt>ServerSocketInfo</tt> object representing the
     * server-socket information for the server-socket of the specified ID. If a
     * server-socket of the given ID is not open or does not exist, this method
     * will return <tt>null</tt>.
     * <p>
     * <b>MBeanServer access </b>: <br>
     * The mapped type of <tt>ServerSocketInfo</tt> is <tt>CompositeData</tt>
     * with attributes as specified in
     * {@link ServerSocketInfo#from ServerSocketInfo}.
     * 
     * @param id the ID of the server-socket. Must be positive.
     * @return a {@link ServerSocketInfo}object for the server-socket of the
     *         given ID; <tt>null</tt> if the server-socket of the given ID is
     *         not open or it does not exist.
     * @throws IllegalArgumentException if <tt>id &lt= 0</tt>.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    ServerSocketInfo getServerSocketInfo(long id);

    /**
     * Returns the server-socket info for each server-socket whose ID is in the
     * input array <tt>ids</tt>.
     * <p>
     * This method returns an array of the <tt>ServerSocketInfo</tt> objects.
     * If a server-socket of a given ID is not open or does not exist, the
     * corresponding element in the returned array will contain <tt>null</tt>.
     * <p>
     * <b>MBeanServer access </b>: <br>
     * The mapped type of <tt>ServerSocketInfo</tt> is <tt>CompositeData</tt>
     * with attributes as specified in
     * {@link ServerSocketInfo#from ServerSocketInfo}.
     * 
     * @param ids an array of server-socket IDs
     * @return an array of the {@link ServerSocketInfo}objects, each containing
     *         information about a server-socket whose ID is in the
     *         corresponding element of the input array of IDs.
     * @throws IllegalArgumentException if any element in the input array
     *             <tt>ids</tt> is <tt>&lt= 0</tt>.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    ServerSocketInfo[] getServerSocketInfo(long[] ids);

    /**
     * Resets the peak server-socket count to the current number of open
     * server-sockets.
     * 
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("control").
     * @see #getPeakServerSocketCount
     * @see #getServerSocketCount
     */
    void resetPeakServerSocketCount();
}