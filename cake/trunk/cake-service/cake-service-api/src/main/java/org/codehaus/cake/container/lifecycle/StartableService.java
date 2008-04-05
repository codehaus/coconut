/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.container.lifecycle;

import org.codehaus.cake.container.ContainerConfiguration;
import org.codehaus.cake.service.ServiceRegistrant;

/**
 * StartableService
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheLifecycle.java 511 2007-12-13 14:37:02Z kasper $
 */
public interface StartableService {

	/**
	 * Starts the service. The specified service registrant can be used to
	 * retrieve other services or register services.
	 * 
	 * @param serviceRegistrant
	 *            the service registrant
	 * @throws Exception
	 *             the service failed to start properly
	 */
	void start(ContainerConfiguration<?> configuration,
			ServiceRegistrant serviceRegistrant) throws Exception;
}
