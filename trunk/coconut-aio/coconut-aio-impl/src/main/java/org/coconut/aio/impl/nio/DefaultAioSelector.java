/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.impl.nio;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;

import org.coconut.core.EventHandler;


/**
 * The default nethandler
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
final class DefaultAioSelector {

    private AsyncSingleSelector acceptHandler;

    private AsyncSingleSelector writeConnectHandler;

    private AsyncSingleSelector readHandler;

    /**
     * Shutdown the AIO Selector.
     */
    void shutdown() {
        acceptHandler.shutdown();
        writeConnectHandler.shutdown();
        readHandler.shutdown();

    }

    /**
     * Start the AioSelector.
     */
    void start() throws IOException {
        int selecttimeout = 1000;
        acceptHandler = new AsyncSingleSelector(new ThreadSupplier("AIO-Accept"), selecttimeout);
        acceptHandler.start();

        writeConnectHandler = new AsyncSingleSelector(new ThreadSupplier("AIO-WriteConnect"),
            selecttimeout);
        writeConnectHandler.start();

        readHandler = new AsyncSingleSelector(new ThreadSupplier("AIO-Read"), selecttimeout);
        readHandler.start();
    }

    void serverSocketRegisterCommand(Runnable runnable) {
        acceptHandler.addFuture(runnable);
    }

    void socketRegisterConnectCommand(Runnable runnable) {
        writeConnectHandler.addFuture(runnable);
    }

    void socketRegisterWriteCommand(Runnable runnable) {
        writeConnectHandler.addFuture(runnable);
    }

    void socketRegisterReadCommand(Runnable runnable) {
        readHandler.addFuture(runnable);
    }
    void datagramRegisterWriteCommand(Runnable runnable) {
        writeConnectHandler.addFuture(runnable);
    }
    void datagramRegisterReadCommand(Runnable runnable) {
        readHandler.addFuture(runnable);
    }

    Callable serverSocketStartAccepting(NioServerSocket socket, ServerSocketChannel channel,
        EventHandler handler) throws IOException {
        return acceptHandler.registerChannel(channel, SelectionKey.OP_ACCEPT, handler);
    }

    Callable socketStartReading(NioSocket socket, SocketChannel channel, EventHandler handler)
        throws IOException {
        return readHandler.registerChannel(channel, SelectionKey.OP_READ, handler);
    }

    Callable datagramStartReading(NioDatagram socket, DatagramChannel channel, EventHandler handler)
        throws IOException {
        return readHandler.registerChannel(channel, SelectionKey.OP_READ, handler);
    }

    void socketStartConnecting(NioSocket socket, SocketChannel channel, EventHandler handler)
        throws IOException {
        writeConnectHandler.registerChannel(channel, SelectionKey.OP_CONNECT, handler);
    }
    Callable socketStartWriting(NioSocket socket, SocketChannel channel, EventHandler handler)
        throws IOException {
        return writeConnectHandler.registerChannel(channel, SelectionKey.OP_WRITE, handler);
    }

    Callable datagramStartWriting(NioDatagram socket, DatagramChannel channel, EventHandler handler)
        throws IOException {
        return writeConnectHandler.registerChannel(channel, SelectionKey.OP_WRITE, handler);
    }

    static class ThreadSupplier implements ThreadFactory {
        final ThreadGroup group;
        final String threadName;

        ThreadSupplier(String threadName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.threadName = threadName;
        }
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, threadName, 0);
            if (!t.isDaemon())
                t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
