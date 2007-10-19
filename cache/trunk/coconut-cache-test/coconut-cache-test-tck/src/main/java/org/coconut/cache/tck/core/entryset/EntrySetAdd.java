/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.entryset;

import java.util.Collections;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;
import static org.coconut.test.CollectionUtils.M1;
/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EntrySetAdd extends AbstractCacheTCKTest {

    @Test(expected = NullPointerException.class)
    public void addNPE() {
        try {
            newCache().entrySet().add(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addUOE() {
        newCache().entrySet().add(M1);
    }

    @Test(expected = NullPointerException.class)
    public void addAllNPE() {
        try {
            newCache().entrySet().addAll(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addAllUOE() {
        newCache().entrySet().addAll(Collections.singleton(M1));
    }
}
