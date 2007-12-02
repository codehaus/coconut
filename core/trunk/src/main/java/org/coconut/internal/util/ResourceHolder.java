/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ResourceHolder {

    private static final Locale LOCALE;

    private final ResourceBundle ressourceBundle;

    static {
        Locale def = Locale.getDefault();
        Locale loc = Locale.getDefault();
        String property = System.getProperty("org.coconut.lang");
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
        Locale.setDefault(def);
        LOCALE = loc;
    }

    public ResourceHolder(String name) {
        ressourceBundle = ResourceBundle.getBundle(name, LOCALE);
    }

    public static ResourceBundle lookup(String name) {
        return ResourceBundle.getBundle(name, LOCALE);
    }

    public String lookup(String key, Object... o) {
        String lookup = getString(ressourceBundle, key);
        if (o != null && o.length > 0) {
            MessageFormat mf = new MessageFormat(lookup, LOCALE);
            return mf.format(o);
        } else {
            return lookup;
        }
    }

    public static String lookupKey(ResourceBundle bundle, String key, Object... o) {
        String lookup = getString(bundle, key);
        if (o != null && o.length > 0) {
            MessageFormat mf = new MessageFormat(lookup, LOCALE);
            return mf.format(o);
        } else {
            return lookup;
        }
    }

    private static String getString(ResourceBundle bundle, String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            //System.out.println(key + " = TODO Fillout");
            throw new RuntimeException("missing entry for key " + key, e);
        }
    }
}
