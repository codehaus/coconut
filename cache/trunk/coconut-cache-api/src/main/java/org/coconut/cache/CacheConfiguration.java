/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.service.event.CacheEventConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.service.threading.CacheThreadingConfiguration;
import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.core.Clock;
import org.coconut.core.Log;

/**
 * This class is the primary class used for representing the configuration of a
 * cache. All general-purpose <tt>Cache</tt> implementation classes should
 * have a constructor with a single argument taking a CacheConfiguration.
 * <p>
 * <b>Usage Examples.</b> The following creates a new cache with the name
 * MyCache. The cache has a maximum of 1000 elements and uses a
 * least-recently-used policy to determine which elements to evict when the
 * specified maximum size has been reached. Elements will automatically expired
 * after having been in the cache for 1 hour. Finally, the cache is registered
 * as a mbean with the platform mbean server using the name of the cache.
 * 
 * <pre>
 * CacheConfiguration&lt;String, Integer&gt; cc = newConf();
 * cc.setName(&quot;MyCache&quot;);
 * cc.eviction().setPolicy(Policies.newLRU()).setMaximumSize(1000);
 * cc.expiration().setDefaultTimeout(60 * 60, TimeUnit.SECONDS);
 * cc.management().setRegister(true);
 * Cache&lt;String, Integer&gt; instance = cc.newInstance(Select_A_Cache_Impl);
 * </pre>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@NotThreadSafe
public final class CacheConfiguration<K, V> extends AbstractCacheConfiguration<K, V> {

    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private Log defaultLogger;

    private String name;

    private Clock timingStrategy = Clock.DEFAULT_CLOCK;

    /**
     * Creates a new Configuration with default settings. CacheConfiguration
     * instances should be created using the {@link #newConf()} method.
     */
    public CacheConfiguration() {

    }

    /**
     * Returns a new Eviction object.
     */
    public CacheEvictionConfiguration serviceEviction() {
        return lazyCreate(CacheEvictionConfiguration.class);
    }

    /**
     * Returns the {@link org.coconut.core.Clock} that the cache should use.
     * 
     * @return the Clock that the cache should use
     * @see #setClock(Clock)
     */
    public Clock getClock() {
        return timingStrategy;
    }

    /**
     * Returns the default log configured for this cache or <tt>null</tt> if
     * no default log has been configured.
     * 
     * @return the default log configured for this cache or null if no default
     *         log has been configured
     */
    public Log getDefaultLog() {
        return defaultLogger;
    }

    /**
     * Returns the name of the cache.
     * 
     * @return the name of the cache, or <tt>null</tt> if no name has been
     *         set.
     * @see #setName(String)
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a map of additional properties for the cache.
     * 
     * @see #setProperty(String, Object)
     */
    public Map<String, ?> getProperties() {
        return Collections.unmodifiableMap(new HashMap<String, Object>(
                additionalProperties));
    }

    /**
     * Returns the property value for the specified key or <tt>null</tt> if no
     * such property exists. A <tt>null</tt> can also indicate that the key
     * was explicitly mapped to <tt>null</tt>.
     * 
     * @param key
     *            the key for which to retrieve the value
     * @return the value of the property or <tt>null</tt> if no such property
     *         exists
     * @throws NullPointerException
     *             if key is <tt>null</tt>
     */
    public Object getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * Returns the property value for the specified key or the specified default
     * value if no such property exists. A property does not exists if it is
     * mapped to <tt>null</tt> either explicitly or because no such entry
     * exists.
     * 
     * @param key
     *            the key for which to retrieve the value
     * @param defaultValue
     *            the default value to return if the property does not exist
     * @return the value of the property or the specified default value if the
     *         property does not exist
     * @throws NullPointerException
     *             if key is <tt>null</tt>
     */
    public Object getProperty(String key, Object defaultValue) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        Object result = additionalProperties.get(key);
        return result == null ? defaultValue : result;
    }

    /**
     * Creates a new cache instance of the specified type using this
     * configuration.
     * 
     * @param clazz
     *            the clazz the type of cache that should be created
     * @return a new Cache instance
     * @throws IllegalArgumentException
     *             if specified class does not contain a public constructor
     *             taking a single CacheConfiguration argument
     * @throws NullPointerException
     *             if the specified class is <tt>null</tt>
     */
    public <T extends Cache<K, V>> T newInstance(Class<? extends Cache> type)
            throws IllegalArgumentException {
        if (type == null) {
            throw new NullPointerException("clazz is null");
        }
        T cache = null;
        Constructor<T> c = null;
        try {
            c = (Constructor<T>) type.getDeclaredConstructor(CacheConfiguration.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Could not create cache instance, no public contructor taking a single CacheConfiguration instance",
                    e);
        }
        try {
            cache = c.newInstance(this);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "Could not create cache instance, specified clazz " + type
                            + ") is an interface or abstract class", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not create instance of " + type, e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Constructor threw exception", e);
        }
        return cache;
    }

    public CacheEventConfiguration serviceEvent() {
        return lazyCreate(CacheEventConfiguration.class);
    }

    public CacheExpirationConfiguration<K, V> serviceExpiration() {
        return lazyCreate(CacheExpirationConfiguration.class);
    }

    public CacheLoadingConfiguration<K, V> serviceLoading() {
        return lazyCreate(CacheLoadingConfiguration.class);
    }

    public CacheManagementConfiguration<K, V> serviceManagement() {
        return lazyCreate(CacheManagementConfiguration.class);
    }

    public CacheThreadingConfiguration serviceThreading() {
        return lazyCreate(CacheThreadingConfiguration.class);
    }

    /**
     * Sets the {@link org.coconut.core.Clock} that the cache should use.
     * Normally users should not need to set this, only if they want to provide
     * another timing mechanism then the built-in
     * {@link java.lang.System#currentTimeMillis()}. For example, a custom NTP
     * protocol.
     * <p>
     * 
     * @param timingStrategy
     *            the Timer to use
     * @return this configuration
     * @throws NullPointerException
     *             if the supplied timingStrategy is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setClock(Clock timingStrategy) {
        if (timingStrategy == null) {
            throw new NullPointerException("timingStrategy is null");
        }
        this.timingStrategy = timingStrategy;
        return this;
    }

    /**
     * Sets the default log for this cache. If a service needs to log
     * information and no special logger has been set for the service. This log
     * should be used.
     * 
     * @param logger
     *            the log to use
     * @return this configuration
     * @see #getDefaultLog()
     */
    public CacheConfiguration<K, V> setDefaultLog(Log logger) {
        this.defaultLogger = logger;
        return this;
    }

    /**
     * Sets the name of the cache. The name should be unique among other
     * configured caches. The name must consists only of alphanumeric characters
     * and '_' or '-'.
     * <p>
     * If no name is set, a cache implementation must generate an unique name to
     * for the cache. How exactly the name is generated is implementation
     * specific.
     * 
     * @param name
     *            the name of the cache
     * @return this configuration
     * @see #getName()
     */
    public CacheConfiguration<K, V> setName(String name) {
        if (name != null) {
            if (name.equals("")) {
                throw new IllegalArgumentException(
                        "Cannot specify the empty string as a name");
            } else if (!Pattern.matches("[\\da-zA-Z\\x5F\\x2D]+", name)) {
                throw new IllegalArgumentException(
                        "not a valid name, must only contain alphanumeric characters and '_' or '-', was "
                                + name);
            }
        }
        this.name = name;
        return this;
    }

    /**
     * Some cache implementations might allow additional properties to be set
     * then those defined by this class. This method can be used to set these
     * additional properties.
     * 
     * @param key
     *            the key of the property
     * @param value
     *            the value of the property
     * @return this configuration
     * @see #getProperties()
     * @see #getProperty(String)
     * @see #getProperty(String, Object)
     * @throws NullPointerException
     *             if key is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setProperty(String key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        additionalProperties.put(key, value);
        return this;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        ByteArrayOutputStream sos = new ByteArrayOutputStream();
        try {
            XmlConfigurator.getInstance().to(this, sos);
        } catch (Exception e) {
            throw new CacheException("This is highly irregular, please report", e);
        }
        return new String(sos.toByteArray());

    }

    /**
     * Creates a new CacheConfiguration with default settings.
     * 
     * @return a new CacheConfiguration
     */
    public static <K, V> CacheConfiguration<K, V> create() {
        return new CacheConfiguration<K, V>();
    }

    /**
     * Creates a new CacheConfiguration with the specified name.
     * 
     * @param name
     *            the name of the cache
     * @return a new CacheConfiguration with the specified name
     */
    public static <K, V> CacheConfiguration<K, V> create(String name) {
        CacheConfiguration<K, V> conf = new CacheConfiguration<K, V>();
        conf.setName(name);
        return conf;
    }

    /**
     * Creates a CacheConfiguration from the specified input stream.
     * @param isXml
     *            the inputstream from where to read the xml configuration
     * @return a CacheConfiguration reflecting the xml configuration
     * @throws Exception
     *             something happended while reading from the stream
     */
    public static <K, V> CacheConfiguration<K, V> createFrom(InputStream isXml)
            throws Exception {
        return XmlConfigurator.getInstance().from(isXml);
    }

    /**
     * Creates a new
     * 
     * @param is
     * @return
     * @throws Exception
     */
    public static <K, V> Cache<K, V> createAndInstantiate(InputStream isXml)
            throws Exception {
        CacheConfiguration<K, V> conf = createFrom(isXml);
        return conf.newInstance((Class) Class.forName(conf.getProperty(
                XmlConfigurator.CACHE_INSTANCE_TYPE).toString()));
    }

}
