/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.ListResourceBundle;
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
    public void abstractCacheServiceConfigurationNPE() {
        new AbstractCacheServiceConfiguration(null) {};
    }

    @Test
    public void abstractCacheServiceConfiguration() {
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
    public void abstractCacheServiceConfiguration2() {
        AbstractCacheServiceConfiguration a = new AbstractCacheServiceConfiguration("foo") {};
        assertEquals("foo", a.getServiceName());
    }

//    @Test
//    public void lookup() {
//        AbstractCacheServiceConfiguration a = new AbstractCacheServiceConfiguration("foo", rb) {};
//        assertEquals("b", a.lookup("a"));
//        assertEquals("d", a.lookup("c"));
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void lookupISE() {
//        AbstractCacheServiceConfiguration a = new AbstractCacheServiceConfiguration("foo", null) {};
//        a.lookup("a");
//    }
//
//    @Test(expected = MissingResourceException.class)
//    public void lookupFail() {
//        AbstractCacheServiceConfiguration a = new AbstractCacheServiceConfiguration("foo") {};
//        a.lookup("a");
//    }

    @Test
    public void toString_() {
        AbstractCacheServiceConfiguration a = new AbstractCacheServiceConfiguration("s") {
            @Override
            protected void toXML(Document doc, Element parent) throws Exception {
                parent.appendChild(doc.createElement("foo"));
            }
        };
        assertTrue(a.toString().contains("<s>"));
        assertTrue(a.toString().contains("<foo/>"));
        assertTrue(a.toString().contains("</s>"));
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
