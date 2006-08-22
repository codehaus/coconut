/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.analyzer.defaults;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
class MonitorElement {

    int[] entries;

    MonitorElement(int size) {
        entries = new int[size];
    }

    void extend() {
        int[] oldEntries = entries;
        entries = new int[entries.length + 1];
        for (int i = 0; i < oldEntries.length; i++) {
            entries[i] = oldEntries[i];
        }
    }
    void remove(int index) {
        int[] oldEntries = entries;
        entries = new int[entries.length - 1];
        for (int i = 0; i < index; i++) {
            entries[i] = oldEntries[i];
        }
        for (int i = index + 1; i < oldEntries.length; i++) {
            entries[i] = oldEntries[i + 1];
        }
    }

    void clear() {
        for (int i = 0; i < entries.length; i++) {
            entries[i] = 0;
        }
    }
}