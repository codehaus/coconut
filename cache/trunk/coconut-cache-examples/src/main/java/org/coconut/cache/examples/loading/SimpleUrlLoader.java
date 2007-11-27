/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.loading;

// START SNIPPET: SimpleUrlLoader
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.service.loading.AbstractCacheLoader;

public class SimpleUrlLoader extends AbstractCacheLoader<String, String> {
    public String load(String key, AttributeMap ignore) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(new URL(key).openStream()));
        StringBuilder sb = new StringBuilder();
        int str;
        while ((str = in.read()) != -1) {
            sb.append((char) str);
        }
        in.close();
        return sb.toString();
    }
}
// END SNIPPET: SimpleUrlLoader
