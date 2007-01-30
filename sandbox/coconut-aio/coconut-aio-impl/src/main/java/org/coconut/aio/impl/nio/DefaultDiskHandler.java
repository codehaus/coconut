/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.impl.nio;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.aio.management.FileInfo;
import org.coconut.aio.management.FileMXBean;
import org.coconut.core.Offerable;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class DefaultDiskHandler {

    private final ExecutorService e = Executors.newCachedThreadPool();

    final Offerable<Runnable> requests;

    private final ConcurrentHashMap<Long, DefaultFile> files = new ConcurrentHashMap<Long, DefaultFile>();

    private final AtomicLong totalFiles = new AtomicLong();

    private final AtomicLong bytesWrittenToFiles = new AtomicLong();
    private final AtomicLong bytesReadFromFiles = new AtomicLong();

    private final AtomicInteger peakFileCount = new AtomicInteger();
    private final FileMXBean fileBean = new DefaultMXFileBean();
    private final NioAioProvider provider;

    public DefaultDiskHandler(NioAioProvider provider) {
        this.provider = provider;

        requests = new Offerable<Runnable>() {
            public boolean offer(Runnable r) {
                e.execute(r);
                return true;
            }
        };
    }
    /**
     *  
     */
    public void start() {

    }

    FileMXBean getFileMXBean() {
        return fileBean;
    }

    private static class DefaultMXFileBean implements FileMXBean {

        /**
         * @see org.coconut.aio.management.FileMXBean#getAllIds()
         */
        public long[] getAllIds() {
            throw new UnsupportedOperationException(); 
        }

        /**
         * @see org.coconut.aio.management.FileMXBean#getTotalBytesWritten()
         */
        public long getTotalBytesWritten() {
            throw new UnsupportedOperationException(); 
        }

        /**
         * @see org.coconut.aio.management.FileMXBean#getTotalBytesRead()
         */
        public long getTotalBytesRead() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.aio.management.FileMXBean#getFileInfo(long)
         */
        public FileInfo getFileInfo(long id) {
            throw new UnsupportedOperationException(); 
        }

        /**
         * @see org.coconut.aio.management.FileMXBean#getFileInfo(long[])
         */
        public FileInfo[] getFileInfo(long[] ids) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.aio.management.FileMXBean#getBytesWritten(long)
         */
        public long getBytesWritten(long id) {
            throw new UnsupportedOperationException(); 
        }

        /**
         * @see org.coconut.aio.management.FileMXBean#getBytesRead(long)
         */
        public long getBytesRead(long id) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.aio.management.FileMXBean#getTotalFileCount()
         */
        public long getTotalFileCount() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.aio.management.FileMXBean#getPeakFileCount()
         */
        public int getPeakFileCount() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.aio.management.FileMXBean#getFileCount()
         */
        public int getFileCount() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.aio.management.FileMXBean#resetPeakFileCount()
         */
        public void resetPeakFileCount() {
            throw new UnsupportedOperationException(); 
        }

    }

    /**
     *  
     */
    public void shutdown() {

    }
}