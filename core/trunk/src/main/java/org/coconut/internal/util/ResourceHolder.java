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
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ResourceHolder {

    private static final Locale LOCALE;
    
    public final ResourceBundle ressourceBundle;


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
    public static ResourceHolder fromPackage(Class c) {
    	return new ResourceHolder(c.getPackage().getName());
    }
    public String lookup(String key, Object... o) {
        String lookup = getString(key);
        if (o != null && o.length > 0) {
            MessageFormat mf = new MessageFormat(lookup, LOCALE);
            return mf.format(o);
        } else {
            return lookup;
        }
    }

    public  String lookup(Class c, String key, Object... o) {
        return lookup(c.getCanonicalName() + "." + key.replace(' ', '_'), o);
    }

    private  String getString(String key) {
        try {
            return ressourceBundle.getString(key);
        } catch (MissingResourceException e) {
//            System.out.println(key + " = TODO Fillout");

            throw new RuntimeException("missing entry for key " + key, e);
           //  return "";
        }
    }
}
