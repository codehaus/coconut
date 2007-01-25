/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.sandbox;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class IsHostReachable implements ActiveMonitor<Boolean> {

    private volatile String name;

    private final InetAddress address;

    private final long timeout;

    private final TimeUnit unit;

    public IsHostReachable(String address, long timeout, TimeUnit unit)
            throws UnknownHostException {
        this.name = address;
        this.address = InetAddress.getByName(address);
        this.timeout = timeout;
        this.unit = unit;
    }

    public IsHostReachable(InetAddress address, long timeout, TimeUnit unit) {
        this.address = address;
        this.timeout = timeout;
        this.unit = unit;
    }

    public static void main(String[] args) throws Exception {
        IsHostReachable ihr = new IsHostReachable(InetAddress.getByName("ssh.it.edu"),
                10, TimeUnit.SECONDS);
        System.out.println(ihr.call());
    }

    IsHostReachable from(InetAddress address, int timeout) {
        return null;
    }

    IsHostReachable from(String address, int timeout) {
        return null;
    }

    IsHostReachable from(String address, boolean refresh) {
        return null;
    }

    /**
     * @see java.util.concurrent.Callable#call()
     */
    public Boolean call() throws Exception {
        return address.isReachable((int) unit.toMillis(timeout));
    }

    /**
     * @see org.coconut.core.Named#getName()
     */
    public String getName() {
        return address + " up";
    }

    /**
     * @see org.coconut.management.spi.Described#getDescription()
     */
    public String getDescription() {
        return "Whether or not " + address + " is reacheable";
    }

    public String getHostName() {
        return name;
    }
}
