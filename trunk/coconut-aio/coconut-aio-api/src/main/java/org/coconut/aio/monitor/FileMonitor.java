/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.monitor;

import org.coconut.aio.AsyncFile;

/**
 * A <tt>FileMonitor</tt> is used for monitoring important file events.
 * 
 * <p>
 * All methods needs to thread-safe as multiple events might be posted
 * concurrently.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class FileMonitor {

    /**
     * This method is called whenever a new <tt>AsyncFile</tt> is opened. This
     * method is only called if the monitor is set as the default monitor for
     * the <tt>AsyncFile</tt> class.
     * 
     * @param file
     *            the file that was opened
     */
    public void opened(AsyncFile file) {
    }

    /**
     * Called whenever a file is closed either explicitly by the user or due to
     * some exception doing reading/writing or other file methods.
     * 
     * @param file
     *            the file that was closed
     * @param cause
     *            the cause of the close or <tt>null</tt> if the file was
     *            closed by the user
     */
    public void closed(AsyncFile file, Throwable cause) {
    }

}