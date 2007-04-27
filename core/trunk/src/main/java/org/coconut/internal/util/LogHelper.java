/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import org.coconut.core.Logger;
import org.coconut.core.Loggers;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LogHelper {

    public static Logger fromLog4j(String name) {
        return Loggers.Log4j.from(name);
    }
    
    public static Logger fromCommons(String name) {
        return Loggers.Commons.from(name);
    }
}
