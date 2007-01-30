package coconut.core.annotation;

import java.lang.annotation.*;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
@Target({ElementType.TYPE})
public @interface Named {
    String value();
}
