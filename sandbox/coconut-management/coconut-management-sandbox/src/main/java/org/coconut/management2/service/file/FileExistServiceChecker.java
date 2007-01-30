/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.service.file;

import org.coconut.filter.util.FileFilters;
import org.coconut.management2.service.spi.AbstractServiceCheckerSession;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class FileExistServiceChecker extends FileServiceMonitor<Boolean> {

    public static final String NAME = "file.exists";

    public static final String DESCRIPTION = "Checks whether or not a specified file exists";

    /**
     * @see org.coconut.management2.service.ServiceChecker#newSession()
     */
    public synchronized AbstractServiceCheckerSession<Boolean> newSession() {
        String name = getFilename();
        return new FileFilterServiceSession(name, FileFilters.FileExistsFilter.INSTANCE,
                "File exist", "File did not exist");
    }
}
