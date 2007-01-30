package coconut.core.annotation;

import java.lang.annotation.*;

/**
*
*  
* @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
*/
@Documented()
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SingleThreaded {

}
