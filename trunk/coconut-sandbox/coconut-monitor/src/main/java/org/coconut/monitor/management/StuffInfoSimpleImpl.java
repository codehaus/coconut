/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.monitor.management;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class StuffInfoSimpleImpl implements StuffInfo {

    /**
     * @see org.coconut.monitor.management.StuffInfo#getHigh()
     */
    public int getHigh() {
       return 5;
    }

    /**
     * @see org.coconut.monitor.management.StuffInfo#getLow()
     */
    public int getLow() {
        return 9;
    }
    
    /**
     * @see org.coconut.monitor.management.StuffInfo#getJj()
     */
    public List<Long> getJj() {
        return Arrays.asList(new Long[] { 87l, 6l, 9l, 6l, 9l });
    }
    /**
     * @see org.coconut.monitor.management.StuffInfo#getValues()
     */
    public long[] getValues() {
        return new long[] { 87l, 6l, 9l, 6l, 9l };
    }
    
    /**
     * @see org.coconut.monitor.management.StuffInfo#getFoo()
     */
    public String[] getFoo() {
        return new String[] { "erer", "lwkjer", "wekljelrkjt", "kljlj" };
    }
}
