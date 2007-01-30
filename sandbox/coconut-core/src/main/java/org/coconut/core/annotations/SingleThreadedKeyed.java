package coconut.core.annotation;

import java.lang.annotation.*;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
@Documented()
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SingleThreadedKeyed {
    String value();
}
