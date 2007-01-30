/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

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
        Locale.setDefault(loc);
        RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, loc);
        Locale.setDefault(def);
        locale = loc;
    }

    public static String lookup(String key, Object... o) {
        String lookup = getString(key);
        if (o != null && o.length > 0) {
            MessageFormat mf = new MessageFormat(lookup, locale);
            return mf.format(o);
        } else {
            return lookup;
        }
    }

    public static String lookup(Class c, String key, Object... o) {
        return lookup(c.getCanonicalName() + "." + key.replace(' ', '_'), o);
    }

    private static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
           // System.out.println(key + " = TODO Fillout");
            throw new RuntimeException("missing entry for key " + key, e);
        }
    }

}
