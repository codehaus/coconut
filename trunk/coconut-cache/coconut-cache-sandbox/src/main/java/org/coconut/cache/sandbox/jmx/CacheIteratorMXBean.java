/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.sandbox.jmx;

import org.coconut.cache.management.CacheEntryInfo;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheIteratorMXBean.java 38 2006-08-22 10:09:08Z kasper $
 */
public interface CacheIteratorMXBean {
    String[] getNextIds(int amount);

    CacheEntryInfo[] getNext(int amount);

    int getAmount();

    // skal kombineres med en eller anden form for notification.
    // Hvor vi siger element added, element removed, etc...
    // på en eller anden måde skal man have en komplet state

    // signup for notification
    // getAllEntries
    // mix de to på den rette måde så vi har et øjebliksbilled.

    void reset();

    void dispose();
}
