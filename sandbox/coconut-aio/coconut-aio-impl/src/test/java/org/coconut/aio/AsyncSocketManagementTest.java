/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.management.ManagementFactory;
import org.coconut.aio.management.SocketInfo;
import org.coconut.aio.management.SocketMXBean;
import org.coconut.core.Offerable;


/**
 * Accept server socket management
 * 
 * @version $Id: AsyncServerSocketManagementTest.java,v 1.3 2004/06/08 12:49:54
 *          kav Exp $
 */
@SuppressWarnings("unchecked")
public class AsyncSocketManagementTest extends AioTestCase {

    public void testSocketManagement() throws IOException {
        final AsyncSocket socket = getFactory().openSocket();
        final SocketMXBean mxBean = ManagementFactory.getSocketMXBean();
        final SocketInfo info = mxBean.getSocketInfo(socket.getId());

        assertNotNull(info);
        assertEquals(socket.getId(), info.getId());
        assertEquals(false, info.isBound());
        assertEquals(false, info.isBound());
        assertEquals(socket.getInetAddress(), info.getInetAddress());
        assertEquals(socket.getLocalPort(), info.getLocalPort());
        assertEquals(socket.getLocalSocketAddress(), info.getLocalSocketAddress());
        assertEquals(socket.getRemoteSocketAddress(), info.getLocalSocketAddress());
        assertEquals(socket.getPort(), info.getPort());
        assertEquals(socket.getLocalAddress(), info.getLocalAddress());
        socket.closeNow().getIO();
    }

    public void testGetAllServerSockets() throws IOException {
        final SocketMXBean mxBean = ManagementFactory.getSocketMXBean();
        long[] allSockets = mxBean.getAllSocketIds();
        long[] all = mxBean.getAllSocketIds();
        long count = mxBean.getTotalSocketCount();

        SocketInfo infos[] = mxBean.getSocketInfo(allSockets);

        final AsyncSocket socket = getFactory().openSocket();

        long[] la = mxBean.getAllSocketIds();
        assertEquals(allSockets.length + 1, la.length);
        assertEquals(all.length + 1, mxBean.getAllSocketIds().length);
        assertEquals(count + 1, mxBean.getTotalSocketCount());
        assertEquals(infos.length + 1, mxBean.getSocketInfo(la).length);

        socket.closeNow().getIO();
    }
    public void testManagementNoneExisting() {
        final SocketMXBean mxBean = ManagementFactory.getSocketMXBean();

        assertEquals(0, mxBean.getBytesRead(Integer.MAX_VALUE));
        assertEquals(0, mxBean.getBytesWritten(Integer.MAX_VALUE));
        assertNull(mxBean.getSocketInfo(Integer.MAX_VALUE));

        SocketInfo[] infos = mxBean.getSocketInfo(new long[]{Integer.MAX_VALUE, Integer.MAX_VALUE + 1});
        assertNull(infos[0]);
        assertNull(infos[1]);
    }

    public void testManagementConnects() throws IOException {
        final int port = getNextPort();
        final SocketMXBean mxBean = ManagementFactory.getSocketMXBean();
        final long connects = mxBean.getTotalSocketConnectCount();

        AsyncServerSocket ass = getFactory().openServerSocket().bind(createBindingAddress(port));

        final AsyncSocket socket1 = getFactory().openSocket();
        AioFuture con = socket1.connect(createConnectAddress(port));
        ass.accept().closeNow().getIO();
        con.getIO();
                
        assertEquals(connects + 1, mxBean.getTotalSocketConnectCount());
        final AsyncSocket socket2 = getFactory().openSocket();
        AioFuture con2 = socket2.connect(createConnectAddress(port));
        ass.accept().closeNow().getIO();
        con2.getIO();
        
        assertEquals(connects + 2, mxBean.getTotalSocketConnectCount());

        socket2.closeNow().getIO();
        socket1.closeNow().getIO();
        ass.close().getIO();
    }

    public void testManagementReadWritten() throws IOException, InterruptedException {
        final int port = getNextPort();
        final AsyncServerSocket socket = getFactory().openServerSocket().bind(createBindingAddress(port));
        final BlockingQueue q = new LinkedBlockingQueue();
        final BlockingQueue q2 = new LinkedBlockingQueue();
        final Offerable o = createQueueOfferableOnce(q);
        final AsyncSocket socketWritten = getFactory().openSocket();

        final SocketMXBean mxBean = ManagementFactory.getSocketMXBean();
        final SocketInfo infoWritten = mxBean.getSocketInfo(socketWritten.getId());

        long read = mxBean.getBytesRead();
        long written = mxBean.getBytesWritten();
        assertEquals(0, infoWritten.getBytesWritten());
        assertEquals(0, mxBean.getBytesWritten(socketWritten.getId()));

        socket.startAccepting(o);

        socketWritten.connect(createConnectAddress(port)).getIO();

        final AsyncSocket socketRead = ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q)).getAcceptedSocket();
        final SocketInfo infoRead = mxBean.getSocketInfo(socketRead.getId());
        assertEquals(0, infoRead.getBytesRead());
        assertEquals(0, mxBean.getBytesRead(socketRead.getId()));

        socketWritten.writeAsync(ByteBuffer.wrap("Hello".getBytes())).getIO();

        ReadHandler handler = new ReadHandler() {
            public void handle(Object socket) throws IOException {
                q2.offer(socket);
            }
        };
        socketRead.setReader(handler);
        awaitOnQueue(q2);

        readAndEqual(socketRead, "Hello");

        assertEquals(5 + infoWritten.getBytesWritten(), mxBean.getSocketInfo(socketWritten.getId()).getBytesWritten());
        assertEquals(5 + written, mxBean.getBytesWritten());
        assertEquals(5, mxBean.getBytesWritten(socketWritten.getId()));

        assertEquals(5 + infoRead.getBytesRead(), mxBean.getSocketInfo(socketRead.getId()).getBytesRead());
        assertEquals(5 + read, mxBean.getBytesRead());
        assertEquals(5, mxBean.getBytesRead(socketRead.getId()));

        socket.close().getIO();
        socketRead.closeNow().getIO();
        socketWritten.closeNow().getIO();
    }

    public void testManagementPeakServer() throws IOException {

        final SocketMXBean mxBean = ManagementFactory.getSocketMXBean();
        mxBean.resetPeakSocketCount();

        assertEquals(0, mxBean.getPeakSocketCount());
        assertEquals(0, mxBean.getSocketCount());
        final AsyncSocket socket = getFactory().openSocket();
        assertEquals(1, mxBean.getPeakSocketCount());
        assertEquals(1, mxBean.getSocketCount());
        final AsyncSocket socket2 = getFactory().openSocket();
        assertEquals(2, mxBean.getPeakSocketCount());
        assertEquals(2, mxBean.getSocketCount());
        socket.closeNow().getIO();
        mxBean.resetPeakSocketCount();
        assertEquals(1, mxBean.getPeakSocketCount());
        assertEquals(1, mxBean.getSocketCount());
        socket2.closeNow().getIO();
        assertEquals(1, mxBean.getPeakSocketCount());
        
        assertEquals(0, mxBean.getSocketCount());
        
    }
}