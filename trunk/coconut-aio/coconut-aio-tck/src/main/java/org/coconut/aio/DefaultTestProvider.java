package org.coconut.aio;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.coconut.aio.spi.AioProvider;
import org.coconut.aio.spi.AsyncFactory;
import org.coconut.core.Offerable;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class DefaultTestProvider implements AsyncFactory {

    private final AioProvider fac;

    // used for finding places where we forgot to call .close();
    public DefaultTestProvider() {
        AioProvider.setProperty("coconut.aio.jmx.InstanceAlreadyExistsException", "true");
        fac = AioProvider.provider();
    }

    private static final boolean DEBUG = false;
    final Map<Long, Throwable> hm = new ConcurrentHashMap<Long, Throwable>();

    public boolean equals(Object obj) {
        return fac.equals(obj);
    }
    public int hashCode() {
        return fac.hashCode();
    }
    public void shutdown() {
        fac.shutdown();
    }
    /**
     * @see org.coconut.aio.spi.AsyncFactory#openDatagram()
     */
    public AsyncDatagram openDatagram() throws IOException {
        AsyncDatagram as = fac.openDatagram();
        as.socket().setReuseAddress(true);
        if (DEBUG)
            hm.put(new Long(as.getId()), new Throwable());
        return as;
    }
    public AsyncDatagram openDatagram(Executor executor) throws IOException {
        AsyncDatagram as = fac.openDatagram(executor);
        as.socket().setReuseAddress(true);
        if (DEBUG)
            hm.put(new Long(as.getId()), new Throwable());
        return as;

    }
    /**
     * @see org.coconut.aio.spi.AsyncFactory#openDatagram(org.coconut.core.Offerable)
     */
    public AsyncDatagram openDatagram(Offerable< ? super AsyncDatagram.Event> e) throws IOException {
        AsyncDatagram as = fac.openDatagram(e);
        as.socket().setReuseAddress(true);
        if (DEBUG)
            hm.put(new Long(as.getId()), new Throwable());
        return as;

    }
    public AsyncDatagram openDatagram(Queue< ? super AsyncDatagram.Event> queue) throws IOException {
        AsyncDatagram as = fac.openDatagram(queue);
        as.socket().setReuseAddress(true);
        if (DEBUG)
            hm.put(new Long(as.getId()), new Throwable());
        return as;

    }
    public AsyncDatagramGroup openDatagramGroup() {
        return fac.openDatagramGroup();
    }
    /**
     * @see org.coconut.aio.spi.AsyncFactory#openFile()
     */
    public AsyncFile openFile() throws IOException {
        return fac.openFile();
    }
    public AsyncFile openFile(Executor executor) throws IOException {
        return fac.openFile(executor);
    }
    /**
     * @see org.coconut.aio.spi.AsyncFactory#openFile(org.coconut.core.Offerable)
     */
    public AsyncFile openFile(Offerable< ? super AsyncFile.Event> e) throws IOException {
        return fac.openFile(e);
    }
    public AsyncFile openFile(Queue< ? super AsyncFile.Event> queue) throws IOException {
        return fac.openFile(queue);
    }
    /**
     * @see org.coconut.aio.spi.AsyncFactory#openServerSocket()
     */
    public AsyncServerSocket openServerSocket() throws IOException {
        AsyncServerSocket as = fac.openServerSocket();
        if (DEBUG)
            hm.put(new Long(as.getId()), new Throwable());
        return as;
    }
    public AsyncServerSocket openServerSocket(Executor executor) throws IOException {
        AsyncServerSocket as = fac.openServerSocket(executor);
        if (DEBUG)
            hm.put(new Long(as.getId()), new Throwable());
        return as;
    }
    /**
     * @see org.coconut.aio.spi.AsyncFactory#openServerSocket(org.coconut.core.Offerable)
     */
    public AsyncServerSocket openServerSocket(Offerable< ? super AsyncServerSocket.Event> e)
        throws IOException {
        AsyncServerSocket as = fac.openServerSocket(e);
        if (DEBUG)
            hm.put(new Long(as.getId()), new Throwable());
        return as;
    }
    public AsyncServerSocket openServerSocket(Queue< ? super AsyncServerSocket.Event> queue)
        throws IOException {
        AsyncServerSocket as = fac.openServerSocket(queue);
        if (DEBUG)
            hm.put(new Long(as.getId()), new Throwable());
        return as;
    }
    /**
     * @see org.coconut.aio.spi.AsyncFactory#openSocket()
     */
    public AsyncSocket openSocket() throws IOException {
        AsyncSocket as = fac.openSocket();

        as.socket().setReuseAddress(true);
        if (DEBUG)
            hm.put(new Long(as.getId()), new Throwable());
        return as;
    }
    public AsyncSocket openSocket(Executor executor) throws IOException {
        AsyncSocket as = fac.openSocket(executor);
        as.socket().setReuseAddress(true);
        if (DEBUG)
            hm.put(new Long(as.getId()), new Throwable());

        return as;
    }
    /**
     * @see org.coconut.aio.spi.AsyncFactory#openSocket(org.coconut.core.Offerable)
     */
    public AsyncSocket openSocket(Offerable< ? super AsyncSocket.Event> e) throws IOException {
        AsyncSocket as = fac.openSocket(e);
        as.socket().setReuseAddress(true);
        if (DEBUG)
            hm.put(new Long(as.getId()), new Throwable());

        return as;
    }
    public AsyncSocket openSocket(Queue< ? super AsyncSocket.Event> queue) throws IOException {
        AsyncSocket as = fac.openSocket(queue);
        as.socket().setReuseAddress(true);
        if (DEBUG)
            hm.put(new Long(as.getId()), new Throwable());
        return as;
    }
    public AsyncSocketGroup openSocketGroup() {
        return fac.openSocketGroup();
    }
    public String toString() {
        return fac.toString();
    }
}