/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.coconut.cache.CacheConfiguration;
import org.coconut.core.Logger;
import org.coconut.core.Loggers;
import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlConfiguratorTest {

    CacheConfiguration conf;

    @Before
    public void setup() {
        conf = CacheConfiguration.create();
    }

    @Test
    public void cacheGetsName() throws Exception {
        String noNamed = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<cache-config version=\"" + XmlConfigurator.CURRENT_VERSION + "\">"
                + "<cache/></cache-config>";
        assertNull(CacheConfiguration.createConfiguration(
                new ByteArrayInputStream(noNamed.getBytes())).getName());
    }

    @Test
    public void cacheType() throws Exception {
        String noNamed = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<cache-config version=\"" + XmlConfigurator.CURRENT_VERSION + "\">"
                + "<cache type=\"foo\"/></cache-config>";
        assertEquals("foo", CacheConfiguration.createConfiguration(
                new ByteArrayInputStream(noNamed.getBytes())).getProperty(
                XmlConfigurator.CACHE_INSTANCE_TYPE));
    }

    static CacheConfiguration rw(CacheConfiguration conf) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new XmlConfigurator().write(conf, os);
        return CacheConfiguration.createConfiguration(new ByteArrayInputStream(os
                .toByteArray()));
    }

    @Test(expected = NullPointerException.class)
    public void to_NPE1() throws Exception {
        new XmlConfigurator().write(null, new ByteArrayOutputStream());
    }

    @Test(expected = NullPointerException.class)
    public void to_NPE2() throws Exception {
        new XmlConfigurator().write(CacheConfiguration.create(), null);
    }

    @Test(expected = NullPointerException.class)
    public void from_NPE1() throws Exception {
        new XmlConfigurator().read(null, new ByteArrayInputStream(new byte[0]));
    }

    @Test(expected = NullPointerException.class)
    public void from_NPE2() throws Exception {
        CacheConfiguration.createConfiguration(null);
    }

// @Test
// public void testJDK() throws Exception {
// conf.setDefaultLog(Loggers.JDK.from("org.coconut"));
// conf = rw(conf);
// java.util.logging.Logger l = Loggers.JDK.getAsJDKLogger(conf.getDefaultLog());
// assertEquals("org.coconut", l.getName());
// }
//
// @Test
// public void testLog4J() throws Exception {
// conf.setDefaultLog(Loggers.Log4j.from("org.coconut"));
// conf = rw(conf);
// org.apache.log4j.Logger l = Loggers.Log4j.getAsLog4jLogger(conf.getDefaultLog()
// );
//
// assertEquals("org.coconut", l.getName());
// }
//
// @Test
// public void testCommons() throws Exception {
// conf.setDefaultLog(Loggers.Commons.from("org.coconut"));
// conf = rw(conf);
// org.apache.commons.logging.Log l =
// Loggers.Commons.getAsCommonsLogger(conf.getDefaultLog() );
//
// // hmm cannot test name
// }

    @Test
    public void testCustomLog() throws Exception {
        conf.setDefaultLog(MockTestCase.mockDummy(Logger.class));
        conf = rw(conf);
        assertNull(conf.getDefaultLog());
    }

    /**
     * This method is used for saving and reloading a configuration object.
     * 
     * @param configuration
     * @return
     * @throws Exception
     */
    public static <K, V, T extends AbstractCacheServiceConfiguration<K, V>> T reloadService(
            T configuration) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        Document doc = builder.newDocument();
        Element parent = doc.createElement(XmlConfigurator.CACHE_TAG); // dummy
        XmlConfigurator xml = new XmlConfigurator();
        Element e = xml.writeCacheService(doc, configuration);
        parent.appendChild(e);
        return (T) xml.readCacheService(doc, parent, (Class) configuration.getClass());
    }
}
