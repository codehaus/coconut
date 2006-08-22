/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.util.AbstractSet;
import java.util.concurrent.Executor;

import org.coconut.aio.monitor.DatagramGroupMonitor;
import org.coconut.aio.spi.AioProvider;
import org.coconut.core.EventHandler;
import org.coconut.core.Offerable;


/**
 * Async Datagram group.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AsyncDatagramGroup extends AbstractSet<AsyncDatagram> {

    /**
     * Retrieves an unique id associated with this group.
     * 
     * @return An unique group id
     */
    public abstract long getId();

    /**
     * Opens a new AsyncDatagramGroup.
     * 
     * @return a new AsyncDatagramGroup
     */
    public static AsyncDatagramGroup open() {
        return AioProvider.provider().openDatagramGroup();
    }

    /**
     * Sets a default DatagramGroupMonitor for all AsyncDatagramGroups. This
     * setting will only effect newly created AsyncDatagramGroups.
     * 
     * @param monitor
     *            the default DatagramGroupMonitor used by all
     *            AsyncDatagramGroups
     */
    public static void setDefaultMonitor(DatagramGroupMonitor monitor) {
        AioProvider.provider().setDefaultMonitor(monitor);
    }

    /**
     * Returns the default DatagramGroupMonitor.
     * 
     * @return the default default DatagramGroupMonitor
     */
    public static DatagramGroupMonitor getDefaultMonitor() {
        return AioProvider.provider().getDefaultDatagramGroupMonitor();
    }

    /**
     * Returns the AsyncDatagramGroup DatagramGroupMonitor.
     * 
     * @return the AsyncDatagramGroup DatagramGroupMonitor
     */
    public abstract DatagramGroupMonitor getMonitor();

    /**
     * Sets the AsyncDatagramGroups monitor.
     * 
     * @param monitor
     *            the monitor
     * @return this group
     */
    public abstract AsyncDatagramGroup setMonitor(DatagramGroupMonitor monitor);

    /**
     * Set the default Executor for all sockets joining this group.
     * 
     * @param executor
     *            the default executor
     * @return the AsyncSocketGroup
     */
    public abstract AsyncDatagramGroup setDefaultExecutor(Executor executor);

    /**
     * Returns the default Executor for all sockets joining this group.
     * 
     * @return the default Executor
     */
    public abstract Executor getDefaultExecutor();

    /**
     * Set the default destination (Offerable) that all sockets joining this
     * group will inherit.
     * 
     * @param offerable
     *            the default destination
     * @return this group
     */
    public abstract AsyncDatagramGroup setDefaultDestination(Offerable< ? super AsyncDatagram.Event> offerable);

    /**
     * Returns the default destination (Offerable) that all sockets joining this
     * group will inherit.
     * 
     * @return the default destination
     */
    public abstract Offerable< ? super AsyncDatagram.Event> getDefaultDestination();

    /**
     * Sets the default ReadHandler.
     * 
     * @param reader
     *            the default ReadHandler
     * 
     * @return this group
     */
    public abstract AsyncDatagramGroup setDefaultReader(ReadHandler<AsyncDatagram> reader);

    /**
     * Returns the default ReadHandler.
     * 
     * @return the default ReaderHandler
     */
    public abstract ReadHandler<AsyncDatagram> getDefaultReader();

    /**
     * Sets a Handler that is called everytime a AsyncDatagram is added to this
     * group.
     * 
     * @param handler
     *            the Handler that is called every time
     * @return this group
     */
    public abstract AsyncDatagramGroup setJoinHandler(EventHandler<AsyncDatagram> handler);

    /**
     * Returns the join handler or <tt>null</tt> is no handler is set.
     * 
     * @return the join handler or <tt>null</tt> is no handler is set
     */
    public abstract EventHandler<AsyncDatagram> getJoinHandler();

    /**
     * Sets a Handler that is called everytime a AsyncDatagram is removed from
     * this group. If the Handler is an instance of ErroneousHandler and
     * AsyncDatagram is removed by the AIO runtime because it was closed due to
     * a failure. The runtime will call the handle method that also accepts a
     * Throwable.
     * 
     * @param handler
     *            the Handler to call when an AsyncDatagram leaves the group
     * @return this group
     */
    public abstract AsyncDatagramGroup setLeaveHandler(EventHandler<AsyncDatagram> handler);

    /**
     * Returns the leave handler or <tt>null</tt> is no handler is set.
     * 
     * @return the leave handler or <tt>null</tt> is no handler is set
     */
    public abstract EventHandler<AsyncDatagram> getLeaveHandler();
   
}