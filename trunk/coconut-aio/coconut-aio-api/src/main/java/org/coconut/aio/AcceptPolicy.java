/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

/**
 * The policy used for accepting new sockets. Can be used in the following way.
 * 
 * <pre>
 * class LowMemoryPolicy implements AcceptPolicy {
 *     public int acceptNext(AsyncServerSocket socket) {
 *         long freeMem = Runtime.getRuntime().freeMemory();
 *         return freeMem &lt; 100000 ? 0 : 5;
 *     }
 * }
 * </pre>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public interface AcceptPolicy {
    /**
     * Returns the number of sockets to accept.
     * 
     * @param socket the asynchronous server-socket that is accepting
     * @return the number of sockets to accept before this method
     *         <tt>must<tt> be called again.
     */
    int acceptNext(AsyncServerSocket socket);
}