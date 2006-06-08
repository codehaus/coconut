/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.core;

/**
 * Components can implement this interface to indicate that they support dynamic
 * interfaces.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface Serviceable {
    /**
     * Returns a service with the specific interface. TODO: probably also throws
     * some other exceptions, if lazy init, for example,
     * 
     * @param <T>
     *            the type of service
     * @param service
     * @return
     * @throws IllegalArgumentException
     *             if the particular service is not supported
     * @throws UnsupportedOperationException
     *             if the dynamic plugins are not supported, but why implement
     *             it then???
     */
    <T> T getService(Class<T> service);
}
