/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import junit.framework.JUnit4TestAdapter;

import org.coconut.filter.util.FileFilters.FileExtensionFilter;
import org.coconut.test.MavenDummyTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class FileFiltersExtensionTest extends MavenDummyTest {

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
        assertTrue(filter.accept(new File("/fo.xml")));
        assertFalse(filter.accept(new File("/fo.Xml")));
        assertFalse(filter.accept(new File("/a")));
    }

    @Test
    public void testCaseInsensitive() {
        FileExtensionFilter filter = new FileExtensionFilter("JAVAjava", false);
        assertTrue(filter.accept(new File("/fo.JAVAjava")));
        assertTrue(filter.accept(new File("/fo.jAvAJaVa")));
        assertFalse(filter.accept(new File("/l")));
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
        FileExtensionFilter.EXT_JAVA.accept(null);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(FileFiltersExtensionTest.class);
    }
}
