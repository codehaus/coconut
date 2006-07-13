package org.coconut.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.coconut.aio.monitor.SocketMonitor;
import org.coconut.core.Offerable;


/**
 * Async socket write test
 * 
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class AsyncSocketReadTest extends AioTestCase {

    public void testSetReaderFuture() throws IOException, ClosedChannelException, InterruptedException {
        final int port = getNextPort();
        final AsyncServerSocket socket = getFactory().openServerSocket().bind(createBindingAddress(port));
        final SocketChannel s = SocketChannel.open();
        final BlockingQueue q = new LinkedBlockingQueue();
        final Offerable o = createQueueOfferableOnce(q);

        socket.startAccepting(o);
        s.connect(createConnectAddress(port));

        final AsyncSocket acceptedSocket = ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q)).getAcceptedSocket();
        acceptedSocket.setReader(IGNORE_READ_HANDLER);
        assertSame(IGNORE_READ_HANDLER, acceptedSocket.getReader());

        acceptedSocket.closeNow().getIO();
        s.close();
        socket.close().getIO();
    }

//    public void testSetReaderOfferable() throws IOException, ClosedChannelException, InterruptedException {
//        final int port = getNextPort();
//        final AsyncServerSocket socket = getFactory().openServerSocket().bind(createBindingAddress(port));
//        final SocketChannel s = SocketChannel.open();
//        final BlockingQueue q = new LinkedBlockingQueue();
//        final Offerable o = createQueueOfferableOnce(q);
//
//        socket.startAccepting(o);
//        s.connect(createConnectAddress(port));
//
//        final AsyncSocket acceptedSocket = ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q)).getAcceptedSocket();
//        final BlockingQueue q2 = new LinkedBlockingQueue();
//        final Offerable o2 = createQueueOfferableOnce(q2);
//        acceptedSocket.setReader(IGNORE_READ_HANDLER).setDestination(o2);
//
//        final AsyncSocket.ReaderSet rSet = ((AsyncSocket.ReaderSet) awaitOnQueue(q2));
//
//        assertSame(acceptedSocket, rSet.async());
//        assertSame(IGNORE_READ_HANDLER, rSet.getReader());
//
//        acceptedSocket.closeNow().getIO();
//        s.close();
//        socket.close();
//    }

    public void testConnectAndRead() throws IOException, ClosedChannelException, InterruptedException {
        final int port = getNextPort();
        final AsyncServerSocket socket = getFactory().openServerSocket().bind(createBindingAddress(port));
        final SocketChannel writeChannel = SocketChannel.open();
        final BlockingQueue q = new LinkedBlockingQueue();
        final BlockingQueue<AsyncSocket> q2 = new LinkedBlockingQueue<AsyncSocket>();
        final Offerable o = createQueueOfferableOnce(q);
        final AtomicBoolean read = new AtomicBoolean();
        final String str = "HelloWorld";

        socket.startAccepting(o);

        writeChannel.connect(createConnectAddress(port));
        writeChannel.write(getBytebuffer(str));

        final AsyncSocket acceptedSocket = ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q)).getAcceptedSocket();
        acceptedSocket.setReader(new ReadHandler<AsyncSocket>() {
            public void handle(AsyncSocket socket) throws IOException {
                if (read.compareAndSet(false, true)) //ignore subsequents reads
                    q2.add(socket);
            }
        });

        readAndEqual(((AsyncSocket) awaitOnQueue(q2)), str);

        acceptedSocket.closeNow().getIO();
        writeChannel.close();
        socket.close().getIO();
    }

    public void testConnectAndReadGathering() throws IOException, ClosedChannelException, InterruptedException {
        final int port = getNextPort();
        final AsyncServerSocket socket = getFactory().openServerSocket().bind(createBindingAddress(port));
        final SocketChannel writeChannel = SocketChannel.open();
        final BlockingQueue q = new LinkedBlockingQueue();
        final BlockingQueue q2 = new LinkedBlockingQueue();
        final Offerable o = createQueueOfferableOnce(q);
        final AtomicBoolean read = new AtomicBoolean();
        final String str = "HelloWorld";

        socket.startAccepting(o);

        writeChannel.connect(createConnectAddress(port));
        writeChannel.write(getBytebuffer(str));

        final AsyncSocket acceptedSocket = ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q)).getAcceptedSocket();
        acceptedSocket.setReader(new ReadHandler() {
            public void handle(Object channel) throws IOException {
                if (read.compareAndSet(false, true)) //ignore subsequents reads
                    q2.add(channel);
            }
        });

        readAndEqualGathering(((AsyncSocket) awaitOnQueue(q2)), str);

        acceptedSocket.closeNow().getIO();
        writeChannel.close();
        socket.close().getIO();
    }
    public void testReadMonitor() throws IOException, ClosedChannelException, InterruptedException {
        final int port = getNextPort();
        final AsyncServerSocket socket = getFactory().openServerSocket().bind(createBindingAddress(port));
        final SocketChannel writeChannel = SocketChannel.open();
        final BlockingQueue q = new LinkedBlockingQueue();
        final BlockingQueue q2 = new LinkedBlockingQueue();
        final Offerable o = createQueueOfferableOnce(q);
        final String str = "HelloHelloHelloHelloHelloHelloHelloHelloHelloHelloHelloHelloHelloHello";
        final CountDownLatch latch = new CountDownLatch(6);
        socket.startAccepting(o);

        writeChannel.connect(createConnectAddress(port));
        writeChannel.write(getBytebuffer(str));

        final AsyncSocket acceptedSocket = ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q)).getAcceptedSocket();
        acceptedSocket.setReader(new ReadHandler() {
            public void handle(Object channel) throws IOException {
                q2.add(channel);
            }
        });

        awaitOnQueue(q2);

        // Test Simple read (Bytebuffer buf)
        final ByteBuffer buf = ByteBuffer.allocate(5);
        acceptedSocket.setMonitor(new SocketMonitor() {
            public void preRead(AsyncSocket socket, ByteBuffer[] buffers, int offset, int length) {
                assertSame(acceptedSocket, socket);
                assertEquals(1, buffers.length);
                assertEquals(buf, buffers[0]);
                assertEquals(5, buffers[0].remaining());
                assertEquals(0, offset);
                assertEquals(1, length);
                latch.countDown();
            }
            public void postRead(AsyncSocket socket, long bytes, ByteBuffer[] buffers, int offset, int length,
                    Throwable throwable) {
                assertSame(acceptedSocket, socket);
                assertEquals(1, buffers.length);
                assertEquals(buf, buffers[0]);
                assertEquals(0, buffers[0].remaining());
                assertEquals(0, offset);
                assertEquals(1, length);
                assertTrue(Arrays.equals("Hello".getBytes(), buf.array()));
                latch.countDown();
            }
        });
        read(acceptedSocket, buf);

        // Test Simple read (Bytebuffer[] buf)
        final ByteBuffer[] bufs = new ByteBuffer[]{ByteBuffer.allocate(5), ByteBuffer.allocate(5),
                ByteBuffer.allocate(5)};
        acceptedSocket.setMonitor(new SocketMonitor() {
            public void preRead(AsyncSocket socket, ByteBuffer[] buffers, int offset, int length) {
                assertSame(acceptedSocket, socket);
                assertEquals(3, buffers.length);
                assertEquals(bufs[0], buffers[0]);
                assertEquals(bufs[1], buffers[1]);
                assertEquals(bufs[2], buffers[2]);
                assertEquals(5, buffers[0].remaining());
                assertEquals(5, buffers[1].remaining());
                assertEquals(5, buffers[2].remaining());
                assertEquals(0, offset);
                assertEquals(3, length);
                latch.countDown();
            }
            public void postRead(AsyncSocket socket, long bytes, ByteBuffer[] buffers, int offset, int length,
                    Throwable throwable) {
                assertSame(acceptedSocket, socket);
                assertEquals(3, buffers.length);
                assertEquals(bufs[0], buffers[0]);
                assertEquals(bufs[1], buffers[1]);
                assertEquals(bufs[2], buffers[2]);
                assertEquals(0, buffers[0].remaining());
                assertEquals(0, buffers[1].remaining());
                assertEquals(0, buffers[2].remaining());
                assertEquals(0, offset);
                assertEquals(3, length);
                assertTrue(Arrays.equals("Hello".getBytes(), bufs[0].array()));
                assertTrue(Arrays.equals("Hello".getBytes(), bufs[1].array()));
                assertTrue(Arrays.equals("Hello".getBytes(), bufs[2].array()));
                latch.countDown();
            }
        });
        read(acceptedSocket, bufs, 0, 1);

        // Test Simple read (Bytebuffer[] buf, int index, int length)
        final ByteBuffer[] bufs2 = new ByteBuffer[]{ByteBuffer.allocate(7), ByteBuffer.allocate(5),
                ByteBuffer.allocate(5), ByteBuffer.allocate(7)};
        acceptedSocket.setMonitor(new SocketMonitor() {
            public void preRead(AsyncSocket socket, ByteBuffer[] buffers, int offset, int length) {
                assertSame(acceptedSocket, socket);
                assertEquals(4, buffers.length);
                assertEquals(bufs2[0], buffers[0]);
                assertEquals(bufs2[1], buffers[1]);
                assertEquals(bufs2[2], buffers[2]);
                assertEquals(bufs2[3], buffers[3]);
                assertEquals(7, buffers[0].remaining());
                assertEquals(5, buffers[1].remaining());
                assertEquals(5, buffers[2].remaining());
                assertEquals(7, buffers[3].remaining());
                assertEquals(1, offset);
                assertEquals(2, length);
                latch.countDown();
            }
            public void postRead(AsyncSocket socket, long bytes, ByteBuffer[] buffers, int offset, int length,
                    Throwable throwable) {
                assertSame(acceptedSocket, socket);
                assertEquals(4, buffers.length);
                assertEquals(bufs2[0], buffers[0]);
                assertEquals(bufs2[1], buffers[1]);
                assertEquals(bufs2[2], buffers[2]);
                assertEquals(bufs2[3], buffers[3]);
                assertEquals(7, buffers[0].remaining());
                assertEquals(0, buffers[1].remaining());
                assertEquals(0, buffers[2].remaining());
                assertEquals(7, buffers[3].remaining());
                assertEquals(1, offset);
                assertEquals(2, length);
                assertTrue(Arrays.equals("Hello".getBytes(), bufs2[1].array()));
                assertTrue(Arrays.equals("Hello".getBytes(), bufs2[2].array()));
                latch.countDown();
            }
        });
        read(acceptedSocket, bufs2, 1, 2);

        awaitOnLatch(latch);
        acceptedSocket.closeNow().getIO();
        writeChannel.close();
        socket.close().getIO();
    }

    //todo test read erroneous
}