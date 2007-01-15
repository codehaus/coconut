/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.service.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.coconut.management2.service.spi.AbstractServiceMonitor;
import org.coconut.management2.service.spi.AbstractServiceCheckerSession;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class IsHostReachable extends AbstractServiceMonitor<Boolean> {

    private int timeout;

    private String hostName = "localhost";

    /**
     * @return the hostName
     */
    public synchronized String getHostName() {
        return hostName;
    }

    /**
     * @param hostName
     *            the hostName to set
     */
    public synchronized void setHostName(String hostName) {
        if (hostName == null) {
            throw new NullPointerException("hostName is null");
        }
        this.hostName = hostName;
    }

    /**
     * @return the milliTimeout
     */
    public synchronized int getTimeout() {
        return timeout;
    }

    /**
     * @param milliTimeout
     *            the milliTimeout to set
     */
    public synchronized void setTimeout(int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException(
                    "timeout must a non-negative number (>=0), was " + timeout);
        }
        this.timeout = timeout;
    }

    /**
     * @see org.coconut.management2.service.ServiceChecker#createSession()
     */
    public synchronized IsHostReachableSession newSession() {
        return new IsHostReachableSession(hostName, timeout);
    }

    public static class IsHostReachableSession extends
            AbstractServiceCheckerSession<Boolean> {

        private final int timeout;

        private final String hostName;

        IsHostReachableSession(String hostName, int timeout) {
            this.timeout = timeout;
            this.hostName = hostName;
        }

        @Override
        protected Boolean doRun() {
            InetAddress address = null;
            try {
                address = InetAddress.getByName(hostName);
            } catch (UnknownHostException e) {
                setUnknown("Unknown host " + hostName, e);
            }

            try {
                if (address.isReachable(timeout)) {
                    setOk("host + " + hostName + " is up");
                } else {
                    setError("host + " + hostName + " is not up");
                }
            } catch (IOException e) {
                setError("Unknown error", e);
            }
            return true;
        }

        /**
         * @return the hostName
         */
        public String getHostName() {
            return hostName;
        }

        /**
         * @return the milliTimeout
         */
        public int getTimeout() {
            return timeout;
        }
    }
}
