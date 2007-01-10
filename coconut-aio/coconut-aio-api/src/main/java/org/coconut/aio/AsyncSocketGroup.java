/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.util.AbstractSet;
import java.util.concurrent.Executor;

import org.coconut.aio.monitor.SocketGroupMonitor;
import org.coconut.aio.spi.AioProvider;
import org.coconut.core.EventHandler;
import org.coconut.core.Offerable;


/**
 * An AsyncSocketGroup can group a number of related AsyncSocket's.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AsyncSocketGroup extends AbstractSet<AsyncSocket> {

    /**
     * Retrieves an unique id associated with this group.
     * 
     * @return An unique group id
     */
    public abstract long getId();

    /**
     * Opens a new AsyncSocketGroup.
     * 
     * @return a new AsyncSocketGroup
     */
    public static AsyncSocketGroup open() {
        return AioProvider.provider().openSocketGroup();
    }

    /**
     * Sets a default SocketGroupMonitor for all AsyncSocketGroups. This setting
     * will only effect newly created AsyncSocketGroups.
     * 
     * @param monitor
     *            the default SocketGroupMonitor used by all AsyncSocketGroups
     */
    public static void setDefaultMonitor(SocketGroupMonitor monitor) {
        AioProvider.provider().setDefaultMonitor(monitor);
    }

    /**
     * Returns the default SocketGroupMonitor.
     * 
     * @return the default default SocketGroupMonitor
     */
    public static SocketGroupMonitor getDefaultMonitor() {
        return AioProvider.provider().getDefaultSocketGroupMonitor();
    }

    /**
     * Returns the AsyncSocketGroups SocketGroupMonitor.
     * 
     * @return the AsyncSocketGroups SocketGroupMonitor
     */
    public abstract SocketGroupMonitor getMonitor();

    /**
     * Sets the AsyncSocketGroups monitor.
     * 
     * @param monitor
     *            the monitor
     * @return the AsyncSocketGroup
     */
    public abstract AsyncSocketGroup setMonitor(SocketGroupMonitor monitor);

    /**
     * Set the default Executor for all sockets joining this group.
     * 
     * @param executor
     *            the default executor
     * @return the AsyncSocketGroup
     */
    public abstract AsyncSocketGroup setDefaultExecutor(Executor executor);

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
     * @return the AsyncSocketGroup
     */
    public abstract AsyncSocketGroup setDefaultDestination(Offerable< ? super AsyncSocket.Event> offerable);

    /**
     * Returns the default destination (Offerable) that all sockets joining this
     * group will inherit.
     * 
     * @return the default destination
     */
    public abstract Offerable< ? super AsyncSocket.Event> getDefaultDestination();

    /**
     * Sets the default ReadHandler.
     * 
     * @param reader
     *            the default ReadHandler
     * 
     * @return the AsyncSocketGroup
     */
    public abstract AsyncSocketGroup setDefaultReader(ReadHandler<AsyncSocket> reader);

    /**
     * Returns the default ReadHandler.
     * 
     * @return the default ReaderHandler
     */
    public abstract ReadHandler<AsyncSocket> getDefaultReader();

    /**
     * Sets a Handler that is called everytime a AsyncSocket is added to this
     * group.
     * 
     * @param handler
     *            the Handler that is called every time
     * @return this group
     */
    public abstract AsyncSocketGroup setJoinHandler(EventHandler<AsyncSocket> handler);

    /**
     * Returns the join handler or <tt>null</tt> is no handler is set.
     * 
     * @return the join handler or <tt>null</tt> is no handler is set
     */
    public abstract EventHandler<AsyncSocket> getJoinHandler();

    /**
     * Sets a Handler that is called everytime a AsyncSocket is removed from
     * this group. If the Handler is an instance of ErroneousHandler and
     * AsyncSocket is removed by the AIO runtime because it was closed due to a
     * failure. The runtime will call the handle method that also accepts a
     * Throwable.
     * 
     * @param handler
     *            the Handler to call when an AsyncSocket leaves the group
     * @return this group
     */
    public abstract AsyncSocketGroup setLeaveHandler(EventHandler<AsyncSocket> handler);

    /**
     * Returns the leave handler or <tt>null</tt> is no handler is set.
     * 
     * @return the leave handler or <tt>null</tt> is no handler is set
     */
    public abstract EventHandler<AsyncSocket> getLeaveHandler();
}