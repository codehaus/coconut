/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.util.monitor.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.coconut.aio.AsyncServerSocket;
import org.coconut.aio.AsyncSocket;
import org.coconut.aio.monitor.ServerSocketMonitor;

/**
 * A ServerSocketMonitor that can be used for debugging
 * 
 * @version $Id: DebugServerSocketMonitor.java,v 1.1 2004/07/11 22:11:57 kasper
 *          Exp $
 */
public class DebugServerSocketMonitor extends ServerSocketMonitor {
    public void bindFailed(AsyncServerSocket socket, SocketAddress address, Throwable cause) {
        log("ServerSocket[" + socket.getId() + "] failed to bind to " + address + " with exception"
                + cause.getMessage());

    }
    public void bound(AsyncServerSocket socket, SocketAddress address) {
        log("ServerSocket[" + socket.getId() + "] bounded to " + socket.getInetAddress() + ":" + socket.getLocalPort());
    }
    public void closed(AsyncServerSocket socket, Throwable cause) {
        log("ServerSocket[" + socket.getId() + "] closed on " + socket.getInetAddress() + ":" + socket.getLocalPort());

    }
    public void opened(AsyncServerSocket socket) {
        log("ServerSocket[" + socket.getId() + "] Opened");
    }
    /* Allow concurrent threads to write */
    private final OutputStream stream;
    /**
     * @param monitor
     * @param serverSocket
     */
    public DebugServerSocketMonitor(OutputStream stream) {
        this.stream = stream;
        Handler h = new ConsoleHandler();
        Logger.getAnonymousLogger().addHandler(h);
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.aio.monitor.ServerSocketMonitor#accepted(coconut.aio.AsyncSocket)
     */
    public void accepted(AsyncServerSocket ss, AsyncSocket socket) {
        
        log("ServerSocket[" + socket.getId() + "] accepted new Socket[" + socket.getId() + "] from " + socket.getInetAddress() + ":" + socket.getPort());
        
        //the +1 is because we call super.accepted after having written
                //+ socket.getLocalAddress() + ":" + socket.getLocalPort());
        // | total=" + (getAccepted() + 1) + " alive-connections=" +
        // (getConnected() + 1));
    } 
    private void log(String msg) {
        try {
            DebugUtil.dumpEvent(msg, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}