package org.coconut.aio.management;

import javax.management.MBeanServer;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;

import org.coconut.aio.spi.AioProvider;

/**
 * The <tt>ManagementFactory</tt> class is a factory class for getting managed
 * beans for the Java Coconut AIO. This class consists of static methods each of
 * which returns one or more <a href="#MXBean">Coconut AIO MXBean(s) </a>
 * representing the management interface of a component of the Java virtual
 * machine.
 * <p>
 * An application can access a Coconut AIO MXBean in the following ways:
 * <ul>
 * <li><i>Direct access to an MXBean interface </i>
 * <ol type="a">
 * <li>Get the MXBean instance through the static factory method and access the
 * MXBean locally of the running virtual machine.</li>
 * <li>Construct an MXBean proxy instance that forwards the method calls to a
 * given {@link MBeanServer MBeanServer}by calling
 * {@link #newPlatformMXBeanProxy newPlatfromMXBeanProxy}. A proxy is typically
 * constructed to remotely access an MXBean of another running virtual machine.
 * </li>
 * </ol>
 * </li>
 * <li><i>Indirect access to an MXBean interface via MBeanServer </i>
 * <ol type="a">
 * <li>Go through the {@link #getPlatformMBeanServer Coconut AIO MBeanServer}
 * to access MXBeans locally or a specific <tt>MBeanServerConnection</tt> to
 * access MXBeans remotely. The attributes and operations of an MXBean use only
 * <em>JMX open types</em> which include basic data types,
 * {@link CompositeData CompositeData}, and {@link TabularData TabularData}
 * defined in {@link javax.management.openmbean.OpenType OpenType}. The mapping
 * is specified below.</li>
 * </ol>
 * </li>
 * </ul>
 * <h4><a name="MXBean">Coconut AIO MXBeans </a></h4>
 * A Coconut AIO MXBean is a <i>managed bean </i> that conforms to the JMX
 * Instrumentation Specification and only uses a set of basic data types
 * described below. A JMX management application and the Coconut AIO
 * <tt>MBeanServer</tt> can interoperate without requiring classes for MXBean
 * specific data types. The data types being transmitted between the JMX
 * connector server and the connector client are
 * {@link javax.management.openmbean.OpenType open types}and this allows
 * interoperation across versions.
 * <p>
 * The Coconut AIO MXBean interfaces use only the following data types:
 * <ul>
 * <li>Primitive types such as <tt>int</tt>,<tt>long</tt>,
 * <tt>boolean</tt>, etc</li>
 * <li>Wrapper classes for primitive types such as
 * {@link java.lang.Integer Integer},{@link java.lang.Long Long},
 * {@link java.lang.Boolean Boolean}, etc and {@link java.lang.String String}
 * </li>
 * <li>{@link java.lang.Enum Enum}classes</li>
 * <li>Classes that define only getter methods and define a static
 * <tt>from</tt> method with a {@link CompositeData CompositeData}argument to
 * convert from an input <tt>CompositeData</tt> to an instance of that class
 * </li>
 * <li>{@link java.util.List List&lt;E&gt;}where <tt>E</tt> is a primitive
 * type, a wrapper class, an enum class, or a class supporting conversion from a
 * <tt>CompositeData</tt> to its class</li>
 * <li>{@link java.util.Map Map&lt;K,V&gt;}where <tt>K</tt> and <tt>V</tt>
 * are a primitive type, a wrapper class, an enum class, or a class supporting
 * conversion from a <tt>CompositeData</tt> to its class</li>
 * </ul>
 * <p>
 * When an attribute or operation of a Coconut AIO MXBean is accessed via an
 * <tt>MBeanServer</tt>, the data types are mapped as follows:
 * <ul>
 * <li>A primitive type or a wrapper class is mapped to the same type.</li>
 * <li>An {@link Enum}is mapped to <tt>String</tt> whose value is the name
 * of the enum constant.
 * <li>A class that defines only getter methods and a static <tt>from</tt>
 * method with a {@link CompositeData CompositeData}argument is mapped to
 * {@link javax.management.openmbean.CompositeData CompositeData}.</li>
 * <li><tt>Map&lt;K,V&gt;</tt> is mapped to {@link TabularData TabularData}
 * whose row type is a {@link CompositeType CompositeType}with two items whose
 * names are <i>"key" </i> and <i>"value" </i> and the item types are the
 * corresponding mapped type of <tt>K</tt> and <tt>V</tt> respectively and
 * the <i>"key" </i> is the index.</li>
 * <li><tt>List&lt;E&gt;</tt> is mapped to an array with the mapped type of
 * <tt>E</tt> as the element type.</li>
 * <li>An array of element type <tt>E</tt> is mapped to an array of the same
 * dimenions with the mapped type of <tt>E</tt> as the element type.</li>
 * </ul>
 * The {@link javax.management.MBeanInfo MBeanInfo}for a Coconut AIO MXBean
 * describes the data types of the attributes and operations as primitive or
 * open types mapped as specified above.
 * <h4><a name="MXBeanNames">MXBean Names </a></h4>
 * Each Coconut AIO MXBean for a Java virtual machine has a unique
 * {@link javax.management.ObjectName ObjectName}for registration in the
 * Coconut AIO <tt>MBeanServer</tt>. A Java virtual machine has a single
 * instance of the following management interfaces: <blockquote><table border>
 * <tr>
 * <th>Management Interface</th>
 * <th>ObjectName</th>
 * </tr>
 * <tr>
 * <td>{@link ServerSocketMXBean}</td>
 * <td>{@link #SERVER_SOCKET_MXBEAN_NAME<tt>coconut.aio:type=ServerSocket</tt>}
 * </td>
 * </tr>
 * <tr>
 * <td>{@link SocketMXBean}</td>
 * <td>{@link #SOCKET_MXBEAN_NAME<tt>coconut.aio:type=Socket</tt>}</td>
 * </tr>
 * <tr>
 * <td>{@link SocketGroupMXBean}</td>
 * <td>{@link #SOCKET_GROUP_MXBEAN_NAME<tt>coconut.aio:type=SocketGroup</tt>}</td>
 * </tr>
 * <tr>
 * <td>{@link DatagramMXBean}</td>
 * <td>{@link #DATAGRAM_MXBEAN_NAME<tt>coconut.aio:type=Datagram</tt>}</td>
 * </tr>
 * <tr>
 * <td>{@link DatagramGroupMXBean}</td>
 * <td>{@link #DATAGRAM_GROUP_MXBEAN_NAME<tt>coconut.aio:type=DatagramGroup</tt>}</td>
 * </tr>
 * <tr>
 * <td>{@link FileMXBean}</td>
 * <td>{@link #DISC_MXBEAN_NAME<tt>coconut.aio:type=Disc</tt>}</td>
 * </tr>
 * </table> </blockquote>
 * 
 * @see <a href="../../../javax/management/package-summary.html"> JMX
 *      Specification. </a>
 * @see <a href="package-summary.html#examples"> Ways to Access Management
 *      Metrics </a>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class ManagementFactory {
    // A class with only static fields and methods.
    private ManagementFactory() {}

    /**
     * String representation of the <tt>ObjectName</tt> for the
     * {@link ServerSocketMXBean}.
     */
    public final static String SERVER_SOCKET_MXBEAN_NAME = "coconut.aio:type=ServerSocket";

    /**
     * String representation of the <tt>ObjectName</tt> for the
     * {@link SocketMXBean}.
     */
    public final static String SOCKET_MXBEAN_NAME = "coconut.aio:type=Socket";

    /**
     * String representation of the <tt>ObjectName</tt> for the
     * {@link SocketGroupMXBean}.
     */
    public final static String SOCKET_GROUP_MXBEAN_NAME = "coconut.aio:type=SocketGroup";

    /**
     * String representation of the <tt>ObjectName</tt> for the
     * {@link DatagramMXBean}.
     */
    public final static String DATAGRAM_MXBEAN_NAME = "coconut.aio:type=Datagram";

    /**
     * String representation of the <tt>ObjectName</tt> for the
     * {@link DatagramGroupMXBean}.
     */
    public final static String DATAGRAM_GROUP_MXBEAN_NAME = "coconut.aio:type=DatagramGroup";

    /**
     * String representation of the <tt>ObjectName</tt> for the
     * {@link FileMXBean}.
     */
    public final static String DISC_MXBEAN_NAME = "coconut.aio:type=Disc";

    public static ServerSocketMXBean getServerSocketMXBean() {
        return AioProvider.provider().getServerSocketMXBean();
    }

    public static SocketMXBean getSocketMXBean() {
        return AioProvider.provider().getSocketMXBean();
    }

    public static DatagramGroupMXBean getDatagramGroupMXBean() {
        return AioProvider.provider().getDatagramGroupMXBean();
    }

    public static SocketGroupMXBean getSocketGroupMXBean() {
        return AioProvider.provider().getSocketGroupMXBean();
    }
    
    public static DatagramMXBean getDatagramMXBean() {
        return AioProvider.provider().getDatagramMXBean();
    }

    public static FileMXBean getDiskMXBean() {
        return AioProvider.provider().getFileMXBean();
    }
}