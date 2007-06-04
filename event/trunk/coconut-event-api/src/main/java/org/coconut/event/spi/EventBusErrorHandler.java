/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.spi;

import java.text.MessageFormat;
import java.util.logging.Level;


import org.coconut.core.Logger;
import org.coconut.core.Loggers;
import org.coconut.event.EventBus;
import org.coconut.event.EventSubscription;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EventBusErrorHandler<E> {
    private Logger logger;

    private boolean isInitialized;

    private volatile String name;

    public EventBusErrorHandler() {

    }

    /**
     * @param default_logger2
     */
    public EventBusErrorHandler(Logger logger) {
        synchronized (this) {
            isInitialized = true;
        }
        this.logger = logger;
    }

    public static final EventBusErrorHandler DEFAULT = new EventBusErrorHandler();

    public void setCacheName(String name) {
        this.name = name;
    }

    public boolean filterFailed(EventSubscription<?> s, final Object element,
            Throwable cause) {
        String msg = "Filter failed to accept event [subscription = " + s.getName()
                + ", element = element]";
        getLogger().error(msg, cause);
        return false;
    }

    public boolean deliveryFailed(EventSubscription<?> s, final Object element,
            Throwable cause) {
        String msg = "Failed to process event [subscription = " + s.getName()
                + ", element = element]";
        getLogger().error(msg, cause);
        return false;
    }

    public Logger getLogger() {
        checkInitialized();
        return logger;
    }

    private synchronized void checkInitialized() {
        if (!isInitialized) {
            isInitialized = true;
            String loggerName = EventBus.class.getPackage().getName() + "." + name;
            java.util.logging.Logger l = java.util.logging.Logger.getLogger(loggerName);
            String infoMsg = Ressources.getString("AbstractEventBus.default_logger");
            logger = Loggers.JDK.from(l);
            logger.info(MessageFormat.format(infoMsg, name, loggerName));
            l.setLevel(Level.SEVERE);
            isInitialized = true;
        }
    }
}
