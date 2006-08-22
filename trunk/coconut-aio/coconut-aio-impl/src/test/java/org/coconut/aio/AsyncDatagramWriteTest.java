/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.core.Offerable;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
public class AsyncDatagramWriteTest extends AioTestCase {

    public void testConnectAndWriteFuture() throws IOException, ClosedChannelException {
        final int port = getNextPort();
        final DatagramChannel readChannel = DatagramChannel.open();
        readChannel.socket().bind(createBindingAddress(port));

        final AsyncDatagram socketWritter = getFactory().openDatagram().connect(createConnectAddress(port));
        readChannel.connect(createConnectAddress(socketWritter.getLocalPort()));
        readChannel.configureBlocking(false);
        
        Number i = socketWritter.writeAsync(ByteBuffer.wrap("Hello".getBytes())).getIO();
        assertEquals(5, i.intValue());
        readAndEqual(readChannel, "Hello");

        readChannel.close();
        socketWritter.close().getIO();
    }

    public void testConnectAndWriteOfferable() throws IOException, ClosedChannelException, InterruptedException {
        final int port = getNextPort();
        final DatagramChannel readChannel = DatagramChannel.open();
        final BlockingQueue q = new LinkedBlockingQueue();
        final Offerable o = createQueueOfferableOnce(q);
        readChannel.socket().bind(createBindingAddress(port));

        final AsyncDatagram socketWritter = getFactory().openDatagram().connect(createConnectAddress(port));
        readChannel.connect(createConnectAddress(socketWritter.getLocalPort()));
        readChannel.configureBlocking(false);
        
       
        readChannel.configureBlocking(false);
        socketWritter.writeAsync(ByteBuffer.wrap("Hello".getBytes())).setDestination(o);
        AsyncDatagram.Written wr = (AsyncDatagram.Written) awaitOnQueue(q);
        assertEquals(socketWritter, wr.async());
        assertEquals(1, wr.getSrcs().length);
        assertEquals(0, wr.getSrcs()[0].remaining());
        assertEquals(1, wr.getLength());
        assertEquals(0, wr.getOffset());
        assertEquals(5, wr.getBytesWritten());

        readAndEqual(readChannel, "Hello");

        readChannel.close();
        readChannel.close();
        socketWritter.close().getIO();
    }

    public void testConnectAndWriteMultiple() throws IOException, ClosedChannelException {
        final int port = getNextPort();
        final DatagramChannel readChannel = DatagramChannel.open();
        readChannel.socket().bind(createBindingAddress(port));

        final AsyncDatagram socketWritter = getFactory().openDatagram().connect(createConnectAddress(port));
        readChannel.connect(createConnectAddress(socketWritter.getLocalPort()));
        readChannel.configureBlocking(false);
        
        socketWritter.writeAsync(ByteBuffer.wrap("Hello".getBytes()));
        socketWritter.writeAsync(ByteBuffer.wrap("Hello".getBytes()));
        socketWritter.writeAsync(ByteBuffer.wrap("Hello".getBytes()));
        readAndEqual(readChannel, "HelloHelloHello");

        readChannel.close();
        socketWritter.close().getIO();

    }

    public void testConnectAndWriteScattering() throws IOException, ClosedChannelException {
        final int port = getNextPort();
        final DatagramChannel readChannel = DatagramChannel.open();
        readChannel.socket().bind(createBindingAddress(port));

        final AsyncDatagram socketWritter = getFactory().openDatagram().connect(createConnectAddress(port));
        readChannel.connect(createConnectAddress(socketWritter.getLocalPort()));
        readChannel.configureBlocking(false);

        ByteBuffer bufs[] = new ByteBuffer[]{ByteBuffer.wrap("1".getBytes()), ByteBuffer.wrap("2".getBytes()),
                ByteBuffer.wrap("3".getBytes()), ByteBuffer.wrap("4".getBytes())};
        socketWritter.writeAsync(bufs);
        readAndEqual(readChannel, "1234");

        readChannel.close();
        readChannel.close();
        socketWritter.close().getIO();
    }

    public void testConnectAndWriteScatteringIndexed() throws IOException, ClosedChannelException {
        final int port = getNextPort();
        final DatagramChannel readChannel = DatagramChannel.open();
        readChannel.socket().bind(createBindingAddress(port));

        final AsyncDatagram socketWritter = getFactory().openDatagram().connect(createConnectAddress(port));
        readChannel.connect(createConnectAddress(socketWritter.getLocalPort()));
        readChannel.configureBlocking(false);

        ByteBuffer bufs[] = new ByteBuffer[]{ByteBuffer.wrap("1".getBytes()), ByteBuffer.wrap("2".getBytes()),
                ByteBuffer.wrap("3".getBytes()), ByteBuffer.wrap("4".getBytes())};
        socketWritter.writeAsync(bufs, 1, 2);
        readAndEqual(readChannel, "23");

        readChannel.close();
        readChannel.close();
        socketWritter.close().getIO();
    }
/*
    public void testWrites() throws IOException, ClosedChannelException, InterruptedException {

        final int port = getNextPort();
        final AsyncDatagram readChannel = getFactory().openDatagram();
        readChannel.bind(createBindingAddress(port));

        final AsyncDatagram socketWritter = getFactory().openDatagram().connect(createConnectAddress(port));
        readChannel.connect(createConnectAddress(socketWritter.getLocalPort()));

        
        final int numberOfBuffers = 256*16;
        final int bufferSize = 256 * 16 ;
        final ByteBuffer buf = DebugUtil.allocate(bufferSize, (byte) 45);
        final ByteBuffer read = ByteBuffer.allocate(bufferSize);
        final AtomicLong bytesRead = new AtomicLong();
        final BlockingQueue q = new LinkedBlockingQueue();

        readChannel.setReader(new ReadHandler() {
            public void handle(Object channel) throws IOException {
                bytesRead.addAndGet(readChannel.getSource().read(read));
                read.rewind();
            }
        }).getIO();

        AioFuture[] futures = new AioFuture[numberOfBuffers];

        for (int i = 0; i < numberOfBuffers; i++) {
            futures[i] = socketWritter.write(buf.duplicate());
        }
        for (int i = 0; i < numberOfBuffers; i++) {
            futures[i].getIO();
        }
        //okay to loose some packages, main thing is to stress write subsystem
        int sleep = 1000;
        long total = numberOfBuffers * bufferSize;
        boolean increase = false;
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

        socketWritter.close().getIO();
    }
*/    
}