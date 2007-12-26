/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.coconut.cache.CacheConfiguration;
import org.coconut.core.Logger;
import org.coconut.test.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class XmlConfiguratorTest {

    CacheConfiguration<?, ?> conf;

    @Before
    public void setUp() {
        conf = CacheConfiguration.create();
    }

    @Test
    public void cacheGetsName() throws Exception {
        String noNamed = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<cache-config version=\""
                + XmlConfigurator.CURRENT_VERSION + "\">" + "<cache/></cache-config>";
        assertNull(CacheConfiguration.loadConfigurationFrom(
                new ByteArrayInputStream(noNamed.getBytes())).getName());
    }

// @Test
// public void cacheType() throws Exception {
// String noNamed = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<cache-config
// version=\""
// + XmlConfigurator.CURRENT_VERSION + "\">" + "<cache type=\"foo\"/></cache-config>";
// assertEquals("foo", CacheConfiguration.loadConfigurationFrom(
// new ByteArrayInputStream(noNamed.getBytes())).getProperty(
// XmlConfigurator.CACHE_INSTANCE_TYPE));
// }

    static CacheConfiguration<?, ?> rw(CacheConfiguration<?, ?> conf) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new XmlConfigurator().write(conf, os);
        return CacheConfiguration.loadConfigurationFrom(new ByteArrayInputStream(os.toByteArray()));
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
        new XmlConfigurator().readInto(null, new ByteArrayInputStream(new byte[0]));
    }

    @Test(expected = NullPointerException.class)
    public void from_NPE2() throws Exception {
        CacheConfiguration.loadConfigurationFrom(null);
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
        conf.setDefaultLogger(TestUtil.dummy(Logger.class));
        conf = rw(conf);
        assertNull(conf.getDefaultLogger());
    }

    public static <K, V, T extends AbstractCacheServiceConfiguration<K, V>> T reloadService(
            T configuration) throws Exception {
        return reloadService(configuration, configuration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoCacheTagsDefined() throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement(XmlConfigurator.CONFIG_TAG);
        doc.appendChild(root);

        new XmlConfigurator().readDocument(CacheConfiguration.create(), doc);
    }

    @Test
    public void simpleReadWrite() throws Exception {
        CacheConfiguration conf = CacheConfiguration.create();
        conf.expiration().setDefaultTimeToLive(10, TimeUnit.SECONDS);
        assertEquals(10, rw(conf).expiration().getDefaultTimeToLive(TimeUnit.SECONDS));
    }

    /**
     * This method is used for saving and reloading a configuration object.
     *
     * @param configuration
     * @return the reloaded configuration
     * @throws Exception
     */
    public static <K, V, T extends AbstractCacheServiceConfiguration<K, V>> T reloadService(
            T configuration, T configuration2) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element parent = doc.createElement(XmlConfigurator.CACHE_TAG); // dummy
        XmlConfigurator xml = new XmlConfigurator();
        Element e = xml.writeCacheService(doc, configuration);
        if (e != null) {
            parent.appendChild(e);
        }
        return (T) xml.readCacheService(parent, configuration2);
    }

    @Test
    public void testOwnConfiguration() throws Exception {
        conf.addConfiguration(new MyConfiguration());
        XmlConfigurator xml = new XmlConfigurator();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element parent = doc.createElement(XmlConfigurator.CACHE_TAG); // dummy

        xml.writeSingleCache(conf, doc, parent);

        MyConf<?, ?> newConf = new MyConf();
        xml.readSingleCache(newConf, parent);
        Object cc = newConf.getConfiguration(MyConfiguration.class);
        assertTrue(((MyConfiguration) cc).isRun);
    }

    static class MyConf<K, V> extends CacheConfiguration<K, V> {

        // visibility
        @Override
        protected AbstractCacheServiceConfiguration<K, V> getConfiguration(Class c) {
            return super.getConfiguration(c);
        }

    }

    static class MyConfiguration extends AbstractCacheServiceConfiguration {

        boolean isRun;

        public MyConfiguration() {
            super("my-conf");
        }

        @Override
        protected void fromXML(Element element) throws Exception {
            assertEquals("foo", element.getAttribute("bad"));
            isRun = true;
        }

        @Override
        protected void toXML(Document doc, Element parent) throws Exception {
            parent.setAttribute("bad", "foo");
        }

    }
}
