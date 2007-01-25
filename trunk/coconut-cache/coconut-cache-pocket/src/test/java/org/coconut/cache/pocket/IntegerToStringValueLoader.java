/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.pocket;

import java.util.Random;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;


/**
 * A simple cache loader used for testing. Will return 1->A, 2->B, 3->C, 4->D,
 * 5->E and <code>null</code> for any other key.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: IntegerToStringLoader.java 38 2006-08-22 10:09:08Z kasper $
 */
public class IntegerToStringValueLoader implements ValueLoader<Integer, String> {

    /**
     * @see org.coconut.cache.util.AbstractCacheLoader#load(java.lang.Object)
     */
    public String load(Integer key) {
        if (1 <= key && key <= 5) {
            return "" + (char) (key + 64);
        } else {
            return null;

        }
    }
    

    public static void main(String[] args) throws InterruptedException,
            InstanceAlreadyExistsException, MBeanRegistrationException {
        UnsafePocketCache c = new UnsafePocketCache(new IntegerToStringValueLoader(), 100);
        PocketCache map = PocketCaches.synchronizedCache(c);
        // map.put(1, "A");
        // map.put(2, "B");
        // map.put(3, "C");
        // map.get(1);
        // map.put(4, "D");
        // map.put(5, "E");
        // map.trimToSize(0);
        PocketCaches.jmxRegisterCache(map, "name");

        map.put(29, "29");
        map.put(30, "30");
        map.put(31, "31");
        map.put(32, "32");
        map.put(33, "33");
        map.put(34, "34");
        Random r=new Random();
         for (int i = 0; i < 10000; i++) {
             map.get(r.nextInt(150));
            // map.get(i);
//            map.put(i, "" + i);
            Thread.sleep(30);
        }
        System.out.println("done");
        // Thread.sleep(1000000);
    }
}
