/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.impl;

import java.util.concurrent.Executor;

import org.coconut.aio.AsyncFile;
import org.coconut.aio.monitor.FileMonitor;
import org.coconut.core.EventHandler;
import org.coconut.core.Offerable;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public abstract class BaseFile extends AsyncFile {

    // -- General final socket fields --

    /** An unique id for the file */
    private final long id;

    /** The AioProvider used for this file */
    private final ManagedAioProvider provider;

    /** An atomic refence to a future used for closing down the socket */
    //private final AtomicReference<AsyncFile.Closed> closeFuture = new AtomicReference<AsyncFile.Closed>();

    /** The number of commited bytes for writing */
    //private final AtomicLong bytesWritten = new AtomicLong();

    /** The number of commited bytes for writing */
    //private final AtomicLong bytesRead = new AtomicLong();

    // -- General volatile user-mod socket fields --

    /** The sockets default executor */
    volatile Executor defaultExecutor;

    /** The sockets default offerable */
    volatile Offerable< ? super Event> defaultDestination;

    /** A user defined attachement */
    private volatile Object attachment;

    /** A user defined close handler */
    private volatile EventHandler<AsyncFile> closeHandler;

    /** A user defined server-socket monitor */
    private volatile FileMonitor monitor;

    /**
     * @param id
     * @param provider
     * @param defaultExecutor
     * @param defaultDestination
     */
    public BaseFile(final ManagedAioProvider provider, final long id, Executor defaultExecutor,
        Offerable< ? super Event> defaultDestination, FileMonitor monitor) {
        super();
        this.id = id;
        this.provider = provider;
        this.defaultExecutor = defaultExecutor;
        this.defaultDestination = defaultDestination;
        this.monitor = monitor;
    }

    
    /**
     * @see org.coconut.aio.AsyncFile#getId()
     */
    public long getId() {
        return id;
    }

    /**
     * @see org.coconut.aio.AsyncFile#getColor()
     */
    public int getColor() {
        return (int) (id ^ (id >>> 32));
    }

    /**
     * @see org.coconut.aio.AsyncFile#getDefaultSink()
     */
    public Offerable< ? super Event> getDefaultDestination() {
        return defaultDestination;
    }

    /**
     * @see org.coconut.aio.AsyncFile#getDefaultExecutor()
     */
    public Executor getDefaultExecutor() {
        return defaultExecutor;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#setMonitor(org.coconut.aio.monitor.SocketMonitor)
     */
    public AsyncFile setMonitor(FileMonitor monitor) {
        this.monitor = monitor;
        return this;
    }

    /**
     * @see org.coconut.aio.AsyncSocket#getMonitor()
     */
    public FileMonitor getMonitor() {
        return monitor;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#attach(java.lang.Object)
     */
    public Object attach(Object attachment) {
        Object o = this.attachment;
        this.attachment = attachment;
        return o;
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#getAttachment()
     */
    public Object attachment() {
        return attachment;
    }

}
