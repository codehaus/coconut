/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.impl.nio;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import org.coconut.aio.AcceptPolicy;
import org.coconut.aio.AsyncServerSocket;
import org.coconut.aio.AsyncSocket;
import org.coconut.aio.impl.BaseServerSocket;
import org.coconut.aio.impl.util.AioFutureTask;
import org.coconut.aio.monitor.ServerSocketMonitor;
import org.coconut.core.Callback;
import org.coconut.core.EventHandler;
import org.coconut.core.Offerable;


/**
 * A NIO implementation of an asynchronous server-socket. The public methods
 * are safe for concurrent access by multiple threads. The package-private are
 * not however. Todo list: * Fix acceptance policy, right now it will just
 * repeatly try to accept sockets Instead it should probably stop accepting new
 * sockets for a period of time *
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
final class NioServerSocket extends BaseServerSocket {

    // -- General final server-socket fields --

    /** Constructs new AioSockets */
    private final NioAioProvider provider;

    /** A reference to the Aio Selector */
    private final DefaultAioSelector netHandler;

    /** The server-sockets channel we are wrapping */
    private final ServerSocketChannel channel;

    // -- Accept Fields, must only be used while holding the accept-lock --

    /** Used for cancelling subscribtion */
    private Callable acceptCancelSubscription;

    // -- Constructors --

    NioServerSocket(DefaultAioSelector handler, NioAioProvider provider, long id,
        ServerSocketChannel channel, ServerSocketMonitor monitor, Offerable< ? super Event> queue,
        Executor executor) {
        super(provider, id, monitor, queue, executor);
        this.netHandler = handler;
        this.provider = provider;
        this.channel = channel;
    }

    // -- Public methods --

    /**
     * @see org.coconut.aio.AsyncServerSocket#socket()
     */
    public ServerSocket socket() {
        return channel.socket();
    }

    /**
     * @see org.coconut.aio.AsyncServerSocket#accept()
     */
    public AsyncSocket accept() throws IOException {
        acceptLock.lock();
        try {
            isAccepting.set(true);
            channel.configureBlocking(true);
            return acceptNext();
        } catch (IOException ioe) {
            userThreadClose(ioe);
            throw ioe;
        } finally {
            isAccepting.set(false);
            acceptLock.unlock();
        }
    }

    // -- Handling starting acceptance --

    protected void startAcceptingRequest(AioFutureTask event) {
        netHandler.serverSocketRegisterCommand(event);
    }

    protected void startAcceptingRun(Executor e, Callback<AsyncSocket> c,
        Offerable< ? super Event> o, AcceptPolicy policy) throws IOException {
        try {
            isAccepting.set(true);
            channel.configureBlocking(false);
            acceptCancelSubscription = netHandler.serverSocketStartAccepting(NioServerSocket.this,
                channel, new EventHandler() {
                    public void handle(Object arg0) {
                        acceptEvents();
                    }
                });
        } catch (IOException ioe) {
            isAccepting.set(false);
            aioThreadClose(ioe);
            throw ioe;
        }
    }

    // -- Handling stopping acceptance --

    /**
     * @see org.coconut.aio.impl.BaseServerSocket#stopAcceptingRequest(org.coconut.aio.impl.AioFutureTask)
     */
    protected void stopAcceptingRequest(AioFutureTask event) {
        netHandler.serverSocketRegisterCommand(event);
    }

    protected void stopAcceptingRun(AioFutureTask task) throws Exception {
        if (acceptCancelSubscription != null)
            acceptCancelSubscription.call();
    }

    protected void closeRequest(AioFutureTask task) {
        netHandler.serverSocketRegisterCommand(task);
    }

    protected void closeCommandRun(AsyncServerSocket.Closed task) throws IOException {
        channel.close();
    }

    // -- Private methods --

    private void acceptEvents() {
        boolean gotLock = acceptLock.tryLock();
        if (gotLock)
            try {
                int acceptRemaining = 0;
                while (isAccepting()) {
                    if (acceptRemaining-- <= 0) {
                        try {
                            acceptRemaining = acceptPolicy.acceptNext(this);
                        } catch (RuntimeException e) {
                            aioThreadClose(e);
                            return;
                        }
                        if (acceptRemaining-- <= 0)
                            return;
                    }  
                    try {
                        if (acceptNext() == null)
                            return;
                    } catch (IOException e) {
                        aioThreadClose(e);
                        return;
                    }
                }
            } finally {
                acceptLock.unlock();
            }
        else {
            IllegalStateException e = new IllegalStateException(
                "tried to asynchronously accept while already blocking accepting");
            aioThreadClose(e);
        }
    }
    
    private NioSocket acceptNext() throws IOException {
        final SocketChannel newChannel;
        newChannel = channel.accept();
        if (newChannel == null)
            return null; // no sockets to process right now
        newChannel.configureBlocking(false);

        final NioSocket newsocket = provider.serverSocketSocketAccepted(this, newChannel,
            getDefaultSocketGroup());
        super.accepted(newsocket);
        return newsocket;
    }
}