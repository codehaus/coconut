/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.management;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.coconut.cache.CacheConfiguration;

public class EnableManagementCustom {
    public static void main(String[] args) {
        CacheConfiguration<String, String> conf = CacheConfiguration.create();
        // START SNIPPET: setDomain
        conf.management().setEnabled(true).setDomain("com.acme");
        // END SNIPPET: setDomain
        // START SNIPPET: setMBeanServer
        MBeanServer server = MBeanServerFactory.newMBeanServer();
        conf.management().setEnabled(true).setMBeanServer(server);
        // END SNIPPET: setMBeanServer
    }
}
