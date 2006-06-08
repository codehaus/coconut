package org.coconut.aio.management;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public interface SocketGroupMXBean {

    /**
     * Returns all live socket group IDs. Some socket group included in the
     * returned array may have been terminated when this method returns.
     * 
     * @return an array of <tt>long</tt>, each is a socket group ID.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    long[] getAllSocketGroupIds();

    /**
     * Returns the total number bytes read by sockets in the group. If the
     * socket-group does not exists this method returns 0.
     * <p>
     * The total count for a socket-group does not decline if a socket leaves
     * the group. Futhermore, it is only the data being read while the socket is
     * a member of a given group that is included in the total count.
     * <p>
     * The value returned does not include the TCP/IP header but only the actual
     * data transfered.
     * 
     * @param id the ID of the socket or socket-group. Must be positive.
     * @return the total number of bytes read.
     */
    long getBytesRead(long id);

    /**
     * Returns the total number bytes written by sockets in the group. If the
     * socket-group does not exists this method returns 0.
     * <p>
     * The total count for a socket-group does not decline if a socket leaves
     * the group. Futhermore, it is only the data being written while the socket
     * is a member of a given group that is included in the total count.
     * <p>
     * The value returned does not include the TCP/IP header but only the actual
     * data transfered.
     * 
     * @param id the ID of the socket-group. Must be positive.
     * @return the total number of bytes written.
     */
    long getBytesWritten(long id);

    /**
     * Returns the socket-group info for a socket-group of the specified
     * <tt>id</tt>.
     * <p>
     * This method returns a <tt>SocketGroupInfo</tt> object representing the
     * socket-group information for the socket-group of the specified ID. If a
     * socket-group of the given ID is not open or does not exist, this method
     * will return <tt>null</tt>.
     * <p>
     * <b>MBeanServer access </b>: <br>
     * The mapped type of <tt>SocketGroupInfo</tt> is <tt>CompositeData</tt>
     * with attributes as specified in
     * {@link SocketGroupInfo#from SocketGroupInfo}.
     * 
     * @param id the ID of the socket-group. Must be positive.
     * @return a {@link SocketGroupInfo}object for the socket-group of the
     *         given ID; <tt>null</tt> if the socket-group of the given ID is
     *         not open or it does not exist.
     * @throws IllegalArgumentException if <tt>id &lt= 0</tt>.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    SocketGroupInfo getSocketGroupInfo(long id);

    /**
     * Returns the socket-group info for each socket-group whose ID is in the
     * input array <tt>ids</tt>.
     * <p>
     * This method returns an array of the <tt>SocketGroupInfo</tt> objects.
     * If a socket-group of a given ID is not open or does not exist, the
     * corresponding element in the returned array will contain <tt>null</tt>.
     * <p>
     * <b>MBeanServer access </b>: <br>
     * The mapped type of <tt>SocketGroupInfo</tt> is <tt>CompositeData</tt>
     * with attributes as specified in
     * {@link SocketGroupInfo#from SocketGroupInfo}.
     * 
     * @param ids an array of socket-group IDs
     * @return an array of the {@link SocketGroupInfo}objects, each containing
     *         information about a socket-group whose ID is in the corresponding
     *         element of the input array of IDs.
     * @throws IllegalArgumentException if any element in the input array
     *             <tt>ids</tt> is <tt>&lt= 0</tt>.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    long[] getSocketsInGroup(long ids);

    /**
     * Returns the current number of sockets in the group with the specified
     * <tt>id</tt> or 0 if no group with <tt>id</tt> exist.
     * 
     * @param groupId the ID of the socket-group. Must be positive.
     * @return the current number of open sockets.
     */
    int getSize(long groupId);
    /**
     * Returns the total number of socket joins for the group with the specified
     * <tt>id</tt>. If the socket-group does not exist this method return 0.
     * <p>
     * Everytime a socket joins a group the count is incremented so if a socket
     * first joins, then leaves and joins again the socket is counted twice.
     * 
     * @param groupId the ID of the socket-group. Must be positive.
     * @return the total number of socket joins for the group.
     */
    long getTotalSocketCount(long groupId);

    /**
     * Returns the peak count of sockets for the group with the specified
     * <tt>id</tt>.
     * 
     * @param groupId the ID of the socket-group. Must be positive.
     * @return the peak live socket count.
     */
    int getPeakSocketCount(long groupId);

    /**
     * Resets the peak socket count for the group with the specified <tt>id</tt>
     * to the current number of sockets in the group.
     * 
     * @param groupId the ID of the group
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("control").
     * @see #getPeakSocketCount
     * @see #getSocketCount
     */
    void resetPeakSocketCount(long groupId);
}
