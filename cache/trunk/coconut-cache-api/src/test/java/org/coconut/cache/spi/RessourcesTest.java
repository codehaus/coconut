/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class RessourcesTest {
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(RessourcesTest.class);
    }

    @Test
    public void testR() {
       // System.out.println(new ImmutableHitStat(1,2));
    }
    
//
//    public static void main(String[] args) {
//        String str = Ressources.lookup("AbstractCache.6");
//        MessageFormat mf = new MessageFormat(str);
//        System.out.println(MessageFormat.format(str, 213));
//
//        // str =
//        // Ressources.getString(CacheStatisticsSupport.class,CacheStatisticsSupport.CACHE_HIT_COUNTER);
//        System.out.println(str);
//    }
//
//    public static void main2(String[] args) {
//        String str = Ressources.lookup("AbstractCache.6");
//        MessageFormat mf = new MessageFormat(str);
//        System.out.println(MessageFormat.format(str, 213));
//        String a = Ressources.lookup("AbstractCache.7");
//        System.out.println();
//        System.out.println(MessageFormat.format(a, "fooBar", "org.coconut.cache.fooBar"));
//        Logger l = Logger.getLogger("org.coconut.cache.fooBar");
//        // LogManager.getLogManager().addLogger(Logger.getLogger(name))
//        System.out.println(l);
//        l.log(Level.INFO, MessageFormat.format(a, "fooBar", "org.coconut.cache.fooBar"));
//        System.out.println();
//        System.out.println(CacheUtil.newImmutableHitStat(23, 34));
//        System.out.println(Locale.getDefault());
//    }
}
