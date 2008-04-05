/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.service;

/**
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheLifecycle.java 511 2007-12-13 14:37:02Z kasper $
 */
public interface ServiceRegistrant  extends ServiceManager {

	/**
	 * Registers the specified service in the container. The service can then
	 * later be retrieved by calls to {@link ServiceManager#getService(Class)}
	 * with the specified class as parameter.
	 * 
	 * @param <T>
	 *            the type of the service
	 * @param key
	 *            the key of the service
	 * @param service
	 *            the service instance to register
	 * @throws NullPointerException
	 *             if either the specified key or service are <code>null</code>
	 * @throws IllegalStateException
	 *             if registration of new services is not allowed. For example,
	 *             if the container has already been started
	 */
	<T> void registerService(Class<T> key, T service);

	// replace service
	// remove service
}
