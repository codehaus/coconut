/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;

import org.coconut.filter.spi.FilterAcceptTypesAnnotation;

/**
 * This file contains common file Filters used in Coconut.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: FileFilters.java 36 2006-08-22 09:59:45Z kasper $
 */
public class FileFilters {

    /**
     * Returns a Filter that accepts all {@link java.io.File Files} that are
     * readable.
     */
    public static FileCanReadFilter canRead() {
        return FileCanReadFilter.INSTANCE;
    }

    /**
     * A Filter that accepts all {@link java.io.File Files} that are readable.
     * <p>
     */
    @FilterAcceptTypesAnnotation(File.class)
    public final static class FileCanReadFilter implements Filter<File>,
            FileFilter, Serializable {

        /** A default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -7655221428681897754L;

        /** Same hashcode for every Filter of this type. */
        private static final int hashcode = FileCanReadFilter.class.getName()
                .hashCode();

        /** A default instance of a FileCanReadFilter. */
        public static final FileCanReadFilter INSTANCE = new FileCanReadFilter();

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        public boolean accept(File file) {
            return file.canRead();
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "file is readable";
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof FileCanReadFilter;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return hashcode;
        }
    }

    /**
     * A Filter that accepts all {@link java.io.File Files} that are writable.
     * <p>
     * Instead of constructing a new instance of this class, use
     * {@link #INSTANCE}.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     * @version $Id: FileFilters.java 36 2006-08-22 09:59:45Z kasper $
     */
    @FilterAcceptTypesAnnotation(File.class)
    public final static class FileCanWriteFilter implements Filter<File>,
            FileFilter, Serializable {

        /** A default instance of a FileCanReadFilter. */
        public static final FileCanWriteFilter INSTANCE = new FileCanWriteFilter();

        /** A default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3544385898989828152L;

        /** Same hashcode for every Filter of this type. */
        private static final int hashcode = FileCanReadFilter.class.getName()
                .hashCode();

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        public boolean accept(File file) {
            return file.canWrite();
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof FileCanWriteFilter;

        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "file is writable";
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return hashcode;
        }
    }

    /**
     * A Filter that accepts all {@link java.io.File Files} that exists.
     * <p>
     * Instead of constructing a new instance of this class, use
     * {@link #INSTANCE}.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     * @version $Id: FileFilters.java 36 2006-08-22 09:59:45Z kasper $
     */
    @FilterAcceptTypesAnnotation(File.class)
    public final static class FileExistsFilter implements Filter<File>,
            FileFilter, Serializable {

        /** A default instance of a FileExistsFilter. */
        public static final FileExistsFilter INSTANCE = new FileExistsFilter();

        /** A default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3257850965406265907L;

        /** Same hashcode for every Filter of this type. */
        private static final int hashcode = FileExistsFilter.class.getName()
                .hashCode();

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        public boolean accept(File file) {
            return file.exists();
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "file exists";
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof FileExistsFilter;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return hashcode;
        }
    }

    /**
     * A Filter that accepts all {@link java.io.File Files} with a given
     * extension.
     * <p>
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     * @version $Id: FileFilters.java 36 2006-08-22 09:59:45Z kasper $
     */
    @FilterAcceptTypesAnnotation(File.class)
    public final static class FileExtensionFilter implements Filter<File>,
            FileFilter, Serializable {

        /** A predifined casesensitive Filter that looks for all .java files. */
        public static final FileExtensionFilter EXT_JAVA = new FileExtensionFilter(
                "java");

        /** A predifined casesensitive Filter that looks for all .xml files. */
        public static final FileExtensionFilter EXT_XML = new FileExtensionFilter(
                "xml");

        /** A predifined casesensitive Filter that looks for all .txt files. */
        public static final FileExtensionFilter EXT_TXT = new FileExtensionFilter(
                "txt");

        /** A default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258408439225857329L;

        /** The extension we are looking for. */
        private final String extension;

        /** Whether or not we are case sensitive. */
        private final boolean isCaseSensitive;

        /**
         * Constructs a new <tt>case sensitive</tt> FileExtension Filter.
         * 
         * @param extension
         *            the extension
         * @throws NullPointerException
         *             if the extension is <code>null</code>
         */
        public FileExtensionFilter(String extension) {
            this(extension, true);
        }

        public FileExtensionFilter(String extension, boolean isCaseSensitive) {
            if (extension == null) {
                throw new NullPointerException("extension is null");
            }
            this.isCaseSensitive = isCaseSensitive;
            // TODO should we append a "."
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }

        public boolean isCaseSensitive() {
            return isCaseSensitive;
        }

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        public boolean accept(File file) {
            if (file == null) {
                throw new NullPointerException("file is null");
            }
            if (isCaseSensitive) {
                return file.getAbsolutePath().endsWith(extension);
            } else {
                String path = file.getAbsolutePath();

                if (extension.length() > path.length()) {
                    return false;
                }

                String end = path.substring(path.length() - extension.length());
                return extension.equalsIgnoreCase(end);
            }
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            if (isCaseSensitive) {
                return "file ends with '" + extension + "' (ignoring case)";
            } else {
                return "file ends with '" + extension + "'";
            }
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return this == obj
                    || (obj instanceof FileExtensionFilter && equals((FileExtensionFilter) obj));
        }

        /**
         * Typed equals, as per normal equals contract
         * 
         * @param the
         *            reference filter with which to compare.
         * @return <tt>true</tt> if this object is the same as the filter
         *         argument; <tt>false</tt> otherwise.
         */
        public boolean equals(FileExtensionFilter filter) {
            if (isCaseSensitive) {
                return filter.isCaseSensitive
                        && extension.equals(filter.extension);
            } else {
                return !filter.isCaseSensitive
                        && extension.equalsIgnoreCase(filter.extension);
            }
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            if (isCaseSensitive) {
                return extension.hashCode();
            } else {
                return extension.hashCode() << 1;
            }
        }
    }

    /**
     * A Filter that accepts all {@link java.io.File Files} that are contained
     * in a particular directory possible including subdirectories.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     * @version $Id: FileFilters.java 36 2006-08-22 09:59:45Z kasper $
     */
    @FilterAcceptTypesAnnotation(File.class)
    public final static class FileInDirectoryFilter implements Filter<File>,
            FileFilter, Serializable {

        /** A default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258411750712883504L;

        /** Whether or not to include subdirectories. */
        private final boolean includeSubdirectories;

        /** The directory the file should be included in. */
        private final File directory;

        /**
         * Create a new FileInDirectoryFilter filter.
         * 
         * @param directory
         *            the directory that files should be included in.
         */
        public FileInDirectoryFilter(final File directory) {
            this(directory, false);
        }

        /**
         * Create a new FileInDirectoryFilter filter.
         * 
         * @param directory
         *            the directory that files should be included in.
         * @param includeSubdirectories
         *            whether or not to include subdirectories.
         */
        public FileInDirectoryFilter(final File directory,
                final boolean includeSubdirectories) {
            if (directory == null) {
                throw new NullPointerException("directory is null");
            }
            if (!directory.isDirectory()) {
                throw new IllegalArgumentException("file is not a directory");
            }
            this.includeSubdirectories = includeSubdirectories;
            this.directory = directory;
        }

        /**
         * Returns the directory that files should be included in.
         */
        public File getDirectory() {
            return directory;
        }

        /**
         * Returns whether or not to include subdirectories.
         */
        public boolean getIncludeSubdirectories() {
            return includeSubdirectories;
        }

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        public boolean accept(File file) {
            if (file == null) {
                throw new NullPointerException("file is null");
            }
            if (includeSubdirectories) {
                File parent = file.getParentFile();
                while (parent != null) {
                    if (directory.equals(parent)) {
                        return true;
                    }
                    parent = parent.getParentFile();
                }
                return false;
            } else {
                File parent = file.getParentFile();
                return parent != null && parent.equals(directory);
            }
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            if (includeSubdirectories) {
                return "file in directory '" + directory
                        + "' or any of it subdirectories";
            } else {
                return "file in directory '" + directory + "'";
            }
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return this == obj
                    || (obj instanceof FileInDirectoryFilter && equals((FileInDirectoryFilter) obj));
        }

        /**
         * Typed equals, as per normal equals contract
         * 
         * @param the
         *            reference filter with which to compare.
         * @return <tt>true</tt> if this object is the same as the filter
         *         argument; <tt>false</tt> otherwise.
         */
        public boolean equals(FileInDirectoryFilter filter) {
            return includeSubdirectories == filter.includeSubdirectories
                    && directory.equals(filter.directory);

        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            if (includeSubdirectories) {
                return directory.hashCode();
            } else {
                return directory.hashCode() << 1;
            }
        }
    }

    /**
     * A Filter that accepts all {@link java.io.File Files} that are hidden.
     * <p>
     * Instead of constructing a new instance of this class, use
     * {@link #INSTANCE}.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     * @version $Id: FileFilters.java 36 2006-08-22 09:59:45Z kasper $
     */
    @FilterAcceptTypesAnnotation(File.class)
    public final static class FileIsHiddenFilter implements Filter<File>,
            FileFilter, Serializable {

        /** The default FileIsHiddenFilter instance. */
        public static final FileIsHiddenFilter INSTANCE = new FileIsHiddenFilter();

        /** A default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3760840164653742384L;

        /** Same hashcode for every Filter of this type. */
        private static final int hashcode = FileIsHiddenFilter.class.getName()
                .hashCode();

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        public boolean accept(File file) {
            return file.isHidden();
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "file is hidden";
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof FileIsHiddenFilter;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return hashcode;
        }
    }

    /**
     * A Filter that accepts all {@link java.io.File Files} that are a
     * directory.
     * <p>
     * Instead of constructing a new instance of this class, use
     * {@link #INSTANCE}.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
     * @version $Id: FileFilters.java 36 2006-08-22 09:59:45Z kasper $
     */
    @FilterAcceptTypesAnnotation(File.class)
    public final static class FileIsDirectoryFilter implements Filter<File>,
            Serializable {

        /** The default FileIsDirectoryFilter instance. */
        public static final FileIsDirectoryFilter INSTANCE = new FileIsDirectoryFilter();

        /** A default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258126947053024568L;

        /** Same hashcode for every Filter of this type. */
        private static final int hashcode = FileIsDirectoryFilter.class
                .getName().hashCode();

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        public boolean accept(File file) {
            return file.isDirectory();
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "file is a directory";
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof FileIsDirectoryFilter;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return hashcode;
        }
    }
}
