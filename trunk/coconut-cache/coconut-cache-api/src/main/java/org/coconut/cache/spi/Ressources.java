/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.coconut.cache.Caches;

/**
 * This is class is used for looking up ressources. The default language is
 * english no matter what the default locale is, unless org.coconut.cache.lang
 * is set.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Ressources {
    private static final String BUNDLE_NAME = "org.coconut.cache.messages";//$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE;

    private static final Locale locale;
    static {

        Locale def = Locale.getDefault();
        Locale loc = Locale.getDefault();
        String property = System.getProperty("org.coconut.cache.lang");
        if (property == null) {
            loc = Locale.US;
        } else {
            for (Locale l : Locale.getAvailableLocales()) {
                if (l.toString().equals(property)) {
                    loc = l;
                }
            }
        }
        // loc=new Locale("da_DK");
        // System.out.println(loc);
        Locale.setDefault(loc);
        RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, loc);
        Locale.setDefault(def);
        // System.out.println(RESOURCE_BUNDLE.getLocale());
        locale = loc;

    }

    public static MessageFormat getMessageFormatter(String key) {
        return new MessageFormat(getString(key), locale);
    }

    public static String getString(Class c, String key) {
        return getString(c.getCanonicalName() + "." + key.replace(' ', '_'));
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            if (true) {
                //throw new RuntimeException("missing entry for key " + key);
            }
            return "No Desc for [key = " + key + "]";
        }
    }

    public static void main(String[] args) {
        String str = Ressources.getString("AbstractCache.6");
        MessageFormat mf = new MessageFormat(str);
        System.out.println(MessageFormat.format(str, 213));

        // str =
        // Ressources.getString(CacheStatisticsSupport.class,CacheStatisticsSupport.CACHE_HIT_COUNTER);
        System.out.println(str);
    }

    public static void main2(String[] args) {
        String str = Ressources.getString("AbstractCache.6");
        MessageFormat mf = new MessageFormat(str);
        System.out.println(MessageFormat.format(str, 213));
        String a = Ressources.getString("AbstractCache.7");
        System.out.println();
        System.out.println(MessageFormat.format(a, "fooBar", "org.coconut.cache.fooBar"));
        Logger l = Logger.getLogger("org.coconut.cache.fooBar");
        // LogManager.getLogManager().addLogger(Logger.getLogger(name))
        System.out.println(l);
        l.log(Level.INFO, MessageFormat.format(a, "fooBar", "org.coconut.cache.fooBar"));
        System.out.println();
        System.out.println(Caches.newImmutableHitStat(23, 34));
        System.out.println(Locale.getDefault());
    }
}
