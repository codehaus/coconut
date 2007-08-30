/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.service.jmx;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface JmxService {

	/**
     * Returns this managementgroup as a MBean that can be registered with a
     * {@link javax.management.MBeanServer}. 1. we must make sure all methods
     * exists and are attributed with management attributes
     * 
     * @param <T>
     * @param c
     * @return
     */
	<T> T asMBean(Class<? extends T> c);
}
