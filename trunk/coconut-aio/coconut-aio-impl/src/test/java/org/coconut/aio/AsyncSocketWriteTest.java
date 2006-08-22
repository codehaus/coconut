/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.aio.AsyncSocket.Written;
import org.coconut.core.Offerable;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
public class AsyncSocketWriteTest extends AioTestCase {

    public void testConnectAndWriteFuture() throws IOException, ClosedChannelException {
        final int port = getNextPort();
        final ServerSocketChannel channel = ServerSocketChannel.open();
        final AsyncSocket socket = getFactory().openSocket();

        channel.socket().bind(createBindingAddress(port));

        socket.connect(createConnectAddress(port));

        final SocketChannel readChannel = channel.accept();
        readChannel.configureBlocking(false);
        Number i = socket.writeAsync(ByteBuffer.wrap("Hello".getBytes())).getIO();
        assertEquals(5, i.intValue());
        readAndEqual(readChannel, "Hello");

        readChannel.close();
        channel.close();
        socket.closeNow().getIO();
    }

    public void testConnectAndWriteOfferable() throws IOException, ClosedChannelException, InterruptedException {
        final int port = getNextPort();
        final ServerSocketChannel channel = ServerSocketChannel.open();
        final AsyncSocket socket = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final Offerable o = createQueueOfferableOnce(q);
        channel.socket().bind(createBindingAddress(port));

        socket.connect(createConnectAddress(port));

        final SocketChannel readChannel = channel.accept();
        readChannel.configureBlocking(false);
        socket.writeAsync(ByteBuffer.wrap("Hello".getBytes())).setDestination(o);
        AsyncSocket.Written wr = (Written) awaitOnQueue(q);
        assertEquals(socket, wr.async());
        assertEquals(1, wr.getSrcs().length);
        assertEquals(0, wr.getSrcs()[0].remaining());
        assertEquals(1, wr.getLength());
        assertEquals(0, wr.getOffset());
        assertEquals(5, wr.getBytesWritten());

        readAndEqual(readChannel, "Hello");

        readChannel.close();
        channel.close();
        socket.closeNow().getIO();
    }

    public void testConnectAndWriteMultiple() throws IOException, ClosedChannelException {
        final int port = getNextPort();
        final ServerSocketChannel channel = ServerSocketChannel.open();
        final AsyncSocket socket = getFactory().openSocket();

        channel.socket().bind(createBindingAddress(port));

        socket.connect(createConnectAddress(port));

        final SocketChannel readChannel = channel.accept();
        readChannel.configureBlocking(false);
        socket.writeAsync(ByteBuffer.wrap("Hello".getBytes()));
        socket.writeAsync(ByteBuffer.wrap("Hello".getBytes()));
        socket.writeAsync(ByteBuffer.wrap("Hello".getBytes()));
        readAndEqual(readChannel, "HelloHelloHello");

        readChannel.close();
        channel.close();
        socket.closeNow().getIO();
    }

    public void testConnectAndWriteScattering() throws IOException, ClosedChannelException {
        final int port = getNextPort();
        final ServerSocketChannel channel = ServerSocketChannel.open();
        final AsyncSocket socket = getFactory().openSocket();

        channel.socket().bind(createBindingAddress(port));

        socket.connect(createConnectAddress(port));

        final SocketChannel readChannel = channel.accept();
        readChannel.configureBlocking(false);
        ByteBuffer bufs[] = new ByteBuffer[]{ByteBuffer.wrap("1".getBytes()), ByteBuffer.wrap("2".getBytes()),
                ByteBuffer.wrap("3".getBytes()), ByteBuffer.wrap("4".getBytes())};
        socket.writeAsync(bufs);
        readAndEqual(readChannel, "1234");

        readChannel.close();
        channel.close();
        socket.closeNow().getIO();
    }

    public void testConnectAndWriteScatteringIndexed() throws IOException, ClosedChannelException {
        final int port = getNextPort();
        final ServerSocketChannel channel = ServerSocketChannel.open();
        final AsyncSocket socket = getFactory().openSocket();

        channel.socket().bind(createBindingAddress(port));

        socket.connect(createConnectAddress(port));

        final SocketChannel readChannel = channel.accept();
        readChannel.configureBlocking(false);
        ByteBuffer bufs[] = new ByteBuffer[]{ByteBuffer.wrap("1".getBytes()), ByteBuffer.wrap("2".getBytes()),
                ByteBuffer.wrap("3".getBytes()), ByteBuffer.wrap("4".getBytes())};
        socket.writeAsync(bufs, 1, 2);
        readAndEqual(readChannel, "23");

        readChannel.close();
        channel.close();
        socket.closeNow().getIO();
    }

    public void testWrites() throws IOException, ClosedChannelException, InterruptedException {

        final int numberOfBuffers = 256;
        final int bufferSize = 256 * 256 * 32;
        final ByteBuffer buf = DebugUtil.allocate(bufferSize, (byte) 45);
        final ByteBuffer read = ByteBuffer.allocate(bufferSize);
        final AtomicLong bytesRead = new AtomicLong();
        final BlockingQueue q = new LinkedBlockingQueue();
        final int port = getNextPort();
        final AsyncServerSocket ass = getFactory().openServerSocket().bind(createBindingAddress(port));
        final AsyncSocket socket = getFactory().openSocket();
        final Offerable o = createQueueOfferableOnce(q);
        ass.startAccepting(o);

        socket.connect(createConnectAddress(port));
        final AsyncSocket acceptedSocket = ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q)).getAcceptedSocket();
        acceptedSocket.setReader(new ReadHandler() {
            public void handle(Object channel) throws IOException {
                bytesRead.addAndGet(acceptedSocket.read(read));
                read.rewind();
            }
        });

        AioFuture[] futures = new AioFuture[numberOfBuffers];

        for (int i = 0; i < numberOfBuffers; i++) {
            futures[i] = socket.writeAsync(buf.duplicate());
        }
        for (int i = 0; i < numberOfBuffers; i++) {
            futures[i].getIO();
        }
        //okay to loose some packages, main thing is to stress write subsystem
        int sleep = 1000;
        long total = numberOfBuffers * bufferSize;
        long value = 0;
        long last = 0;

        for (;;) {
            value = bytesRead.longValue();
            if (value != total) {
                if (last == value) {
                    sleep = sleep * 2;
                }
                if (sleep > 9999)
                    fail("did not finish");

                Thread.sleep(sleep);
            } else
                break;

            last = value;

        }
        ass.close().getIO();
        acceptedSocket.closeNow().getIO();
        socket.closeNow().getIO();
    }
}