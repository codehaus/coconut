/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.memory;

import org.apache.jcs.engine.behavior.ICompositeCacheAttributes;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EHcacheMemoryOverheadTest extends AbstractMemoryOverheadTest{

    /**
     * @see org.coconut.cache.benchmark.memory.MemoryOverheadTest#test(org.coconut.cache.benchmark.memory.MemoryTestResult)
     */
    public void test(MemoryTestResult result) throws Exception {
        result.start();
        result.stop();
        
    }

}
