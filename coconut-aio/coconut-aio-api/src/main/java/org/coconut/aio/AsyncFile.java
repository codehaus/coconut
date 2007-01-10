/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Queue;
import java.util.concurrent.Executor;

import org.coconut.aio.monitor.FileMonitor;
import org.coconut.aio.spi.AioProvider;
import org.coconut.core.Colored;
import org.coconut.core.Offerable;


/**
 * AsyncFile Work in progress TODO document
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public abstract class AsyncFile implements Colored {

    public static AsyncFile open() throws IOException {
        return AioProvider.provider().openFile();
    }
    public static AsyncFile open(Offerable< ? super Event> queue) throws IOException {
        if (queue == null) {
            throw new NullPointerException("queue is null");
        }
        return AioProvider.provider().openFile(queue);
    }
    public static AsyncFile open(Executor executor) throws IOException {
        if (executor == null) {
            throw new NullPointerException("executor is null");
        }
        return AioProvider.provider().openFile(executor);
    }
    public static AsyncFile open(Queue< ? super Event> queue) throws IOException {
        if (queue == null) {
            throw new NullPointerException("queue is null");
        }
        return AioProvider.provider().openFile(queue);
    }

    /**
     * Sets the default SocketMonitor. All new Sockets will automatically have
     * this monitor set.
     * 
     * @param monitor
     *            the monitor
     */
    public static void setDefaultMonitor(FileMonitor monitor) {
        AioProvider.provider().setDefaultMonitor(monitor);
    }

    /**
     * Returns the default SocketMonitor
     * 
     * @return the default SocketMonitor
     */
    public static FileMonitor getDefaultMonitor() {
        return AioProvider.provider().getDefaultFileMonitor();
    }

    public abstract boolean isOpen();

    public abstract Opened openFile(File file, String mode);
    public abstract Opened openFile(String file, String mode);
    //public abstract Read openFileAndRead(File file, String mode, Pool<ByteBuffer> buf);
    //public abstract Read openFileAndRead(File file, String mode, Pool<ByteBuffer> buf, int bufSizes);
//    public abstract TransferedTo transferTo(long position, long count, WritableByteChannel target);

    public abstract Truncated truncate(long size);
    
    public abstract boolean isWritable();

    /**
     * Sets the SocketMonitor for this socket.
     * 
     * @param monitor
     *            the monitor
     * @return this socket
     */
    public abstract AsyncFile setMonitor(FileMonitor monitor);

    /**
     * Return the sockets monitor
     * 
     * @return the monitor
     */
    public abstract FileMonitor getMonitor();
    /**
     * Returns the default Offerable or <tt>null</tt> is no Offerable is set.
     * 
     * @return the default Offerable for this file
     */
    public abstract Offerable< ? super Event> getDefaultDestination();

    /**
     * Returns the default Executor or <tt>null</tt> is no Executor is set.
     * 
     * @return the default Executor for this file
     */
    public abstract Executor getDefaultExecutor();

    /**
     * Attaches the given object to this key.
     * 
     * <p>
     * An attached object may later be retrieved via the {@link #attachment
     * attachment} method. Only one object may be attached at a time; invoking
     * this method causes any previous attachment to be discarded. The current
     * attachment may be discarded by attaching <tt>null</tt>.
     * </p>
     * 
     * @param ob
     *            The object to be attached; may be <tt>null</tt>
     * 
     * @return The previously-attached object, if any, otherwise <tt>null</tt>
     */
    public abstract Object attach(Object ob);

    /**
     * Retrieves the current attachment.
     * </p>
     * 
     * @return The object currently attached to this key, or <tt>null</tt> if
     *         there is no attachment
     */
    public abstract Object attachment();

    public abstract long getId();
    
    public abstract File getFile();
    
    public abstract Closed close();
    
    
    /*
     * public abstract Forced force(boolean metaData); public abstract Map
     * map(FileChannel.MapMode mode, long position, long size) ; public abstract
     * Position position() ; public abstract Position position(long newPosition) ;
     * 
     * public abstract Read read(ByteBuffer dst) ; public abstract Read
     * read(ByteBuffer[] dsts) ;
     * 
     * public abstract Read read(ByteBuffer[] dsts, int offset, int length) ;
     * public abstract Read read(ByteBuffer dst, long position) ; public
     * abstract Size size() ;
     * 
     * 
     * public abstract FileLock tryLock() throws IOException; public abstract
     * FileLock tryLock(long position, long size, boolean shared) throws
     * IOException; public abstract Written write(ByteBuffer src); public
     * abstract Written write(ByteBuffer[] srcs); public abstract Written
     * write(ByteBuffer[] srcs, int offset, int length); public abstract Written
     * write(ByteBuffer src, long position);
     */
    /**
     * The base event used for all asynchronous file events.
     */
    public interface Event extends Colored {
        /**
         * Returns the asynchronous file that created this event.
         * 
         * @return the asynchronous file that created this event.
         */
        AsyncFile async();

    }

    /**
     * An event indicating that some problem occured while performing an
     * operation. The particular operation in question can be retrieved by
     * calling getEvent();
     *  
     */
    public interface ErroneousEvent extends Event {
        /**
         * Returns the cause of the error.
         * 
         * @return the cause.
         */
        Throwable getCause();

        /**
         * Returns the message of this event.
         * 
         * @return the message.
         */
        String getMessage();

        /**
         * Returns the event that caused this exception.
         * 
         * @return the event that caused this exception.
         */
        Event getEvent();
    }

    public interface Read extends Event, AioFuture<Long, Event> {
        /**
         * Returns the maximum number of buffers that was accessed.
         * 
         * @return the maximum number of buffers that was accessed.
         */
        int getLength();
        /**
         * Returns offset within the buffer array of the first buffer from which
         * bytes are to be retrieved.
         * 
         * @return the maximum number of buffers that was accessed.
         */
        int getOffset();

        /**
         * Returns the number of bytes read.
         * 
         * @return the number of bytes read
         */
        long getBytesRead();

        /**
         * Returns the position that was read from.
         * 
         * @return the position that was read from.
         */
        long getPosition();
        /**
         * Returns the buffers that was read.
         * 
         * @return the buffers that was read
         */
        ByteBuffer[] getDsts();
    }

    public interface Written extends Event, AioFuture<Long, Event> {
        /**
         * Returns the maximum number of buffers that was accessed.
         * 
         * @return the maximum number of buffers that was accessed.
         */
        int getLength();
        /**
         * Returns offset within the buffer array of the first buffer from which
         * bytes are to be retrieved.
         * 
         * @return the maximum number of buffers that was accessed.
         */
        int getOffset();

        /**
         * Returns the position that was read from.
         * 
         * @return the position that was read from.
         */
        long getPosition();

        /**
         * Returns the number of bytes written.
         * 
         * @return the number of bytes written
         */
        long getBytesWritten();

        /**
         * Returns the buffers that was written.
         * 
         * @return the buffers that was written
         */
        ByteBuffer[] getSrcs();
    }

    public interface Truncated extends AsyncFile.Event {
        public long getSize();
    }

    public interface Opened extends AsyncFile.Event, AioFuture {
        public File getFile();
        public String getMode();
    }

    public interface NewPosition extends AsyncFile.Event {
        public long getNewPosition();
    }

    public interface Size extends AsyncFile.Event {
        public long getSize();
    }

    public interface Position extends AsyncFile.Event {
        public long getPosition();
    }

    public interface MapFile extends AsyncFile.Event {
        public MappedByteBuffer getBuffer();
        public FileChannel.MapMode getMode();
        public long getPosition();
        public long getSize();
    }
    public interface Forced extends AsyncFile.Event {
        public boolean getMetadata();
    }

    public interface Lock extends AsyncFile.Event {
        public FileLock getLock();
        public long getPosition();
        public boolean isShared();
        public long getSize();
    }

    /**
     * A Closed Future.
     *  
     */
    public interface Closed extends Event, AioFuture {
        /**
         * Returns the cause of the close or <tt>null</tt> if the file was
         * closed by explicit by the user.
         * 
         * @return the cause.
         */
        Throwable getCause();
    }

//    public interface TransferedFrom extends Event, AioFuture {
//        public long getBytesTransfered();
//        public long getCount();
//        public long getPosition();
//        public ReadableByteChannel getSrc();
//    }
//    public interface TransferedTo extends Event, AioFuture {
//        public long getBytesTransfered();
//        public long getCount();
//        public long getPosition();
//        public WritableByteChannel getTarget();
//    }
}