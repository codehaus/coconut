package org.coconut.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.management.DatagramGroupInfo;
import org.coconut.aio.management.DatagramGroupMXBean;
import org.coconut.aio.management.ManagementFactory;

@SuppressWarnings("unchecked")
public class AsyncDatagramGroupManagementTest extends AioTestCase {

    public void testGroupManagementGetAll() {
        getFactory().openDatagramGroup();
        final DatagramGroupMXBean mxBean = ManagementFactory.getDatagramGroupMXBean();
        long[] l = mxBean.getAllDatagramGroupIds();
        int length = l.length;
        getFactory().openDatagramGroup();

        assertEquals(length + 1, mxBean.getAllDatagramGroupIds().length);

    }
    public void testDatagramInGroupManagement() throws IOException {
        AsyncDatagramGroup group = getFactory().openDatagramGroup();
        final DatagramGroupMXBean mxBean = ManagementFactory.getDatagramGroupMXBean();

        assertEquals(0, mxBean.getDatagramsInGroup(group.getId()).length);
        AsyncDatagram s1 = getFactory().openDatagram().setGroup(group);
        AsyncDatagram s2 = getFactory().openDatagram().setGroup(group);
        AsyncDatagram s3 = getFactory().openDatagram().setGroup(group);

        long[] ids = mxBean.getDatagramsInGroup(group.getId());
        assertEquals(3, ids.length);
        group.remove(s1);
        group.remove(s2);
        group.remove(s3);
        assertEquals(0, mxBean.getDatagramsInGroup(group.getId()).length);

        s1.close().getIO();
        s2.close().getIO();
        s3.close().getIO();
    }

    public void testManagementPeak() throws IOException {

        final AsyncDatagramGroup group = getFactory().openDatagramGroup();
        final DatagramGroupMXBean mxBean = ManagementFactory.getDatagramGroupMXBean();

        assertEquals(0, mxBean.getPeakDatagramCount(group.getId()));
        assertEquals(0, mxBean.getSize(group.getId()));
        assertEquals(0, mxBean.getTotalDatagramCount(group.getId()));

        final AsyncDatagram socket = getFactory().openDatagram().setGroup(group);
        assertEquals(1, mxBean.getPeakDatagramCount(group.getId()));
        assertEquals(1, mxBean.getSize(group.getId()));
        assertEquals(1, mxBean.getTotalDatagramCount(group.getId()));


        final AsyncDatagram socket2 = getFactory().openDatagram().setGroup(group);
        assertEquals(2, mxBean.getPeakDatagramCount(group.getId()));
        assertEquals(2, mxBean.getSize(group.getId()));
        assertEquals(2, mxBean.getTotalDatagramCount(group.getId()));

        socket.close().getIO();
        mxBean.resetPeakDatagramCount(group.getId());

        assertEquals(1, mxBean.getPeakDatagramCount(group.getId()));
        assertEquals(1, mxBean.getSize(group.getId()));
        assertEquals(2, mxBean.getTotalDatagramCount(group.getId()));

        socket2.close().getIO();
        assertEquals(1, mxBean.getPeakDatagramCount(group.getId()));
        assertEquals(0, mxBean.getSize(group.getId()));
        assertEquals(2, mxBean.getTotalDatagramCount(group.getId()));

    }

    public void testManagemenGroupInfo() throws IOException {
        AsyncDatagramGroup group = getFactory().openDatagramGroup();
        final DatagramGroupMXBean mxBean = ManagementFactory.getDatagramGroupMXBean();
        DatagramGroupInfo info = mxBean.getDatagramGroupInfo(group.getId());
        
        assertEquals(0, info.getSize());
        assertEquals(group.getId(), info.getId());

        AsyncDatagram s1 = getFactory().openDatagram().setGroup(group);
        info = mxBean.getDatagramGroupInfo(group.getId());
        assertEquals(1, info.getSize());
        
        AsyncDatagram s2 = getFactory().openDatagram().setGroup(group);
        info = mxBean.getDatagramGroupInfo(group.getId());
        assertEquals(2, info.getSize());

        s1.close().getIO();
        s2.close().getIO();

    
    }
    public void testManagementReadWritten() throws InterruptedException, IOException {
        final int port = getNextPort();
        final AsyncDatagramGroup group = getFactory().openDatagramGroup();
        final AsyncDatagram socketRead = getFactory().openDatagram().setGroup(group).bind(createBindingAddress(port));
        final AsyncDatagram socketWritten = getFactory().openDatagram().setGroup(group).connect(
                createConnectAddress(port));
        final BlockingQueue q2 = new LinkedBlockingQueue();

        final DatagramGroupMXBean mxBean = ManagementFactory.getDatagramGroupMXBean();
        final DatagramGroupInfo infoWritten = mxBean.getDatagramGroupInfo(group.getId());

        socketRead.connect(createConnectAddress(socketWritten.getLocalPort()));

        assertEquals(0, infoWritten.getBytesWritten());
        assertEquals(0, mxBean.getBytesWritten(group.getId()));

        final DatagramGroupInfo infoRead = mxBean.getDatagramGroupInfo(group.getId());
        assertEquals(0, infoRead.getBytesRead());
        assertEquals(0, mxBean.getBytesRead(group.getId()));

        socketWritten.writeAsync(ByteBuffer.wrap("Hello".getBytes())).getIO();

        ReadHandler handler = new ReadHandler() {
            public void handle(Object socket) throws IOException {
                q2.offer(socket);
            }
        };
        socketRead.setReader(handler);
        awaitOnQueue(q2);

        readAndEqual(socketRead.getSource(), "Hello");

        assertEquals(5, mxBean.getDatagramGroupInfo(group.getId()).getBytesWritten());
        assertEquals(5, mxBean.getBytesWritten(group.getId()));

        assertEquals(5, mxBean.getDatagramGroupInfo(group.getId()).getBytesRead());
        assertEquals(5, mxBean.getBytesRead(group.getId()));        

        socketRead.close().getIO();
        socketWritten.close().getIO();
    }

    public void testManagementNonExistant() {
        final DatagramGroupMXBean mxBean = ManagementFactory.getDatagramGroupMXBean();

        assertEquals(0, mxBean.getPeakDatagramCount(Integer.MAX_VALUE));
        assertEquals(0, mxBean.getSize(Integer.MAX_VALUE));
        assertEquals(0, mxBean.getTotalDatagramCount(Integer.MAX_VALUE));
        assertNull(mxBean.getDatagramGroupInfo(Integer.MAX_VALUE));
        assertEquals(0, mxBean.getDatagramsInGroup(Integer.MAX_VALUE).length);

        mxBean.resetPeakDatagramCount(Integer.MAX_VALUE);
    }

}