package org.coconut.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.management.DatagramInfo;
import org.coconut.aio.management.DatagramMXBean;
import org.coconut.aio.management.ManagementFactory;


/**
 * Accept server socket management
 * 
 * @version $Id: AsyncServerDatagramManagementTest.java,v 1.3 2004/06/08 12:49:54
 *          kav Exp $
 */
@SuppressWarnings("unchecked")
public class AsyncDatagramManagementTest extends AioTestCase {

    public void testDatagramManagement() throws IOException {
        final AsyncDatagram socket = getFactory().openDatagram();
        final DatagramMXBean mxBean = ManagementFactory.getDatagramMXBean();
        final DatagramInfo info = mxBean.getDatagramInfo(socket.getId());

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
        socket.close().getIO();
    }

    public void testGetAllDatagrams() throws IOException {
        final DatagramMXBean mxBean = ManagementFactory.getDatagramMXBean();
        long[] allDatagrams = mxBean.getAllDatagramIds();
        long[] all = mxBean.getAllDatagramIds();
        long count = mxBean.getTotalDatagramCount();

        DatagramInfo infos[] = mxBean.getDatagramInfo(allDatagrams);

        final AsyncDatagram socket = getFactory().openDatagram();

        long[] la = mxBean.getAllDatagramIds();
        assertEquals(allDatagrams.length + 1, la.length);
        assertEquals(all.length + 1, mxBean.getAllDatagramIds().length);
        assertEquals(count + 1, mxBean.getTotalDatagramCount());
        assertEquals(infos.length + 1, mxBean.getDatagramInfo(la).length);

        socket.close().getIO();
    }
    public void testManagementNoneExisting() {
        final DatagramMXBean mxBean = ManagementFactory.getDatagramMXBean();

        assertEquals(0, mxBean.getBytesRead(Integer.MAX_VALUE));
        assertEquals(0, mxBean.getBytesWritten(Integer.MAX_VALUE));
        assertNull(mxBean.getDatagramInfo(Integer.MAX_VALUE));

        DatagramInfo[] infos = mxBean.getDatagramInfo(new long[]{Integer.MAX_VALUE, Integer.MAX_VALUE + 1});
        assertNull(infos[0]);
        assertNull(infos[1]);
    }



    public void testManagementReadWritten() throws InterruptedException, IOException {
        final int port = getNextPort();
        final AsyncDatagram socketRead = getFactory().openDatagram().bind(createBindingAddress(port));
        final AsyncDatagram socketWritten = getFactory().openDatagram().connect(createConnectAddress(port));
        final BlockingQueue q2 = new LinkedBlockingQueue();

        final DatagramMXBean mxBean = ManagementFactory.getDatagramMXBean();
        final DatagramInfo infoWritten = mxBean.getDatagramInfo(socketWritten.getId());

        socketRead.connect(createConnectAddress(socketWritten.getLocalPort()));

        long read = mxBean.getBytesRead();
        long written = mxBean.getBytesWritten();
        assertEquals(0, infoWritten.getBytesWritten());
        assertEquals(0, mxBean.getBytesWritten(socketWritten.getId()));

        final DatagramInfo infoRead = mxBean.getDatagramInfo(socketRead.getId());
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

        readAndEqual(socketRead.getSource(), "Hello");

        assertEquals(5 + infoWritten.getBytesWritten(), mxBean.getDatagramInfo(socketWritten.getId()).getBytesWritten());
        assertEquals(5 + written, mxBean.getBytesWritten());
        assertEquals(5 , mxBean.getBytesWritten(socketWritten.getId()));

        assertEquals(5 + infoRead.getBytesRead(), mxBean.getDatagramInfo(socketRead.getId()).getBytesRead());
        assertEquals(5 + read, mxBean.getBytesRead());
        assertEquals(5 , mxBean.getBytesRead(socketRead.getId()));

        socketRead.close().getIO();
        socketWritten.close().getIO();
    }

    public void testManagementPeakServer() throws IOException {

        final DatagramMXBean mxBean = ManagementFactory.getDatagramMXBean();
        mxBean.resetPeakDatagramCount();

        assertEquals(0, mxBean.getPeakDatagramCount());
        assertEquals(0, mxBean.getDatagramCount());
        final AsyncDatagram socket = getFactory().openDatagram();
        assertEquals(1, mxBean.getPeakDatagramCount());
        assertEquals(1, mxBean.getDatagramCount());
        final AsyncDatagram socket2 = getFactory().openDatagram();
        assertEquals(2, mxBean.getPeakDatagramCount());
        assertEquals(2, mxBean.getDatagramCount());
        socket.close().getIO();
        mxBean.resetPeakDatagramCount();
        assertEquals(1, mxBean.getPeakDatagramCount());
        assertEquals(1, mxBean.getDatagramCount());
        socket2.close().getIO();
        assertEquals(1, mxBean.getPeakDatagramCount());

        assertEquals(0, mxBean.getDatagramCount());
        
    }
}