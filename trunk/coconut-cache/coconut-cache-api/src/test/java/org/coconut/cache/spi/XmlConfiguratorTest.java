/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.coconut.cache.CacheConfiguration;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlConfiguratorTest {

    static XmlConfigurator c = XmlConfigurator.getInstance();

    @Test
    public void cacheGetsName() throws Exception {
        String noNamed = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<cache-config version=\"" + XmlConfigurator.CURRENT_VERSION + "\">"
                + "<cache/></cache-config>";
        assertNotNull(c.from(new ByteArrayInputStream(noNamed.getBytes())).getName());
    }

    @Test
    public void cacheType() throws Exception {
        String noNamed = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<cache-config version=\"" + XmlConfigurator.CURRENT_VERSION + "\">"
                + "<cache type=\"foo\"/></cache-config>";
        assertEquals("foo", c.from(new ByteArrayInputStream(noNamed.getBytes()))
                .getProperty(XmlConfigurator.CACHE_INSTANCE_TYPE));
    }

    static CacheConfiguration rw(CacheConfiguration conf) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        c.to(conf, os);
        return c.from(new ByteArrayInputStream(os.toByteArray()));
    }

    @Test(expected = NullPointerException.class)
    public void to_NPE1() throws Exception {
        c.to(null, new ByteArrayOutputStream());
    }

    @Test(expected = NullPointerException.class)
    public void to_NPE2() throws Exception {
        c.to(CacheConfiguration.create(), null);
    }

    @Test(expected = NullPointerException.class)
    public void from_NPE1() throws Exception {
        c.from(null, new ByteArrayInputStream(new byte[0]));
    }

    @Test(expected = NullPointerException.class)
    public void from_NPE2() throws Exception {
        c.from(null);
    }
}
