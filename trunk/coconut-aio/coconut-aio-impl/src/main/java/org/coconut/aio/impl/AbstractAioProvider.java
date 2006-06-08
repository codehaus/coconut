package org.coconut.aio.impl;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.coconut.aio.AsyncDatagram;
import org.coconut.aio.AsyncFile;
import org.coconut.aio.AsyncServerSocket;
import org.coconut.aio.AsyncSocket;
import org.coconut.aio.management.FileMXBean;
import org.coconut.aio.monitor.DatagramGroupMonitor;
import org.coconut.aio.monitor.DatagramMonitor;
import org.coconut.aio.monitor.FileMonitor;
import org.coconut.aio.monitor.ServerSocketMonitor;
import org.coconut.aio.monitor.SocketGroupMonitor;
import org.coconut.aio.monitor.SocketMonitor;
import org.coconut.aio.spi.AioErrorHandler;
import org.coconut.aio.spi.AioProvider;
import org.coconut.core.Offerable;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public abstract class AbstractAioProvider extends AioProvider {

    private final Lock lock = new ReentrantLock();
    private final AtomicLong idGenerator = new AtomicLong();

    volatile ServerSocketMonitor defaultServerSocketMonitor;
    volatile SocketGroupMonitor defaultSocketGroupMonitor;
    volatile SocketMonitor defaultSocketMonitor;
    volatile DatagramGroupMonitor defaultDatagramGroupMonitor;
    volatile DatagramMonitor defaultDatagramMonitor;
    volatile FileMonitor defaultFileMonitor;


    private AioStatus diskStatus = AioStatus.NOT_STARTED;
    private AioStatus netStatus = AioStatus.NOT_STARTED;

    private volatile AioErrorHandler defaultHandler;

    protected abstract void startupDisk() throws Exception;

    protected abstract void startupNet() throws Exception;

    protected abstract void stopDisk();

    protected abstract void stopNet();

    protected abstract BaseSocket openAsyncSocket(Offerable< ? super AsyncSocket.Event> queue,
        Executor executor) throws IOException;

    protected abstract BaseServerSocket openAsyncServerSocket(
        Offerable< ? super AsyncServerSocket.Event> queue, Executor executor) throws IOException;

    protected abstract BaseFile openAsyncFile(Offerable< ? super AsyncFile.Event> queue,
        Executor executor);

    protected abstract BaseDatagram openAsyncDatagram(
        Offerable< ? super AsyncDatagram.Event> queue, Executor executor) throws IOException;



    protected final long getNextId() {
        return idGenerator.incrementAndGet();
    }

    private void checkNetStarted() {
        try {
            lock.lock();
            if (netStatus == AioStatus.NOT_STARTED) {
                try {
                    startupNet();
                    // checkJMXStarted();
                    netStatus = AioStatus.RUNNING;
                } catch (Exception ioe) {
                    throw new IllegalStateException("Could not start AIO layer", ioe);
                }
            } else if (netStatus == AioStatus.DEAD) {
                throw new IllegalStateException("Coconut-AIO layer closed");
            }
        } finally {
            lock.unlock();
        }
    }
    private void checkFileStarted() {
        try {
            lock.lock();
            if (diskStatus == AioStatus.NOT_STARTED) {
                try {
                    startupDisk();
                    // checkJMXStarted();
                    diskStatus = AioStatus.RUNNING;
                } catch (Exception ioe) {
                    throw new IllegalStateException("Could not start AIO layer", ioe);
                }
            } else if (diskStatus == AioStatus.DEAD) {
                throw new IllegalStateException("Coconut-AIO layer closed");
            }
        } finally {
            lock.unlock();
        }
    }

    protected void finalize() {
        shutdown();
    }
    public void shutdown() {
        try {
            lock.lock();
            if (netStatus == AioStatus.RUNNING) {
                stopNet();
            }
            if (diskStatus == AioStatus.RUNNING) {
                stopDisk();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * @throws IOException
     * @see org.coconut.aio.spi.AsyncFactory#openServerSocket()
     */
    public AsyncServerSocket openServerSocket() throws IOException {
        checkNetStarted();
        return openAsyncServerSocket(null, null);
    }

    /**
     * @see org.coconut.aio.spi.AsyncFactory#openDatagram()
     */
    public AsyncDatagram openDatagram() throws IOException {
        checkNetStarted();
        return openAsyncDatagram(null, null);
    }

    /**
     * @see org.coconut.aio.spi.AsyncFactory#openFile()
     */
    public AsyncFile openFile() {
        checkFileStarted();
        return openAsyncFile(null, null);
    }

    /**
     * @throws IOException
     * @see org.coconut.aio.spi.AsyncFactory#openSocket()
     */
    public AsyncSocket openSocket() throws IOException {
        checkNetStarted();
        return openAsyncSocket(null, null);
    }

    /**
     * @throws IOException
     * @see org.coconut.aio.spi.AsyncFactory#openServerSocket(org.coconut.core.Offerable)
     */
    public AsyncServerSocket openServerSocket(
        Offerable< ? super AsyncServerSocket.Event> destination) throws IOException {
        checkNetStarted();
        return openAsyncServerSocket(destination, null);
    }

    /**
     * @see org.coconut.aio.spi.AsyncFactory#openDatagram(org.coconut.core.Offerable)
     */
    public AsyncDatagram openDatagram(Offerable< ? super AsyncDatagram.Event> destination)
        throws IOException {
        checkNetStarted();
        return openAsyncDatagram(destination, null);
    }

    /**
     * @see org.coconut.aio.spi.AsyncFactory#openFile(org.coconut.core.Offerable)
     */
    public AsyncFile openFile(Offerable< ? super AsyncFile.Event> destination) {
        checkFileStarted();
        return openAsyncFile(destination, null);
    }

    /**
     * @throws IOException
     * @see org.coconut.aio.spi.AsyncFactory#openSocket(org.coconut.core.Offerable)
     */
    public AsyncSocket openSocket(Offerable< ? super AsyncSocket.Event> destination)
        throws IOException {
        checkNetStarted();
        return openAsyncSocket(destination, null);
    }

    /**
     * @throws IOException
     * @see org.coconut.aio.spi.AsyncFactory#openServerSocket(java.util.concurrent.Executor)
     */
    public AsyncServerSocket openServerSocket(Executor executor) throws IOException {
        checkNetStarted();
        return openAsyncServerSocket(null, executor);
    }

    /**
     * @see org.coconut.aio.spi.AsyncFactory#openDatagram(java.util.concurrent.Executor)
     */
    public AsyncDatagram openDatagram(Executor executor) throws IOException {
        checkNetStarted();
        return openAsyncDatagram(null, executor);
    }

    /**
     * @see org.coconut.aio.spi.AsyncFactory#openFile(java.util.concurrent.Executor)
     */
    public AsyncFile openFile(Executor executor) {
        checkFileStarted();
        return openAsyncFile(null, executor);
    }

    /**
     * @throws IOException
     * @see org.coconut.aio.spi.AsyncFactory#openSocket(java.util.concurrent.Executor)
     */
    public AsyncSocket openSocket(Executor executor) throws IOException {
        checkNetStarted();
        return openAsyncSocket(null, executor);
    }

    /**
     * @throws IOException
     * @see org.coconut.aio.spi.AsyncFactory#openServerSocket(java.util.Queue)
     */
    public AsyncServerSocket openServerSocket(final Queue< ? super AsyncServerSocket.Event> queue)
        throws IOException {
        checkNetStarted();
        return openAsyncServerSocket(wrapQueue(queue), null);
    }

    /**
     * @throws IOException
     * @see org.coconut.aio.spi.AsyncFactory#openDatagram(java.util.Queue)
     */
    public AsyncFile openFile(final Queue< ? super AsyncFile.Event> queue) throws IOException {
        checkFileStarted();
        return openAsyncFile(wrapQueue(queue), null);
    }

    /**
     * @throws IOException
     * @see org.coconut.aio.spi.AsyncFactory#openDatagram(java.util.Queue)
     */
    public AsyncDatagram openDatagram(final Queue< ? super AsyncDatagram.Event> queue)
        throws IOException {
        checkNetStarted();
        return openAsyncDatagram(wrapQueue(queue), null);
    }

    /**
     * @throws IOException
     * @see org.coconut.aio.spi.AsyncFactory#openSocket(java.util.Queue)
     */
    public AsyncSocket openSocket(final Queue< ? super AsyncSocket.Event> queue) throws IOException {
        checkNetStarted();
        return openAsyncSocket(wrapQueue(queue), null);
    }

    /**
     * @see org.coconut.aio.spi.AioProvider#getFileMXBean()
     */
    public FileMXBean getFileMXBean() {
        return null;
    }

    /**
     * @see org.coconut.aio.spi.AioProvider#setErrorHandler(org.coconut.aio.spi.AioErrorHandler)
     */
    public void setErrorHandler(AioErrorHandler< ? > handler) {
        this.defaultHandler = handler;
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#getErrorHandler()
     */
    public AioErrorHandler< ? > getErrorHandler() {
        return defaultHandler;
    }
    
    void unhandledException(Object o, String msg, Throwable cause) {

    }
    /**
     * @see org.coconut.aio.spi.AioProvider#getDefaultDatagramGroupMonitor()
     */
    public DatagramGroupMonitor getDefaultDatagramGroupMonitor() {
        return defaultDatagramGroupMonitor;
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#getDefaultDatagramMonitor()
     */
    public DatagramMonitor getDefaultDatagramMonitor() {
        return defaultDatagramMonitor;
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#getDefaultFileMonitor()
     */
    public FileMonitor getDefaultFileMonitor() {
        return defaultFileMonitor;
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#getDefaultServerSocketMonitor()
     */
    public ServerSocketMonitor getDefaultServerSocketMonitor() {
        return defaultServerSocketMonitor;
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#getDefaultSocketGroupMonitor()
     */
    public SocketGroupMonitor getDefaultSocketGroupMonitor() {
        return defaultSocketGroupMonitor;
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#getDefaultSocketMonitor()
     */
    public SocketMonitor getDefaultSocketMonitor() {
        return defaultSocketMonitor;
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#setDefaultMonitor(org.coconut.aio.monitor.DatagramGroupMonitor)
     */
    public void setDefaultMonitor(DatagramGroupMonitor m) {
        defaultDatagramGroupMonitor = m;
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#setDefaultMonitor(org.coconut.aio.monitor.DatagramMonitor)
     */
    public void setDefaultMonitor(DatagramMonitor m) {
        defaultDatagramMonitor = m;
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#setDefaultMonitor(org.coconut.aio.monitor.FileMonitor)
     */
    public void setDefaultMonitor(FileMonitor m) {
        defaultFileMonitor = m;
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#setDefaultMonitor(org.coconut.aio.monitor.ServerSocketMonitor)
     */
    public void setDefaultMonitor(ServerSocketMonitor m) {
        defaultServerSocketMonitor = m;
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#setDefaultMonitor(org.coconut.aio.monitor.SocketGroupMonitor)
     */
    public void setDefaultMonitor(SocketGroupMonitor m) {
        defaultSocketGroupMonitor = m;
    }
    /**
     * @see org.coconut.aio.spi.AioProvider#setDefaultMonitor(org.coconut.aio.monitor.SocketMonitor)
     */
    public void setDefaultMonitor(SocketMonitor m) {
        defaultSocketMonitor = m;
    }

    private static <E> Offerable<E> wrapQueue(final Queue< ? super E> queue) {
        return new Offerable<E>() {
            public boolean offer(E element) {
                return queue.offer(element);
            }
        };
    }

    private enum AioStatus {
        NOT_STARTED, RUNNING, DEAD;
    }
}
