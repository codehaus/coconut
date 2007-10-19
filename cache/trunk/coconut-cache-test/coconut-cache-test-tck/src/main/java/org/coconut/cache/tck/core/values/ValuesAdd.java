/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.values;

import java.util.Collections;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ValuesAdd extends AbstractCacheTCKTest {

    @Test(expected = NullPointerException.class)
    public void addNPE() {
        try {
            newCache().values().add(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addUOE() {
        newCache().values().add("1");
    }

    @Test(expected = NullPointerException.class)
    public void addAllNPE() {
        try {
            newCache().values().addAll(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addAllUOE() {
        newCache().values().addAll(Collections.singleton("1"));
    }
}
