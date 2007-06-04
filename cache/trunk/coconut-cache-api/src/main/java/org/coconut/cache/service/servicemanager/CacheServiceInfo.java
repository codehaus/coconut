/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheServiceInfo {
    private final String name;

    private final String status;

    private final String[] services;

    CacheServiceInfo(String name, String status, String[] services) {
        this.name = name;
        this.status = status;
        this.services = services;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
}
