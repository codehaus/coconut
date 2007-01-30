/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
public class AsyncFileTransferToTest extends AioTestCase {

    public void testNone() {
        
    }
//    public void testTransferToAsyncSocketFuture() throws IOException {
//        final int port = getNextPort();
//        final AsyncFile af = getFactory().openFile();
//        final ServerSocketChannel channel = ServerSocketChannel.open();
//        final AsyncSocket socket = getFactory().openSocket();
//
//        af.openFile(createTmpFile("Hello".getBytes()), "rws").getIO();
//
//        channel.socket().bind(createBindingAddress(port));
//        socket.connect(createConnectAddress(port));
//
//        final SocketChannel readChannel = channel.accept();
//        readChannel.configureBlocking(true);
//        af.transferTo(0, 5, socket).getIO();
//        readAndEqual(readChannel, "Hello");
//
//        readChannel.close();
//        channel.close();
//        socket.closeNow().getIO();
//    }
//    public void testTransferToAsyncSocketCallback() throws IOException, InterruptedException {
//
//        final CountDownLatch latch = new CountDownLatch(1);
//        final int port = getNextPort();
//        final AsyncFile af = getFactory().openFile();
//        final ServerSocketChannel channel = ServerSocketChannel.open();
//        final AsyncSocket socket = getFactory().openSocket();
//
//        af.openFile(createTmpFile("Hello".getBytes()), "rws").getIO();
//
//        channel.socket().bind(createBindingAddress(port));
//        socket.connect(createConnectAddress(port));
//
//        final SocketChannel readChannel = channel.accept();
//        readChannel.configureBlocking(true);
//        af.transferTo(0, 5, socket).setCallback(OWN_THREAD, createCallbackCompleted(latch));
//        awaitOnLatch(latch);
//        readAndEqual(readChannel, "Hello");
//
//        readChannel.close();
//        channel.close();
//        socket.closeNow().getIO();
//    }
//
//    public void testTransferToAsyncSocketOfferable() throws IOException, InterruptedException {
//
//        final BlockingQueue q = new LinkedBlockingQueue();
//        final int port = getNextPort();
//        final AsyncFile af = getFactory().openFile();
//        final ServerSocketChannel channel = ServerSocketChannel.open();
//        final AsyncSocket socket = getFactory().openSocket();
//
//        af.openFile(createTmpFile("Hello".getBytes()), "rws").getIO();
//
//        channel.socket().bind(createBindingAddress(port));
//        socket.connect(createConnectAddress(port));
//
//        final SocketChannel readChannel = channel.accept();
//        readChannel.configureBlocking(true);
//        af.transferTo(0, 10, socket).setDestination(createQueueOfferableOnce(q));
//        Object o = awaitOnQueue(q);
//        assertTrue(o instanceof AsyncFile.TransferedTo);
//        AsyncFile.TransferedTo c = (AsyncFile.TransferedTo) o;
//        assertEquals(socket, c.getTarget());
//        assertEquals(5, c.getBytesTransfered());
//        assertEquals(0, c.getPosition());
//        assertEquals(10, c.getCount());
//        readAndEqual(readChannel, "Hello");
//
//        readChannel.close();
//        channel.close();
//        socket.closeNow().getIO();
//    }
}