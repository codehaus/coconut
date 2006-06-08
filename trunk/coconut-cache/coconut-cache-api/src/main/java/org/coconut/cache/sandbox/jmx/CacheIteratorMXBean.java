/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.sandbox.jmx;

import org.coconut.cache.management.CacheEntryInfo;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheIteratorMXBean {
    String[] getNextIds(int amount);

    CacheEntryInfo[] getNext(int amount);

    int getAmount();

    // skal kombineres med en eller anden form for notification.
    // Hvor vi siger element added, element removed, etc...
    // p� en eller anden m�de skal man have en komplet state

    // signup for notification
    // getAllEntries
    // mix de to p� den rette m�de s� vi har et �jebliksbilled.

    void reset();

    void dispose();
}
