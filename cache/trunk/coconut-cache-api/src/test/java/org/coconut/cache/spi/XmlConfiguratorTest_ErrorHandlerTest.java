/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.coconut.cache.spi.XmlConfiguratorTest.rw;

import java.util.logging.Logger;

import org.coconut.cache.CacheConfiguration;
import org.coconut.core.Log;
import org.coconut.core.util.Logs;
import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;

;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlConfiguratorTest_ErrorHandlerTest {

    XmlConfigurator c;

    CacheConfiguration conf;

    @Before
    public void setup() {
        conf = CacheConfiguration.create();
        c = XmlConfigurator.getInstance();
    }

    @Test
    public void testJDK() throws Exception {
        conf.setErrorHandler(new CacheErrorHandler(Logs.JDK.from("org.coconut")));
        conf = rw(conf);
        Logger l = Logs.JDK.getAsJDKLogger(conf.getErrorHandler().getLogger());
        assertEquals("org.coconut", l.getName());
    }

    @Test
    public void testLog4J() throws Exception {
        conf.setErrorHandler(new CacheErrorHandler(Logs.Log4j.from("org.coconut")));
        conf = rw(conf);
        org.apache.log4j.Logger l = Logs.Log4j.getAsLog4jLogger(conf.getErrorHandler()
                .getLogger());

        assertEquals("org.coconut", l.getName());
    }

    @Test
    public void testCommons() throws Exception {
        conf.setErrorHandler(new CacheErrorHandler(Logs.Commons.from("org.coconut")));
        conf = rw(conf);
        org.apache.commons.logging.Log l = Logs.Commons.getAsCommonsLogger(conf.getErrorHandler()
                .getLogger());

        //hmm cannot test name 
    }
    
    @Test
    public void testCustomLog() throws Exception {
        conf.setErrorHandler(new CacheErrorHandler(MockTestCase.mockDummy(Log.class)));
        conf = rw(conf);
        assertFalse(conf.getErrorHandler().hasLogger());
    }
    @Test
    public void testCustomEH() throws Exception {
        
        conf.setErrorHandler(new MyErrorHandler());
        conf = rw(conf);
        assertEquals(CacheErrorHandler.class, conf.getErrorHandler().getClass());
    }

    static class MyErrorHandler extends CacheErrorHandler {

    }
}
