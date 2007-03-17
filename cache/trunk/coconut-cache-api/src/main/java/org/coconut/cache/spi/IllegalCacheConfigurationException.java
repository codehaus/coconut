/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import org.coconut.cache.CacheException;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class IllegalCacheConfigurationException extends CacheException {

    /** serialVersionUID */
    private static final long serialVersionUID = -1695400915732143052L;

    /**
     * 
     */
    public IllegalCacheConfigurationException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public IllegalCacheConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public IllegalCacheConfigurationException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public IllegalCacheConfigurationException(Throwable cause) {
        super(cause);
    }
}
