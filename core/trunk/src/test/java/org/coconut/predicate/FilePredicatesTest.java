/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.coconut.predicate.FilePredicates;
import org.coconut.predicate.Predicate;
import org.coconut.predicate.FilePredicates.FileCanReadFilter;
import org.coconut.predicate.FilePredicates.FileCanWriteFilter;
import org.coconut.predicate.FilePredicates.FileExistsFilter;
import org.coconut.predicate.FilePredicates.FileIsDirectoryFilter;
import org.coconut.predicate.FilePredicates.FileIsHiddenFilter;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class FilePredicatesTest {

    @Test
    public void testFileCanReadFilter() throws IOException {
        Predicate<File> filter = FilePredicates.canRead();
        File f = File.createTempFile("ttt", "ttt");
        assertEquals(filter.evaluate(f), f.canRead());
        f.delete();

        assertEquals(filter, filter);
        assertEquals(filter, new FileCanReadFilter());
        assertEquals(filter.hashCode(), new FileCanReadFilter().hashCode());
        filter.toString();
    }

    @Test
    public void testFileCanWriteFilter() throws IOException {
        Predicate<File> filter = FileCanWriteFilter.INSTANCE;
        File f = File.createTempFile("ttt", "ttt");
        assertTrue(filter.evaluate(f));
        assertTrue(f.setReadOnly());
        // TODO Does not appear to be working under linux
        // assertFalse(filter.accept(f));
        f.delete();

        assertEquals(filter, filter);
        assertEquals(filter, new FileCanWriteFilter());
        assertEquals(filter.hashCode(), new FileCanWriteFilter().hashCode());

        filter.toString();
    }

    @Test
    public void testFileExistsFilter() throws IOException {
        Predicate<File> filter = FileExistsFilter.INSTANCE;
        File f = File.createTempFile("ttt", "ttt");
        assertTrue(filter.evaluate(f));
        f.delete();
        assertFalse(filter.evaluate(f));
        assertEquals(filter, filter);
        assertEquals(filter, new FileExistsFilter());
        assertEquals(filter.hashCode(), new FileExistsFilter().hashCode());

        filter.toString();
    }

    @Test
    public void testFileIsDirectoryFilter() throws IOException {
        Predicate<File> filter = FileIsDirectoryFilter.INSTANCE;
        File f = File.createTempFile("ttt", "ttt");
        assertFalse(filter.evaluate(f));
        assertTrue(filter.evaluate(f.getParentFile()));
        f.delete();

        assertEquals(filter, filter);
        assertEquals(filter, new FileIsDirectoryFilter());
        assertEquals(filter.hashCode(), new FileIsDirectoryFilter().hashCode());
        filter.toString();
    }

    @Test
    public void testFileIsHiddenFilter() throws IOException {
        Predicate<File> filter = FileIsHiddenFilter.INSTANCE;
        File f = File.createTempFile("ttt", "ttt");
        assertEquals(filter.evaluate(f), f.isHidden());
        f.delete();

        assertEquals(filter, filter);
        assertEquals(filter, new FileIsHiddenFilter());
        assertEquals(filter.hashCode(), new FileIsHiddenFilter().hashCode());
        filter.toString();
    }

}
