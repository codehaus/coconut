/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.memory;

import java.io.PrintWriter;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractMemoryOverheadTest implements MemoryOverheadTest {
    // OSCache only takes string keys
    private final static String[] keys = new String[100000];

    private final static Integer[] values = new Integer[100000];

    static {
        for (int i = 0; i < values.length; i++) {
            keys[i] = Integer.toString(i);
            values[i] = Integer.valueOf(i);
        }
    }
    public static Integer getInt(int i) {
        return values[i];
    }
    
    public static String getString(int i) {
        return keys[i];
    }

    public static void shutupLogger(String logger) {
        Logger.getLogger(logger).setLevel(Level.OFF);
//         Logger.getLogger(logger).setLevel(Level.DEBUG);
//         ConsoleAppender ap = new ConsoleAppender();
//         ap.setWriter(new PrintWriter(System.err));
//         ap.setLayout(new SimpleLayout());
//         Logger.getLogger(logger).addAppender(ap);
    }
}
