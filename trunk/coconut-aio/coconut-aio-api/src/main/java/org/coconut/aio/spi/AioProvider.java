package org.coconut.aio.spi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.coconut.aio.AioFuture;
import org.coconut.aio.management.DatagramGroupMXBean;
import org.coconut.aio.management.DatagramMXBean;
import org.coconut.aio.management.FileMXBean;
import org.coconut.aio.management.ServerSocketMXBean;
import org.coconut.aio.management.SocketGroupMXBean;
import org.coconut.aio.management.SocketMXBean;
import org.coconut.aio.monitor.DatagramGroupMonitor;
import org.coconut.aio.monitor.DatagramMonitor;
import org.coconut.aio.monitor.FileMonitor;
import org.coconut.aio.monitor.ServerSocketMonitor;
import org.coconut.aio.monitor.SocketGroupMonitor;
import org.coconut.aio.monitor.SocketMonitor;


/**
 * Service-provider class for asynchronous channels.
 * <p>
 * A selector provider is a concrete subclass of this class that has a
 * zero-argument constructor and implements the abstract methods specified
 * below. A given invocation of the Java virtual machine maintains a single
 * system-wide default provider instance, which is returned by the {@link
 * #provider provider} method. The first invocation of that method will locate
 * the default provider as specified below.
 * <p>
 * The system-wide default provider is used by the static <tt>open</tt>
 * methods of the {@link org.coconut.aio.AsyncSocket#open AsyncSocket},
 * {@link org.coconut.aio.AsyncSocketGroup#open AsyncSocketGroup},
 * {@link org.coconut.aio.AsyncDatagram#open AsyncDatagram},
 * {@link org.coconut.aio.AsyncDatagramGroup#open AsyncDatagramGroup}, {@link
 * org.coconut.aio.AsyncServerSocket#open AsyncServerSocket}, and {@link
 * org.coconut.aio.AsyncFile#open AsyncFile} classes. 
 * <p>
 * A program may make use of a provider other than the default provider
 * by instantiating that provider and then directly invoking the <tt>open</tt> ({link AsyncFactory})
 * methods defined in this class.
 * <p>
 * All of the methods in this class are safe for use by multiple concurrent
 * threads.
 * </p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public abstract class AioProvider implements AsyncFactory {
    private static final Object lock = new Object();
    private static AioProvider provider;
    protected final static Properties properties = new Properties();
    protected final static Properties messages = new Properties();
    /**
     * Initializes a new instance of this class.
     * </p>
     * 
     * @throws SecurityException If a security manager has been installed and it
     *             denies {@link RuntimePermission}
     *             <tt>("aioProvider")</tt>
     */
    protected AioProvider() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null)
            sm.checkPermission(new RuntimePermission("aioProvider"));
    }
    protected static Object instantiateObject(String className) {
        if (className == null)
            throw new NullPointerException("className");
        try {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            final Class c = Class.forName(className, true, loader);
            return c.newInstance();
        } catch (ClassNotFoundException x) {
            throw new RuntimeException(x);
        } catch (IllegalAccessException x) {
            throw new RuntimeException(x);
        } catch (InstantiationException x) {
            throw new RuntimeException(x);
        } catch (SecurityException x) {
            throw new RuntimeException(x);
        }
    }
    /**
     * Returns the system-wide default AIO provider for this invocation of the
     * Java virtual machine.
     * <p>
     * The first invocation of this method locates the default provider object
     * as follows:
     * </p>
     * <ol>
     * <li>
     * <p>
     * If the system property <tt>coconut.aio.spi.AioProvider</tt>
     * is defined then it is taken to be the fully-qualified name of a concrete
     * provider class. The class is loaded and instantiated; if this process
     * fails then an unspecified error is thrown.
     * </p>
     * </li>
     * <li>
     * <p>
     * If a provider class has been installed in a jar file that is visible to
     * the system class loader, and that jar file contains a
     * provider-configuration file named
     * <tt>java.nio.channels.spi.SelectorProvider</tt> in the resource
     * directory <tt>META-INF/services</tt>, then the first class name
     * specified in that file is taken. The class is loaded and instantiated; if
     * this process fails then an unspecified error is thrown.
     * </p>
     * </li>
     * <li>
     * <p>
     * Finally, if no provider has been specified by any of the above means then
     * the system-default provider class is instantiated and the result is
     * returned.
     * </p>
     * </li>
     * </ol>
     * <p>
     * Subsequent invocations of this method return the provider that was
     * returned by the first invocation.
     * </p>
     * 
     * @return The system-wide default selector provider
     */
    public static AioProvider provider() {
        //TODO remove lock for common case (provider allready instantiated)
        synchronized (lock) {
            if (provider != null)
                return provider;
            {
                InputStream isMsg = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("org/coconut/aio/messages.conf");
                try {
                    messages.load(isMsg);
                } catch (IOException ioe) {
                    throw new IllegalStateException(
                        "The internal default messages.conf is missing. "
                            + "This is highly irregular, your Coconut JAR is "
                            + "most likely corrupt.");
                }
                InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("org/coconut/aio/coconut-aio.conf");
                Properties props = new Properties();
                try {
                    props.load(is);
                } catch (IOException ioe) {
                    throw new IllegalStateException(
                        "The internal default coconut-aio.conf is missing. "
                            + "This is highly irregular, your Coconut JAR is "
                            + "most likely corrupt.");
                }
                Properties system = System.getProperties();
                for (Enumeration en = system.propertyNames(); en.hasMoreElements();) {
                    String s = (String) en.nextElement();
                    if (s.startsWith("org.coconut.aio") && !properties.contains(s)) {
                        properties.put(s, system.getProperty(s));
                    }
                }
                for (Enumeration en = props.propertyNames(); en.hasMoreElements();) {
                    String s = (String) en.nextElement();
                    if (s.startsWith("org.coconut.aio") && !properties.contains(s)) {
                        properties.put(s, props.getProperty(s));
                    }
                }
            }
            String cn = properties.getProperty("coconut.aio.spi.AioProvider");
            provider = (AioProvider) instantiateObject(cn);
            return provider;
        }
    }
    public static void setProperty(String key, String value) {
        synchronized (lock) {
            properties.setProperty(key, value);
        }
    }
    public abstract void shutdown();
    public abstract Map<?, List<AioFuture>> shutdownNow();
    public abstract void setDefaultMonitor(ServerSocketMonitor m);
    public abstract ServerSocketMonitor getDefaultServerSocketMonitor();

    public abstract void setDefaultMonitor(SocketMonitor m);
    public abstract SocketMonitor getDefaultSocketMonitor();
    public abstract void setDefaultMonitor(SocketGroupMonitor m);
    public abstract SocketGroupMonitor getDefaultSocketGroupMonitor();

    public abstract void setDefaultMonitor(DatagramMonitor m);
    public abstract DatagramMonitor getDefaultDatagramMonitor();
    public abstract void setDefaultMonitor(DatagramGroupMonitor m);
    public abstract DatagramGroupMonitor getDefaultDatagramGroupMonitor();

    public abstract void setDefaultMonitor(FileMonitor m);
    public abstract FileMonitor getDefaultFileMonitor();

    public abstract ServerSocketMXBean getServerSocketMXBean();
    public abstract SocketMXBean getSocketMXBean();
    public abstract SocketGroupMXBean getSocketGroupMXBean();
    public abstract DatagramMXBean getDatagramMXBean();
    public abstract DatagramGroupMXBean getDatagramGroupMXBean();
    public abstract FileMXBean getFileMXBean();

    public abstract void setErrorHandler(AioErrorHandler< ? > handler);
    public abstract AioErrorHandler< ? > getErrorHandler();
}