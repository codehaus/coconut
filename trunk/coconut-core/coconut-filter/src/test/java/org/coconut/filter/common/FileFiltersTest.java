/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import junit.framework.JUnit4TestAdapter;

import org.coconut.filter.Filter;
import org.coconut.filter.util.FileFilters;
import org.coconut.filter.util.FileFilters.FileCanReadFilter;
import org.coconut.filter.util.FileFilters.FileCanWriteFilter;
import org.coconut.filter.util.FileFilters.FileExistsFilter;
import org.coconut.filter.util.FileFilters.FileIsDirectoryFilter;
import org.coconut.filter.util.FileFilters.FileIsHiddenFilter;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class FileFiltersTest {

    @Test
    public void testFileCanReadFilter() throws IOException {
        Filter<File> filter = FileFilters.canRead();
        File f = File.createTempFile("ttt", "ttt");
        assertEquals(filter.accept(f), f.canRead());
        f.delete();

        assertEquals(filter, filter);
        assertEquals(filter, new FileCanReadFilter());
        assertEquals(filter.hashCode(), new FileCanReadFilter().hashCode());
        filter.toString();
    }

    @Test
    public void testFileCanWriteFilter() throws IOException {
        Filter<File> filter = FileCanWriteFilter.INSTANCE;
        File f = File.createTempFile("ttt", "ttt");
        assertTrue(filter.accept(f));
        f.setReadOnly();
        assertFalse(filter.accept(f));
        f.delete();
        
        assertEquals(filter, filter);
        assertEquals(filter, new FileCanWriteFilter());
        assertEquals(filter.hashCode(), new FileCanWriteFilter().hashCode());

        filter.toString();
    }

    @Test
    public void testFileExistsFilter() throws IOException {
        Filter<File> filter = FileExistsFilter.INSTANCE;
        File f = File.createTempFile("ttt", "ttt");
        assertTrue(filter.accept(f));
        f.delete();
        assertFalse(filter.accept(f));
        assertEquals(filter, filter);
        assertEquals(filter, new FileExistsFilter());
        assertEquals(filter.hashCode(), new FileExistsFilter().hashCode());

        filter.toString();
    }

    @Test
    public void testFileIsDirectoryFilter() throws IOException {
        Filter<File> filter = FileIsDirectoryFilter.INSTANCE;
        File f = File.createTempFile("ttt", "ttt");
        assertFalse(filter.accept(f));
        assertTrue(filter.accept(f.getParentFile()));
        f.delete();
        
        assertEquals(filter, filter);
        assertEquals(filter, new FileIsDirectoryFilter());
        assertEquals(filter.hashCode(), new FileIsDirectoryFilter().hashCode());
        filter.toString();
    }

    @Test
    public void testFileIsHiddenFilter() throws IOException {
        Filter<File> filter = FileIsHiddenFilter.INSTANCE;
        File f = File.createTempFile("ttt", "ttt");
        assertEquals(filter.accept(f), f.isHidden());
        f.delete();
        
        assertEquals(filter, filter);
        assertEquals(filter, new FileIsHiddenFilter());
        assertEquals(filter.hashCode(), new FileIsHiddenFilter().hashCode());
        filter.toString();
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(FileFiltersTest.class);
    }
}
