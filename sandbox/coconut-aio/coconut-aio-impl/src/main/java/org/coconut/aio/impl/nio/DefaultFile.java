/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.impl.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.coconut.aio.AioFuture;
import org.coconut.aio.AsyncFile;
import org.coconut.aio.impl.BaseFile;
import org.coconut.aio.impl.ManagedAioProvider;
import org.coconut.aio.impl.util.AioFutureTask;
import org.coconut.aio.monitor.FileMonitor;
import org.coconut.core.Offerable;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class DefaultFile extends BaseFile {

    /** The sockets default offerable */
    private final Offerable<Runnable> completion;
    
    /** A reference to the main handler */
    private final DefaultDiskHandler netHandler;

    /** The sockets channel */
    private volatile RandomAccessFile raf;

    /* A list of outstanding events on the file */
    private final Queue<Runnable> events = new ConcurrentLinkedQueue<Runnable>();

    private volatile File f;

    private static final int MODE_READONLY = 1;
    private static final int MODE_READWRITE = 2;
    private static final int MODE_SYNC = 4;
    private static final int MODE_SYNCMETADATA = 8;
    private volatile int mode;

    private volatile boolean isOpen;

    private final static Executor exe = Executors.newCachedThreadPool();
    DefaultFile(ManagedAioProvider provider, DefaultDiskHandler handler, Offerable<Runnable> requests, long id, FileMonitor monitor,
            Offerable< ? super Event> destination, Executor executor) {
        super(provider, id,executor,destination,monitor);
        this.completion = requests;
        this.netHandler = handler;
    }

    /**
     * @see org.coconut.aio.AsyncFile#isOpen()
     */
    public boolean isOpen() {
        return isOpen;
    }


    /**
     * @see org.coconut.aio.AsyncFile#openFile(java.lang.String, java.lang.String)
     */
    public Opened openFile(String file, String mode) {
        return openFile(file, mode, false);
    }

    /**
     * @see org.coconut.aio.AsyncFile#openFile(java.io.File, java.lang.String)
     */
    public Opened openFile(File file, String mode) {
        return openFile(file, mode, false);
    }

    /**
     * @see org.coconut.aio.AsyncFile#openCreateFile(java.io.File, java.lang.String)
     */
    public Opened openFile(File file, String mode, boolean create) {
        OpenedEvent o = new OpenedEvent(file, create, getMode(mode));
        execute(o);
        return o;
    }

    /**
     * @see org.coconut.aio.AsyncFile#openCreateFile(java.lang.String,
     *      java.lang.String)
     */
    public Opened openFile(String file, String mode, boolean create) {
        OpenedEvent o = new OpenedEvent(file, create, getMode(mode));
        execute(o);
        return o;
    }

    private int getMode(String mode) {
        if (mode.equals("r"))
            return MODE_READONLY;
        else if (mode.equals("rw"))
            return MODE_READWRITE;
        else if (mode.equals("rws"))
            return MODE_SYNC;
        else if (mode.equals("rwd"))
            return MODE_SYNCMETADATA;
        else
            throw new IllegalArgumentException("Unknown mode " + mode);
    }

    private void execute(Runnable r) {
        completion.offer(r);
    }

    /**
     * @see org.coconut.aio.AsyncFile#isWritable()
     */
    public boolean isWritable() {
        return mode > 1;
    }

    /**
     * @param e
     */
    private void closed(IOException e) {
        if (e != null)
            e.printStackTrace();
    }

    private abstract class BaseEvent<V> extends AioFutureTask<V, Event> implements AsyncFile.Event, AioFuture<V, Event> {
        private BaseEvent() {
            super(getDefaultExecutor(), getDefaultDestination());
        }
        public AsyncFile async() {
            return DefaultFile.this;
        }
        public int getColor() {
            return DefaultFile.this.getColor();
        }
        /**
         * @see org.coconut.aio.AsyncServerSocket.Event#setDestination(org.coconut.core.Offerable)
         */
        public void setDestination(Offerable< ? super Event> dest) {
            super.setDest(dest);
        }
        protected void deliverFailure(Offerable< ? super Event> dest, final Throwable t) {
            Event error = new ErroneousEvent() {
                public Throwable getCause() {
                    return t;
                }
                public int getColor() {
                    return DefaultFile.this.getColor();

                }
                public String getMessage() {
                    return t.getMessage();
                }
                public Event getEvent() {
                    return BaseEvent.this;
                }
                public AsyncFile async() {
                    return DefaultFile.this;
                }
            };
            dest.offer(error);
        }
    }

    private class OpenedEvent extends BaseEvent implements AsyncFile.Opened {
        private String name;
        private final int mode;

        private volatile int created;
        private OpenedEvent(String file, boolean create, int mode) {
            this.name = file;
            this.mode = mode;
            if (create)
                created = 1;
        }
        private OpenedEvent(File file, boolean create, int mode) {
            this.mode = mode;
            f = file;
            if (create)
                created = 1;
        }

        /**
         * @see org.coconut.aio.AsyncFile.Opened#getFile()
         */
        public File getFile() {
            return f;
        }
        public boolean isNew() {
            return created == 2;
        }
        /**
         * @see org.coconut.aio.AsyncFile.Opened#getMode()
         */
        public String getMode() {
            if (mode == MODE_READONLY)
                return "r";
            else if (mode == MODE_READWRITE)
                return "rw";
            else if (mode == MODE_SYNC)
                return "rws";
            else
                return "rwd";
        }
        public Object call() throws IOException {
            try {
                if (name != null)
                    f = new File(name);
                if (created > 0 && f.createNewFile())
                    created = 2;
                raf = new RandomAccessFile(f, getMode());
                DefaultFile.this.mode = mode;
                isOpen = true;
            } catch (IOException e) {
                closed(e);
                throw e;
            }

            return null;
        }
    }

    private class ClosedEvent extends BaseEvent implements AsyncFile.Closed {
        private final Throwable cause;

        private ClosedEvent(Throwable cause) {
            this.cause = cause;
        }

        public Throwable getCause() {
            return cause;
        }
        /**
         * @see org.coconut.aio.impl.BaseCallable#call()
         */
        public Object call() throws Exception {
            isOpen = false;
            try {
                if (raf != null)
                    raf.close();
            } catch (IOException e) {
                closed(cause, e);
                throw e;
            }
            return null;
        }
    }
    private class TruncatedEvent extends BaseEvent implements AsyncFile.Truncated {
        private final long size;

        private TruncatedEvent(long size) {
            this.size = size;
        }

        public long getSize() {
            return size;
        }
        /**
         * @see org.coconut.aio.impl.BaseCallable#call()
         */
        public Object call() throws Exception {
            raf.getChannel().truncate(size);
            return null;
        }
    }

//    class TransferedFromEvent extends BaseEvent implements AsyncFile.TransferedFrom {
//        private final ReadableByteChannel src;
//        private final ReadableByteChannel realSrc;
//        private final CountDownLatch latch;
//        private final Runnable completer;
//        private final long count;
//        private final long position;
//        volatile long bytes;
//        private TransferedFromEvent(ReadableByteChannel src, ReadableByteChannel realSrc, CountDownLatch latch,
//                Runnable completer, long position, long count) {
//            this.src = src;
//            this.realSrc = realSrc;
//            this.latch = latch;
//            this.completer = completer;
//            this.count = count;
//            this.position = position;
//        }
//
//        /**
//         * @see org.coconut.aio.impl.BaseCallable#call()
//         */
//        public Object call() throws Exception {
//            return null;
//        }
//
//        /**
//         * @see org.coconut.aio.AsyncFile.TransferedFrom#getBytesTransfered()
//         */
//        public long getBytesTransfered() {
//            return bytes;
//        }
//
//        /**
//         * @see org.coconut.aio.AsyncFile.TransferedFrom#getCount()
//         */
//        public long getCount() {
//            return count;
//        }
//
//        /**
//         * @see org.coconut.aio.AsyncFile.TransferedFrom#getPosition()
//         */
//        public long getPosition() {
//            return position;
//        }
//
//        /**
//         * @see org.coconut.aio.AsyncFile.TransferedFrom#getSrc()
//         */
//        public ReadableByteChannel getSrc() {
//            return src;
//        }
//    }
//    class TransferedToEvent extends BaseEvent implements AsyncFile.TransferedTo {
//        private final WritableByteChannel target;
//        private final WritableByteChannel realTarget;
//        private final CountDownLatch latch;
//        private final Runnable completer;
//        private final long count;
//        private final long position;
//        volatile long bytes;
//        private TransferedToEvent(WritableByteChannel target, WritableByteChannel realTarget, CountDownLatch latch,
//                Runnable completer, long position, long count) {
//            this.target = target;
//            this.realTarget = realTarget;
//            this.count = count;
//            this.completer = completer;
//            this.latch = latch;
//            this.position = position;
//        }
//
//        /**
//         * @see org.coconut.aio.impl.BaseCallable#call()
//         */
//        public Object call() throws Exception {
//            if (latch.getCount() == 0) {
//                bytes = raf.getChannel().transferTo(position, count, realTarget);
//                return Long.valueOf(bytes);
//            } else {
//                System.out.println("wait a bit");
//                return null;
//            }
//        }
//
//        /**
//         * @see org.coconut.aio.AsyncFile.TransferedFrom#getBytesTransfered()
//         */
//        public long getBytesTransfered() {
//            return bytes;
//        }
//
//        /**
//         * @see org.coconut.aio.AsyncFile.TransferedFrom#getCount()
//         */
//        public long getCount() {
//            return count;
//        }
//
//        /**
//         * @see org.coconut.aio.AsyncFile.TransferedFrom#getPosition()
//         */
//        public long getPosition() {
//            return position;
//        }
//
//        /**
//         * @see org.coconut.aio.AsyncFile.TransferedFrom#getSrc()
//         */
//        public WritableByteChannel getTarget() {
//            return target;
//        }
//    }
    /**
     * @see org.coconut.aio.AsyncFile#getFile()
     */
    public File getFile() {
        return f;
    }

    /**
     * @see org.coconut.aio.AsyncFile#close()
     */
    public Closed close() {
        ClosedEvent e = new ClosedEvent(null);
        execute(e);
        return e;
    }

    /**
     * @param cause2
     * @param e
     */
    private void closed(Throwable cause2, IOException e) {
        throw new UnsupportedOperationException(); //TODO fix
    }

//    /**
//     * @see org.coconut.aio.AsyncFile#transferFrom(java.lang.Object, long, long)
//     */
//    public TransferedFrom transferFrom(ReadableByteChannel src, long position, long count) {
//        final TransferedFromEvent tfe;
//        //enqueue it on socket write queue and file read queue
//        CountDownLatch latch = new CountDownLatch(2);
//        if (src instanceof NioSocket) {
//            NioSocket ds = ((NioSocket) src);
//            //Runnable event = ds.createTransferTo(latch);
//            Runnable event = null;
//            tfe = new TransferedFromEvent(src, ds.channel, latch, event, position, count);
//        } else {
//            return null;
//        }
//        execute(tfe);
//        return tfe;
//    }
//
//    /**
//     * @see org.coconut.aio.AsyncFile#transferTo(long, long, java.lang.Object)
//     */
//    public TransferedTo transferTo(long position, long count, WritableByteChannel target) {
//        final TransferedToEvent tte;
//        CountDownLatch latch = new CountDownLatch(1);
//        if (target instanceof NioSocket) {
//            NioSocket ds = (NioSocket) target;
//            Runnable event = ds.createTransferFrom(latch);
//            tte = new TransferedToEvent(target, ds.channel, latch, event, position, count);
//        } else {
//            throw new UnsupportedOperationException();
//        }
//        execute(tte);
//        return tte;
//    }

    /**
     * @see org.coconut.aio.AsyncFile#truncate(long)
     */
    public Truncated truncate(long size) {
        TruncatedEvent event = new TruncatedEvent(size);
        execute(event);
        return event;
    }
}