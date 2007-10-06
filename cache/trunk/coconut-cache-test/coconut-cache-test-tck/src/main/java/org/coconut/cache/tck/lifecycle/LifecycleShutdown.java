package org.coconut.cache.tck.lifecycle;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class LifecycleShutdown extends AbstractCacheTCKTest {

    private void startCache() {
        // we don't have anything better to start with right now
        c.put(1, "foo");
    }

    @Test(expected = IllegalStateException.class)
    public void shutdownPutISE() {
        c = newCache();
        startCache();
        c.shutdown();
        c.put(1, "foo");
    }
    
    @Test(expected = IllegalStateException.class)
    public void shutdownContainsKeyISE() {
        c = newCache();
        startCache();
        c.put(1, "foo");
        assertTrue(c.containsKey(1));
        c.shutdown();
        assertFalse(c.containsKey(1)); //hmm
        
        c.containsKey(1);
        c.containsValue("d");
        
        c.entrySet();//hmm empty set?
        c.keySet();
        c.values();

        
        c.get(1); //hmm null?
        c.getAll(null); //hmm empty map?
        c.getEntry(null);

        c.peek(null);
        c.peekEntry(null);
        
        c.getName(); //should not fail
        c.getService(null); //should not fail
        //maybe it some statistics service
        c.getVolume();//should not fail
        c.size();//should not fail
        c.isEmpty();//should not fail

        c.put(null, null);
        c.putAll(null);
        c.putIfAbsent(null, null);
        c.remove(null);
        c.remove(null, null);
        c.replace(null, null);
        c.replace(null,null, null);
    }

}
