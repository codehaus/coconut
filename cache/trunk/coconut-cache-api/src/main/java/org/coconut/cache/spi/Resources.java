/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import org.coconut.internal.util.ResourceHolder;

/**
 * This is class is used for looking up ressources. The default language is
 * english no matter what the default locale is, unless org.coconut.cache.lang
 * is set.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Resources {

    private static final String BUNDLE_NAME = "org.coconut.cache.messages";//$NON-NLS-1$

    private static final ResourceHolder RESOURCE_HOLDER = new ResourceHolder(
            BUNDLE_NAME);

    public static String lookup(Class c, String key, Object... o) {
        return RESOURCE_HOLDER.lookup(c, key, o);
    }
    
    public static String getHighlyIrregular() {
        //take from configuration
        return "This is a highly irregular exception, please report this ";
    }
}
