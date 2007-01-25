/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.management.MBeanServer;

import org.coconut.annotation.ThreadSafe;
import org.coconut.cache.management.CacheMXBean;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.CacheErrorHandler;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;

/**
 * This class is the primary class used for representing the configuration of a
 * cache. All general-purpose <tt>Cache</tt> implementation classes should
 * have a constructor with a single argument taking a CacheConfiguration.
 * <p>
 * <b>Usage Examples.</b> The following creates a new cache with the name
 * MyCache. The cache has a maximum of 1000 elements and uses a
 * least-recently-used policy to determine which elements to evict when the
 * capacity has been reached. Elements are also marked as expired after not
 * having been request in 1 hour. Finally the cache is registered as a mbean
 * with the platform mbean server using the name of the cache.
 * 
 * <pre>
 * CacheConfiguration&lt;String, Integer&gt; cc = newConf();
 * cc.setName(&quot;MyCache&quot;);
 * cc.eviction().setPolicy(Policies.newLRU()).setMaximumCapacity(1000);
 * cc.expiration().setDefaultTimeout(60 * 60, TimeUnit.SECONDS);
 * cc.jmx().setRegister(true);
 * Cache&lt;String, Integer&gt; instance = cc.newInstance(Select_A_Cache_Impl);
 * </pre>
 * 
 * <p>
 * In addition to the general methods available on CacheConfiguration This class
 * is using a number of inner classes to . This The following categories exist
 * <dl>
 * <dt>{@link Backend}</dt>
 * <dd>Handles storage and loading of entries from cache loaders and cache
 * stores</dd>
 * <dt>{@link Events}</dt>
 * <dd>Handles dispatching of events</dd>
 * <dt>{@link Eviction}</dt>
 * <dd>Handles maximum size of the cache and what entries will be evicted</dd>
 * <dt>{@link Expiration}</dt>
 * <dd>Handles expired items and timeout settings for entries</dd>
 * <dt>{@link JMX}</dt>
 * <dd>Handles JMX registration of the cache</dd>
 * <dt>{@link Locking}</dt>
 * <dd>Handles locking of individual items and lock ordering</dd>
 * </dl>
 * <p>
 * A few point on the naming of methods. Methods postfixed with <tt>Hint</tt>
 * are merely hits to implementation on how to .... and can as such be ignored
 * by any implementation. They are only used to assist an implementation in
 * choosing performance optimal configuration.
 * <p>
 * If Extension of this class is possible with the following TODO what about
 * extensions of this class?? could be usefull for special purpose caches, if so
 * we should change some of the static methods to allow overriding.
 * <p>
 * Currently no XML mappings exists for CacheConfigurations, however this is
 * planned see <a href="http://jira.codehaus.org/browse/COCACHE-2">COCACHE-2</a>
 * <p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@ThreadSafe(false)
public class CacheConfiguration<K, V> implements Cloneable {

    /**
     * The default configuration that can be used by any cache that is not
     * provided with a cache configuration object.
     * TODO what if someone changes the default configuration....!Smart or not
     */
    public static final CacheConfiguration DEFAULT_CONFIGURATION = CacheConfiguration
            .newConf();

    /**
     * The default maximum size of a newly
     */

    // 5F = _
    private final static Pattern CACHE_NAME_PATTERN = Pattern
            .compile("[\\da-zA-Z\\x5F]*(\\x2E([\\da-z\\x5F])+)*");

    public static <K, V> CacheConfiguration<K, V> newConf() {
        return new CacheConfiguration<K, V>();
    }

    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private long defaultExpirationRefreshDuration = -1;

    private long defaultExpirationTimeoutDuration = Cache.NEVER_EXPIRE;

    private String domain = CacheMXBean.DEFAULT_JMX_DOMAIN;

    private CacheErrorHandler<K, V> errorHandler = CacheErrorHandler.getDefault();

    private Executor executor;

    private Filter<CacheEntry<K, V>> expirationFilter;

    private Filter<CacheEntry<K, V>> expirationRefreshFilter;

    private CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> extendedLoader;

    private Map<? extends K, ? extends V> initialMap;

    private CacheLoader<? super K, ? extends V> loader;

    private long maximumCapacity = Long.MAX_VALUE;

    private int maximumSize = Eviction.DEFAULT_MAXIMUM_SIZE;

    private MBeanServer mBeanServer;

    private String name = UUID.randomUUID().toString();

    private long preferableCapacity; // default??

    private int preferableSize; // default??

    /** Whether or not the cache is automatically registered for JMX usage. */
    private boolean registerForJMXAutomatically;

    private ReplacementPolicy replacementPolicy;

    private boolean shutdownExecutor;

    private boolean statisticsEnabled = false;

    private Clock timingStrategy = Clock.DEFAULT_CLOCK;

    /**
     * Creates a new Configuration with default settings. CacheConfiguration
     * instances should be created using the {@link #newConf()} method.
     */
    CacheConfiguration() {
        // package private for now, might change at a later time.
    }

    public Backend backend() {
        return new Backend();
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
    public <T extends Cache<K, V>> T create(Class<? extends Cache> clazz)
            throws IllegalArgumentException {
        if (clazz == null) {
            throw new NullPointerException("clazz is null");
        }
        T cache = null;
        Constructor<T> c = null;
        try {
            c = (Constructor<T>) clazz.getDeclaredConstructor(CacheConfiguration.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Could not create cache instance, no public contructor taking a single CacheConfiguration instance",
                    e);
        }
        try {
            cache = c.newInstance(this);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "Could not create cache instance, specified clazz " + clazz
                            + ") is an interface or abstract class", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not create instance of " + clazz, e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Constructor threw exception", e);
        }
        if (cache instanceof AbstractCache) {
            ((AbstractCache) cache).initialize();
        }
        return cache;
    }

    public <T extends Cache<K, V>> T createAndStart(Class<? extends AbstractCache> clazz) {
        AbstractCache t = create(clazz);
        t.start();
        return (T) t;
    }

    /**
     * Returns a new Eviction object.
     */
    public Eviction eviction() {
        return new Eviction();
    }

    /**
     * Returns a {@link Expiration} instance that can be used to configure the
     * expiration settings for the cache. For example, default expiration times
     * or custom expiration filters.
     * 
     * @return returns a expiration configuration instance
     */
    public Expiration expiration() {
        return new Expiration();
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
     * Returns the log configured for the cache.
     * 
     * @return the log configured for the cache, or <tt>null</tt> if no log is
     *         configured
     * @see #setLog(Log)
     */
    public CacheErrorHandler<K, V> getErrorHandler() {
        return errorHandler;
    }

    /**
     * Returns a map whose mappings are to be placed in the cache as initial
     * entries or <code>null</code> if no map has been specified.
     * 
     * @see #setInitialMap(Map)
     */
    public Map<? extends K, ? extends V> getInitialMap() {
        return initialMap;
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
     * Returns a map of any additional properties that is defined. This map is a
     * copy of the current properties.
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
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        return additionalProperties.get(key);
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
        if (name == null) {
            throw new NullPointerException("name is null");
        }

        Object result = additionalProperties.get(key);
        return result == null ? defaultValue : result;
    }

    /**
     * Returns a {@link JMX} instance that can be used to configure the JMX
     * settings for the cache.
     * 
     * @return returns a jmx configuration instance
     */
    public JMX jmx() {
        return new JMX();
    }

    /**
     * Sets the {@link org.coconut.core.Clock} that the cache should use.
     * Normally users should not need to set this, only if they want to provide
     * another timing mechanism then the built-in
     * {@link java.lang.System#currentTimeMillis()}. For example, a custom NTP
     * protocol.
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
     * Sets the log that the cache should use for logging anomalies and errors.
     * If no log is set the cache will redirect all output to dev/null
     * 
     * @param log
     *            the log to use
     * @return this configuration
     * @see org.coconut.core.Logs
     * @see org.coconut.core.Log
     * @throws NullPointerException
     *             if log is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setErrorHandler(CacheErrorHandler<K, V> errorHandler) {
        if (errorHandler == null) {
            throw new NullPointerException("errorHandler is null");
        }
        this.errorHandler = errorHandler;
        return this;
    }

    /**
     * Sets a map whose mappings are to be placed in the Cache as initial
     * entries.
     * 
     * @param map
     *            the map to set
     * @return this configuration
     * @see #getInitialMap()
     * @throws NullPointerException
     *             if map is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setInitialMap(Map<? extends K, ? extends V> map) {
        if (map == null) {
            throw new NullPointerException("map is null");
        }
        initialMap = map;
        return this;
    }

    /**
     * Sets the name of the cache. The name should be unique among other
     * configured caches. Allthough not a strict requirement it is advised that
     * only alphanumeric characters are used in the name.
     * <p>
     * If no name is set, most cache implementation will generate an unique name
     * to reference the cache. How exactly this name is generated is
     * implementation specific.
     * 
     * @param name
     *            the name of the cache
     * @return this configuration
     * @see #getName()
     * @throws NullPointerException
     *             if name is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (name.equals("")) {
            throw new IllegalArgumentException(
                    "Cannot specify the empty string as a name");
        } else if (!Pattern.matches("[\\da-zA-Z\\x5F\\x2D]+", name)) {
            throw new IllegalArgumentException(
                    "not a valid name, must only contain alphanumeric characters and '_' or '-', was "
                            + name);
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
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        additionalProperties.put(key, value);
        return this;
    }

    public class Backend {

        /**
         * Returns the CacheConfiguration that this instance is part of.
         * 
         * @return the CacheConfiguration that this instance is part of
         */
        public CacheConfiguration<K, V> c() {
            return CacheConfiguration.this;
        }

        /**
         * Returns the CacheLoader that the cache should use for loading new
         * key-value bindings. If this method returns <code>null</code> no
         * initial loader will be set.
         */
        public CacheLoader<? super K, ? extends V> getBackend() {
            return loader;
        }

        public CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> getExtendedBackend() {
            return extendedLoader;
        }

        // /**
        // * The cache will try to interrupt the thread that is loading a a
        // * element if one with a corresponding key is added. Primarily usefull
        // * for distributed caches with very expensive values (in terms of
        // * calculation) where an update for another host might occur in the
        // * middle of a calculation. Hmm not sure this is so usefull... Lets
        // see
        // * if this is a problem before doing anything against it.
        // */
        // public void setInterruptLoad(boolean interrupt) {
        //
        // }

        /**
         * Sets the loader that should be used for loading new elements into the
         * cache. If the specified loader is <code>null</code> no loader will
         * be used for loading new key-value bindings. All values must then put
         * into the cache by using put or putAll.
         * 
         * @param loader
         *            the loader to set
         * @return the current CacheConfiguration
         */
        public Backend setBackend(CacheLoader<? super K, ? extends V> loader) {
            if (extendedLoader != null) {
                throw new IllegalStateException(
                        "extended loader already set, cannot set a loader");
            }
            CacheConfiguration.this.loader = loader;
            return this;
        }

        /**
         * Sets the loader that should be used for loading new elements into the
         * cache. If the specified loader is <code>null</code> no loader will
         * be used for loading new key-value bindings. All values must then put
         * into the cache by using put or putAll.
         * 
         * @param loader
         *            the loader to set
         * @return the current CacheConfiguration
         */
        public Backend setExtendedBackend(
                CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader) {
            if (CacheConfiguration.this.loader != null) {
                throw new IllegalStateException(
                        "loader already set, cannot set an extended loader");
            }
            extendedLoader = loader;
            return this;
        }
    }

    public class Eviction {
        /**
         * The default maximum size of a cache unless otherwise specified.
         */
        public final static int DEFAULT_MAXIMUM_SIZE = Integer.MAX_VALUE;

        /**
         * Returns the CacheConfiguration that this instance is part of.
         * 
         * @return the CacheConfiguration that this instance is part of
         */
        public CacheConfiguration<K, V> c() {
            return CacheConfiguration.this;
        }

        /**
         * Integer.MAX_VALUE = unlimited
         * 
         * @return the maximum number of elements.
         */
        public long getMaximumCapacity() {
            return maximumCapacity;
        }

        /**
         * Integer.MAX_VALUE = unlimited
         * 
         * @return the maximum number of elements.
         */
        public int getMaximumSize() {
            return maximumSize;
        }

        public ReplacementPolicy getPolicy() {
            return replacementPolicy;

        }

        public long getPreferableCapacity() {
            return preferableCapacity;
        }

        public int getPreferableSize() {
            return preferableSize;
        }

        /**
         * Sets that maximum number of elements that a cache can contain. If the
         * limit is reached the cache should evict elements according to the
         * cache policy specified in setExpirationStrategy.
         * <p>
         * The default value is Integer.MAX_VALUE.
         * 
         * @param elements
         *            the maximum capacity.
         */
        public Eviction setMaximumCapacity(long capacity) {
            if (capacity <= 0) {
                throw new IllegalArgumentException("capacity must greater then 0, was "
                        + capacity);
            }
            maximumCapacity = capacity;
            return this;
        }

        /**
         * Sets that maximum number of elements that the cache can contain. If
         * the limit is reached the cache should evict elements according to the
         * cache policy specified in {@link #setPolicy(ReplacementPolicy)}.
         * <p>
         * The default value is Integer.MAX_VALUE. If 0 is specified the cache
         * will never retain any elements. An effective method for disabling
         * caching.
         * 
         * @param elements
         *            the maximum capacity.
         */
        public Eviction setMaximumSize(int elements) {
            if (elements < 0) {
                throw new IllegalArgumentException(
                        "number of maximum elements must be 0 or greater, was "
                                + elements);
            }
            maximumSize = elements;
            return this;
        }

        /**
         * policies are passed a CacheEntry, because it is the easiest way to
         * support cost/sized based policies.
         * 
         * @param policy
         * @return
         */
        public Eviction setPolicy(ReplacementPolicy policy) {
            replacementPolicy = policy;
            return this;
        }

        public Eviction setPreferableCapacity(long capacity) {
            preferableCapacity = capacity;
            return this;
        }

        public Eviction setPreferableSize(int size) {
            preferableSize = size;
            return this;
        }
    }

    public class Expiration {
        /**
         * Returns the CacheConfiguration that this instance is part of.
         * 
         * @return the CacheConfiguration that this instance is part of
         */
        public CacheConfiguration<K, V> c() {
            return CacheConfiguration.this;
        }

        public long getDefaultTimeout(TimeUnit unit) {
            if (defaultExpirationTimeoutDuration == Cache.NEVER_EXPIRE) {
                // don't convert relative to time unit
                return defaultExpirationTimeoutDuration;
            } else {
                return unit.convert(defaultExpirationTimeoutDuration,
                        TimeUnit.NANOSECONDS);
            }
        }

        public Filter<CacheEntry<K, V>> getFilter() {
            return expirationFilter;
        }

        public Filter<CacheEntry<K, V>> getRefreshFilter() {
            return expirationRefreshFilter;
        }

        public long getRefreshInterval(TimeUnit unit) {
            return unit.convert(defaultExpirationRefreshDuration, TimeUnit.NANOSECONDS);
        }

        /**
         * Sets the default expiration time for elements added to the cache.
         * Elements added using the {@link Cache#put(Object, Object)} will
         * expire at <tt>time_of_insert + default_expiration_time</tt>.
         * Expired elements are handled accordingly to the
         * {@link ExpirationStrategy} set for the cache using
         * {@link #setFilter(Filter)}. The default expiration is infinite, that
         * is elements never expires..
         * 
         * @param duration
         *            the default timeout for elements added to the cache, must
         *            be positive
         * @param unit
         *            the time unit of the duration argument
         */
        public Expiration setDefaultTimeout(long duration, TimeUnit unit) {
            if (duration <= 0) {
                throw new IllegalArgumentException(
                        "duration must be greather then 0, was " + duration);
            } else if (unit == null) {
                throw new NullPointerException("unit is null");
            }
            if (duration == Cache.NEVER_EXPIRE) {
                defaultExpirationTimeoutDuration = Cache.NEVER_EXPIRE;
                // don't convert relative to time unit
            } else {
                defaultExpirationTimeoutDuration = unit.toNanos(duration);
            }
            return this;
        }

        /**
         * Sets a specific expiration that can be used in <tt>addition</tt> to
         * the time based expiration filter to check if items has expired. If no
         * filter has been set items are expired according to their registered
         * expiration time. If an expiration filter is set cache entries are
         * first checked against that filter then against the time based
         * expiration times.
         */
        public Expiration setFilter(Filter<CacheEntry<K, V>> filter) {
            expirationFilter = filter;
            return this;
        }

        public Expiration setRefreshFilter(Filter<CacheEntry<K, V>> filter) {
            expirationRefreshFilter = filter;
            return this;
        }

        /**
         * Sets the default refresh interval. Setting of the refresh window only
         * makes sense if an asynchronously loader has been specified. -1
         * 
         * @param interval
         *            the i
         * @param unit
         *            the unit of the interval
         * @return this Expiration
         */
        public Expiration setRefreshInterval(long interval, TimeUnit unit) {
            if (interval <= 0) {
                throw new IllegalArgumentException(
                        "interval must be greather then 0, was " + interval);
            } else if (unit == null) {
                throw new NullPointerException("unit is null");
            }
            defaultExpirationRefreshDuration = unit.toNanos(interval);
            return this;
        }
    }

    /**
     * This class defines JMX configuration. To register a cache in the platform
     * MBeanServer with the name TODO, simply call
     * <code>setAutomaticRegister(true)</code>. If for some reason the cache
     * fails to properly register with the MBeanServer at construction time a
     * {@link CacheException} is thrown. The user remember to deregister the jmx
     * instance when shutting the down the server. (Or use a softreference)
     */
    public class JMX {

        /**
         * Returns the CacheConfiguration that this instance is part of.
         * 
         * @return the CacheConfiguration that this instance is part of
         */
        public CacheConfiguration<K, V> c() {
            return CacheConfiguration.this;
        }

        public String getDomain() {
            return domain;
        }

        /**
         * @return the configured MBeanServer or the platform MBeanServer if no
         *         server has been set
         */
        public MBeanServer getMBeanServer() {
            if (mBeanServer == null) {
                mBeanServer = ManagementFactory.getPlatformMBeanServer();
            }
            return mBeanServer;
        }

        /**
         * Returns whether or not the cache is automatically registered with a
         * {@link javax.management.MBeanServer} when constructed.
         * 
         * @return <tt>true</tt> if the cache should be automatically
         *         registered with a {@link javax.management.MBeanServer},
         *         otherwise <tt>false</tt>
         * @see #setRegister(boolean)
         */
        public boolean isRegister() {
            return registerForJMXAutomatically;
        }

        /**
         * Sets the specific {@link ObjectName} that this cache should register
         * with. If no ObjectName but the cache has been registered with a name
         * {@link #setName()} the cache will be registered under foo+name. If no
         * name has been set foo+S System.identityHashCode(cache) the name
         * should probably also be initialized to this value (hexed)
         * 
         * @param name
         *            the object name
         * @return this configuration
         */
        public JMX setDomain(String name) {
            if (name == null) {
                throw new NullPointerException("name is null");
            }
            // TODO validate domain name
            CacheConfiguration.this.domain = domain;
            return this;
        }

        /**
         * Sets the {@link MBeanServer}} that the cache should register with.
         * If no value is set the platform {@link MBeanServer} will be used.
         * 
         * @param server
         *            the server that the cache should register with
         * @return this configuration
         * @throws NullPointerException
         *             if server is <tt>null</tt>
         */
        public JMX setMbeanServer(MBeanServer server) {
            if (server == null) {
                throw new NullPointerException("server is null");
            }
            mBeanServer = server;
            return this;
        }

        /**
         * Determines whether or not the cache will be register with its
         * MBeanServer at construction time.
         * 
         * @param registerAutomatic
         *            whether or not the cache should register with the
         *            configured MBeanServer at startup
         * @return this configuration
         * @see #isRegister()
         * @see #setObjectName(ObjectName)
         */
        public JMX setRegister(boolean registerAutomatic) {
            registerForJMXAutomatically = registerAutomatic;
            return this;
        }

        /**
         * We can now deliver jmx events.
         * 
         * @param events
         *            the types of events that should be delivered
         * @return this configuration
         */
        public JMX setRegisterEventsForNotification(Class<? extends CacheEvent>... events) {
            return this;
        }
    }

    public class Statistics {
        public boolean getEnabled() {
            return statisticsEnabled;
        }

        public void setEnabled(boolean isEnabled) {
            statisticsEnabled = isEnabled;
        }

    }

    public class Threading {
        
        /**
         * Returns the CacheConfiguration that this instance is part of.
         * 
         * @return the CacheConfiguration that this instance is part of
         */
        public CacheConfiguration<K, V> c() {
            return CacheConfiguration.this;
        }

        public Executor getExecutor() {
            return executor;
        }

        public boolean getShutdownExecutorService() {
            return shutdownExecutor;
        }

        public void setExecutor(Executor e) {
            executor = e;
        }

        public void setShutdownExecutorService(boolean shutdown) {
            shutdownExecutor = shutdown;
        }

        public long getScheduledEvictionAtFixedRate(TimeUnit unit) {
            return unit.convert(scheduleEvictionAtFixedRateNanos, TimeUnit.NANOSECONDS);
        }

        public void SetScheduledEvictionAtFixedRate(long period, TimeUnit unit) {
            if (period < 0) {
                throw new IllegalArgumentException("period must be 0 or greater, was "
                        + period);
            } 
            if (executor == null || !(executor instanceof ScheduledExecutorService)) {
                throw new IllegalStateException("A ScheduledExecutorService must be set");
            }
            scheduleEvictionAtFixedRateNanos = unit.toNanos(period);
        }
    }

    long scheduleEvictionAtFixedRateNanos = 0;
    
    public Threading threading() {
        return new Threading();
    }
}
