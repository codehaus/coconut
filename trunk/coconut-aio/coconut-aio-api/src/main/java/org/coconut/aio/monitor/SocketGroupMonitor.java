package org.coconut.aio.monitor;

import org.coconut.aio.AsyncSocket;
import org.coconut.aio.AsyncSocketGroup;

/**
 * A <tt>SocketGroupMonitor</tt> is used for monitoring important socket group
 * events.
 * 
 * <p>
 * All methods needs to thread-safe as multiple events might be posted
 * concurrently
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class SocketGroupMonitor {

    /**
     * This method is called whenever a new <tt>AsyncSocketGroup</tt> is
     * opened. This method is only called if the monitor is set as the default
     * monitor for the <tt>AsyncSocketGroup</tt> class.
     * 
     * @param group
     *            the group that was opened.
     */
    public void opened(AsyncSocketGroup group) {

    }

    /**
     * Called whenever a socket joins the group.
     * 
     * @param group
     *            the group the socket is joining
     * @param socket
     *            the socket that is joining the group
     */
    public void join(AsyncSocketGroup group, AsyncSocket socket) {

    }

    /**
     * Called whenever a socket is leaving a group.
     * 
     * @param group
     *            the group the socket is leaving
     * @param socket
     *            the socket that is leaving this group
     * @param cause
     *            if the socket was closed due to an exception the cause depicts
     *            the exception or <tt>null</tt> if the did not leave the
     *            group due to any error
     */
    public void leave(AsyncSocketGroup group, AsyncSocket socket, Throwable cause) {

    }

    /**
     * This method is called whenever the group is finalized. Since
     * <tt>AsyncSocketGroup</tt> are not explicitly closed this method might
     * never be called.
     * 
     * @param group
     *            the group that is closing
     */
    public void closed(AsyncSocketGroup group) {

    }
}