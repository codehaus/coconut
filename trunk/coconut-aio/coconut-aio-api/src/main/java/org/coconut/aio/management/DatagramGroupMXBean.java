package org.coconut.aio.management;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
public interface DatagramGroupMXBean {

    /**
     * Returns all live datagram group IDs. Some datagram group included in the
     * returned array may have been terminated when this method returns.
     * 
     * @return an array of <tt>long</tt>, each is a datagram group ID.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    long[] getAllDatagramGroupIds();
    
    /**
     * Returns the datagram-group info for a datagram-group of the specified
     * <tt>id</tt>.
     * <p>
     * This method returns a <tt>DatagramGroupInfo</tt> object representing
     * the datagram-group information for the datagram-group of the specified
     * ID. If a datagram-group of the given ID is not open or does not exist,
     * this method will return <tt>null</tt>.
     * <p>
     * <b>MBeanServer access </b>: <br>
     * The mapped type of <tt>DatagramGroupInfo</tt> is <tt>CompositeData</tt>
     * with attributes as specified in
     * {@link DatagramGroupInfo#from DatagramGroupInfo}.
     * 
     * @param id the ID of the datagram-group. Must be positive.
     * @return a {@link DatagramGroupInfo}object for the datagram-group of the
     *         given ID; <tt>null</tt> if the datagram-group of the given ID
     *         is not open or it does not exist.
     * @throws IllegalArgumentException if <tt>id &lt= 0</tt>.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    DatagramGroupInfo getDatagramGroupInfo(long id);
    
    /**
     * Returns the datagram-group info for each datagram-group whose ID is in
     * the input array <tt>ids</tt>.
     * <p>
     * This method returns an array of the <tt>DatagramGroupInfo</tt> objects.
     * If a datagram-group of a given ID is not open or does not exist, the
     * corresponding element in the returned array will contain <tt>null</tt>.
     * <p>
     * <b>MBeanServer access </b>: <br>
     * The mapped type of <tt>DatagramGroupInfo</tt> is <tt>CompositeData</tt>
     * with attributes as specified in
     * {@link DatagramGroupInfo#from DatagramGroupInfo}.
     * 
     * @param ids an array of datagram-group IDs
     * @return an array of the {@link DatagramGroupInfo}objects, each
     *         containing information about a datagram-group whose ID is in the
     *         corresponding element of the input array of IDs.
     * @throws IllegalArgumentException if any element in the input array
     *             <tt>ids</tt> is <tt>&lt= 0</tt>.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    long[] getDatagramsInGroup(long ids);
    
    /**
     * Returns the current number of datagrams in the group with the specified
     * <tt>id</tt> or 0 if no group with <tt>id</tt> exist.
     * 
     * @param groupId the ID of the datagram-group. Must be positive.
     * @return the current number of open datagrams.
     */
    int getSize(long groupId);
    /**
     * Returns the peak count of datagrams for the group with the specified
     * <tt>id</tt>.
     * 
     * @param groupId the ID of the datagram-group. Must be positive.
     * @return the peak live datagram count.
     */
    int getPeakDatagramCount(long groupId);

    /**
     * Returns the total number bytes written on the datagram socket with the
     * specified <tt>id</tt> or if the <tt>id</tt> refers to a
     * datagram-group the total number of bytes read by datagram in the group.
     * If the datagram is closed or if the datagram/datagram-group does not
     * exists this method returns 0.
     * <p>
     * The total count for a datagram-group does not decline if a socket leaves
     * the group. Futhermore, it is only the data being written while the
     * datagram is a member of a given group that is included in the total
     * count. The value returned does not include the UDP/IP header but only the
     * actual data transfered.
     * 
     * @param id the ID of the dagram. Must be positive.
     * @return the total number of bytes written.
     */
    long getBytesWritten(long id);
   
    /**
     * Returns the total number bytes read on the datagram with the specified
     * <tt>id</tt> or if the id refers to a datagram-group the total number of
     * bytes read by datagrams in the group. If the datagram is closed or if the
     * datagram or datagram group does not exists this method returns 0.
     * <p>
     * The total count for a datagram-group does not decline if a datagram
     * leaves the group. Futhermore, it is only the data being read while the
     * datagram is a member of a given group that is included in the total
     * count. The value returned does not include the TCP/IP header but only the
     * actual data transfered.
     * 
     * @param id the ID of the datagram or datagram-group. Must be positive.
     * @return the total number of bytes read.
     */
    long getBytesRead(long id);
    
    /**
     * Returns the total number of datagram joins for the group with the
     * specified <tt>id</tt>. If the datagram-group does not exist this
     * method return 0.
     * <p>
     * Everytime a datagram joins a group the count is incremented so if a
     * datagram first joins, then leaves and joins again the datagram is counted
     * twice.
     * 
     * @param groupId the ID of the datagram-group. Must be positive.
     * @return the total number of datagram joins for the group.
     */
    long getTotalDatagramCount(long groupId);

    /**
     * Resets the peak datagram count for the group with the specified
     * <tt>id</tt> to the current number of datagrams in the group.
     * 
     * @param groupId the ID of the group
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("control").
     * @see #getPeakDatagramCount
     * @see #getDatagramCount
     */
    void resetPeakDatagramCount(long groupId);
}
