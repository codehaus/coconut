package org.coconut.aio.management;

/**
 * The management interface for asynchronous sockets for the AIO subsystem.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public interface SocketMXBean {

    /**
     * Returns all live socket IDs. Some socket included in the returned array
     * may have been terminated when this method returns.
     * 
     * @return an array of <tt>long</tt>, each is a socket ID.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    long[] getAllSocketIds();

    /**
     * Returns the total number bytes written on any socket since the Java
     * virtual machine started. The value returned does not include the TCP/IP
     * header but only the actual data transfered.
     * 
     * @return the total number of bytes written.
     */
    long getBytesWritten();

    /**
     * Returns the total number bytes written on the socket with the specified
     * <tt>id</tt>. If the socket is closed or if the socket does not exists
     * this method returns 0.
     * <p>
     * The value returned does not include the TCP/IP header but only the actual
     * data transfered.
     * 
     * @param id the ID of the socket. Must be positive.
     * @return the total number of bytes written on the socket.
     */
    long getBytesWritten(long id);

    /**
     * Returns the total number bytes read on any socket since the Java virtual
     * machine started. The value returned does not include the TCP/IP header
     * but only the actual data transfered.
     * 
     * @return the total number of bytes read.
     */
    long getBytesRead();

    /**
     * Returns the total number bytes read on the socket with the specified
     * <tt>id</tt>. If the socket is closed or if the socket does not exists
     * this method returns 0.
     * <p>
     * The value returned does not include the TCP/IP header but only the actual
     * data transfered.
     * 
     * @param id the ID of the socket. Must be positive.
     * @return the total number of bytes read on the socket.
     */
    long getBytesRead(long id);

    /**
     * Returns the total number of sockets opened since the Java virtual machine
     * started.
     * 
     * @return the total number of sockets opened.
     */
    long getTotalSocketCount();

    /**
     * Returns the peak open socket count since the Java virtual machine started
     * or peak was reset.
     * 
     * @return the peak live socket count.
     */
    int getPeakSocketCount();

    /**
     * Returns the current number of open sockets.
     * 
     * @return the current number of open sockets.
     */
    int getSocketCount();

    /**
     * Returns the total number of sockets that succesfully connected to a
     * remote host since the Java virtual machine started.
     * 
     * @return the total number of connected sockets.
     */
    long getTotalSocketConnectCount();

    /**
     * Returns the socket info for a socket of the specified <tt>id</tt>.
     * <p>
     * This method returns a <tt>SocketInfo</tt> object representing the
     * socket information for the socket of the specified ID. If a socket of the
     * given ID is not open or does not exist, this method will return
     * <tt>null</tt>.
     * <p>
     * <b>MBeanServer access </b>: <br>
     * The mapped type of <tt>SocketInfo</tt> is <tt>CompositeData</tt> with
     * attributes as specified in {@link SocketInfo#from SocketInfo}.
     * 
     * @param id the ID of the socket. Must be positive.
     * @return a {@link SocketInfo}object for the socket of the given ID;
     *         <tt>null</tt> if the socket of the given ID is not open or it
     *         does not exist.
     * @throws IllegalArgumentException if <tt>id &lt= 0</tt>.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    SocketInfo getSocketInfo(long id);

    /**
     * Returns the socket info for each socket whose ID is in the input array
     * <tt>ids</tt>.
     * <p>
     * This method returns an array of the <tt>SocketInfo</tt> objects. If a
     * socket of a given ID is not open or does not exist, the corresponding
     * element in the returned array will contain <tt>null</tt>.
     * <p>
     * <b>MBeanServer access </b>: <br>
     * The mapped type of <tt>SocketInfo</tt> is <tt>CompositeData</tt> with
     * attributes as specified in {@link SocketInfo#from SocketInfo}.
     * 
     * @param ids an array of socket IDs
     * @return an array of the {@link SocketInfo}objects, each containing
     *         information about a socket whose ID is in the corresponding
     *         element of the input array of IDs.
     * @throws IllegalArgumentException if any element in the input array
     *             <tt>ids</tt> is <tt>&lt= 0</tt>.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    SocketInfo[] getSocketInfo(long[] ids);

    /**
     * Resets the peak socket count to the current number of open sockets.
     * 
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("control").
     * @see #getPeakSocketCount
     * @see #getSocketCount
     */
    void resetPeakSocketCount();

}