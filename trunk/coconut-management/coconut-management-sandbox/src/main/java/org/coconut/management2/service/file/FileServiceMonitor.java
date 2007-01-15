/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.service.file;

import java.io.File;

import org.coconut.core.Log;
import org.coconut.filter.Filter;
import org.coconut.management.annotation.ManagedOperation;
import org.coconut.management2.service.ServiceCheckStatus;
import org.coconut.management2.service.spi.AbstractServiceMonitor;
import org.coconut.management2.service.spi.AbstractServiceCheckerSession;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class FileServiceMonitor<V> extends AbstractServiceMonitor {
    private String filename;

    /**
     * @return the filename
     */
    public synchronized String getFilename() {
        return filename;
    }

    /**
     * @param filename
     *            the filename to set
     */
    public synchronized void setFilename(String filename) {
        this.filename = filename;
    }

    static class FileFilterServiceSession extends AbstractServiceCheckerSession<Boolean> {
        private final File name;

        private final Filter<File> filter;

        private final String ok;

        private final String error;

        /**
         * @return the name
         */
        @ManagedOperation(defaultValue = "filename", description = "the name of the file to check")
        public synchronized String getName() {
            return name.getPath();
        }

        /**
         * @param name
         */
        public FileFilterServiceSession(final String name, final Filter<File> filter,
                String ok, String error) {
            if (name == null) {
                throw new NullPointerException("name is null");
            } else if (filter == null) {
                throw new NullPointerException("filter is null");
            }
            this.name = new File(name);
            this.filter = filter;
            this.ok = ok;
            this.error = error;
        }

        /**
         * @see org.coconut.management2.service.AbstractServiceCheckerSession#doRun()
         */
        @Override
        protected Boolean doRun() {
            log(Log.Level.Info, "Starting check on " + name);
            if (filter.accept(name)) {
                setStatus(ServiceCheckStatus.OK);
                log(Log.Level.Info, ok);
                return true;
            } else {
                setStatus(ServiceCheckStatus.ERROR);
                log(Log.Level.Info, error);
                return false;
            }
        } 
    }
}
