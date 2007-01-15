/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.service.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

import org.coconut.core.Log;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management2.service.ServiceCheck;
import org.coconut.management2.service.ServiceCheckStatus;
import org.coconut.management2.service.spi.AbstractServiceMonitor;
import org.coconut.management2.service.spi.AbstractServiceCheckerSession;
import org.coconut.management2.service.spi.CheckTerminatedException;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractTcpServiceChecker extends AbstractServiceMonitor {
    abstract static class AbstractTcpServiceCheckerSession extends
            AbstractServiceCheckerSession {

        private BufferedReader reader;

        private Socket s;

        private Writer writer;

        volatile int connectTimeout;

        volatile String hostName;

        volatile int port;

        volatile int readTimeout;

        /**
         * @return the connectTimeout
         */
        public int getConnectTimeout() {
            return connectTimeout;
        }

        /**
         * @return the hostName
         */
        public String getHostName() {
            return hostName;
        }

        /**
         * @return the port
         */
        public int getPort() {
            return port;
        }

        /**
         * @return the readTimeout
         */
        public int getReadTimeout() {
            return readTimeout;
        }

        protected void checkCancelled(IOException e) {
            if (isCancelled()
                    && (e instanceof ClosedByInterruptException || e instanceof ClosedChannelException)) {
                setCancelled("Cancelled", e);
            }
        }

        protected void close() {
            try {
                s.close();
                log(Log.Level.Info, "Socket closed succesfully");
            } catch (IOException e) {
                setError("Unknown error", e);
            }
        }

        protected void closeSilent() {
            if (s != null && s.isConnected()) {
                try {
                    s.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }

        protected void connect() {
            final InetSocketAddress adr = new InetSocketAddress(hostName, port);
            SocketChannel sc = null;
            try {
                sc = SocketChannel.open();
            } catch (IOException e) {
                setUnknown("Could not create Socket", e);
            }
            s = sc.socket();
            try {
                s.setSoTimeout(readTimeout);
                log(Log.Level.Debug, "SoTimeout set to " + readTimeout);
            } catch (SocketException e) {
                setUnknown("Failed to set SoTimeout = " + readTimeout, e);
            }

            // CONNECTING
            long connectTime = log(Log.Level.Info, "Trying to connect (host = "
                    + hostName + ", port = " + port + ")");
            try {
                s.connect(adr, connectTimeout);
                log(Log.Level.Info, "Socket connected succesfully (duration = "
                        + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - connectTime)
                        + " ms)");
            } catch (UnknownHostException e) {
                setUnknown("Unknown host " + hostName, e);
            } catch (SocketTimeoutException e) {
                setError("Socket failed to connect, timeout time = "
                        + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - connectTime)
                        + " ms)", e);
            } catch (ClosedByInterruptException e) {
                if (isCancelled()) {
                    setCancelled("Cancelled, (Tried connecting for "
                            + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()
                                    - connectTime) + " ms)", e);
                } else {
                    setError("Unknown error", e);
                }
            } catch (IOException e) {
                if (e.getCause() instanceof ClosedByInterruptException) {
                    setCancelled("Cancelled, (Tried connecting for "
                            + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()
                                    - connectTime) + " ms)", e);
                } else {
                    setError("Unknown error from " + hostName, e);
                }
            }
            // Initializing readers and writers
            try {
                writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            } catch (IOException e) {
                if (e instanceof SocketException && isCancelled()) {
                    setCancelled("Cancelled", e);
                }
                setError("Unknown error", e);
            }
        }

        protected String readline() {
            String line = null;
            try {
                line = reader.readLine();
                log(Log.Level.Debug, "Received : " + line + "\n");
            } catch (IOException e) {
                checkCancelled(e);
                setError("Failed to receive message from host", e);
            }
            return line;
        }

        protected void sendNow(String msg) {
            try {
                writer.write(msg);
                writer.flush();
                log(Log.Level.Debug, "Send     : " + msg);
            } catch (IOException e) {
                checkCancelled(e);
                setError("Failed to send message " + msg, e);
            }
        }

        protected boolean isConnected() {
            return s.isConnected();
        }

        protected void setCancelled(String message) {
            setCancelled(message, null);
        }

        protected void setCancelled(String message, Exception e) {
            log(Log.Level.Info, message);
            setStatus(ServiceCheckStatus.WARNING);
            setException(e);
            closeSilent();
            throw new CheckTerminatedException();
        }

        @Override
        protected void setUnknown(String message, Exception e) {
            closeSilent();
            super.setUnknown(message, e);
        }
        @Override
        protected void setError(String message, Exception e) {
            closeSilent();
            super.setError(message, e);
        }        
    }

    private int connectTimeout;

    private String hostName;

    private int port;

    private int readTimeout;

    /**
     * @see org.coconut.management2.service.ServiceChecker#newSession()
     */
    public synchronized AbstractTcpServiceCheckerSession newSession() {
        if (port == 0) {
            throw new IllegalStateException("No port defined");
        } else if (hostName == null) {
            throw new IllegalStateException("No hostname defined");
        }
        AbstractTcpServiceCheckerSession tcp = newTcpSession();
        tcp.connectTimeout = connectTimeout;
        tcp.hostName = hostName;
        tcp.port = port;
        tcp.readTimeout = readTimeout;
        return tcp;
    }

    /**
     * @return the connectTimeout
     */
    @ManagedAttribute(defaultValue = "connectTimeout", description = "The timeout value used when connecting (ms), a timeout of 0 is interpreted as an infinite timeout.")
    public synchronized int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * @return the hostName
     */
    @ManagedAttribute(defaultValue = "hostname", description = "The name of the host to connect to (must be set)")
    public synchronized String getHostName() {
        return hostName;
    }

    /**
     * @return the port
     */
    @ManagedAttribute(defaultValue = "port", description = "The port used for connecting")
    public synchronized int getPort() {
        return port;
    }

    /**
     * @return the readTimeout
     */
    @ManagedAttribute(defaultValue = "readTimeout", description = "The timeout used for waiting for data from the remote host")
    public synchronized int getReadTimeout() {
        return readTimeout;
    }

    /**
     * @param connectTimeout
     *            the connectTimeout to set
     */
    public synchronized void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * @param hostName
     *            the hostName to set
     */
    public synchronized void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * @param port
     *            the port to set
     */
    public synchronized void setPort(int port) {
        this.port = port;
    }

    /**
     * @param readTimeout
     *            the readTimeout to set
     */
    public synchronized void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    protected abstract AbstractTcpServiceCheckerSession newTcpSession();
}
