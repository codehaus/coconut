/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.event.spi;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This is class is used for looking up ressources. The default language is
 * english no matter what the default locale is, unless org.coconut.cache.lang
 * is set.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Ressources.java 124 2006-10-10 16:55:26Z kasper $
 */
final class Ressources {
    private static final String BUNDLE_NAME = "org.coconut.event.messages";//$NON-NLS-1$

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
}
