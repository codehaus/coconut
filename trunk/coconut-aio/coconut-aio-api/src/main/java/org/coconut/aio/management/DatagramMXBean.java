package org.coconut.aio.management;

/**
 * The management interface for asynchronous datagrams for the AIO subsystem.
 * Most of these methods should be changed to report the number of datagrams
 * received and not the number of datagrams opened.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public interface DatagramMXBean {

    /**
     * Returns all live datagram socket IDs. Some datagram socket included in
     * the returned array may have been terminated when this method returns.
     * 
     * @return an array of <tt>long</tt>, each is a socket ID.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    long[] getAllDatagramIds();


    /**
     * Returns the total number bytes written on any datagram socket since the
     * Java virtual machine started. The value returned does not include the
     * UDP/IP header but only the actual data transfered.
     * 
     * @return the total number of bytes written.
     */
    long getBytesWritten();

    /**
     * Returns the total number bytes written on the datagram socket with the
     * specified <tt>id</tt>. If the datagram is closed or if the datagram
     * does not exists this method returns 0.
     * <p>
     * The value returned does not include the UDP/IP header but only the actual
     * data transfered.
     * 
     * @param id the ID of the dagram. Must be positive.
     * @return the total number of bytes written.
     */
    long getBytesWritten(long id);

    /**
     * Returns the total number bytes read on any datagram socket since the Java
     * virtual machine started. The value returned does not include the UDP/IP
     * header but only the actual data transfered.
     * 
     * @return the total number of bytes read.
     */
    long getBytesRead();

    /**
     * Returns the total number bytes read on the datagram with the specified
     * <tt>id</tt>. If the datagram is closed or if the datagram does not
     * exists this method returns 0.
     * <p>
     * The value returned does not include the TCP/IP header but only the actual
     * data transfered.
     * 
     * @param id the ID of the datagram. Must be positive.
     * @return the total number of bytes read on this datagram.
     */
    long getBytesRead(long id);

    /**
     * Returns the total number of datagram opened since the Java virtual
     * machine started.
     * 
     * @return the total number of datagram opened.
     */
    long getTotalDatagramCount();

    /**
     * Returns the peak open datagram count since the Java virtual machine
     * started or peak was reset.
     * 
     * @return the peak live datagram count.
     */
    int getPeakDatagramCount();

    /**
     * Returns the current number of open datagrams.
     * 
     * @return the current number of open datagrams.
     */
    int getDatagramCount();

    /**
     * Returns the datagram info for a datagram of the specified <tt>id</tt>.
     * <p>
     * This method returns a <tt>DatagramInfo</tt> object representing the
     * datagram information for the datagram of the specified ID. If a datagram
     * of the given ID is not open or does not exist, this method will return
     * <tt>null</tt>.
     * <p>
     * <b>MBeanServer access </b>: <br>
     * The mapped type of <tt>DatagramInfo</tt> is <tt>CompositeData</tt>
     * with attributes as specified in {@link DatagramInfo#from DatagramInfo}.
     * 
     * @param id the ID of the datagram. Must be positive.
     * @return a {@link DatagramInfo}object for the datagram of the given ID;
     *         <tt>null</tt> if the datagram of the given ID is not open or it
     *         does not exist.
     * @throws IllegalArgumentException if <tt>id &lt= 0</tt>.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    DatagramInfo getDatagramInfo(long id);

    /**
     * Returns the datagram info for each datagram whose ID is in the input
     * array <tt>ids</tt>.
     * <p>
     * This method returns an array of the <tt>DatagramInfo</tt> objects. If a
     * datagram of a given ID is not open or does not exist, the corresponding
     * element in the returned array will contain <tt>null</tt>.
     * <p>
     * <b>MBeanServer access </b>: <br>
     * The mapped type of <tt>SocketInfo</tt> is <tt>CompositeData</tt> with
     * attributes as specified in {@link DatagramInfo#from DatagramInfo}.
     * 
     * @param ids an array of datagram IDs
     * @return an array of the {@link DatagramInfo}objects, each containing
     *         information about a datagram whose ID is in the corresponding
     *         element of the input array of IDs.
     * @throws IllegalArgumentException if any element in the input array
     *             <tt>ids</tt> is <tt>&lt= 0</tt>.
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("monitor").
     */
    DatagramInfo[] getDatagramInfo(long[] ids);

    /**
     * Resets the peak datagram count to the current number of open datagrams.
     * 
     * @throws java.lang.SecurityException if a security manager exists and the
     *             caller does not have ManagementPermission("control").
     * @see #getPeakDatagramCount
     * @see #getDatagramCount
     */
    void resetPeakDatagramCount();
}