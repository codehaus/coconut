/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import junit.framework.JUnit4TestAdapter;

import org.coconut.filter.FileFilters.FileInDirectoryFilter;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class FileFiltersInDirectoryTest {

    @Test
    public void testFilter() throws IOException {
        File f = File.createTempFile("ttt", "ttt");
        File childDir = new File(f.getParentFile(), "mydir");
        childDir.mkdir();
        File childFile = new File(childDir, "myfile");
        FileInDirectoryFilter filter = new FileInDirectoryFilter(f
                .getParentFile());
        assertFalse(filter.getIncludeSubdirectories());
        assertEquals(f.getParentFile(), filter.getDirectory());
        assertTrue(filter.accept(f));
        assertTrue(filter.accept(childDir));
        assertFalse(filter.accept(childFile));
        f.delete();
        childDir.delete();
        childFile.delete();
        filter.toString();
    }

    @Test
    public void testFilterSubdir() throws IOException {
        File f = File.createTempFile("ttt", "ttt");
        File childDir = new File(f.getParentFile(), "mydir");
        childDir.mkdir();
        File childChildDir = new File(childDir, "mydir");
        childChildDir.mkdir();
        File childDir2 = new File(f.getParentFile(), "mydir2");
        childDir2.mkdir();
        File childFile = new File(childDir, "myfile");
        FileInDirectoryFilter filter = new FileInDirectoryFilter(childDir, true);
        assertTrue(filter.getIncludeSubdirectories());
        assertEquals(childDir, filter.getDirectory());
        assertFalse(filter.accept(f));
        assertFalse(filter.accept(childDir));
        assertTrue(filter.accept(childFile));
        assertTrue(filter.accept(childChildDir));
        f.delete();
        childFile.delete();
        childDir.delete();
        childDir2.delete();
        filter.toString();
    }

    @Test
    public void testEquals() throws IOException {
        File f = File.createTempFile("ttt", "ttt");
        File f1 = new File(f.getParentFile(), "mydir");
        f1.mkdir();
        File f2 = new File(f.getParentFile(), "mydir2");
        f2.mkdir();
        
        FileInDirectoryFilter filter = new FileInDirectoryFilter(f1, true);
        assertEquals(filter, filter);
        assertFalse(filter.equals(new Object()));
        assertEquals(filter, new FileInDirectoryFilter(f1, true));
        assertEquals(new FileInDirectoryFilter(f1, false), new FileInDirectoryFilter(f1, false));
        assertFalse(filter.equals(new FileInDirectoryFilter(f1, false)));
        assertFalse(filter.equals(new FileInDirectoryFilter(f2, true)));
        f.delete();
        f1.delete();
        f2.delete();
    }

    @Test
    public void testHashCode() throws IOException {
        File f = File.createTempFile("ttt", "ttt");
        File f1 = new File(f.getParentFile(), "mydir");
        f1.mkdir();
        File f2 = new File(f.getParentFile(), "mydir2");
        f2.mkdir();
        assertEquals(new FileInDirectoryFilter(f1, true).hashCode(),
                new FileInDirectoryFilter(f1, true).hashCode());
        assertFalse(new FileInDirectoryFilter(f1, true).hashCode() == new FileInDirectoryFilter(f1, false).hashCode());
        assertFalse(new FileInDirectoryFilter(f1, true).hashCode() == new FileInDirectoryFilter(f2, false).hashCode());
        f.delete();
        f1.delete();
        f2.delete();
    }

    
    @Test(expected = NullPointerException.class)
    public void testFilterAcceptNull() throws IOException {
        File f = File.createTempFile("ttt", "ttt");
        FileInDirectoryFilter filter = new FileInDirectoryFilter(f
                .getParentFile());
        try {
            filter.accept(null);
        } finally {
            f.delete();
        }

    }

    @Test(expected = NullPointerException.class)
    public void testFileFilterNull() {
        new FileInDirectoryFilter(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFileNotDirectory() throws IOException {
        File f = File.createTempFile("ttt", "ttt");
        try {
            new FileInDirectoryFilter(f);
        } finally {
            f.delete();
        }
    }
    
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(FileFiltersInDirectoryTest.class);
    }
}
