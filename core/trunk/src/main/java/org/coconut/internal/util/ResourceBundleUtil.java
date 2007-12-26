/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ResourceBundleUtil {

    public static String lookupKey(ResourceBundle bundle, String key, Object... o) {
        String lookup = bundle.getString(key);
// String lookup = null;
// try {
// lookup = bundle.getString(key);
// } catch (MissingResourceException e) {
// System.out.println(key + " = TODO Fillout");
// throw new RuntimeException("missing entry for key " + key, e);
// }
        if (o != null && o.length > 0) {
            MessageFormat mf = new MessageFormat(lookup, Locale.US);
            return mf.format(o);
        } else {
            return lookup;
        }
    }
}
