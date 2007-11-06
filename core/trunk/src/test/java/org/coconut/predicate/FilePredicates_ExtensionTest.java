/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.coconut.predicate.FilePredicates.FileExtensionFilter;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class FilePredicates_ExtensionTest  {

    @Test
    public void testFilterConstructor1() {
        FileExtensionFilter filter = new FileExtensionFilter("xml");
        assertTrue(filter.isCaseSensitive());
        assertEquals("xml", filter.getExtension());
        filter.toString();
    }

    @Test
    public void testFilterConstructor2() {
        FileExtensionFilter filter = new FileExtensionFilter("java", false);
        assertFalse(filter.isCaseSensitive());
        assertEquals("java", filter.getExtension());
        filter.toString();
    }

    @Test
    public void testCaseSensitive() {
        FileExtensionFilter filter = new FileExtensionFilter("xml", true);
        assertTrue(filter.evaluate(new File("/fo.xml")));
        assertFalse(filter.evaluate(new File("/fo.Xml")));
        assertFalse(filter.evaluate(new File("/a")));
    }

    @Test
    public void testCaseInsensitive() {
        FileExtensionFilter filter = new FileExtensionFilter("JAVAjava", false);
        assertTrue(filter.evaluate(new File("/fo.JAVAjava")));
        assertTrue(filter.evaluate(new File("/fo.jAvAJaVa")));
        assertFalse(filter.evaluate(new File("/l")));
        filter.toString();
    }

    @Test
    public void testEquals() {
        FileExtensionFilter filterCase = new FileExtensionFilter("JAVAjava",
                true);
        FileExtensionFilter filterNoCase = new FileExtensionFilter("JAVAjava",
                false);
        assertEquals(filterCase, filterCase);
        assertFalse(filterCase.equals(new Object()));
        assertEquals(filterCase, new FileExtensionFilter("JAVAjava", true));
        assertEquals(filterNoCase, new FileExtensionFilter("JAVAjava", false));
        assertTrue(filterNoCase.equals(new FileExtensionFilter("JAVAJAVA",
                false)));
        assertFalse(filterNoCase.equals(new FileExtensionFilter("JAVAJAVA",
                true)));
        assertFalse(filterCase
                .equals(new FileExtensionFilter("JAVAJAVA", false)));
    }

    @Test
    public void testHashCode() {
        assertEquals(new FileExtensionFilter("abc", true).hashCode(),
                new FileExtensionFilter("abc", true).hashCode());
        assertFalse(new FileExtensionFilter("abc", true).hashCode() == new FileExtensionFilter(
                "abc", false).hashCode());
        assertFalse(new FileExtensionFilter("abc", true).hashCode() == new FileExtensionFilter(
                "cba", true).hashCode());

    }

    @Test(expected = NullPointerException.class)
    public void testFileExtensionFilterNull() {
        new FileExtensionFilter(null);
    }

    @Test(expected = NullPointerException.class)
    public void testFileExtensionFilterNullAccept() {
        FileExtensionFilter.EXT_JAVA.evaluate(null);
    }

}
