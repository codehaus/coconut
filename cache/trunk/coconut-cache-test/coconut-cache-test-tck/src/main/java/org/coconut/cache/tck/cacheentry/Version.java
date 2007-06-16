package org.coconut.cache.tck.cacheentry;

/**
 * Currently we don't support Version on attributes 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Version {

    /**
     * Tests that a cache does not attempt to call any configured cache backends
     * if a value does not already exists in the cache. The reason is that if it
     * did that it should also effect the cache statistics (which would most
     * likely surprise people) gathered by the cache. However I don't think most
     * people will use getEntry for that purpose. It would also be nessessary to
     * send out and Accessed event. Perhaps we can make it configurable
     */
    public void testNoBackendConsulting() {

    }

//    /**
//     * When a put for a given key occures with the same value as the existing
//     * entry the version of the entry is updated even though there are no
//     * difference. This is done so we can avoid running invoking equals on the 2
//     * elements. We could implement a simple identity based check. However, I
//     * think that people would expect either no check or a check based on
//     * equals, definitly not a check based on identity.
//     */
//    @Test
//    public void testIncrementVersion() {
//        c = c1;
//        CacheEntry<Integer, String> ce = getEntry(M1);
//        assertEquals(1l, ce.getVersion());
//
//        get(M1);
//        ce = getEntry(M1);
//        assertEquals(1l, ce.getVersion());
//
//        put(M1); // its a new version even though
//        ce = getEntry(M1);
//        assertEquals(2l, ce.getVersion());
//
//        c.put(M1.getKey(), M2.getValue());
//        ce = getEntry(M1);
//        assertEquals(3l, ce.getVersion());
//
//        putAll(M1, M2);
//        ce = getEntry(M1);
//        assertEquals(4l, ce.getVersion());
//    }
}
