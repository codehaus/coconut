package org.coconut.aio.monitor;

import org.coconut.aio.AsyncDatagram;
import org.coconut.aio.AsyncDatagramGroup;

/**
 * A <tt>DatagramGroupMonitor</tt> is used for monitoring important datagram
 * group events.
 * 
 * <p>
 * All methods needs to thread-safe as multiple events might be posted
 * concurrently.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DatagramGroupMonitor {

    /**
     * This method is called whenever a new <tt>AsyncDatagramGroup</tt> is
     * opened. This method is only called if the monitor is set as the default
     * monitor for the <tt>AsyncDatagramGroup</tt> class.
     * 
     * @param group
     *            the group that was opened.
     */
    public void opened(AsyncDatagramGroup group) {

    }

    /**
     * Called whenever a datagram socket joins the group.
     * 
     * @param group
     *            the group the socket is joining
     * @param socket
     *            the datagram socket that is joining the group
     */
    public void join(AsyncDatagramGroup group, AsyncDatagram socket) {

    }

    /**
     * Called whenever a datagram socket is leaving a group.
     * 
     * @param group
     *            the group the socket is leaving
     * @param socket
     *            the datagram socket that is leaving this group
     * @param cause
     *            if the datagram socket was closed due to an exception the
     *            cause depicts the exception or <tt>null</tt> if the did not
     *            leave the group due to any error
     */
    public void leave(AsyncDatagramGroup group, AsyncDatagram socket, Throwable cause) {

    }

    /**
     * This method is called whenever the group is finalized. Since
     * <tt>AsyncDatagramGroup</tt> are not explicitly closed this method might
     * never be called.
     * 
     * @param group
     *            the group that is closing
     */
    public void closed(AsyncDatagramGroup group) {

    }
}