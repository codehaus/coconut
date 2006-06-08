package org.coconut.aio.impl;

import java.net.DatagramSocket;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.aio.AsyncDatagram;
import org.coconut.aio.management.DatagramInfo;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
public abstract class BaseDatagram extends AsyncDatagram {

    /** The number of commited bytes for writing */
    protected final AtomicLong bytesWritten = new AtomicLong();

    /** The number of commited bytes for writing */
    protected final AtomicLong bytesRead = new AtomicLong();


    /**
     * Returns info from this server-socket
     */
    public DatagramInfo getSocketInfo() {
        final BaseDatagramGroup group = (BaseDatagramGroup) getGroup();
        final DatagramSocket socket = socket();
        return new DatagramInfo(getId(), 0, 0, group == null ? 0 : group.getId(), socket.isBound(),
            socket.isConnected(), socket.getInetAddress(), socket.getLocalSocketAddress(), socket
                .getPort(), socket.getLocalPort(), socket.getRemoteSocketAddress(), socket
                .getLocalAddress(), bytesRead.get(), bytesWritten.get());
    }
    /**
     * Returns info from this server-socket
     */
    public long getNumberOfBytesRead() {
        return bytesRead.get();
    }

    /**
     * Returns info from this server-socket
     */
    public long getNumberOfBytesWritten() {
        return bytesWritten.get();
    }
    /**
     * @param group
     * @return
     */
    protected abstract boolean innerSetGroup(BaseDatagramGroup group);
}
