/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.util.monitor.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import org.coconut.aio.AsyncSocket;
import org.coconut.aio.monitor.SocketMonitor;

/**
 * A debug SocketMonitor
 * 
 * @version $Id$
 */
public class DebugSocketMonitor extends SocketMonitor {

    /* Allow concurrent threads to write */
    private final OutputStream stream;
    private final boolean logWriteDetails = true;
    private final boolean logReadDetails = true;
    /**
     * @param monitor
     * @param serverMonitor
     * @param socket
     */
    protected DebugSocketMonitor(OutputStream stream) {
        this.stream = stream;
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.aio.monitor.SocketMonitor#closed(java.lang.Throwable)
     */
    public void closed(AsyncSocket socket, Throwable throwable) {
        if (throwable == null)
            log("Socket[" + socket.getId() + "] closed");
        else
            log("Socket[" + socket.getId() + "] closed with exception: " + throwable.getMessage());
    }

    public void bindFailed(AsyncSocket socket, SocketAddress address, Throwable cause) {
        log("Socket[" + socket.getId() + "] failed to bind to " + address + " with exception" + cause.getMessage());
    }
    public void bound(AsyncSocket socket, SocketAddress address) {
        log("Socket[" + socket.getId() + "] bounded to " + socket.getLocalAddress() + ":" + socket.getLocalPort());
    }
    public void connectFailed(AsyncSocket socket, SocketAddress remote, Throwable cause) {
        log("Socket[" + socket.getId() + "] failed to connect with exception: " + cause.getMessage());
    }
    public void disconnected(AsyncSocket socket) {
        log("Socket[" + socket.getId() + "] disconnected");
    }
    public void opened(AsyncSocket socket) {
        log("Socket[" + socket.getId() + "] opened");
    }

    /*
     * (non-Javadoc)
     * 
     * @see coconut.aio.monitor.SocketMonitor#connected()
     */
    public void connected(AsyncSocket socket, SocketAddress address) {
        log("Socket[" + socket.getId() + "] connected to " + socket.getInetAddress() + ":" + socket.getPort()
                + " from " + socket.getLocalAddress() + ":" + socket.getLocalPort());
    }

    public void preRead(AsyncSocket socket, ByteBuffer[] buffers, int offset, int length) {
        ByteUtil.pushBytes(buffers, offset, length);
    }
    public void postRead(AsyncSocket socket, long bytes, ByteBuffer[] buffers, int offset, int length,
            Throwable throwable) {
        log("Socket[" + socket.getId() + "] read " + bytes + " bytes from " + socket.getInetAddress() + ":"
                + socket.getPort());
        if (logReadDetails)
            DebugUtil.dumpAndPrintException("Socket[" + socket.getId() + "] ",ByteUtil.popBytes(), 0, stream, 0);
    }

    public void preWrite(AsyncSocket socket, ByteBuffer[] buffers, int offset, int length) {
        //TODO just duplicate the buffers instead (bufffers.dublicate())
        ByteUtil.pushBytes(buffers, offset, length);
    }

    public void postWrite(AsyncSocket socket, long bytes, ByteBuffer[] buffers, int offset, int length, int attempts,
            Throwable throwable) {
        log("Socket[" + socket.getId() + "] wrote " + bytes + " bytes to " + socket.getInetAddress() + ":"
                + socket.getPort());
        if (logWriteDetails)
            DebugUtil.dumpAndPrintException("Socket[" + socket.getId() + "] ", ByteUtil.popBytes(), 0, stream, 0);
        if (bytes == 0) {
            try {
                throw new Exception();
            } catch (Exception e) {
                System.out.println(socket.getId());
                e.printStackTrace();
            }
        }
    }
    protected void log(String msg) {
        try {
            DebugUtil.dumpEvent(msg, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}