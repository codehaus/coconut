package org.coconut.aio.impl.nio;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.coconut.aio.AioFuture;
import org.coconut.aio.impl.BaseDatagram;
import org.coconut.aio.impl.BaseFile;
import org.coconut.aio.impl.BaseServerSocket;
import org.coconut.aio.impl.BaseSocket;
import org.coconut.aio.impl.BaseSocketGroup;
import org.coconut.aio.impl.ManagedAioProvider;
import org.coconut.aio.monitor.DatagramMonitor;
import org.coconut.aio.monitor.FileMonitor;
import org.coconut.aio.monitor.ServerSocketMonitor;
import org.coconut.aio.monitor.SocketMonitor;
import org.coconut.core.Offerable;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class NioAioProvider extends ManagedAioProvider {

    private final DefaultAioSelector netHandler;
    private final DefaultDiskHandler diskHandler;

    public NioAioProvider() {
        netHandler = new DefaultAioSelector();
        diskHandler = new DefaultDiskHandler(this);
    }
    /**
     * @throws IOException
     * @see org.coconut.aio.impl.AbstractAioProvider#startupDisk()
     */
    protected void startupManagedDisk() throws IOException {
        netHandler.start();
    }

    /**
     * @throws IOException
     * @see org.coconut.aio.impl.AbstractAioProvider#startupNet()
     */
    protected void startupManagedNet() throws IOException {
        netHandler.start();
    }

    /**
     * @see org.coconut.aio.impl.AbstractAioProvider#stopDisk()
     */
    protected void stopManagedDisk() {}

    /**
     * @see org.coconut.aio.impl.AbstractAioProvider#stopNet()
     */
    protected void stopManagedNet() {}

    /**
     * @see org.coconut.aio.impl.AbstractAioProvider#openAsyncSocket(org.coconut.core.Offerable,
     *      java.util.concurrent.Executor)
     */
    protected BaseSocket openAsyncSocket(Offerable< ? super org.coconut.aio.AsyncSocket.Event> queue,
        Executor executor) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        final SocketMonitor m = getDefaultSocketMonitor();
        NioSocket s = new NioSocket(netHandler, this, this.getNextId(), channel, m, queue, executor);
        this.opened(s);

        if (m != null)
            m.opened(s);
        return s;
    }

    /**
     * @see org.coconut.aio.impl.AbstractAioProvider#openAsyncServerSocket(org.coconut.core.Offerable,
     *      java.util.concurrent.Executor)
     */
    protected BaseServerSocket openAsyncServerSocket(
        Offerable< ? super org.coconut.aio.AsyncServerSocket.Event> queue, Executor executor)
        throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        // channel.configureBlocking(false);
        final ServerSocketMonitor m = getDefaultServerSocketMonitor();

        NioServerSocket s = new NioServerSocket(netHandler, this, getNextId(), channel, m, queue,
            executor);

        if (m != null)
            m.opened(s);
        serverSocketOpened(s);
        return s;
    }
    /**
     * @see org.coconut.aio.impl.AbstractAioProvider#openAsyncFile(org.coconut.core.Offerable,
     *      java.util.concurrent.Executor)
     */
    protected BaseFile openAsyncFile(Offerable< ? super org.coconut.aio.AsyncFile.Event> queue,
        Executor executor) {

        final FileMonitor m = getDefaultFileMonitor();
        DefaultFile f = new DefaultFile(this,diskHandler, diskHandler.requests, getNextId(), m, queue,
            executor);
        // management.opened(s);

        if (m != null)
            m.opened(f);
        return f;
    }

    /**
     * @see org.coconut.aio.impl.AbstractAioProvider#openAsyncDatagram(org.coconut.core.Offerable,
     *      java.util.concurrent.Executor)
     */
    protected BaseDatagram openAsyncDatagram(
        Offerable< ? super org.coconut.aio.AsyncDatagram.Event> queue, Executor executor)
        throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        final DatagramMonitor m = getDefaultDatagramMonitor();
        NioDatagram s = new NioDatagram(netHandler, this, getNextId(), channel, m, queue, executor);
        opened(s);
        if (m != null)
            m.opened(s);
        return s;
    }

    /**
     * @param impl
     * @param newChannel
     * @param defaultAcceptedSocketGroup
     * @return
     */
    NioSocket serverSocketSocketAccepted(NioServerSocket impl, SocketChannel newChannel,
        BaseSocketGroup group) {
        incrementAccepts();
        final SocketMonitor m = getDefaultSocketMonitor();
        NioSocket s = new NioSocket(netHandler, this, getNextId(), newChannel, m, null, null);
        opened(s);

        s.setGroup(group);

        if (m != null) {
            try {
                m.opened(s);
            } catch (RuntimeException e) {
                // TODO close server socket;
            }
        }
        return s;
    }
    @Override
    public Map<?, List<AioFuture>> shutdownNow() {
        throw new UnsupportedOperationException();
    }
}
