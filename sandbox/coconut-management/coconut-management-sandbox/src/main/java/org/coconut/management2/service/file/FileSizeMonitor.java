/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.service.file;

import java.io.File;

import org.coconut.core.util.EventUtils;
import org.coconut.management2.service.spi.AbstractServiceCheckerSession;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class FileSizeMonitor extends FileServiceMonitor<Long> {
    public static final String NAME = "file.size";

    public static final String DESCRIPTION = "Checks whether or not the size of a specified file is within limits";

    private long minimumSize;

    private long maximumSize = Long.MAX_VALUE;

    private long minimumWarningSize;

    private long maximumWarningSize = Long.MAX_VALUE;

    public static void main(String[] args) {
        FileSizeMonitor m = new FileSizeMonitor();
        m.setFilename("c:\\foo.txt");
        m.createAndRun(EventUtils.toSystemOut());
    }

    /**
     * @see org.coconut.management2.service.spi.AbstractServiceChecker#create()
     */
    @Override
    public AbstractServiceCheckerSession<Long> newSession() {
        return new FileSizeMonitorCheck(minimumSize, maximumSize, minimumWarningSize,
                maximumWarningSize, new File(getFilename()));
    }

    class FileSizeMonitorCheck extends AbstractServiceCheckerSession<Long> {
        private final long minimumSize;

        private final long maximumSize;

        private final long minimumWarningSize;

        private final long maximumWarningSize;

        private final File file;

        private volatile long length;

        /**
         * @param minimumSize
         * @param maximumSize
         * @param minimumWarningSize
         * @param maximumWarningSize
         */
        public FileSizeMonitorCheck(final long minimumSize, final long maximumSize,
                final long minimumWarningSize, final long maximumWarningSize,
                final File file) {
            this.minimumSize = minimumSize;
            this.maximumSize = maximumSize;
            this.minimumWarningSize = minimumWarningSize;
            this.maximumWarningSize = maximumWarningSize;
            this.file = file;
            System.out.println(this.maximumSize);
        }

        /**
         * @see org.coconut.management2.service.spi.AbstractServiceCheckerSession#doRun()
         */
        @Override
        protected Long doRun() {
            length = file.length();
            if (length == 0) {
                // do some checks
                if (file.exists()) {
                    setUnknown("File does not exist");
                } else if (file.isDirectory()) {
                    setUnknown("File is a directory");
                }
            }
            if (length < minimumSize) {
                setError("Size of file is below minimum of " + minimumSize
                        + " bytes, was " + length);
            } else if (length > maximumSize) {
                setError("Size of file is above minimum of " + maximumSize
                        + " bytes, was " + length);
            } else if (length < minimumWarningSize) {
                setWarning("Size of file is below minimum of " + minimumWarningSize
                        + " bytes, was " + length);
            } else if (length > maximumWarningSize) {
                setWarning("Size of file is above minimum of " + maximumWarningSize
                        + " bytes, was " + length);
            }
            setOk("File size within limites, length " + length);
            return length;
        }

        /**
         * @return the length
         */
        public long getLength() {
            return length;
        }

        /**
         * @return the maximumSize
         */
        public long getMaximumSize() {
            return maximumSize;
        }

        /**
         * @return the maximumWarningSize
         */
        public long getMaximumWarningSize() {
            return maximumWarningSize;
        }

        /**
         * @return the minimumSize
         */
        public long getMinimumSize() {
            return minimumSize;
        }

        /**
         * @return the minimumWarningSize
         */
        public long getMinimumWarningSize() {
            return minimumWarningSize;
        }
    }
}
