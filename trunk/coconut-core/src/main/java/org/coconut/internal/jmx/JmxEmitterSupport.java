/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.jmx;

import java.util.Properties;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.StandardMBean;

import org.coconut.core.Offerable;

/**
 * Helper class. Any easier way to get notification in there???
 */
public abstract class JmxEmitterSupport extends StandardMBean implements
        NotificationEmitter, Offerable<Notification> {
    private final NotificationBroadcasterSupport nbs = new NotificationBroadcasterSupport();

    private final Properties messages;


    private final Class clazz;
    private MBeanInfo minfo;

    protected JmxEmitterSupport(Properties messages, Class mxbeanInterface)
            throws NotCompliantMBeanException {
        super(mxbeanInterface);
        if (messages == null) {
            throw new NullPointerException("messages is null");
        } else if (mxbeanInterface == null) {
            throw new NullPointerException("mxbeanInterface is null");
        }
        this.clazz = mxbeanInterface;
        this.messages = messages;
    }

    private MBeanNotificationInfo[] notifInfo = null;

    protected abstract MBeanNotificationInfo[] getNotifInfo();

    public synchronized MBeanNotificationInfo[] getNotificationInfo() {
        if (notifInfo == null) {
            notifInfo = getNotifInfo();
        }
        return notifInfo.clone();
    }

    public synchronized MBeanInfo getMBeanInfo() {
        if (minfo != null) {
            return minfo;
        }
        MBeanInfo info = super.getMBeanInfo();

        minfo = new MBeanInfo(info.getClassName(), info.getDescription(), info
                .getAttributes(), info.getConstructors(), info.getOperations(),
                getNotificationInfo());
        return minfo;
    }

    /**
     * @see javax.management.NotificationEmitter#removeNotificationListener(javax.management.NotificationListener,
     *      javax.management.NotificationFilter, java.lang.Object)
     */
    public void removeNotificationListener(NotificationListener listener,
            NotificationFilter filter, Object handback)
            throws ListenerNotFoundException {
        nbs.removeNotificationListener(listener, filter, handback);
    }

    /**
     * @see javax.management.NotificationBroadcaster#addNotificationListener(javax.management.NotificationListener,
     *      javax.management.NotificationFilter, java.lang.Object)
     */
    public void addNotificationListener(NotificationListener listener,
            NotificationFilter filter, Object handback)
            throws IllegalArgumentException {
        nbs.addNotificationListener(listener, filter, handback);
    }

    /**
     * @see javax.management.NotificationBroadcaster#removeNotificationListener(javax.management.NotificationListener)
     */
    public void removeNotificationListener(NotificationListener listener)
            throws ListenerNotFoundException {
        nbs.removeNotificationListener(listener);

    }

    public void sendNotification(Notification n) {
        nbs.sendNotification(n);
    }

    /**
     * @see org.coconut.core.Offerable#offer(java.lang.Object)
     */
    public boolean offer(Notification element) {
        sendNotification(element);
        return true;
    }

    protected String getDescription(MBeanAttributeInfo info) {
        String msg = messages.getProperty("jmx." + clazz.getName()
                + ".attribute.desc." + info.getName(),
                "No description available.");
        return msg;
    }

    protected String getDescription(MBeanOperationInfo info) {
        String msg = messages.getProperty("jmx." + clazz.getName()
                + ".operation.desc." + info.getName(),
                "No description available.");
        return msg;
    }
}
