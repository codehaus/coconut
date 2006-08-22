/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.coconut.aio.monitor.DatagramMonitor;
import org.coconut.core.Offerable;


/**
 * Async socket write test
 * 
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class AsyncDatagramReadTest extends AioTestCase {

    public void testSetReaderFuture() throws IOException, ClosedChannelException {
        final AsyncDatagram socket = getFactory().openDatagram();

        socket.setReader(IGNORE_READ_HANDLER).getIO();
        assertSame(IGNORE_READ_HANDLER, socket.getReader());

        socket.close().getIO();
    }

    public void testSetReaderOfferable() throws IOException, ClosedChannelException, InterruptedException {
        final AsyncDatagram socket = getFactory().openDatagram();
        final BlockingQueue q2 = new LinkedBlockingQueue();
        final Offerable o2 = createQueueOfferableOnce(q2);

        socket.setReader(IGNORE_READ_HANDLER).setDestination(o2);

        final AsyncDatagram.ReaderSet rSet = ((AsyncDatagram.ReaderSet) awaitOnQueue(q2));

        assertSame(socket, rSet.async());
        assertSame(IGNORE_READ_HANDLER, rSet.getReader());

        socket.close().getIO();
    }


    
    public void testConnectAndRead() throws IOException, ClosedChannelException, InterruptedException {
        final int port = getNextPort();
        final DatagramChannel writeChannel = DatagramChannel.open();
        final String str = "HelloWorld";
        final BlockingQueue<AsyncDatagram> q2 = new LinkedBlockingQueue<AsyncDatagram>();
        final AtomicBoolean read = new AtomicBoolean();
        
        writeChannel.socket().bind(createBindingAddress(port));

        final AsyncDatagram socket = getFactory().openDatagram().connect(createConnectAddress(port));
        writeChannel.connect(createConnectAddress(socket.getLocalPort()));
        
        socket.setReader(new ReadHandler<AsyncDatagram>() {
            public void handle(AsyncDatagram socket) throws IOException {
                if (read.compareAndSet(false, true)) //ignore subsequents reads
                {
                    q2.add(socket);
                }
            }
        }).getIO();

        writeChannel.write(getBytebuffer(str));
        
        readAndEqual(((AsyncDatagram) awaitOnQueue(q2)).getSource(), str);
         writeChannel.close();
        socket.close().getIO();
    }

    public void testConnectAndReadGathering() throws IOException, ClosedChannelException, InterruptedException {
        final int port = getNextPort();
        final DatagramChannel writeChannel = DatagramChannel.open();
        final String str = "HelloWorld";
        final BlockingQueue<AsyncDatagram> q2 = new LinkedBlockingQueue<AsyncDatagram>();
        final AtomicBoolean read = new AtomicBoolean();
        
        writeChannel.socket().bind(createBindingAddress(port));

        final AsyncDatagram socket = getFactory().openDatagram().connect(createConnectAddress(port));
        writeChannel.connect(createConnectAddress(socket.getLocalPort()));
        
        socket.setReader(new ReadHandler<AsyncDatagram>() {
            public void handle(AsyncDatagram socket) throws IOException {
                if (read.compareAndSet(false, true)) //ignore subsequents reads
                {
                    q2.add(socket);
                }
            }
        }).getIO();

        writeChannel.write(getBytebuffer(str));
        
        readAndEqualGathering(((AsyncDatagram) awaitOnQueue(q2)).getSource(), str);
         writeChannel.close();
        socket.close().getIO();
    }
    public void testReadMonitor() throws IOException, ClosedChannelException, InterruptedException {
        final int port = getNextPort();
        final DatagramChannel writeChannel = DatagramChannel.open();
        final String str = "Hello";
        final BlockingQueue<AsyncDatagram> q2 = new LinkedBlockingQueue<AsyncDatagram>();
        final AtomicBoolean read = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(2);
        
        writeChannel.socket().bind(createBindingAddress(port));

        final AsyncDatagram socket = getFactory().openDatagram().connect(createConnectAddress(port));
        writeChannel.connect(createConnectAddress(socket.getLocalPort()));
        writeChannel.write(getBytebuffer(str));
        
        socket.setReader(new ReadHandler<AsyncDatagram>() {
            public void handle(AsyncDatagram socket) throws IOException {
                if (read.compareAndSet(false, true)) //ignore subsequents reads
                {
                    q2.add(socket);
                }
            }
        }).getIO();
        
        awaitOnQueue(q2);

        // Test Simple read (Bytebuffer buf)
        final ByteBuffer buf = ByteBuffer.allocate(5);
        socket.setMonitor(new DatagramMonitor() {
            public void preRead(AsyncDatagram s, ByteBuffer[] buffers, int offset, int length) {
                assertSame(socket, s);
                assertEquals(1, buffers.length);
                assertEquals(buf, buffers[0]);
                assertEquals(5, buffers[0].remaining());
                assertEquals(0, offset);
                assertEquals(1, length);
                latch.countDown();
            }
            public void postRead(AsyncDatagram s, long bytes, ByteBuffer[] buffers, int offset, int length,
                    Throwable throwable) {
                assertSame(socket, socket);
                assertEquals(1, buffers.length);
                assertEquals(buf, buffers[0]);
                assertEquals(0, buffers[0].remaining());
                assertEquals(0, offset);
                assertEquals(1, length);
                assertTrue(Arrays.equals("Hello".getBytes(), buf.array()));
                latch.countDown();
            }
        });
        read(socket.getSource(), buf);
        
        awaitOnLatch(latch);
        socket.close().getIO();
        writeChannel.close();
        socket.close().getIO();
    }

    public void testReadArrayMonitor() throws IOException, ClosedChannelException, InterruptedException {
        final int port = getNextPort();
        final DatagramChannel writeChannel = DatagramChannel.open();
        final String str = "HelloHelloHello";
        final BlockingQueue<AsyncDatagram> q2 = new LinkedBlockingQueue<AsyncDatagram>();
        final AtomicBoolean read = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(2);
        
        writeChannel.socket().bind(createBindingAddress(port));

        final AsyncDatagram socket = getFactory().openDatagram().connect(createConnectAddress(port));
        writeChannel.connect(createConnectAddress(socket.getLocalPort()));
        writeChannel.write(getBytebuffer(str));
        
        socket.setReader(new ReadHandler<AsyncDatagram>() {
            public void handle(AsyncDatagram socket) throws IOException {
                if (read.compareAndSet(false, true)) //ignore subsequents reads
                {
                    q2.add(socket);
                }
            }
        }).getIO();
        
        awaitOnQueue(q2);

        // Test Simple read (Bytebuffer buf)
        // Test Simple read (Bytebuffer[] buf)
        final ByteBuffer[] bufs = new ByteBuffer[]{ByteBuffer.allocate(5), ByteBuffer.allocate(5),
                ByteBuffer.allocate(5)};
        socket.setMonitor(new DatagramMonitor() {
            public void preRead(AsyncDatagram s, ByteBuffer[] buffers, int offset, int length) {
                assertSame(socket, s);

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
            public void postRead(AsyncDatagram s, long bytes, ByteBuffer[] buffers, int offset, int length,
                    Throwable throwable) {
                assertSame(socket, s);
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
        read(socket.getSource(), bufs, 0, 1);
        
        awaitOnLatch(latch);
        socket.close().getIO();
        writeChannel.close();
        socket.close().getIO();
    }

    public void testReadArrayIndexedMonitor() throws IOException, ClosedChannelException, InterruptedException {
        final int port = getNextPort();
        final DatagramChannel writeChannel = DatagramChannel.open();
        final String str = "HelloHello";
        final BlockingQueue<AsyncDatagram> q2 = new LinkedBlockingQueue<AsyncDatagram>();
        final AtomicBoolean read = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(2);
        
        writeChannel.socket().bind(createBindingAddress(port));

        final AsyncDatagram socket = getFactory().openDatagram().connect(createConnectAddress(port));
        writeChannel.connect(createConnectAddress(socket.getLocalPort()));
        writeChannel.write(getBytebuffer(str));
        
        socket.setReader(new ReadHandler<AsyncDatagram>() {
            public void handle(AsyncDatagram socket) throws IOException {
                if (read.compareAndSet(false, true)) //ignore subsequents reads
                {
                    q2.add(socket);
                }
            }
        }).getIO();
        
        awaitOnQueue(q2);

        // Test Simple read (Bytebuffer buf)
        // Test Simple read (Bytebuffer[] buf)
        final ByteBuffer[] bufs2 = new ByteBuffer[]{ByteBuffer.allocate(7), ByteBuffer.allocate(5),
                ByteBuffer.allocate(5), ByteBuffer.allocate(7)};
        socket.setMonitor(new DatagramMonitor() {
            public void preRead(AsyncDatagram s, ByteBuffer[] buffers, int offset, int length) {
                assertSame(socket, s);

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
            public void postRead(AsyncDatagram s, long bytes, ByteBuffer[] buffers, int offset, int length,
                    Throwable throwable) {
                assertSame(socket, s);
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
        read(socket.getSource(), bufs2, 1, 2);
        
        awaitOnLatch(latch);
        socket.close().getIO();
        writeChannel.close();
        socket.close().getIO();
    }
    //todo test read erroneous
}