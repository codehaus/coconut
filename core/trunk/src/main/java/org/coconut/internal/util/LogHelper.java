/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import org.coconut.core.Log;
import org.coconut.core.Logs;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LogHelper {

    public static Log fromLog4j(String name) {
        return Logs.Log4j.from(name);
    }
    
    public static Log fromCommons(String name) {
        return Logs.Commons.from(name);
    }
}
