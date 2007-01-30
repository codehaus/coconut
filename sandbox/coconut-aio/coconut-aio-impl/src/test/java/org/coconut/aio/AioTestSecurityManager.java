/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.FileDescriptor;
import java.security.Permission;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class AioTestSecurityManager extends SecurityManager {
    private boolean listenAllowed = true;
    private boolean connectAllowed = true;
    private AioTestSecurityManager() {

    }

    public static AioTestSecurityManager getInstance() {
        synchronized (AioTestSecurityManager.class) {
            AioTestSecurityManager m = (AioTestSecurityManager) System.getSecurityManager();
            if (m == null) {
                m = new AioTestSecurityManager();
                System.setSecurityManager(m);
            }
            return m;
        }
    }
    public void setListenAllowed(boolean allowed) {
        this.listenAllowed = allowed;
    }

    public void setConnectAllowed(boolean allowed) {
        this.connectAllowed = allowed;
    }

    public void checkListen(int port) {
        if (!listenAllowed)
            throw new SecurityException();
    }

    public void checkExit(int status) {

    }
    public void checkAccept(String host, int port) {

    }
    public void checkAccess(Thread t) {

    }
    public void checkAccess(ThreadGroup g) {
    }
    public void checkAwtEventQueueAccess() {
    }
    public void checkConnect(String host, int port, Object context) {
        if (!connectAllowed)
            throw new SecurityException();
    }
    public void checkConnect(String host, int port) {
        if (!connectAllowed)
            throw new SecurityException();
    }
    public void checkCreateClassLoader() {
    }
    public void checkDelete(String file) {
    }
    public void checkExec(String cmd) {
    }
    public void checkLink(String lib) {
    }

    public void checkPackageAccess(String pkg) {
    }
    public void checkPackageDefinition(String pkg) {
    }
    public void checkPermission(Permission perm, Object context) {
    }
    public void checkPermission(Permission perm) {

    }
    public void checkPrintJobAccess() {
    }
    public void checkPropertiesAccess() {
    }
    public void checkPropertyAccess(String key) {
    }
    public void checkRead(FileDescriptor fd) {
    }
    public void checkRead(String file, Object context) {
    }
    public void checkRead(String file) {
    }
    public void checkSecurityAccess(String target) {
    }
    public void checkSetFactory() {
    }
    public void checkSystemClipboardAccess() {
    }
    public boolean checkTopLevelWindow(Object window) {
        return true;
    }
    public void checkWrite(FileDescriptor fd) {

    }
    public void checkWrite(String file) {
    }

}