/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

import java.util.ListResourceBundle;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.coconut.cache.CacheException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dummy.FailingAbstractCacheServiceConfiguration;

public class AbstraktCacheServiceConfigurationTest {
    private static ResourceBundle rb = new ListResourceBundle() {
        @Override
        protected Object[][] getContents() {
            return new Object[][] { { "a", "b" }, { "c", "d" } };
        }
    };

    @Test(expected = NullPointerException.class)
    public void AbstractCacheServiceConfigurationNPE() {
        new AbstractCacheServiceConfiguration(null) {};
    }

    @Test
    public void AbstractCacheServiceConfiguration() {
        AbstractCacheServiceConfiguration a = new AbstractCacheServiceConfiguration("foo") {};
        assertEquals("foo", a.getServiceName());

    }

    @Test
    public void toFromXML() throws Exception {
        AbstractCacheServiceConfiguration a = new AbstractCacheServiceConfiguration("foo") {};
        a.toXML(null, null);// does nothing
        a.fromXML(null);// does nothing
    }

    @Test
    public void AbstractCacheServiceConfiguration2() {
        AbstractCacheServiceConfiguration a = new AbstractCacheServiceConfiguration("foo", rb) {};
        assertEquals("foo", a.getServiceName());
        assertSame(rb, a.getResourceBundle());
    }

    @Test
    public void lookup() {
        AbstractCacheServiceConfiguration a = new AbstractCacheServiceConfiguration("foo", rb) {};
        assertEquals("b", a.lookup("a"));
        assertEquals("d", a.lookup("c"));
    }

    @Test(expected = IllegalStateException.class)
    public void lookupISE() {
        AbstractCacheServiceConfiguration a = new AbstractCacheServiceConfiguration("foo", null) {};
        a.lookup("a");
    }

    @Test(expected = MissingResourceException.class)
    public void lookupFail() {
        AbstractCacheServiceConfiguration a = new AbstractCacheServiceConfiguration("foo") {};
        a.lookup("a");
    }

    @Test
    public void toString_() {
        AbstractCacheServiceConfiguration a = new AbstractCacheServiceConfiguration("s") {
            @Override
            protected void toXML(Document doc, Element parent) throws Exception {
                parent.appendChild(doc.createElement("foo"));
            }
        };
        String newline = System.getProperty("line.separator");
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + newline
                + "<s>" + newline + "    <foo/>" + newline + "</s>" + newline;
        assertEquals(expected, a.toString());
    }

    @Test(expected = CacheException.class)
    public void toStringFail() {
        AbstractCacheServiceConfiguration a = new AbstractCacheServiceConfiguration("s") {
            @Override
            protected void toXML(Document doc, Element parent) throws Exception {
                throw new Exception();
            }
        };
        a.toString();
    }
    @Test(expected = CacheException.class)
    public void toStringFail2() {
        new FailingAbstractCacheServiceConfiguration().toString();
    }
}
